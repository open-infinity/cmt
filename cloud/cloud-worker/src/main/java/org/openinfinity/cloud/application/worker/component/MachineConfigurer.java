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
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.KeyService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.util.PropertyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
	
	@Async
	public void configure(Machine m) {
		String threadName = Thread.currentThread().getName();
		LOG.info("Configurer starting to configure machine "+m.getId()+" on thread "+threadName);
		Cluster c = clusterService.getCluster(m.getClusterId());
		Instance instance = instanceService.getInstance(c.getInstanceId());
		Key k = keyService.getKeyByInstanceId(instance.getInstanceId());
		
		boolean connectOK = false;
		boolean needNewRun = false;
		String connectionRetryTimes = PropertyManager.getProperty("cloudadmin.worker.configurer.connection.retrys");
		int x = Integer.parseInt(connectionRetryTimes);
		if(x == 0) {
			x = 60;
		}
		while(!connectOK) {
			x--;
			Socket s = null;
			if(m.getDnsName().startsWith("euca-0-0-0-0")) {
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
			jsch.addIdentity("root", privkey, null, emptyPassPhrase);
			Session session = jsch.getSession("root", m.getDnsName(), 22);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			LOG.info(threadName+": Connecting with ssh to "+m.getDnsName());
			
			session.connect();
			String command = "/usr/bin/puppet agent --test --no-daemonize --onetime --certname ";
			command += instance.getInstanceId()+"_";
			command += c.getId()+"_";
			command += m.getId()+"_";
			command += ClusterService.CLUSTER_TYPE_MACHINE_NAME[c.getType()];
			if(m.getType().equals("loadbalancer")) {
				command += "_lb";
			}
			
			LOG.info(threadName+": Running command: '"+command+"'");
			Channel channel = session.openChannel("exec");
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
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return;
		}
		if(needNewRun) {
			LOG.info(threadName+": Machine "+m.getId()+" needs new configure run, updating configure info to database");
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_NOT_STARTED);
		} else {
			LOG.info(threadName+": Configuration ready for machine "+m.getId());
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_READY);
		}
	}

}
