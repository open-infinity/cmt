/*
 * Copyright (c) 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openinfinity.cloud.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

import org.openinfinity.cloud.domain.UsageHour.VirtualMachineState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represent usage period of certain virtual machines and their state.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
@Data
public class UsagePeriod {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UsagePeriod.class);
	
	private long organizationId;
	
	private Date startTime;
	
	private Date endTime;
	
	private long uptime = 0;
	
	private long downtime = 0;
	
	private Collection<UsageHour> usageHours = Collections.checkedCollection(new ArrayList<UsageHour>(), UsageHour.class);
	
	public void addUsageHour(UsageHour usageHour) {
		usageHours.add(usageHour);
	}
	
	public Collection<UsageHour> loadUsageHours() {
		return Collections.unmodifiableCollection(usageHours);
	}
	
	/**
	 * Return a Map that contains uptime per machine. The machine id acts as a key for the Map.
	 * 
	 * @return
	 */
	public Map<Integer, MachineUsage> getUptimeHoursPerMachine() {
		
		/*
		 * Collect data to this Map and return it.
		 */
		Map<Integer, MachineUsage> uptimePerMachine = new HashMap<Integer, MachineUsage>();
		
		Map<Integer, List<UsageHour>> usageEventsPerMachine = generateUsageHourTreePerMachineId();
		
		for (Entry<Integer, List<UsageHour>> eventSet : usageEventsPerMachine.entrySet()) {
			
			List<UsageHour> usageEventsPerMachineList = eventSet.getValue();
			
			Date machineStartTime = null;
			Date machineStopTime = null;
			int errorCount = 0;
			String errorMessage = "";
			
			for (UsageHour event : usageEventsPerMachineList) {
				
				switch (event.getVirtualMachineState()) {
				case STARTED:
					if (machineStartTime != null) {
						/*
						 * Too many START events.
						 */
						errorCount++;
						errorMessage = "Too many START events with machine id : " + event.getMachineId() + ". Using the most recent.";
						LOGGER.warn(errorMessage);
					}
					LOGGER.debug("Machine id {} started : {}", event.getMachineId(), event.getTimeStamp());
					machineStartTime = event.getTimeStamp();
					break;
				case STOPPED:
					if (machineStartTime == null) {
						/*
						 * There is no start time for the machine. Can not calculate uptime.
						 */
						errorCount++;
						errorMessage = "No START event for the machine id : " + event.getMachineId() + ". Can not calculate uptime.";
						LOGGER.warn(errorMessage);
					}
					if (machineStopTime != null) {
						/*
						 * Too many STOP events. Using the oldest one.
						 */
						errorCount++;
						errorMessage = "Too many STOP events with machine id : " + event.getMachineId() + ". Using the oldest one.";
						LOGGER.warn(errorMessage);
					} else {
						LOGGER.debug("Machine id {} stopped : {}", event.getMachineId(), event.getTimeStamp());
						machineStopTime = event.getTimeStamp();
					}
					break;
					
					/*
					 * These events are not supported at the moment. Throws exception.
					 */
				case RESUMED:
				case PAUSED:
				case TERMINATED:
					throw new RuntimeException("Calculation of the following events are not implemented yet: RESUMED, PAUSED, TERMINATED");
				}
				
			}
			
			UsageHour uh = usageEventsPerMachineList.get(0);
			
			if (machineStartTime == null) {
				errorCount++; // There should be error count, but increment just in case.
				errorMessage = "No start time for the machine id : " + uh.getMachineId() + ". Can not calculate uptime.";
				LOGGER.warn(errorMessage);
			}
			
			long uptime = 0;
			if (machineStartTime != null &&
				machineStartTime.getTime() < endTime.getTime() &&
				(machineStopTime == null || machineStopTime.getTime() > startTime.getTime())) {				
				Date countStartTime;
				Date countEndTime;				
				if (machineStartTime.getTime() > startTime.getTime()) {
					countStartTime = machineStartTime;
				} else {
					// The machine has been started in previous period. Using period start time for calculation.
					countStartTime = startTime;
				}
				if (machineStopTime != null && machineStopTime.getTime() < endTime.getTime()) {
					countEndTime = machineStopTime;
				} else {
					// The machine is still running. Using period end time for calculation.
					countEndTime = endTime;
				}
				uptime = countEndTime.getTime() - countStartTime.getTime();				
			}
			
			MachineUsage mu = new MachineUsage(
					uh.getMachineId(),
					uh.getInstanceId(),
					uh.getClusterTypeTitle(),
					uh.getClusterId(),
					uh.getMachineTypeId(),
					uh.getMachineTypeName(),
					uh.getMachineTypeSpec(),
					uh.getMachineMachineType(),
					uh.getClusterEbsImageUsed(),
					uh.getClusterEbsVolumesUsed(),
					machineStartTime,
					machineStopTime,
					startTime,
					endTime,
					uptime,
					errorCount,
					errorMessage
					);
			LOGGER.debug(mu.toString());
			LOGGER.debug("Uptime (sec): {}", mu.getUptimeInSeconds());
			LOGGER.debug("Uptime (min): {}", mu.getUptimeInMinutes());
			uptimePerMachine.put(mu.getMachineId(), mu);
		}
		
		return uptimePerMachine;
	}
	
	public void loadUptimeHours() {
		//FIXME: Problem with downtime calculations
		long gatheringStartTime = startTime.getTime();
		long gatheringEndTime = endTime.getTime();	
		LOGGER.debug("Gathering start time: " + gatheringStartTime);
		LOGGER.debug("Gathering end time: " + gatheringEndTime);
		long previousGatheringPointOfTime = 0;
		Map<Integer, List<UsageHour>> usageHoursPerMachine = generateUsageHourTreePerMachineId();
		for (Map.Entry<Integer, List<UsageHour>> entry : usageHoursPerMachine.entrySet()) {
			List<UsageHour> usageHoursList = entry.getValue();
			boolean firstIndex = false;
			boolean lastIndex = false;
			for (int index = 0 ; index < usageHoursList.size() ; index++) {
				firstIndex = (index == 0) ? true : false;
				lastIndex  = (index == usageHoursList.size()-1) ? true : false;
				UsageHour usageHour = usageHoursList.get(index);
				VirtualMachineState virtualMachineState = usageHour.getVirtualMachineState();
				LOGGER.trace("Usage hour state modification time: " + usageHour.getTimeStamp().getTime()+ ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
				switch (virtualMachineState) {
					case STARTED : 
						uptime   += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);  
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour); 
						LOGGER.trace("State : started, Uptime : " + uptime+ ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
						break;
					case STOPPED : 
						downtime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);  
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour); 
						LOGGER.trace("State : stopped, Downtime : " + uptime + ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
						break;
					case RESUMED : 
						uptime   += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime); 
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour); 
						LOGGER.trace("State : resumed, Uptime : " + uptime+ ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
						break;
					case PAUSED : 
						downtime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);  
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour); 
						LOGGER.trace("State : paused, Downtime : " + uptime+ ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
						break;
					case TERMINATED : 
						downtime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);  
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour); 
						LOGGER.trace("State : terminated, Downtime : " + uptime);
						break;
				}
				LOGGER.debug("Point of time : {}", previousGatheringPointOfTime);
			}	
		}
		long periodTime = endTime.getTime()-startTime.getTime();
		LOGGER.trace("Period: " + periodTime);
		LOGGER.trace("System uptime: " + (periodTime - uptime));
		LOGGER.trace("System downtime: " + (periodTime - (periodTime - uptime)));
		uptime = (periodTime - uptime);
		downtime = (periodTime - (periodTime - uptime));	
	}
	
	private long getPointOfTime(long gatheringStartTime, long gatheringEndTime, boolean firstIndex, boolean lastIndex, UsageHour usageHour) {
		return firstIndex ? gatheringStartTime : (lastIndex ? gatheringEndTime : usageHour.getTimeStamp().getTime());
	}
	
	private Map<Integer, List<UsageHour>> generateUsageHourTreePerMachineId() {
		Map<Integer, List<UsageHour>> usageHoursPerMachine = new HashMap<Integer, List<UsageHour>>(); 
		for (UsageHour usageHour : usageHours) {
			int machineId = usageHour.getMachineId();
			if (usageHoursPerMachine.containsKey(machineId)) {
				List<UsageHour> usageHours = usageHoursPerMachine.get(machineId);
				usageHours.add(usageHour);
			} else {
				List<UsageHour> usageHourList = new ArrayList<UsageHour>();
				usageHourList.add(usageHour);
				usageHoursPerMachine.put(machineId, usageHourList);
			}
		}
		return usageHoursPerMachine;
	}
}
