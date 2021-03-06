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

import java.lang.Integer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private Map<Integer, List<UsageHour>> usageHoursPerMachine;

    private Map<Integer, Long> uptimePerMachine;

    private Map<Integer, Long> downtimePerMachine;

	public void addUsageHour(UsageHour usageHour) {
		usageHours.add(usageHour);
	}

	public Collection<UsageHour> loadUsageHours() {
		return Collections.unmodifiableCollection(usageHours);
	}

	public void loadUptimeHours() {
		//FIXME: Problem with downtime calculations
		long gatheringStartTime = startTime.getTime();
		long gatheringEndTime = endTime.getTime();
		LOGGER.debug("Gathering start time: " + gatheringStartTime);
		LOGGER.debug("Gathering end time: " + gatheringEndTime);
		long previousGatheringPointOfTime = 0;

		generateUsageHourTreePerMachineId();
		for (Map.Entry<Integer, List<UsageHour>> entry : usageHoursPerMachine.entrySet()) {
			List<UsageHour> usageHoursList = entry.getValue();
			boolean firstIndex = false;
			boolean lastIndex = false;
			for (int index = 0 ; index < usageHoursList.size() ; index++) {
				firstIndex = (index == 0) ? true : false;
				lastIndex  = (index == usageHoursList.size()-1) ? true : false;
				UsageHour usageHour = usageHoursList.get(index);
				VirtualMachineState virtualMachineState = usageHour.getVirtualMachineState();
				LOGGER.debug("Usage hour state modification time: " + usageHour.getTimeStamp().getTime()+ ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
				switch (virtualMachineState) {
					case STARTED :
						uptime   += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour);
						LOGGER.debug("State : started, Uptime : " + uptime+ ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
						break;
					case STOPPED :
						downtime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour);
						LOGGER.debug("State : stopped, Downtime : " + uptime + ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
						break;
					case RESUMED :
						uptime   += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour);
						LOGGER.debug("State : resumed, Uptime : " + uptime+ ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
						break;
					case PAUSED :
						downtime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour);
						LOGGER.debug("State : paused, Downtime : " + uptime+ ", previous point of time: " + previousGatheringPointOfTime + ", current time: " + usageHour.getTimeStamp().getTime());
						break;
					case TERMINATED :
						downtime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = getPointOfTime(gatheringStartTime, gatheringEndTime, firstIndex, lastIndex, usageHour);
						LOGGER.debug("State : terminated, Downtime : " + uptime);
						break;
				}
				System.out.println("Point of time : " + previousGatheringPointOfTime);
			}
		}
		long periodTime = endTime.getTime()-startTime.getTime();
		LOGGER.debug("Period: " + periodTime);
		LOGGER.debug("System uptime: " + (periodTime - uptime));
		LOGGER.debug("System downtime: " + (periodTime - (periodTime - uptime)));
		uptime = (periodTime - uptime);
		downtime = (periodTime - (periodTime - uptime));
	}

	public void loadUptimeHoursPerMachine() {
        LOGGER.debug("loadUptimeHoursPerMachine ENTER");
        uptimePerMachine = new HashMap<Integer, Long>();
        downtimePerMachine = new HashMap<Integer, Long>();

		long gatheringStartTime = startTime.getTime();
		long gatheringEndTime = endTime.getTime();
		LOGGER.debug("Gathering start time: " + gatheringStartTime + " " + startTime);
		LOGGER.debug("Gathering end time: " + gatheringEndTime + " " + endTime);
        LOGGER.debug("OrganizationId: " + organizationId);

        long periodTime = gatheringEndTime - gatheringStartTime;
        long previousGatheringPointOfTime = 0;
        generateUsageHourTreePerMachineId();
		for (Map.Entry<Integer, List<UsageHour>> entry : usageHoursPerMachine.entrySet()) {
			List<UsageHour> usageHoursList = entry.getValue();
			long machineUptime = 0;
			long machineDowntime = 0;
			boolean firstIndex = false;
			boolean lastIndex = false;
            previousGatheringPointOfTime = gatheringStartTime;
            for (int index = 0 ; index < usageHoursList.size() ; index++) {
				firstIndex = (index == 0) ? true : false;
				lastIndex  = (index == usageHoursList.size()-1) ? true : false;
				UsageHour usageHour = usageHoursList.get(index);
				VirtualMachineState virtualMachineState = usageHour.getVirtualMachineState();
				LOGGER.debug("Usage hour time: " + usageHour.getTimeStamp().getTime()+ "," +
                             "previous point of time: " + previousGatheringPointOfTime +
                             " diff = " + (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime) +
                             " index =" + index);
                LOGGER.debug("firstIndex:" + firstIndex);
                LOGGER.debug("lastIndex:" + lastIndex);

                switch (virtualMachineState) {
					case STARTED :
                        machineDowntime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = usageHour.getTimeStamp().getTime();
                        if (lastIndex) {
                            machineUptime += gatheringEndTime - usageHour.getTimeStamp().getTime();
                        }
						LOGGER.debug("State : started, previousGatheringPointOfTime: "+ previousGatheringPointOfTime);
						break;
					case STOPPED :
                        machineUptime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = usageHour.getTimeStamp().getTime();
                        if (lastIndex) {
                            machineDowntime += gatheringEndTime - usageHour.getTimeStamp().getTime();
                        }
                        LOGGER.debug("State : stoped, previousGatheringPointOfTime: "+ previousGatheringPointOfTime);
                        break;
					case RESUMED :
                        machineDowntime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = usageHour.getTimeStamp().getTime();
                        if (lastIndex) {
                            machineUptime += gatheringEndTime - usageHour.getTimeStamp().getTime();
                        }
                        LOGGER.debug("State : resumed, previousGatheringPointOfTime: "+ previousGatheringPointOfTime);
                        break;
					case PAUSED :
                        machineUptime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = usageHour.getTimeStamp().getTime();
                        if (lastIndex) {
                            machineDowntime += gatheringEndTime - usageHour.getTimeStamp().getTime();
                        }
                        LOGGER.debug("State : paused, previousGatheringPointOfTime: "+ previousGatheringPointOfTime);
                        break;
					case TERMINATED :
                        machineUptime += (usageHour.getTimeStamp().getTime() - previousGatheringPointOfTime);
						previousGatheringPointOfTime = usageHour.getTimeStamp().getTime();
                        if (lastIndex) {
                            machineDowntime += gatheringEndTime - usageHour.getTimeStamp().getTime();
                        }
                        LOGGER.debug("State : terminated, previousGatheringPointOfTime: "+ previousGatheringPointOfTime);
                        break;
				}
			}
			uptimePerMachine.put(entry.getKey(), machineUptime);
			downtimePerMachine.put(entry.getKey(), machineDowntime);
            LOGGER.debug("Period: " + periodTime);
            LOGGER.debug("MachineId: " + entry.getKey());

            LOGGER.debug("System uptime: " + machineUptime);
            LOGGER.debug("System downtime: " + machineDowntime);
		}
	}


	private long getPointOfTime(long gatheringStartTime, long gatheringEndTime, boolean firstIndex, boolean lastIndex, UsageHour usageHour) {
        return firstIndex ? gatheringStartTime : usageHour.getTimeStamp().getTime();

    }

	private void generateUsageHourTreePerMachineId() {
		this.usageHoursPerMachine = new HashMap<Integer, List<UsageHour>>();
        LOGGER.debug("UsageHours size:" + usageHours.size());
		for (UsageHour usageHour : usageHours) {
            LOGGER.debug("MachineId: " + usageHour.getMachineId());
			int machineId = usageHour.getMachineId();
			if (this.usageHoursPerMachine.containsKey(machineId)) {
				List<UsageHour> usageHours = this.usageHoursPerMachine.get(machineId);
                LOGGER.debug("Add usage hour");
                usageHours.add(usageHour);
			} else {
                LOGGER.debug("Create new usage hour");
                List<UsageHour> usageHourList = new ArrayList<UsageHour>();
				usageHourList.add(usageHour);
				this.usageHoursPerMachine.put(machineId, usageHourList);
			}
		}
        LOGGER.debug("usageHoursPerMachine size:" + usageHoursPerMachine.size());
	}
}
