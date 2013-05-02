/*
 * Copyright (c) 2011 the original author or authors.
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

package org.openinfinity.cloud.application.worker.component;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.worker.Worker;
import org.openinfinity.cloud.domain.AuthorizationRoute;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.ElasticIP;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.MulticastAddress;
import org.openinfinity.cloud.service.administrator.AuthorizationRoutingService;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.EC2Wrapper;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.administrator.KeyService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.administrator.MulticastAddressService;
import org.openinfinity.cloud.service.usage.UsageService;
import org.openinfinity.cloud.util.PropertyManager;
import org.openinfinity.cloud.util.WorkerException;
import org.openinfinity.cloud.util.XmlParse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * EC2Worker Component
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Component("EC2Worker")
public class EC2Worker implements Worker {
	private static final Logger LOG = Logger.getLogger(EC2Worker.class.getName());
	private static final String PROPERTY_PREFIX = "cloudadmin.worker.service.";
	
	@Autowired
	@Qualifier("instanceService")
	private InstanceService instanceService;

	@Autowired
	@Qualifier("jobService")
	private JobService jobService;
	
	@Autowired
	@Qualifier("clusterService")
	private ClusterService clusterService;
	
	@Autowired
	@Qualifier("authorizationRoutingService")
	private AuthorizationRoutingService arService;
	
	@Autowired
	@Qualifier("machineService")
	private MachineService machineService;
	
	@Autowired
	@Qualifier("keyService")
	private KeyService keyService;
	
	@Autowired
	@Qualifier("cloudCredentials")
	private AWSCredentials eucaCredentials;
	
	@Autowired
	@Qualifier("multicastAddressService")
	private MulticastAddressService maService;
	
	@Autowired
	@Qualifier("usageService")
	private UsageService usageService;
	
	@Async
	public void work(Job job) {
		String threadName = Thread.currentThread().getName();
		LOG.info("Worker on thread "+threadName+" starting working on job "+job.getJobId());
		jobService.setStartTime(job.getJobId());
		
		String jobType = job.getJobType();
		if(jobType.equalsIgnoreCase("create_instance")) {
			LOG.info(threadName+": Creating new instance");
			try {
				if(createNewInstance(job) < 1) {
					LOG.debug(threadName+": Got error, setting status to ERROR");
					job.setJobStatus(JobService.CLOUD_JOB_ERROR);
				} else {
					LOG.debug(threadName+": Success, setting status to READY");
					job.setJobStatus(JobService.CLOUD_JOB_READY);
				}
			} catch (Exception e) {
				LOG.error(threadName+": Error creating instance: "+e.getMessage());
				job.setJobStatus(JobService.CLOUD_JOB_ERROR);
			}
		} else if(jobType.equalsIgnoreCase("delete_instance")) {
			LOG.info(threadName+": Deleting instance "+job.getInstanceId());
			try {
				if(deleteInstance(job) < 1) {
					LOG.debug(threadName+": Got error, setting status to ERROR");
					job.setJobStatus(JobService.CLOUD_JOB_ERROR);
				} else {
					LOG.debug(threadName+" Success, setting status to READY");
					job.setJobStatus(JobService.CLOUD_JOB_READY);
				}
			} catch (Exception e) {
				LOG.error(threadName+": Error deleting instance "+job.getInstanceId()+": "+e.getMessage());
				job.setJobStatus(JobService.CLOUD_JOB_ERROR);
			}
		} else if(jobType.equalsIgnoreCase("delete_cluster")) {
			LOG.info(threadName+": Deleting cluster in instance "+job.getInstanceId());
			try {
				if(deleteCluster(job) < 1) {
					LOG.debug(threadName+": Got error, setting status to ERROR");
					job.setJobStatus(JobService.CLOUD_JOB_ERROR);
				} else {
					LOG.debug(threadName+" Success, setting status to READY");
					job.setJobStatus(JobService.CLOUD_JOB_READY);
				}
			} catch (Exception e) {
				LOG.error(threadName+": Error deleting instance "+job.getInstanceId()+": "+e.getMessage());
				job.setJobStatus(JobService.CLOUD_JOB_ERROR);
			}
		} else if(jobType.equalsIgnoreCase("add_service")) {
			LOG.info(threadName+": Adding service to instance "+job.getInstanceId());
			try {
				if(addServiceToInstance(job) < 1) {
					LOG.debug(threadName+" Got error, setting status to ERROR");
					job.setJobStatus(JobService.CLOUD_JOB_ERROR);
				} else {
					LOG.debug(threadName+" Success, setting status to READY");
					job.setJobStatus(JobService.CLOUD_JOB_READY);
				}
			} catch (Exception e) {
				LOG.error(threadName+": Error adding service to instance "+job.getInstanceId()+": "+e.getMessage());
				job.setJobStatus(JobService.CLOUD_JOB_ERROR);
			}
		} else if(jobType.equalsIgnoreCase("scale_cluster")) {
			LOG.info(threadName+": Scaling cluster in instance "+job.getInstanceId());
			try {
				if(scaleCluster(job) < 1) {
					LOG.error(threadName+": Got error, setting status to ERROR");
					job.setJobStatus(JobService.CLOUD_JOB_ERROR);
				} else {
					LOG.debug(threadName+": Success, setting status to READY");
					job.setJobStatus(JobService.CLOUD_JOB_READY);
				}
			} catch (Exception e) {
				LOG.error(threadName+": Error scaling cluster in instance "+job.getInstanceId());
				job.setJobStatus(JobService.CLOUD_JOB_ERROR);
			}
		} else if(jobType.equalsIgnoreCase("create_bigdata_service")) {
			LOG.info(threadName+": Creating new big data service for instance "+job.getInstanceId());
			try {
				if(createBigDataService(job, ClusterService.CLUSTER_TYPE_BIGDATA) < 1) {
					LOG.debug(threadName+" Got error, setting status to ERROR");
					job.setJobStatus(JobService.CLOUD_JOB_ERROR);
				} else {
					LOG.debug(threadName+": Success, setting status to READY");
					job.setJobStatus(JobService.CLOUD_JOB_READY);
				}
			} catch (Exception e) {
				LOG.error(threadName+" Error creating bigdata service to instance "+job.getInstanceId()+": "+e.getMessage());
				job.setJobStatus(JobService.CLOUD_JOB_ERROR);
			}
		} else if(jobType.equalsIgnoreCase("create_nosql_service")) {
			LOG.info(threadName+": Creating new big data service for instance "+job.getInstanceId());
			try {
				if(createBigDataService(job, ClusterService.CLUSTER_TYPE_NOSQL) < 1) {
					LOG.debug(threadName+" Got error, setting status to ERROR");
					job.setJobStatus(JobService.CLOUD_JOB_ERROR);
				} else {
					LOG.debug(threadName+": Success, setting status to READY");
					job.setJobStatus(JobService.CLOUD_JOB_READY);
				}
			} catch (Exception e) {
				LOG.error(threadName+" Error creating bigdata service to instance "+job.getInstanceId()+": "+e.getMessage());
				job.setJobStatus(JobService.CLOUD_JOB_ERROR);
			} 
		} else {
			LOG.error(threadName+": Unknown job type");
		}
		LOG.debug(threadName+" Updating status to database");
		jobService.updateStatus(job.getJobId(), job.getJobStatus());
		
		jobService.setEndTime(job.getJobId());
		LOG.info(threadName+": Processing ended job "+job.getJobId()+" in thread "+threadName);
		
	}
	
	private int deleteInstance(Job job) throws WorkerException {
		int returnValue = -1;
		String threadName = Thread.currentThread().getName();
		LOG.debug(threadName+": EC2Worker:deleteInstance starting");
		EC2Wrapper ec2 = new EC2Wrapper();
		String endPoint = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint");
		if(job.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			// TODO
		} else if(job.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(endPoint);
			ec2.init(eucaCredentials, job.getCloud());
		}
		Instance instance = instanceService.getInstance(job.getInstanceId());
		
		if(instance != null) {
			LOG.info(threadName+": Instance id: "+instance.getInstanceId());
			Collection<Cluster> clusters = clusterService.getClusters(instance.getInstanceId());
			if(clusters == null || clusters.size() == 0) {
				Key key = keyService.getKeyByInstanceId(instance.getInstanceId());
				if(key != null) {
					ec2.deleteKeypair(key.getName());
					keyService.deleteKey(instance.getInstanceId());
				}
				instanceService.deleteInstance(job.getInstanceId());
				LOG.info(threadName+": Instance "+instance.getInstanceId()+" deleted");
			} else {
				LOG.info(threadName+": Found "+clusters.size()+" clusters");
				LOG.debug(threadName+": Iterating clusters");
				Iterator<Cluster> i = clusters.iterator();
				
				while(i.hasNext()) {
					Cluster c = i.next();
					ElasticIP eIP = arService.getClustersElasticIP(c.getId());
					if (eIP != null && eIP.getIpAddress() != null) {
						try {
							ec2.removeElasticIPFromInstance(eIP.getIpAddress());
						} catch (Exception ex) {
							LOG.error("Error removing elastic IP "+eIP.getIpAddress()+" from cluster "+c.getId());
						}
						arService.freeElasticIP(eIP);
					}
					Collection<Machine> machines = machineService.getMachinesInCluster(c.getId());
					ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance> instanceL = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
					Iterator<Machine> j = machines.iterator();
					while(j.hasNext()) {
						Machine machine = j.next();
						com.amazonaws.services.elasticloadbalancing.model.Instance aInstance = new com.amazonaws.services.elasticloadbalancing.model.Instance();
						aInstance.setInstanceId(machine.getInstanceId());
						instanceL.add(aInstance);
					}
					try {
						ec2.deregisterInstancesToLoadBalancer(instanceL, c.getLbName());
					} catch (Exception ex) {
						LOG.warn(threadName+": Error with the loadbalancer in cluster "+c.getName()+", continuing with the delete");
					}
					j = machines.iterator();
					while(j.hasNext()) {
						Machine machine = j.next();
						String ebsVolumeId = machine.getEbsVolumeId();
						if(ebsVolumeId != null) {
							LOG.info(threadName+": We need to delete EBS volume too");
							ec2.detachVolume(ebsVolumeId);
							String volumeState = ec2.getVolumeState(ebsVolumeId);
							int maxWaitCount = 60;
							while(!volumeState.equalsIgnoreCase("available") && maxWaitCount > 0) {
								LOG.info(threadName+": Waiting the volume "+ebsVolumeId+" to be detached");
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									LOG.warn(threadName+": Someone just interrupted my sleep! "+e.getMessage());
								}
								maxWaitCount--;
								volumeState = ec2.getVolumeState(ebsVolumeId);
							}
							LOG.info(threadName+": Deleting volume "+ebsVolumeId);
							ec2.deleteVolume(machine.getEbsVolumeId());
						}
						ec2.terminateInstance(machine.getInstanceId());
						// usage
						try {
							usageService.stopVirtualMachineUsageMonitoring(instance.getOrganizationid(), c.getType(), c.getId(), machine.getId());
						} catch (Exception e) {
							LOG.error(threadName+": Error stopping usage monitoring "+e.getMessage());
						}
					}
					try {
						ec2.deleteLoadBalancer(c.getLbName(), c.getLbInstanceId());
						if(instance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
							// usage
							Machine m = machineService.getMachine(c.getLbInstanceId());
							try {
								usageService.stopVirtualMachineUsageMonitoring(instance.getOrganizationid(), c.getType(), c.getId(), m.getId());
							} catch (Exception e) {
								LOG.error(threadName+": Error stopping usage monitoring "+e.getMessage());
							}
						}
					} catch (Exception ex) {
						LOG.warn(threadName+": Error deleting loadbalacer, continuing with the delete");
					}
					boolean secGroupDeleted = false;
					int secGroupDeleteCounter = 0;
					while(!secGroupDeleted) {
						try {
							ec2.deleteSecurityGroup(c.getSecurityGroupName());
							secGroupDeleted = true;
						} catch (Exception e) {
							LOG.info(threadName+": Could not delete security group yet, trying again in a moment");
							secGroupDeleted = false;
							if(secGroupDeleteCounter >= 30) {
								throw new WorkerException("Could not delete security group");
							}
							try {
								Thread.sleep(10000);
							} catch (Exception ex) {
								LOG.error(threadName+": Someone is trying to stop my sleep!");
							}
							secGroupDeleteCounter++;
						}
					}
					if(c.getMulticastAddress() != null && c.getMulticastAddress().length() > 0) {
						maService.deleteMulticastAddress(c.getMulticastAddress());
					}
					
					clusterService.deleteCluster(c);
					LOG.info(threadName+": Cluster "+c.getName()+" deleted");
				}
				Key key = keyService.getKeyByInstanceId(job.getInstanceId());
				if(key != null) {
					ec2.deleteKeypair(key.getName());
					keyService.deleteKey(job.getInstanceId());
				}
				
				arService.deleteInstanceIPs(job.getInstanceId());
				instanceService.deleteInstance(job.getInstanceId());
				LOG.info(threadName+": Instance "+job.getInstanceId()+" deleted");
			}
		}
		returnValue = 1;
		
		return returnValue;
	}
	
	private int deleteCluster(Job job) throws WorkerException {
		int returnValue = -1;
		String threadName = Thread.currentThread().getName();
		LOG.debug(threadName + ": EC2Worker:deleteCluster starting");		
		try {
			EC2Wrapper ec2 = new EC2Wrapper(PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint"), job.getCloud(), eucaCredentials);
			doDeleteCluster(Integer.parseInt(job.getServices().split(",")[0]), ec2, threadName);
			returnValue = 1;
		} catch(InterruptedException ie){
			LOG.error(threadName+": Someone is trying to stop my sleep!");
		}
		return returnValue;
	}
	
	private void doDeleteCluster(int clusterId, EC2Wrapper ec2, String threadName) throws InterruptedException, WorkerException{
		Cluster c = clusterService.getCluster(clusterId);
		ElasticIP eIP = arService.getClustersElasticIP(c.getId());
		if (eIP != null && eIP.getIpAddress() != null) {
			ec2.removeElasticIPFromInstance(eIP.getIpAddress());
			arService.freeElasticIP(eIP);
		}
		deleteMachinesInCluster(c, threadName, ec2);
		boolean secGroupDeleted = false;
		int secGroupDeleteCounter = 0;
		while(!secGroupDeleted) {
			try {
				ec2.deleteSecurityGroup(c.getSecurityGroupName());
				secGroupDeleted = true;
			} catch (Exception e) {
				LOG.info(threadName+": Could not delete security group yet, trying again in a moment");
				secGroupDeleted = false;
				if(secGroupDeleteCounter >= 30) throw new WorkerException("Could not delete security group");					
				Thread.sleep(10000);
				secGroupDeleteCounter++;
			}
		}			
		clusterService.deleteCluster(c);
		LOG.info(threadName+": Cluster "+c.getName()+" deleted");
	}
	
	private void deleteMachinesInCluster(Cluster c, String threadName, EC2Wrapper ec2){
		Instance oiInstance = instanceService.getInstance(c.getInstanceId());
		Collection<Machine> machines = machineService.getMachinesInCluster(c.getId());
		ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance> instanceL = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
		Iterator<Machine> j = machines.iterator();
		while(j.hasNext()) {
			Machine machine = j.next();
			com.amazonaws.services.elasticloadbalancing.model.Instance aInstance = new com.amazonaws.services.elasticloadbalancing.model.Instance();
			aInstance.setInstanceId(machine.getInstanceId());
			instanceL.add(aInstance);
		}
		try {
			ec2.deregisterInstancesToLoadBalancer(instanceL, c.getLbName());
		} catch (Exception ex) {
			LOG.warn(threadName+": Error with the loadbalancer in cluster "+c.getName()+", continuing with the delete");
		}
		j = machines.iterator();
		while(j.hasNext()) {
			Machine machine = j.next();
			String ebsVolumeId = machine.getEbsVolumeId();
			if(ebsVolumeId != null) {
				LOG.info(threadName+": We need to delete EBS volume too");
				ec2.detachVolume(ebsVolumeId);
				String volumeState = ec2.getVolumeState(ebsVolumeId);
				int maxWaitCount = 60;
				while(!volumeState.equalsIgnoreCase("available") && maxWaitCount > 0) {
					LOG.info(threadName+": Waiting the volume "+ebsVolumeId+" to be detached");
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						LOG.warn(threadName+": Someone just interrupted my sleep! "+e.getMessage());
					}
					maxWaitCount--;
					volumeState = ec2.getVolumeState(ebsVolumeId);
				}
				LOG.info(threadName+": Deleting volume "+ebsVolumeId);
				ec2.deleteVolume(machine.getEbsVolumeId());
			}
			ec2.terminateInstance(machine.getInstanceId());
			// usage
			try {
				usageService.stopVirtualMachineUsageMonitoring(oiInstance.getOrganizationid(), c.getType(), c.getId(), machine.getId());
			}catch (Exception e) {
				LOG.error(threadName+": Error stopping usage monitoring "+e.getMessage());
			}
		}
		try {
			ec2.deleteLoadBalancer(c.getLbName(), c.getLbInstanceId());
			if(oiInstance.getCloudType() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
				// usage
				Machine m = machineService.getMachine(c.getLbInstanceId());
				try {
					usageService.stopVirtualMachineUsageMonitoring(oiInstance.getOrganizationid(), c.getType(), c.getId(), m.getId());
				} catch (Exception e) {
					LOG.error(threadName+": Error stopping usage monitoring "+e.getMessage());
				}
			}
		} catch (Exception ex) {
			LOG.warn(threadName+": Error deleting loadbalacer, continuing with the delete");
		}	
	}
	
	private int addServiceToInstance(Job job) throws WorkerException {
		int returnValue = -1;
		String threadName = Thread.currentThread().getName();
		
		LOG.debug(threadName+": EC2Worker:addServiceToInstance starting");
		Instance instance = instanceService.getInstance(job.getInstanceId());
		if(instance == null) {
			throw new WorkerException("Can't find instance "+job.getInstanceId()+" from database");
		}
		String endPoint = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint");
		EC2Wrapper ec2 = new EC2Wrapper();
		if(job.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			// TODO
		} else if(job.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(endPoint);
			ec2.init(eucaCredentials, job.getCloud());
		}
		Key key = keyService.getKeyByInstanceId(instance.getInstanceId());
		if(key == null) {
			throw new WorkerException("Can't find keypair for instance "+job.getInstanceId());
		}
		if(createServices(job, key, ec2)) {
			returnValue = 1;
		}
		
		return returnValue;
	}
	
	private int createNewInstance(Job job) throws WorkerException {
		int returnValue = -1;
		String threadName = Thread.currentThread().getName();
		
		LOG.debug(threadName+": EC2Worker:createNewInstance starting");
		String endPoint = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint");
		EC2Wrapper ec2 = new EC2Wrapper();
		if(job.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			// TODO
		} else if(job.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(endPoint);
			ec2.init(eucaCredentials, job.getCloud());
		}
		
		LOG.debug(threadName+": Creating keypair");
		String keyName = "TOASinstance" + Integer.toString(job.getInstanceId());

		Key key = null;
		try {
			KeyPair keyPair = ec2.createKeypair(keyName);
			key = new Key();
			key.setFingerprint(keyPair.getKeyFingerprint());
			key.setInstanceId(job.getInstanceId());
			key.setName(keyPair.getKeyName());
			key.setSecret_key(keyPair.getKeyMaterial());
			keyService.addKey(key);
		} catch (Exception e) {
			LOG.error("Error: "+e.getMessage());
		}

		if(createServices(job, key, ec2)) {
			returnValue = 1;
			instanceService.updateInstanceStatus(job.getInstanceId(), "Running");
		} else {
			instanceService.updateInstanceStatus(job.getInstanceId(), "Error");
		}
		return returnValue;
	}
	
	private int scaleCluster(Job job) throws WorkerException {
		String threadName = Thread.currentThread().getName();
		
		LOG.debug(threadName+": EC2Worker::scaleCluster starting");
		String endPoint = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint");
		EC2Wrapper ec2 = new EC2Wrapper();
		if(job.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			// TODO
		} else if(job.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(endPoint);
			ec2.init(eucaCredentials, job.getCloud());
		}
		Instance oiInstance = instanceService.getInstance(job.getInstanceId());
		Key key = keyService.getKeyByInstanceId(job.getInstanceId());
		String[] services = job.getServices().split(",");
		int clusterId = Integer.parseInt(services[0]);
		LOG.info(threadName+": Cluster number: "+clusterId);
		int machines = Integer.parseInt(services[1]);
		Cluster cluster = clusterService.getCluster(clusterId);
		String service = ClusterService.SERVICE_NAME[cluster.getType()];
		if(cluster.getNumberOfMachines() > machines) {
			int needToTerminate = cluster.getNumberOfMachines() - machines;
			LOG.info(threadName+": Scaling down cluster "+clusterId+", terminating "+needToTerminate+" machines");
			List<Machine> mList = (List<Machine>) machineService.getMachinesInCluster(clusterId);
			Machine lb = null;
			int j = 0;
			for(j = 0; j < mList.size(); j++) {
				Machine temp = mList.get(j);
				if(temp.getType().equalsIgnoreCase("loadbalancer")) {
					lb = temp;
					break;
				}
			}
			if(lb == null) {
				LOG.error(threadName+": Can't find loadbalancer for cluster "+clusterId);
				return -1;
			}
			mList.remove(j);
			int terminated = 0;
			int i = 0;
			while(terminated < needToTerminate) {
				Machine m = mList.get(i);
				machineService.stopMachine(m.getId());
				ec2.terminateInstance(m.getInstanceId());
				// usage service implementation
				try {
					usageService.stopVirtualMachineUsageMonitoring(oiInstance.getOrganizationid(), cluster.getType(), cluster.getId(), m.getId());
				} catch (Exception e) {
					LOG.error(threadName+": Error stopping usage monitoring "+e.getMessage());
				}
				terminated++;
				i++;
			}
			
			LOG.info(threadName+": terminated "+terminated+" machines");
			// force reconfigure for loadbalancer
			// machineService.updateMachineConfigure(lb.getId(), MachineService.MACHINE_CONFIGURE_NOT_STARTED);
		} else if(cluster.getNumberOfMachines() < machines) {
			LOG.info(threadName+": Scaling up cluster "+clusterId);
			if(cluster.getType() == ClusterService.CLUSTER_TYPE_BIGDATA || cluster.getType() == ClusterService.CLUSTER_TYPE_NOSQL) {
				if(upscaleBigDataService(job, cluster, machines) < 0) {
					return -1;
				}
			} else {
				List<Machine> mList = (List<Machine>) machineService.getMachinesInCluster(clusterId);
				Machine lb = null;
				int j = 0;
				for (j = 0; j < mList.size(); j++) {
					Machine temp = mList.get(j);
					if (temp.getType().equalsIgnoreCase("loadbalancer")) {
						lb = temp;
						continue;
					}
				}
				if (lb == null) {
					LOG.error(threadName + ": Can't find loadbalancer for cluster " + clusterId);
					return -1;
				}
				int needToStart = machines - cluster.getNumberOfMachines();
				LOG.info(threadName + ": starting " + needToStart + " new machines");
				List<String> securityGroups = new ArrayList<String>();
				securityGroups.add(cluster.getSecurityGroupName());
				String instanceType = PropertyManager.getProperty(PROPERTY_PREFIX + service + ".instancetype."+cluster.getMachineType());
				Reservation reservation = ec2.createInstance(imageId(job, service, cluster), needToStart, key.getName(), job.getZone(),
						instanceType, securityGroups);
				if (reservation == null) {
					throw new WorkerException("Error creating virtual machines for service " + service);
				}
				Iterator<com.amazonaws.services.ec2.model.Instance> ite = reservation.getInstances().iterator();
				while (ite.hasNext()) {
					com.amazonaws.services.ec2.model.Instance tempInstance = ite.next();
					Machine machine = new Machine();
					machine.setName(cluster.getName());
					machine.setCloud(job.getCloud());
					machine.setType("clustermember");

					machine.setInstanceId(tempInstance.getInstanceId());

					int maxWait = 20;
					while ((tempInstance.getPrivateDnsName().equals("0.0.0.0") || tempInstance.getPrivateDnsName().startsWith("euca-0-0-0-0")) && maxWait > 0) {
						LOG.info(threadName + ": Cloud not get IP address yet, waiting for a moment");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							LOG.error(threadName + ": Something interrupted my sleep: " + e.getMessage());
						}
						maxWait--;
						tempInstance = ec2.describeInstance(tempInstance.getInstanceId());
					}
					LOG.info(threadName + ": Private dns name: " + tempInstance.getPrivateDnsName());
					LOG.info(threadName + ": Private IP address: " + tempInstance.getPrivateIpAddress());
					AuthorizationRoute ip = new AuthorizationRoute();
					if (tempInstance.getPrivateIpAddress() == null) {
						ip.setCidrIp(tempInstance.getPrivateDnsName() + "/32");
					} else {
						ip.setCidrIp(tempInstance.getPrivateIpAddress() + "/32");
					}
					ip.setClusterId(cluster.getId());
					ip.setInstanceId(cluster.getInstanceId());
					ip.setFromPort(0);
					ip.setToPort(65535);
					ip.setProtocol("tcp");
					ip.setSecurityGroupName(cluster.getSecurityGroupName());
					arService.addIP(ip);
					machine.setDnsName(tempInstance.getPublicDnsName());
					machine.setPrivateDnsName(tempInstance.getPrivateDnsName());
					machine.setState(tempInstance.getState().getName());
					machine.setClusterId(cluster.getId());
					machine.setUserName("root");
					machine.setConfigured(0);
					machine.setRunning(1);
					machineService.addMachine(machine);
					
					// Usage Service implementation
					try {
						usageService.startVirtualMachineUsageMonitoring(oiInstance.getOrganizationid(), cluster.getType(), cluster.getId(), machine.getId());
					} catch (Exception e) {
						LOG.error(threadName+": Error starting usage monitoring");
					}
					
				}
				//machineService.updateMachineConfigure(lb.getId(), MachineService.MACHINE_CONFIGURE_NOT_STARTED);	
			}
		}
		cluster.setNumberOfMachines(machines);
		clusterService.updateCluster(cluster);
		// Once creation is done force update on all machines. Needed for nodelist.conf from oi-healthmonitoring
		List<Machine> updatedMachineList = (List<Machine>) machineService.getMachinesInCluster(clusterId);
		for (Machine m: updatedMachineList) machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_NOT_STARTED);
		
		
	/*	Collection<Cluster> clusterList = clusterService.getClusters(job.getInstanceId());
		Iterator<Cluster> k = clusterList.iterator();
		while(k.hasNext()) {
			Cluster c = k.next();
			Collection<AuthorizationRoute> ipList = arService.getInstanceIPs(c.getInstanceId());
			Iterator<AuthorizationRoute> i = ipList.iterator();
			while(i.hasNext()) {
				AuthorizationRoute ip = i.next();
				ec2.authorizeIPs(c.getSecurityGroupName(), ip.getCidrIp(), ip.getFromPort(), ip.getToPort(), ip.getProtocol());
			}
		} */
		
		return 1;
	}
	
	private int upscaleBigDataService(Job job, Cluster cluster, int numberOfMachines) throws WorkerException {
		String threadName = Thread.currentThread().getName();
		LOG.info(threadName+": upscaleBigDataService starting");
		Instance instance = instanceService.getInstance(cluster.getInstanceId());
		String endPoint = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint");
		EC2Wrapper ec2 = new EC2Wrapper();
		if(job.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			// TODO
		} else if(job.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(endPoint);
			ec2.init(eucaCredentials, job.getCloud());
		}
		
		Key key = keyService.getKeyByInstanceId(instance.getInstanceId());
		Machine managementMachine = machineService.getClusterManagementMachine(cluster.getId());
		String command = "/opt/bigdata/bin/ask-roles.py --xml "+numberOfMachines;
		String output = sshRunCommand(managementMachine, command, key);
		
		if(output == null) {
			return -1;
		}
		List<String> roles = null;
		try {
			roles = XmlParse.getValues(output, "role");
		} catch (Exception e) {
			LOG.error("Error parsing XML response");
			return -1;
		}
		int small = 0;
		int large = 0;
		for(int i = cluster.getNumberOfMachines(); i < numberOfMachines;i++) {
			String role = roles.get(i-1);
			
			if(role.equalsIgnoreCase("zookeeper") || role.equalsIgnoreCase("config")) {
				small++;
			} else if(role.equalsIgnoreCase("shard") || role.equalsIgnoreCase("slave")) {
				large++;
			}
		}
		List<String> securityGroups = new ArrayList<String>();
		securityGroups.add(cluster.getSecurityGroupName());
		String service = ClusterService.SERVICE_NAME[cluster.getType()];
		List<com.amazonaws.services.ec2.model.Instance> instances = new ArrayList<com.amazonaws.services.ec2.model.Instance>();
		Reservation reservation = null; 
		if (small > 0) {
			reservation = ec2.createInstance(imageId(job, service, cluster), small, key.getName(), job.getZone(), "m1.small", securityGroups);
			if (reservation == null) {
				throw new WorkerException("Error creating virtual machines for service " + service);
			}
			instances.addAll(reservation.getInstances());
		}
		if (large > 0) {
			reservation = ec2.createInstance(imageId(job, service, cluster), large, key.getName(), job.getZone(), "m1.large", securityGroups);
			if (reservation == null) {
				throw new WorkerException("Error creating virtual machines for service " + service);
			}
			instances.addAll(reservation.getInstances());
		}
		Iterator<com.amazonaws.services.ec2.model.Instance> ite = instances.iterator();
		int y=cluster.getNumberOfMachines()+1;
		int type = cluster.getType();
		while (ite.hasNext()) {
			com.amazonaws.services.ec2.model.Instance tempInstance = ite.next();
			Machine machineTmp = new Machine();
			if(type == ClusterService.CLUSTER_TYPE_BIGDATA) {
				machineTmp.setName("hbase"+y);
			} else {
				machineTmp.setName("mongo"+y);
			}
			
			machineTmp.setCloud(job.getCloud());

			machineTmp.setInstanceId(tempInstance.getInstanceId());
			if(tempInstance.getInstanceType().equalsIgnoreCase("m1.small")) {
				if(type == ClusterService.CLUSTER_TYPE_BIGDATA) {
					machineTmp.setType("zookeeper");
				} else {
					machineTmp.setType("config");
				}
			} else if(tempInstance.getInstanceType().equalsIgnoreCase("m1.large")) {
				if(type == ClusterService.CLUSTER_TYPE_BIGDATA) {
					machineTmp.setType("slave");
				} else {
					machineTmp.setType("shard");
				}
			} 
			int maxWait = 20;
			while (tempInstance.getPrivateDnsName().equals("0.0.0.0") && maxWait > 0) {
				LOG.info(threadName + ": Cloud not get IP address yet, waiting for a moment");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOG.error(threadName + ": Something interrupted my sleep: " + e.getMessage());
				}
				maxWait--;
				tempInstance = ec2.describeInstance(tempInstance.getInstanceId());
			}
			LOG.info(threadName + ": Private dns name: " + tempInstance.getPrivateDnsName());
			LOG.info(threadName + ": Private IP address: " + tempInstance.getPrivateIpAddress());
		/*	AuthorizationRoute ip = new AuthorizationRoute();
			if (tempInstance.getPrivateIpAddress() == null) {
				ip.setCidrIp(tempInstance.getPrivateDnsName() + "/32");
			} else {
				ip.setCidrIp(tempInstance.getPrivateIpAddress() + "/32");
			}
			ip.setClusterId(cluster.getId());
			ip.setInstanceId(cluster.getInstanceId());
			ip.setFromPort(0);
			ip.setToPort(65535);
			ip.setProtocol("tcp");
			ip.setSecurityGroupName(cluster.getSecurityGroupName());
			arService.addIP(ip); */
			machineTmp.setDnsName(tempInstance.getPublicDnsName());
			machineTmp.setPrivateDnsName(tempInstance.getPrivateDnsName());
			machineTmp.setState(tempInstance.getState().getName());
			machineTmp.setClusterId(cluster.getId());
			machineTmp.setUserName("root");
			machineTmp.setConfigured(MachineService.DATA_MACHINE_CONFIGURE_NOT_STARTED);
			machineTmp.setRunning(1);
			machineService.addMachine(machineTmp);
			y++;
		}
		
		Collection<Cluster> clusterList = clusterService.getClusters(job.getInstanceId());
		Iterator<Cluster> k = clusterList.iterator();
		while(k.hasNext()) {
			Cluster c = k.next();
			Collection<AuthorizationRoute> ipList = arService.getInstanceIPs(c.getInstanceId());
			Iterator<AuthorizationRoute> i = ipList.iterator();
			while(i.hasNext()) {
				AuthorizationRoute ip = i.next();
				ec2.authorizeIPs(c.getSecurityGroupName(), ip.getCidrIp(), ip.getFromPort(), ip.getToPort(), ip.getProtocol());
			}
		}
		
		return 1;
	}
	
	private int createBigDataService(Job job, int type) throws WorkerException {
		String threadName = Thread.currentThread().getName();
		String service = ClusterService.SERVICE_NAME[type];
		
		LOG.debug(threadName+": EC2Worker::createBigDataService starting");
		String endPoint = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint");
		EC2Wrapper ec2 = new EC2Wrapper();
		if(job.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			// TODO
		} else if(job.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			ec2.setEndpoint(endPoint);
			ec2.init(eucaCredentials, job.getCloud());
		}
		Key key = keyService.getKeyByInstanceId(job.getInstanceId());
		String serviceName = PropertyManager.getProperty(PROPERTY_PREFIX+service+".name");
		Cluster cluster = new Cluster();
		cluster.setName(serviceName);
		cluster.setInstanceId(job.getInstanceId());
		cluster.setType(type);
		cluster.setNumberOfMachines(Integer.parseInt(job.getServices()));
		cluster.setPublished(ClusterService.CLUSTER_STATUS_UNPUBLISHED);
		clusterService.addCluster(cluster);
		String securityGroupName = "C"+Integer.toString(cluster.getId());
		String securityGroupId = ec2.createSecurityGroup(securityGroupName, "Cluster "+Integer.toString(cluster.getId())+" security group");
		cluster.setSecurityGroupId(securityGroupId);
		cluster.setSecurityGroupName(securityGroupName);
		
		clusterService.updateCluster(cluster);
	//	ec2.authorizeIPs(cluster.getSecurityGroupName(), "0.0.0.0/0", 22, 22, "tcp");
		String adminPortalAddress = PropertyManager.getProperty("cloudadmin.worker.adminportal.address");
		ec2.authorizeIPs(cluster.getSecurityGroupName(), adminPortalAddress+"/32", 22, 22, "tcp");
		ec2.authorizeIPs(cluster.getSecurityGroupName(), adminPortalAddress+"/32", 8181, 8181, "tcp");
		List<String> securityGroups = new ArrayList<String>();
		securityGroups.add(cluster.getSecurityGroupName());
		
		String imageId = imageId(job, service, cluster);
		Reservation reservation = ec2.createInstance(imageId, 1, key.getName(), job.getZone(), "m1.small", securityGroups);
		if(reservation == null) {
			ec2.deleteSecurityGroup(cluster.getSecurityGroupName());
			throw new WorkerException("Error creating virtual machines for service "+service);
		}
		com.amazonaws.services.ec2.model.Instance machineInstance = reservation.getInstances().get(0);
		Machine machine = new Machine();
		machine.setName(cluster.getName());
		machine.setCloud(job.getCloud());
		machine.setInstanceId(machineInstance.getInstanceId());
		machine.setType("manager");
		machine.setConfigured(MachineService.MACHINE_CONFIGURE_STARTED);
		int maxWait = 60;
		while((machineInstance.getPrivateDnsName().equals("0.0.0.0") || machineInstance.getPrivateDnsName().equals("euca-0-0-0-0")) && maxWait > 0) {
			LOG.info(threadName+": Could not get IP address yet, waiting for a moment");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				LOG.error(threadName+": Something interrupted my sleep: "+e.getMessage());
			}
			maxWait--;
			machineInstance = ec2.describeInstance(machineInstance.getInstanceId());
		}
		LOG.debug(threadName+": Private dns name: "+machineInstance.getPrivateDnsName());
		LOG.debug(threadName+": Private IP address: "+machineInstance.getPrivateIpAddress());
		machine.setDnsName(machineInstance.getPublicDnsName());
		machine.setPrivateDnsName(machineInstance.getPrivateDnsName());
		machine.setState(machineInstance.getState().getName());
		machine.setClusterId(cluster.getId());
		machine.setUserName("root");
		machine.setRunning(1);
		machineService.addMachine(machine);
		String command = "/usr/bin/puppet agent --test --no-daemonize --onetime --certname ";
		command += job.getInstanceId()+"_";
		command += cluster.getId()+"_";
		command += machine.getId()+"_";
		command += ClusterService.CLUSTER_TYPE_MACHINE_NAME[cluster.getType()];
		String output = null;
		
		output = sshRunCommand(machine, command, key);
		if(output == null) {
			machineService.updateMachineConfigure(machine.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return -1;
		}
		if(type == ClusterService.CLUSTER_TYPE_BIGDATA) {
			command = "/opt/bigdata/bin/initialize.py hbase";
		} else {
			command = "/opt/bigdata/bin/initialize.py mongodb";
		}
		output = sshRunCommand(machine, command, key);
		if(output == null) {
			machineService.updateMachineConfigure(machine.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return -1;
		}
		machine.setConfigured(MachineService.MACHINE_CONFIGURE_READY);
		machineService.updateMachineConfigure(machine.getId(), MachineService.MACHINE_CONFIGURE_READY);
	
		
		// Lets get the roles for whole cluster
		LOG.info(threadName+": Lets get the roles for the cluster");
		command = "/opt/bigdata/bin/ask-roles.py --xml "+cluster.getNumberOfMachines();
		output = sshRunCommand(machine, command, key);
		if(output == null) {
			machineService.updateMachineConfigure(machine.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return -1;
		}
		List<String> roles = null;
		try {
			roles = XmlParse.getValues(output, "role");
		} catch (Exception e) {
			LOG.error("Error parsing XML response");
			machineService.updateMachineConfigure(machine.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return -1;
		}
		int small = 0;
		int large = 0;
		int xlarge = 0;
		if(type == ClusterService.CLUSTER_TYPE_BIGDATA) {
			Iterator<String> i = roles.iterator();
			while(i.hasNext()) {
				String role = i.next();
				if(role.equals("zookeeper")) {
					small++;
				} else if(role.equals("hmaster")) {
					xlarge++;
				} else {
					large++;
				}
			}
			
		} else {
			Iterator<String> i = roles.iterator();
			while(i.hasNext()) {
				String role = i.next();
				if(role.equals("config")) {
					small++;
				} else if(role.equals("shard")) {
					large++;
				}
			}
		}
		reservation = ec2.createInstance(imageId, small, key.getName(), job.getZone(), "m1.small", securityGroups);
		if(reservation == null) {
			throw new WorkerException("Error creating virtual machines for service "+service);
		}
		Reservation largeReservation = ec2.createInstance(imageId, large, key.getName(), job.getZone(), "m1.large", securityGroups);
		if(largeReservation == null) {
			throw new WorkerException("Error creating virtual machines for service "+service);
		}
		Reservation xlargeReservation = null;
		if (xlarge > 0) {
			xlargeReservation = ec2.createInstance(imageId, xlarge, key.getName(), job.getZone(), "m1.xlarge",
					securityGroups);
			if (xlargeReservation == null) {
				throw new WorkerException("Error creating virtual machines for service " + service);
			}
		}
		List<com.amazonaws.services.ec2.model.Instance> instances = new ArrayList<com.amazonaws.services.ec2.model.Instance>();
		instances.addAll(reservation.getInstances());
		instances.addAll(largeReservation.getInstances());
		if (xlarge > 0) {
			instances.addAll(xlargeReservation.getInstances());
		}

		Iterator<com.amazonaws.services.ec2.model.Instance> ite = instances.iterator();
	//	Collection<String> machinesToTag = new ArrayList<String>();
	//	Collection<com.amazonaws.services.elasticloadbalancing.model.Instance> lbInstanceList = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
		int y=1;
		
		while (ite.hasNext()) {
			com.amazonaws.services.ec2.model.Instance tempInstance = ite.next();
			Machine machineTmp = new Machine();
			if(type == ClusterService.CLUSTER_TYPE_BIGDATA) {
				machineTmp.setName("hbase"+y);
			} else {
				machineTmp.setName("mongo"+y);
			}
			
			machineTmp.setCloud(job.getCloud());

			machineTmp.setInstanceId(tempInstance.getInstanceId());
			if(tempInstance.getInstanceType().equalsIgnoreCase("m1.small")) {
				if(type == ClusterService.CLUSTER_TYPE_BIGDATA) {
					machineTmp.setType("zookeeper");
				} else {
					machineTmp.setType("config");
				}
			} else if(tempInstance.getInstanceType().equalsIgnoreCase("m1.large")) {
				if(type == ClusterService.CLUSTER_TYPE_BIGDATA) {
					machineTmp.setType("slave");
				} else {
					machineTmp.setType("shard");
				}
			} else if(tempInstance.getInstanceType().equalsIgnoreCase("m1.xlarge")) {
				machineTmp.setType("hmaster");
			}
			maxWait = 60;
			
			while ((tempInstance.getPrivateDnsName().equals("0.0.0.0") || tempInstance.getPrivateDnsName().equals("euca-0-0-0-0")) && maxWait > 0) {
				LOG.info(threadName + ": Cloud not get IP address yet, waiting for a moment");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					LOG.error(threadName + ": Something interrupted my sleep: " + e.getMessage());
				}
				maxWait--;
				tempInstance = ec2.describeInstance(tempInstance.getInstanceId());
			}
			LOG.info(threadName + ": Private dns name: " + tempInstance.getPrivateDnsName());
			LOG.info(threadName + ": Private IP address: " + tempInstance.getPrivateIpAddress());
		/*	AuthorizationRoute ip = new AuthorizationRoute();
			if (tempInstance.getPrivateIpAddress() == null) {
				ip.setCidrIp(tempInstance.getPrivateDnsName() + "/32");
			} else {
				ip.setCidrIp(tempInstance.getPrivateIpAddress() + "/32");
			}
			ip.setClusterId(cluster.getId());
			ip.setInstanceId(cluster.getInstanceId());
			ip.setFromPort(0);
			ip.setToPort(65535);
			ip.setProtocol("tcp");
			ip.setSecurityGroupName(cluster.getSecurityGroupName());
			arService.addIP(ip); */
			machineTmp.setDnsName(tempInstance.getPublicDnsName());
			machineTmp.setPrivateDnsName(tempInstance.getPrivateDnsName());
			machineTmp.setState(tempInstance.getState().getName());
			machineTmp.setClusterId(cluster.getId());
			machineTmp.setUserName("root");
			machineTmp.setConfigured(MachineService.DATA_MACHINE_CONFIGURE_NOT_STARTED);
			machineTmp.setRunning(1);
			machineService.addMachine(machineTmp);
			y++;
		}
		
		Collection<Cluster> clusterList = clusterService.getClusters(job.getInstanceId());
		Iterator<Cluster> k = clusterList.iterator();
		while(k.hasNext()) {
			Cluster c = k.next();
			Collection<AuthorizationRoute> ipList = arService.getInstanceIPs(c.getInstanceId());
			Iterator<AuthorizationRoute> i = ipList.iterator();
			while(i.hasNext()) {
				AuthorizationRoute ip = i.next();
				ec2.authorizeIPs(c.getSecurityGroupName(), ip.getCidrIp(), ip.getFromPort(), ip.getToPort(), ip.getProtocol());
			}
		}
		
		return 1;
	}
	
	private String sshRunCommand(Machine m, String command, Key key) {
		String retVal = null;
		String threadName = Thread.currentThread().getName();
		
		boolean connectOK = false;
		int x = 30;
		while(!connectOK) {
			x--;
			Socket s = null;
			try {
				s = new Socket();
				s.setReuseAddress(true);
				LOG.debug(threadName+": Trying to connect address: "+m.getDnsName());
				SocketAddress sa = new InetSocketAddress(m.getDnsName(), 22);
				s.connect(sa, 3000);
				connectOK = true;
				LOG.info(threadName+": Connected OK to the host "+m.getDnsName());
			} catch(IOException e) {
				if(x == 0) {
					return retVal;
				}
				LOG.info(threadName+": Got IO exception connecting to ssh port, trying for "+x+" more times...");
				LOG.info(threadName+": Updating machine info");
				m = machineService.getMachine(m.getId());
				LOG.info("Updated the machine info");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					LOG.error(threadName+": Someone interrupted my sleep! How rude! "+e.getMessage());
				}
			} finally {
				if(s != null) {
					try {
						s.close();
					} catch (IOException e) {
						LOG.error(threadName+": Error closing socket");
					}
				}
			}
		}
		
		byte[] privkey = key.getSecret_key().getBytes();
		final byte[] emptyPassPhrase = new byte[0];
		JSch jsch = new JSch();
		try {
			jsch.addIdentity("root", privkey, null, emptyPassPhrase);
			Session session = jsch.getSession("root", m.getDnsName(), 22);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			LOG.info(threadName+": Connecting with ssh to "+m.getDnsName());
			
			session.connect();
			
			LOG.info(threadName+": Running command: '"+command+"'");
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			InputStream in = channel.getInputStream();
			channel.connect();
			StringBuffer buffer = new StringBuffer();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					String tempString = new String(tmp, 0, i);
					LOG.debug(threadName+": "+tempString);
					buffer.append(tempString);
				}
				if (channel.isClosed()) {
					int exitStatus = channel.getExitStatus();
					if(exitStatus == 0 && buffer.length() == 0) {
						buffer.append("ok");
					}
					LOG.info(threadName+": exit-status: "
							+ exitStatus);
					
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			if(buffer.length() > 0) {
				retVal = buffer.toString();
			}
			channel.disconnect();
			session.disconnect();
			
		} catch (Exception e) {
			LOG.error(threadName+": Error running command to machine "+m.getDnsName()+": "+e.getMessage());
		}
		
		return retVal;
	} 
	
	private boolean createServices(Job job, Key key, EC2Wrapper ec2) throws WorkerException {
		boolean returnValue = false; 
		String threadName = Thread.currentThread().getName();
		
		String[] services = job.getServices().split(",");
		if(services == null || services.length < 1) {
			return returnValue;
		}
		LOG.info(threadName+": Starting the service create");
	//	List<Cluster> clusterList = new ArrayList<Cluster>();
		for(int i = 0; i < services.length; i = i+5) {
			LOG.debug(threadName+": Creating service "+services[i]);
	//		String lbAddresses = "";
			String service = services[i];
			if(service == null || service.length() < 1) {
				throw new WorkerException("NULL service in service list");
			}
			int serviceCount = 0;
			try {
				serviceCount = Integer.parseInt(services[i+1]);
			} catch (NumberFormatException e) {
				throw new WorkerException("Error parsing service count in services for service "+service+": "+e.getMessage());
			}
			int machineType = 0;
			try {
				machineType = Integer.parseInt(services[i+2]);
			} catch (NumberFormatException e) {
				throw new WorkerException("Error parsing machine type in services for service "+service+": "+e.getMessage());
			}
			int ebsImageUsed = 0;
			try {
				ebsImageUsed = Integer.parseInt(services[i+3]);
			} catch (NumberFormatException e ) {
				throw new WorkerException("Error parsing ebs image use in services for service "+service+": "+e.getMessage());
			}
			int ebsDiscSize = 0;
			String tmp = services[i+4];
			if(tmp != null && (!tmp.equals("null"))) {
				try {
					ebsDiscSize = Integer.parseInt(tmp);
				} catch (NumberFormatException e) {
					throw new WorkerException("Error parsing ebs disc size in services for service "+service+": "+e.getMessage());
				}
			}
			String serviceAvailable = PropertyManager.getProperty(PROPERTY_PREFIX+service+".available");
			if(serviceAvailable == null || !serviceAvailable.equalsIgnoreCase("yes")) {
				throw new WorkerException("Service "+service+" is not available");
			}
			if(service.equals("bigdata_platform") || service.equals("nosql_platform")) {
				Job newJob = new Job();
				newJob.setJobStatus(1);
				if(service.equals("bigdata_platform")) {
					newJob.setJobType("create_bigdata_service");
				} else {
					newJob.setJobType("create_nosql_service");
				}
				newJob.setExtraData(job.getExtraData());
				newJob.setInstanceId(job.getInstanceId());
				newJob.setServices(Integer.toString(serviceCount));
				newJob.setCloud(job.getCloud());
				newJob.setZone(job.getZone());
				jobService.addJob(newJob);
				continue;
			} 
			String serviceName = PropertyManager.getProperty(PROPERTY_PREFIX+service+".name");
			if(serviceName == null || serviceName.length() < 1) {
				throw new WorkerException("Service "+service+" has no name defined in properties");
			}

			String clusterType = PropertyManager.getProperty(PROPERTY_PREFIX+service+".clustertype");
			int type = Integer.parseInt(clusterType);
			String instanceType = null;
			if (type != ClusterService.CLUSTER_TYPE_BIGDATA && type != ClusterService.CLUSTER_TYPE_NOSQL) {
				instanceType = PropertyManager.getProperty(PROPERTY_PREFIX + service + ".instancetype."+Integer.toString(machineType));
				if (instanceType == null || instanceType.length() < 3) {
					throw new WorkerException("Can't find instance type for service " + service);
				}
			}
			Cluster cluster = new Cluster();
			cluster.setName(serviceName);
			cluster.setInstanceId(job.getInstanceId());
			cluster.setMachineType(machineType);
			String needsLoadBalancer = PropertyManager.getProperty(PROPERTY_PREFIX+service+".loadbalancer");
			if(needsLoadBalancer != null && needsLoadBalancer.equalsIgnoreCase("yes")) {
				cluster.setLbName(PropertyManager.getProperty(PROPERTY_PREFIX+service+".loadbalancer.name", "lb"));
			}
			String needsHazel = PropertyManager.getProperty(PROPERTY_PREFIX+service+".hazelcast");
			if(needsHazel != null && needsHazel.equalsIgnoreCase("yes")) {
				String address = getFreeMulticastAddress();
				cluster.setMulticastAddress(address);
				
			}
			cluster.setType(type);
			cluster.setNumberOfMachines(serviceCount);
			cluster.setPublished(ClusterService.CLUSTER_STATUS_UNPUBLISHED);
			cluster.setLive(0);
			cluster.setEbsImageUsed(ebsImageUsed);
			if(ebsDiscSize > 0) {
				cluster.setEbsVolumesUsed(ebsDiscSize);
			} else {
				cluster.setEbsVolumesUsed(0);
			}
			clusterService.addCluster(cluster);
			if (needsHazel != null && needsHazel.equalsIgnoreCase("yes")) {
				MulticastAddress addr = new MulticastAddress();
				addr.setAddress(cluster.getMulticastAddress());
				addr.setClusterId(cluster.getId());
				addr.setInstanceId(job.getInstanceId());
				maService.addAddress(addr);
			}
			
			if(createCluster(cluster, service, ec2, job.getZone(), job.getCloud(), key.getName(), key.getSecret_key(), imageId (job, service, cluster), instanceType, needsLoadBalancer) == true) {
				cluster.setLive(1);
				clusterService.updateCluster(cluster);
				//clusterList.add(cluster);
			}
			
		}
		String securityGroupOwner = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.securitygroup.owner");
		Collection<Cluster> clusterList = clusterService.getClusters(job.getInstanceId());
		Iterator<Cluster> k = clusterList.iterator();
		while(k.hasNext()) {
			Cluster c = k.next();
		/*	Collection<AuthorizationRoute> ipList = arService.getInstanceIPs(c.getInstanceId());
			Iterator<AuthorizationRoute> i = ipList.iterator();
			while(i.hasNext()) {
				AuthorizationRoute ip = i.next();
				ec2.authorizeIPs(c.getSecurityGroupName(), ip.getCidrIp(), ip.getFromPort(), ip.getToPort(), ip.getProtocol());
			} */
			Collection<String> groupList = arService.getAllSecurityGroupsInInstance(c.getInstanceId());
			Iterator<String> i = groupList.iterator();
			while(i.hasNext()) {
				String group = i.next();
				if(!group.equals(c.getSecurityGroupName())) {
					ec2.authorizeGroup(c.getSecurityGroupName(), group, securityGroupOwner, 0, 65535, "tcp");
					ec2.authorizeGroup(c.getSecurityGroupName(), group, securityGroupOwner, 0, 65535, "udp");
				}
			}
		}
		return true;
	}
	
	private boolean createCluster(Cluster cluster, String service, EC2Wrapper ec2, String zone, int cloud, String key, String secretKey, String image, String instanceType, String needsLoadBalancer) throws WorkerException {
		String lbAddresses = "";
		boolean hasLoadBalancer = false;
		String threadName = Thread.currentThread().getName();
		
		String securityGroupName = "C"+Integer.toString(cluster.getId());
		String securityGroupId = ec2.createSecurityGroup(securityGroupName, "Cluster "+Integer.toString(cluster.getId())+" security group");
		cluster.setSecurityGroupId(securityGroupId);
		cluster.setSecurityGroupName(securityGroupName);
		clusterService.updateCluster(cluster);
		String adminPortalAddress = PropertyManager.getProperty("cloudadmin.worker.adminportal.address");
	//	ec2.authorizeIPs(cluster.getSecurityGroupName(), "0.0.0.0/0", 22, 22, "tcp");
		ec2.authorizeIPs(cluster.getSecurityGroupName(), adminPortalAddress+"/32", 22, 22, "tcp");
		ec2.authorizeIPs(cluster.getSecurityGroupName(), adminPortalAddress+"/32", 8181, 8181, "tcp");
		List<String> securityGroups = new ArrayList<String>();
		securityGroups.add(cluster.getSecurityGroupName());
		List<com.amazonaws.services.ec2.model.Instance> instances = new ArrayList<com.amazonaws.services.ec2.model.Instance>();
		if(needsLoadBalancer != null && needsLoadBalancer.equalsIgnoreCase("yes")) {
			if (cloud == InstanceService.CLOUD_TYPE_AMAZON) {
				String lbDns = ec2.createLoadBalancer(cluster.getLbName(), zone, key);
				if (lbDns == null) {
					throw new WorkerException("Error creating load balancer for service " + service);
				}
				cluster.setLbDns(lbDns);
				ec2.setAppCookieStickiness("JSESSIONID", "JavaSessionPolicy", cluster.getLbName());
				ec2.setLoadBalancerPoliciesOfListener("JavaSessionPolicy", cluster.getLbName(), 80);
			} else {
				//cluster.setNumberOfMachines(cluster.getNumberOfMachines()+1);
				
				
				Reservation reservation = ec2.createInstance(imageId(cloud, service, 0), 1, key, zone, "m1.small", securityGroups);
				if(reservation == null) {
					ec2.deleteSecurityGroup(cluster.getSecurityGroupName());
					throw new WorkerException("Error creating virtual machines for service "+service);
				}
				instances.addAll(reservation.getInstances());
			}
			clusterService.updateCluster(cluster);
			hasLoadBalancer = true;
		}
		
		Reservation reservation = ec2.createInstance(image, cluster.getNumberOfMachines(), key, zone, instanceType, securityGroups);
		if(reservation == null) {
			ec2.deleteLoadBalancer(cluster.getLbName(), cluster.getLbInstanceId());
			ec2.deleteSecurityGroup(cluster.getSecurityGroupName());
			throw new WorkerException("Error creating virtual machines for service "+service);
		}
		instances.addAll(reservation.getInstances());
		Iterator<com.amazonaws.services.ec2.model.Instance> ite = instances.iterator();
		Collection<String> machinesToTag = new ArrayList<String>();
	//	Collection<com.amazonaws.services.elasticloadbalancing.model.Instance> lbInstanceList = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
		boolean loadBalancerMarked = false;
		while(ite.hasNext()) {
			
			com.amazonaws.services.ec2.model.Instance tempInstance = ite.next();
			Machine machine = new Machine();
			machine.setName(cluster.getName());
			machine.setCloud(cloud);
			
			machine.setInstanceId(tempInstance.getInstanceId());
			LOG.debug(threadName+": Instance "+tempInstance.getInstanceId()+" root device type: "+tempInstance.getRootDeviceType());
			if(tempInstance.getInstanceType().equalsIgnoreCase("m1.small") && !loadBalancerMarked && hasLoadBalancer && tempInstance.getRootDeviceType().equalsIgnoreCase("instance-store")) {
				machine.setType("loadbalancer");
				loadBalancerMarked = true;
				cluster.setLbInstanceId(machine.getInstanceId());
				clusterService.updateCluster(cluster);
			} else {
				machine.setType("clustermember");
				
				if(cluster.getEbsVolumesUsed() > 0) {
					String volumeId = ec2.createVolume(cluster.getEbsVolumesUsed(), zone);
					machine.setEbsVolumeId(volumeId);
					machine.setEbsVolumeSize(cluster.getEbsVolumesUsed());
				}
			}
			int maxWait = 120;
			while((tempInstance.getPrivateDnsName().equals("0.0.0.0") || tempInstance.getPrivateDnsName().startsWith("euca-0-0-0-0")) && maxWait > 0) {
				LOG.info(threadName+": Could not get IP address yet, waiting for a moment");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					LOG.error(threadName+": Something interrupted my sleep: "+e.getMessage());
				}
				maxWait--;
				tempInstance = ec2.describeInstance(tempInstance.getInstanceId());
			}
			LOG.info(threadName+": Private dns name: "+tempInstance.getPrivateDnsName());
			LOG.info(threadName+": Private IP address: "+tempInstance.getPrivateIpAddress());
		/*	AuthorizationRoute ip = new AuthorizationRoute();
			if(tempInstance.getPrivateIpAddress() == null) {
				ip.setCidrIp(tempInstance.getPrivateDnsName()+"/32");
			} else {
				ip.setCidrIp(tempInstance.getPrivateIpAddress()+"/32");
			}
			ip.setClusterId(cluster.getId());
			ip.setInstanceId(cluster.getInstanceId());
			ip.setFromPort(0);
			ip.setToPort(65535);
			ip.setProtocol("tcp");
			ip.setSecurityGroupName(cluster.getSecurityGroupName());
			arService.addIP(ip); */
			machine.setDnsName(tempInstance.getPublicDnsName());
			machine.setPrivateDnsName(tempInstance.getPrivateDnsName());
			machine.setState(tempInstance.getState().getName());
			machine.setClusterId(cluster.getId());
			machine.setUserName("root");
			machine.setConfigured(0);
			machine.setRunning(1);
			
			
			machineService.addMachine(machine);
			
			// Usage Service implementation
			Instance instance = instanceService.getInstance(cluster.getInstanceId());
			try {
				usageService.startVirtualMachineUsageMonitoring(instance.getOrganizationid(), cluster.getType(), cluster.getId(), machine.getId());
			} catch (Exception e) {
				LOG.error(threadName+": Error starting usage monitoring");
			}
						
			machinesToTag.add(tempInstance.getInstanceId());
		/*	if(tempInstance.getInstanceType().equalsIgnoreCase("m1.small")) {
				com.amazonaws.services.elasticloadbalancing.model.Instance lbI = new com.amazonaws.services.elasticloadbalancing.model.Instance();
				lbI.setInstanceId(tempInstance.getInstanceId());
				lbInstanceList.add(lbI);
			} */
			
		
		}
		//LOG.info("Addresses: "+lbAddresses);
		Tag clusterNameTag = new Tag();
		clusterNameTag.setKey("Name");
		clusterNameTag.setValue(cluster.getName());
		Collection<Tag> tags = new ArrayList<Tag>();
		tags.add(clusterNameTag);
		ec2.setTags(tags, machinesToTag);
	/*	if(needsLoadBalancer != null && needsLoadBalancer.equalsIgnoreCase("yes")) {
			ec2.registerInstancesToLoadBalancer(lbInstanceList, cluster.getLbName(), lbAddresses, secretKey);
		} */
		
		return true;
	}
	
	private String getFreeMulticastAddress() {
		String threadName = Thread.currentThread().getName();
		int b = 2;
		int c = 1;
		int d = 1;
		String tempAddress = "";
		boolean usable = true;
		Collection<MulticastAddress> aList = maService.getAddresses();
		if(aList == null || aList.size() < 1) {
			LOG.debug(threadName+": Address list was 0, using the default address 224.2.1.1");
			tempAddress = "224."+b+"."+c+"."+d;
		} else {
			for (c = 1; c < 254; c++) {
				for (d = 1; d < 254; d++) {
					usable = true;
					tempAddress = "224." + b + "." + c + "." + d;
					Iterator<MulticastAddress> ite = aList.iterator();
					LOG.debug(threadName+": Comparing address "+tempAddress);
					while (ite.hasNext()) {
						MulticastAddress a = ite.next();
						LOG.debug(threadName+": Address to compare "+a.getAddress());
						if (a.getAddress() != null && a.getAddress().equals(tempAddress)) {
							LOG.debug(threadName+": Found the same, marking unusable");
							usable = false;
						}
					}
					if (usable)
						break;
				}
				if (usable)
					break;
			}
		}
		return tempAddress;
	}
	
	private String imageId(Job job, String service, Cluster cluster) throws WorkerException {
		String imageId = null;
		if (job.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			imageId = PropertyManager.getProperty(PROPERTY_PREFIX + service + ".image.amazon."+cluster.getEbsImageUsed());
		} else if (job.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			imageId = PropertyManager.getProperty(PROPERTY_PREFIX + service + ".image.eucalyptus."+cluster.getEbsImageUsed());
		}
		if (imageId == null || imageId.length() < 5) {
			throw new WorkerException("Can't find image id from service " + service);
		}
		return imageId;
	}
	
	private String imageId(int cloudType, String service, int ebs) throws WorkerException {
		String imageId = null;
		if(cloudType == InstanceService.CLOUD_TYPE_AMAZON) {
			imageId = PropertyManager.getProperty(PROPERTY_PREFIX + service + ".image.amazon."+ebs);
		} else if(cloudType == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			imageId = PropertyManager.getProperty(PROPERTY_PREFIX + service + ".image.eucalyptus."+ebs);
		}
		if (imageId == null || imageId.length() < 5) {
			throw new WorkerException("Can't find image id from service " + service);
		}
		return imageId;
	}
	
}
