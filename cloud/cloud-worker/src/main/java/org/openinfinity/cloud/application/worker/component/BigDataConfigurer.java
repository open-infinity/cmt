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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * BigData (and NOSQL) configurer Component
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Component("BigDataConfigurer")
public class BigDataConfigurer implements Configurer {
	private static final Logger LOG = Logger.getLogger(BigDataConfigurer.class.getName());
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
		String command = null;
		Cluster c = clusterService.getCluster(m.getClusterId());
		Instance instance = instanceService.getInstance(c.getInstanceId());
		Key k = keyService.getKeyByInstanceId(instance.getInstanceId());
		Machine managementMachine = machineService.getClusterManagementMachine(m.getClusterId());
		
		int maxWaitForRunning = 96;
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
		
		Pattern successPattern = Pattern.compile("status=\"(\\w+)\"");
		if(m.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			command = "/usr/bin/sudo /usr/bin/puppet agent --test --no-daemonize --onetime --certname ";
		} else {
			command = "/usr/bin/puppet agent --test --no-daemonize --onetime --certname ";
		}
		
		command += instance.getInstanceId()+"_";
		command += m.getClusterId()+"_";
		command += m.getId()+"_";
		command += ClusterService.CLUSTER_TYPE_MACHINE_NAME[c.getType()];
		command += "_host";
		String output = sshRunCommand(m, command, k);
		if(output == null) {
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return;
		}
		if(m.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			command = "/usr/bin/sudo /bin/hostname "+m.getName();
		} else {
			command = "/bin/hostname "+m.getName();
		}
		
		output = sshRunCommand(m, command, k);
		if(output == null) {
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return;
		}
		if(m.getCloud() == InstanceService.CLOUD_TYPE_AMAZON) {
			command = "/usr/bin/sudo /opt/bigdata/bin/attach-node.py --xml --role="+m.getType()+" "+m.getName()+" "+m.getPrivateDnsName();
		} else {
			command = "/opt/bigdata/bin/attach-node.py --xml --role="+m.getType()+" "+m.getName()+" "+m.getPrivateDnsName();
		}
		
		output = sshRunCommand(managementMachine, command, k);
		if(output == null) {
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return;
		}
		
		Matcher matcher = successPattern.matcher(output);
		if (matcher.find()) {
			String success = matcher.group(1);
			LOG.info(threadName + ": Got reply from configuration machine: " + success);

			if (!success.equalsIgnoreCase("success")) {
				machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
				return;
			}
		} else {
			LOG.info(threadName+": Could not find status information from the reply");
			machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_ERROR);
			return;
		}
		machineService.updateMachineConfigure(m.getId(), MachineService.MACHINE_CONFIGURE_READY);
	}
	
	private String sshRunCommand(Machine m, String command, Key key) {
		String retVal = null;
		String threadName = Thread.currentThread().getName();
		
		boolean connectOK = false;
		int x = 60;
		
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
						return retVal;
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
		
	/*	while(!connectOK) {
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
		} */
		
		byte[] privkey = key.getSecret_key().getBytes();
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
			
			LOG.info(threadName+": Running command: '"+command+"'");
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setPty(true);
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

}
