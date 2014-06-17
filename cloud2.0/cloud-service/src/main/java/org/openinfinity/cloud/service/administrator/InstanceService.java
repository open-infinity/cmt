package org.openinfinity.cloud.service.administrator;

import java.util.Collection;

import org.openinfinity.cloud.domain.Instance;

/**
 * Cloud instance handling interface for handling cloud instances.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 */
public interface InstanceService {
	
	public static final int CLOUD_TYPE_AMAZON = 0;
	
	public static final int CLOUD_TYPE_EUCALYPTUS = 1;
	
	Collection<Instance> getInstances(Long userId);
	
	Collection<Instance> getOrganizationInstances(Long organizationId);
	
	void addInstance(Instance instance);
	
	Instance getInstance(int userId, String name);
	
	Instance getInstanceByMachineId(int machineId);
	
	void deleteInstance(int instanceId);
	
	Instance getInstance(int instanceId);
	
	void updateInstanceStatus(int instanceId, String status);
	
	Collection<Instance> getUserInstances(Collection<Long> organizationIds);

	Collection<Instance> getAllActiveInstances();
	
}