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

package org.openinfinity.cloud.service.administrator;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.model.*;
import org.apache.log4j.Logger;
import org.openinfinity.core.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.openinfinity.cloud.service.administrator.InstanceService.CLOUD_TYPE_AMAZON;
import static org.openinfinity.cloud.service.administrator.InstanceService.CLOUD_TYPE_EUCALYPTUS;

/**
 * EC2 wrapper.
 * 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public class EC2Wrapper {
	
	private static final Logger LOG = Logger.getLogger(EC2Wrapper.class.getName());
	private AmazonEC2 ec2 = null;
	private AmazonElasticLoadBalancing lb = null;
	private int cloudType;
	private String endpoint;
		
	public EC2Wrapper(){}
	
	public EC2Wrapper(String aEndpoint, int aCloudType, AWSCredentials credentials){
		if(aCloudType == InstanceService.CLOUD_TYPE_AMAZON) {
			//endpoint = "ec2.eu-west-1.amazonaws.com";
			endpoint = aEndpoint;
			this.init(credentials, aCloudType);
		} else if(aCloudType == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			endpoint = aEndpoint;
			this.init(credentials, aCloudType);
		}
		cloudType = aCloudType;	
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void init(AWSCredentials credentials, int cloudType) {
		this.cloudType = cloudType;
		try {
			if (ec2 == null) {
				if (cloudType == CLOUD_TYPE_AMAZON) {
					LOG.info("Credentials: "+credentials.getAWSAccessKeyId()+", "+credentials.getAWSSecretKey());
					ec2 = new AmazonEC2Client(credentials);
					ec2.setEndpoint(endpoint);
				} else if (cloudType == CLOUD_TYPE_EUCALYPTUS) {
					LOG.info("Credentials: "+credentials.getAWSAccessKeyId()+", "+credentials.getAWSSecretKey());
					ec2 = new AmazonEC2Client(credentials);
					ec2.setEndpoint(endpoint);
				}
			}
			if (lb == null) {
				/* if (cloudType == CLOUD_TYPE_AMAZON) {
					lb = new AmazonElasticLoadBalancingClient(credentials);
					lb.setEndpoint("elasticloadbalancing.eu-west-1.amazonaws.com");
				} */
			}
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error initialising EC2 connection: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public Reservation createInstance(String amiId, Integer amount, String keyName, String zone, String instanceType, List<String> securityGroups) {
		Reservation reservation = null;
		try {

	        Integer minimumAmount = amount;
	        Integer maximumAmount = amount;
	        if(amiId == null) {
	        	return null;
	        }
	        String myAmiId = null;
	        
	        myAmiId = amiId;
	       
	        RunInstancesRequest runInstancesRequest = new RunInstancesRequest(myAmiId, minimumAmount, maximumAmount);
	        runInstancesRequest.setKeyName(keyName);
	        Placement placement = new Placement();
	        placement.setAvailabilityZone(zone);
	        runInstancesRequest.setPlacement(placement);
	        runInstancesRequest.setSecurityGroups(securityGroups);
	        if(instanceType != null) {
	        	runInstancesRequest.setInstanceType(instanceType);
	        }
	        RunInstancesResult result = ec2.runInstances(runInstancesRequest);
	        reservation = result.getReservation();

		} catch (AmazonServiceException ase) {
			String message = ase.getMessage();
			LOG.error("Caught Exception: " + message);
			LOG.error("Response Status Code: " + ase.getStatusCode());
			LOG.error("Error Code: " + ase.getErrorCode());
			LOG.error("Request ID: " + ase.getRequestId());
			ExceptionUtil.throwSystemException(message, ase);
		} catch (AmazonClientException e) {
			
			e.printStackTrace();
			ExceptionUtil.throwSystemException(e.getMessage(), e);
		}
		
		return reservation;
	}
	
	public String createSecurityGroup(String groupName, String description) {
		CreateSecurityGroupResult result = null;
		try {
			CreateSecurityGroupRequest request = new CreateSecurityGroupRequest();
			request.setDescription(description);
			request.setGroupName(groupName);
			result = ec2.createSecurityGroup(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error creating security group: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		return result.getGroupId();
	}
	
	public void deleteSecurityGroup(String groupName) {
		try {
			DeleteSecurityGroupRequest request = new DeleteSecurityGroupRequest();
			request.setGroupName(groupName);
			ec2.deleteSecurityGroup(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error deleting security group: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public void authorizeIPs(String securityGroupName, String cidrIp, Integer fromPort, Integer toPort, String protocol) {
		try {
			AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest();
			
			if(this.cloudType == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
				request.setFromPort(fromPort);
				request.setToPort(toPort);
				request.setCidrIp(cidrIp);
				request.setIpProtocol(protocol);
			} else {

				IpPermission perm = new IpPermission();
				perm.setFromPort(fromPort);
				perm.setToPort(toPort);
				perm.setIpProtocol(protocol);
				List<String> ipRanges = new ArrayList<String>();
				ipRanges.add(cidrIp);
				perm.setIpRanges(ipRanges);
				List<IpPermission> permList = new ArrayList<IpPermission>();
				permList.add(perm);
				request.setIpPermissions(permList);
			}
			request.setGroupName(securityGroupName);
			ec2.authorizeSecurityGroupIngress(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Could not set authorized IP:s to security group: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public void authorizeGroup(String securityGroupName, String sourceGroupName, String sourceGroupOwner, Integer fromPort, Integer toPort, String protocol) {
		try {
			AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest();
		//	if(this.cloudType == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
		/*		request.setFromPort(fromPort);
				request.setToPort(toPort);
				request.setSourceSecurityGroupName(sourceGroupName);
				request.setSourceSecurityGroupOwnerId(sourceGroupOwner);
				request.setIpProtocol(protocol); */
	//		} else {

				UserIdGroupPair pair = new UserIdGroupPair();
				pair.setGroupName(sourceGroupName);
				pair.setUserId(sourceGroupOwner);
				List<UserIdGroupPair> idList = new ArrayList<UserIdGroupPair>();
				idList.add(pair);
				IpPermission perm = new IpPermission();
				perm.setUserIdGroupPairs(idList);
				perm.setFromPort(fromPort);
				perm.setToPort(toPort);
				perm.setIpProtocol(protocol);
				List<IpPermission> permList = new ArrayList<IpPermission>();
				permList.add(perm);
				request.setIpPermissions(permList);
	//		}
			request.setGroupName(securityGroupName);
			
			ec2.authorizeSecurityGroupIngress(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Could not set authorized IP:s to security group: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public void revokeGroup(String securityGroupName, String sourceGroupName, String sourceGroupOwner, Integer fromPort, Integer toPort, String protocol) {
		try {
			RevokeSecurityGroupIngressRequest request = new RevokeSecurityGroupIngressRequest();
			UserIdGroupPair pair = new UserIdGroupPair();
			pair.setGroupName(sourceGroupName);
			pair.setUserId(sourceGroupOwner);
			List<UserIdGroupPair> idList = new ArrayList<UserIdGroupPair>();
			idList.add(pair);
			IpPermission perm = new IpPermission();
			perm.setUserIdGroupPairs(idList);
			perm.setFromPort(fromPort);
			perm.setToPort(toPort);
			perm.setIpProtocol(protocol);
			List<IpPermission> permList = new ArrayList<IpPermission>();
			permList.add(perm);
			request.setIpPermissions(permList);

			request.setGroupName(securityGroupName);
			ec2.revokeSecurityGroupIngress(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Could not set authorized IP:s to security group: "
					+ message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public void revokeIPs(String securityGroupName, String cidrIp, Integer fromPort, Integer toPort, String protocol) {
		try {
			RevokeSecurityGroupIngressRequest request = new RevokeSecurityGroupIngressRequest();
			request.setGroupName(securityGroupName);
			request.setCidrIp(cidrIp);
			request.setFromPort(fromPort);
			request.setToPort(toPort);
			request.setIpProtocol(protocol);
			ec2.revokeSecurityGroupIngress(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Cloud not revoke IP.s from security group: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public void deregisterInstancesToLoadBalancer(Collection<com.amazonaws.services.elasticloadbalancing.model.Instance> instanceList, String lbName) {
		if (this.cloudType == CLOUD_TYPE_AMAZON) {
			try {
				DeregisterInstancesFromLoadBalancerRequest request = new DeregisterInstancesFromLoadBalancerRequest();
				request.setInstances(instanceList);
				request.setLoadBalancerName(lbName);
				lb.deregisterInstancesFromLoadBalancer(request);
			} catch (Exception e) {
				String message = e.getMessage();
				LOG.error("Error deregistering instances: "+message);
				ExceptionUtil.throwSystemException(message, e);
			}
		}
	}
	
	public String createLoadBalancer(String lbName, String zone, String keyName) {
		CreateLoadBalancerResult result = null;
		
	/*	if (this.cloudType == InstanceService.CLOUD_TYPE_AMAZON) {

			try {
				CreateLoadBalancerRequest request = new CreateLoadBalancerRequest();
				ArrayList<String> zones = new ArrayList<String>();
				zones.add(zone);
				request.setAvailabilityZones(zones);
				ArrayList<Listener> listeners = new ArrayList<Listener>();
				Listener listener = new Listener();
				listener.setLoadBalancerPort(80);
				listener.setInstancePort(8080);
				listener.setProtocol("HTTP");
				listeners.add(listener);
				request.setListeners(listeners);
				request.setLoadBalancerName(lbName);
				LOG.info(request.toString());
				result = lb.createLoadBalancer(request);
			} catch (Exception e) {
				String message = e.getMessage();
				LOG.error("Error creating load balancer: " + message);
				ExceptionUtil.throwSystemException(message, e);
			}
			if (result != null) {
				LOG.info(result.toString());
				String lbDns = result.getDNSName();
				if (lbDns != null) {
					LOG.info("Returning DNS name: " + lbDns);
					return lbDns;
				}
			}
		} else if(this.cloudType == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			// TODO currently do nothing...
		}  */
		
		
		return null;
	}
	
	public void deleteLoadBalancer(String name, String lbInstanceId) {
	/*	if (this.cloudType == CLOUD_TYPE_AMAZON) {
			try {
				DeleteLoadBalancerRequest request = new DeleteLoadBalancerRequest();
				request.setLoadBalancerName(name);
				lb.deleteLoadBalancer(request);
			} catch (Exception e) {
				String message = e.getMessage();
				LOG.error("Error deleting loadbalancer: "+message);
				ExceptionUtil.throwSystemException(message, e);
			} 
		} else if(this.cloudType == CLOUD_TYPE_EUCALYPTUS) { */
			terminateInstance(lbInstanceId);
	/*	} */
	}
	
	public void setAppCookieStickiness(String cookieName, String policyName, String lbName) {
		if (this.cloudType == InstanceService.CLOUD_TYPE_AMAZON) {
			try {

				CreateAppCookieStickinessPolicyRequest request = new CreateAppCookieStickinessPolicyRequest();
				request.setCookieName(cookieName);
				request.setPolicyName(policyName);
				request.setLoadBalancerName(lbName);
				LOG.info(request.toString());
				lb.createAppCookieStickinessPolicy(request);
			} catch (Exception e) {
				String message = e.getMessage();
				LOG.error("Error creating app cookie stickness policy to load balancer: " + message);
				ExceptionUtil.throwSystemException(message, e);
			}
		}

	}
	
	public void setLoadBalancerPoliciesOfListener(String policyName, String lbName, Integer lbPort) {
		if (this.cloudType == InstanceService.CLOUD_TYPE_AMAZON) {
			try {
				SetLoadBalancerPoliciesOfListenerRequest request = new SetLoadBalancerPoliciesOfListenerRequest();
				request.setLoadBalancerName(lbName);
				request.setLoadBalancerPort(lbPort);
				ArrayList<String> policyList = new ArrayList<String>();
				policyList.add(policyName);
				request.setPolicyNames(policyList);
				LOG.info(request.toString());
				lb.setLoadBalancerPoliciesOfListener(request);
			} catch (Exception e) {
				String message = e.getMessage();
				LOG.error("Error setting load balancer policy: "
						+ message);
				ExceptionUtil.throwSystemException(message, e);
			}
		}
	}
	
	
	public Instance describeInstance(String instanceId) {
		Instance instance = null;
		try {
			DescribeInstancesRequest request = new DescribeInstancesRequest();
			ArrayList<String> instanceList = new ArrayList<String>();
			instanceList.add(instanceId);
			request.setInstanceIds(instanceList);
			DescribeInstancesResult result = ec2.describeInstances(request);
			if(result != null) {
				List<Reservation> resList = result.getReservations();
				Iterator<Reservation> i = resList.iterator();
				while(i.hasNext()) {
					Reservation r = i.next();
					List<Instance> iList = r.getInstances();
					Iterator<Instance> j = iList.iterator();
					while(j.hasNext()) {
						Instance inst = j.next();
						if(inst.getInstanceId().equals(instanceId)) {
							instance = inst;
							break;
						}
					}
				}
			}
			
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error getting instance information: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		return instance;
	}
	
	public Collection<Reservation> describeInstances(Collection<String> instanceIds) {
		Collection<Reservation> resList = null;
		try {
			DescribeInstancesRequest request = new DescribeInstancesRequest();
			request.setInstanceIds(instanceIds);
			DescribeInstancesResult result = ec2.describeInstances(request);
			if(result != null) {
				resList = result.getReservations();
			}
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error getting instance information: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		
		return resList;
	}
	
	
	
	public boolean terminateInstance(String instanceId) {
		try {
			ArrayList<String> instances = new ArrayList<String>();
			instances.add(instanceId);
			TerminateInstancesRequest terminateReq = new TerminateInstancesRequest(instances);
			ec2.terminateInstances(terminateReq);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error terminating instance ("+instanceId+"): "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		return true;
		
	}
	
	public boolean assignElasticIPToInstance(String instanceId, String ipAddress) {
		try {
			AssociateAddressRequest associateAddressRequest = new AssociateAddressRequest(instanceId, ipAddress);
			ec2.associateAddress(associateAddressRequest);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error associating elastic IP address to instance "+instanceId+": "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		return true;
	}
	
	public boolean removeElasticIPFromInstance(String ipAddress) {
		try {
			DisassociateAddressRequest req = new DisassociateAddressRequest(ipAddress);
			ec2.disassociateAddress(req);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error removing elastic IP address ("+ipAddress+") from instance");
			ExceptionUtil.throwSystemException(message, e);
		}
		return true;
	}
	
	public boolean setTags(Collection<Tag> tags, Collection<String> resources) {
		if(this.cloudType == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			return true;
		}
		try {
			CreateTagsRequest tagReq = new CreateTagsRequest();
			tagReq.setTags(tags);
			tagReq.setResources(resources);
			ec2.createTags(tagReq);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error setting tags: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		return true;
	}
	
	public void registerInstancesToLoadBalancer(Collection<com.amazonaws.services.elasticloadbalancing.model.Instance> instanceList, String lbName, String addresses, String secretKey) {
		if (this.cloudType == InstanceService.CLOUD_TYPE_AMAZON) {
			try {
				RegisterInstancesWithLoadBalancerRequest request = new RegisterInstancesWithLoadBalancerRequest();
				request.setInstances(instanceList);
				request.setLoadBalancerName(lbName);
				lb.registerInstancesWithLoadBalancer(request);
			} catch (Exception e) {
				String message = e.getMessage();
				LOG.error("Error registering instances: " + message);
				ExceptionUtil.throwSystemException(message, e);
			}
		} else if(this.cloudType == InstanceService.CLOUD_TYPE_EUCALYPTUS) {
			// TODO nothing to do at the moment
		}
		
	}
	
	public KeyPair createKeypair(String name) {
		CreateKeyPairResult result = null;
		try {
			CreateKeyPairRequest kprq = new CreateKeyPairRequest(name);
			result = ec2.createKeyPair(kprq);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error creating KeyPair: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		if(result != null) {
			return result.getKeyPair();
		} else {
			return null;
		}
	}
	
	public void deleteKeypair(String name)  {
		try {
			DeleteKeyPairRequest request = new DeleteKeyPairRequest();
			request.setKeyName(name);
			ec2.deleteKeyPair(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error deleting keypair: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public String createVolume(Integer size, String zone) {
		CreateVolumeResult result = null;
		try {
			CreateVolumeRequest request = new CreateVolumeRequest(size, zone);
			result = ec2.createVolume(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error creating Volume: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		if(result != null) {
			return result.getVolume().getVolumeId();
		} else {
			return null;
		}
	}
	
	public String getVolumeState(String volumeId) {
		DescribeVolumesResult result = null;
		List<String> volumeList = new ArrayList<String>();
		volumeList.add(volumeId);
		try {
			DescribeVolumesRequest request = new DescribeVolumesRequest(volumeList);
			result = ec2.describeVolumes(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error getting Volume status: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
		if(result != null) {
			String resultStr = result.getVolumes().get(0).getState();
			if(resultStr.equals("in-use")) {
				resultStr = result.getVolumes().get(0).getAttachments().get(0).getState();
			}
			return resultStr;
		} else {
			return null;
		}
	}
	
	public void attachVolume(String volumeId, String instanceId, String deviceName) {
		try {
			AttachVolumeRequest request = new AttachVolumeRequest(volumeId, instanceId, deviceName);
			ec2.attachVolume(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error attaching volume to instance: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public void deleteVolume(String volumeId) {
		try {
			DeleteVolumeRequest request = new DeleteVolumeRequest(volumeId);
			ec2.deleteVolume(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error deleting volume: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}
	
	public void detachVolume(String volumeId) {
		try {
			DetachVolumeRequest request = new DetachVolumeRequest(volumeId);
			ec2.detachVolume(request);
		} catch (Exception e) {
			String message = e.getMessage();
			LOG.error("Error detaching volume: "+message);
			ExceptionUtil.throwSystemException(message, e);
		}
	}

}
