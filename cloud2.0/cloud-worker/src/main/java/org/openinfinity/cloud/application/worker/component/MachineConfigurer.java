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
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.application.worker.Configurer;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.EC2Wrapper;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.KeyService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.util.PropertyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * Machine configurer Component
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Component("MachineConfigurer")
public class MachineConfigurer implements Configurer {
	private static final Logger LOG = Logger.getLogger(MachineConfigurer.class.getName());
	
	@Autowired
	@Qualifier("instanceService")
	private InstanceService instanceService;
	
	@Autowired
	@Qualifier("machineService")
	private MachineService machineService;
	
	@Autowired
	@Qualifier("clusterService")
	private ClusterService clusterService;
	
	@Autowired
	@Qualifier("keyService")
	private KeyService keyService;
	
	@Autowired
	@Qualifier("cloudCredentials")
	private AWSCredentials eucaCredentials;
	
	@Autowired
	@Qualifier("amazonCredentials")
	private AWSCredentials amazonCredentials;
	
	@Async
	public void configure(Machine m) {
		String threadName = Thread.currentThread().getName();
		LOG.info("Configurer starting to configure machine "+m.getId()+" on thread "+threadName);
		Cluster c = clusterService.getCluster(m.getClusterId());
		Instance instance = instanceService.getInstance(c.getInstanceId());
		Key k = keyService.getKeyByInstanceId(instance.getInstanceId());
		
		EC2Wrapper ec2 = new EC2Wrapper();
		
		if(m.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			String endPoint = PropertyManager.getProperty("cloudadmin.worker.amazon.endpoint");
			ec2.setEndpoint(endPoint);
			ec2.init(amazonCredentials, m.getCloud());
		} else if(m.getCloud() == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			String endPoint = PropertyManager.getProperty("cloudadmin.worker.eucalyptus.endpoint");
			ec2.setEndpoint(endPoint);
			ec2.init(eucaCredentials, m.getCloud());
		}
		int maxWaitForRunning = 500;
		while(!m.getState().equals("running") && maxWaitForRunning > 0) {
			LOG.info(threadName+": Waiting instance "+m.getInstanceId()+" to be at 'running' state. Waiting for "+maxWaitForRunning+" times");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOG.error(threadName+": Someone interrupted my sleep! How rude! "+e.getMessage());
			}
			m = machineService.getMachine(m.getId());
			maxWaitForRunning--;
		}
		if(maxWaitForRunning == 0) {
			LOG.error(threadName+": Machine did not start in time, not waiting anymore.");
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return;
		}
		if(m.getEbsVolumeSize() > 0) {	
			String volumeState = ec2.getVolumeState(m.getEbsVolumeId());
			if(volumeState != null && volumeState.equals("creating")) {
				LOG.info(threadName+": EBS volume still in creating phase, we need to wait a bit");
				int maxWaitForEBS = 500;
				while(volumeState.equals("creating") && maxWaitForEBS > 0) {
					LOG.info(threadName+": Waiting for EBS volume "+m.getEbsVolumeId()+" to be available");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						LOG.error(threadName+": Someone interrupted my sleep! How rude! "+e.getMessage());
					}
					volumeState = ec2.getVolumeState(m.getEbsVolumeId());
					maxWaitForEBS--;
				}
				if(maxWaitForEBS == 0) {
					LOG.error(threadName+": Creating EBS volume not ready in time, not waiting anymore...");
					machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
					return;
				}
			}
			if (volumeState != null && volumeState.equals("available")) {
				String ebsDevice = null;
				if (c.getEbsImageUsed() > 0) {
					ebsDevice = "vdc";
				} else {
					ebsDevice = "vdb";
				}
				LOG.info(threadName + ": Attachin ebs volume "
						+ m.getEbsVolumeId() + " to instance "
						+ m.getInstanceId() + " as device " + ebsDevice);
				ec2.attachVolume(m.getEbsVolumeId(), m.getInstanceId(),
						ebsDevice);
				m.setEbsVolumeDevice(ebsDevice);
				machineService.updateMachine(m);			
			}
			volumeState = ec2.getVolumeState(m.getEbsVolumeId());
			LOG.info(threadName+": Current volume state is "+volumeState);
			if(volumeState != null && volumeState.equals("attaching")) {
				int maxWaitForEBS = 500;
				while(volumeState.equals("attaching") && maxWaitForEBS > 0) {
					LOG.info(threadName+": Waiting for EBS volume "+m.getEbsVolumeId()+" to be attached");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						LOG.error(threadName+": Someone interrupted my sleep! How rude! "+e.getMessage());
					}
					volumeState = ec2.getVolumeState(m.getEbsVolumeId());
					maxWaitForEBS--;
				}
				if(maxWaitForEBS == 0) {
					LOG.error(threadName+": EBS volume did not attach in time, not waiting anymore");
					machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
					return;
				}
			}
		}
		
		boolean connectOK = false;
		boolean needNewRun = false;
		String connectionRetryTimes = PropertyManager.getProperty("cloudadmin.worker.configurer.connection.retrys");
		int x = Integer.parseInt(connectionRetryTimes);
		if(x == 0) {
			x = 500;
		}
		while(!connectOK) {
			x--;
			Socket s = null;
			if(m.getDnsName().startsWith("euca-0-0-0-0") || m.getDnsName().startsWith("0.0.0.0")) {
				LOG.info("Machine dnsname not yet set correctly by eucalyptus, waiting for a moment");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					LOG.error(threadName+": Someone interrupted my sleep! How rude! "+e.getMessage());
				}
				m = machineService.getMachine(m.getId());
			} else {
				try {
					s = new Socket();
					s.setReuseAddress(true);
					LOG.debug(threadName + ": Trying to connect machine " + m.getId() + ", address: " + m.getDnsName());
					SocketAddress sa = new InetSocketAddress(m.getDnsName(), 22);
					s.connect(sa, 3000);
					connectOK = true;
					LOG.info(threadName + ": Connected OK to the instance " + m.getInstanceId());
				} catch (IOException e) {
					if (x == 0) {
						machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
						return;
					}
					LOG.info(threadName + ": Got IO exception connecting to ssh port, trying for " + x
							+ " more times...");
					LOG.info(threadName + ": Updating machine info");
					m = machineService.getMachine(m.getId());
					try {
						Thread.sleep(3000);
					} catch (InterruptedException ex) {
						LOG.error(threadName + ": Someone interrupted my sleep! How rude! " + e.getMessage());
					}
				} finally {
					if (s != null) {
						try {
							s.close();
						} catch (IOException e) {
							LOG.error(threadName + ": Error closing socket");
						}
					}
				}
			}
		}
		
		
		byte[] privkey = k.getSecret_key().getBytes();
		final byte[] emptyPassPhrase = new byte[0];
		JSch jsch = new JSch();
		try {
			jsch.addIdentity(m.getUserName(), privkey, null, emptyPassPhrase);
			Session session = jsch.getSession(m.getUserName(), m.getDnsName(), 22);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			LOG.info(threadName+": Connecting with ssh to "+m.getDnsName());
			
			session.connect();
			String command = null;
			if(m.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
				command = "/usr/bin/sudo /usr/bin/puppet agent --test --no-daemonize --onetime --certname ";
			} else {
				command = "/usr/bin/puppet agent --test --no-daemonize --onetime --certname ";
			}
			command += instance.getInstanceId()+"_";
			command += c.getId()+"_";
			command += m.getId()+"_";
			command += ClusterService.CLUSTER_TYPE_MACHINE_NAME[c.getType()];
			if(m.getType().equals("loadbalancer")) {
				command += "_lb";
			}
			
			LOG.info(threadName+": Running command: '"+command+"'");
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setPty(true);
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			InputStream in = channel.getInputStream();
			channel.connect();
			
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					LOG.info(threadName+": "+new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					int exitStatus = channel.getExitStatus();
					LOG.info(threadName+": exit-status: "
							+ exitStatus);
					if(exitStatus > 2) {
						needNewRun = true;
					}
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error(threadName+": Error configuring machine: "+message);
			if(message.startsWith("Auth fail")) {
				needNewRun = true;
			} else {
			
				machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
				return;
			}
		}
		if(needNewRun) {
			LOG.info(threadName+": Machine "+m.getId()+" needs new configure run, updating configure info to database");
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_NOT_STARTED);
		} else {
			LOG.info(threadName+": Configuration ready for machine "+m.getId());
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_READY);
		}
		if(m.getType().equalsIgnoreCase("clustermember")) {
			Machine loadBalancer = machineService.getMachine(c.getLbInstanceId());
			if(loadBalancer != null) {
				LOG.debug(threadName+": Loadbalancer configure status: "+loadBalancer.getConfigured());
				if(loadBalancer.getConfigured() == MachineService.MACHINE_CONFIGURE_READY) {
					LOG.info(threadName+": Forcing loadbalancer reconfigure");
					machineService.updateMachineConfigure(loadBalancer.getId(), MachineService.MACHINE_CONFIGURE_NOT_STARTED);
				}
			} else {
				LOG.warn(threadName+": Loadbalancer was null, this coud be a problem...");
			}
		}
	}

}
