/*
 * Copyright (c) 2012 the original author or authors.
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

import java.sql.Timestamp;

import lombok.Data;

/**
 * Job domain object
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
@Data
public class Job {
	
	private int jobId;
	private String jobType;
	private int jobStatus;
	private int instanceId;
	private String services;
	private String extraData;
	private int cloud;
	private String zone;
	private Timestamp createTime;
	private Timestamp startTime;
	private Timestamp endTime;
	
	// Constructors
	public Job (){};
	
	public Job (String aJobType, int aInstanceId, int aCloud, int aJobStatus, String aZone){
		jobType = aJobType;
		instanceId = aInstanceId;
		cloud = aCloud;
		jobStatus = aJobStatus;
		zone = aZone;
	}
	
	public Job (String aJobType, int aInstanceId, int aCloud, int aJobStatus, String aZone, String aService){
		jobType = aJobType;
		instanceId = aInstanceId;
		cloud = aCloud;
		jobStatus = aJobStatus;
		zone = aZone;
		services = aService;
	}
	
	public Job (String aJobType, int aInstanceId, int aCloud, int aJobStatus, String aZone, String aService, int aNumberOfMachines){
		jobType = aJobType;
		instanceId = aInstanceId;
		cloud = aCloud;
		jobStatus = aJobStatus;
		zone = aZone;
		addService(aService, aNumberOfMachines);
	}
	
	public Job (String aJobType, int aInstanceId, int aCloud, int aJobStatus, String aZone, String aService, int aNumberOfMachines,int aMachineSize){
		jobType = aJobType;
		instanceId = aInstanceId;
		cloud = aCloud;
		jobStatus = aJobStatus;
		zone = aZone;
		addService(aService, aNumberOfMachines, aMachineSize);
	}
	
	public Job (String aJobType, int aInstanceId, int aCloud, int aJobStatus){
		this(aJobType, aInstanceId, aCloud, aJobStatus, "");
	}
	
	// Service string creators
	public void addService(String service, int numberOfMachines) {
		if(this.services == null) {
			this.services = new String();
		}
		if(this.services != null && this.services.length() > 0) {
			this.services += ",";
		}
		this.services += service+","+numberOfMachines;
	}
	
	public void addService(String service, int numberOfMachines, int machineSize) {
		addService(service, numberOfMachines);
		this.services += "," + machineSize;
	}
	
	public void addService(String service, String numberOfMachines, String machineSize, String imageType, String ebsVolumeSize) {
		addService(service, Integer.parseInt(numberOfMachines));
		this.services += "," + machineSize + "," + imageType + "," +ebsVolumeSize;
	}
	
}
