package org.openinfinity.cloud.service.administrator;

import java.util.ArrayList;
import java.util.Collection;

import org.openinfinity.cloud.domain.Instance;
import org.openinfinity.cloud.domain.InstanceParameter;
import org.openinfinity.cloud.domain.repository.administrator.InstanceRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Cloud instance handling interface implementation for handling cloud instances.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 */
@Service("instanceService")
public class InstanceServiceImpl implements InstanceService {
	
	@Autowired
	@Qualifier("instanceRepository")
	private InstanceRepository instanceRepository;
	
	@Log
	public Collection<Instance> getInstances(Long userId) {
		return instanceRepository.getInstances(userId);
	}

	@Log
	public void addInstance(Instance instance) {
		instanceRepository.addInstance(instance);
	}

	@Log
	public Instance getInstance(int userId, String name) {
		return instanceRepository.getInstance(userId, name);		
	}
	
	@Log
	public void deleteInstance(int instanceId) {
		instanceRepository.deleteInstance(instanceId);
	}
	
	@Log
	public void updateInstanceStatus(int instanceId, String status) {
		instanceRepository.updateInstanceStatus(instanceId, status);
	}

	@Log
	public Collection<Instance> getOrganizationInstances(Long organizationId) {
		return instanceRepository.getOrganizationInstances(organizationId);
	}

	@Log
	public Instance getInstanceByMachineId(int machineId) {
		return instanceRepository.getInstanceByMachineId(machineId);
	}

	@Log
	public Instance getInstance(int instanceId) {
		return instanceRepository.getInstance(instanceId);
	}
	
	@Log
	public Collection<Instance> getUserInstances(Collection<Long> organizationIds) {
		Collection<Instance> instanceList = new ArrayList<Instance>();

		for (Long oId : organizationIds) {
			Collection<Instance> tempInstanceList = getOrganizationInstances(oId);
			if (tempInstanceList != null) {
				instanceList.addAll(tempInstanceList);
			}
		}

		return instanceList;
	}
	
	@Log
	public InstanceParameter getInstanceParameterByName(
			Collection<InstanceParameter> parameters, String key) {
		if (parameters==null || key==null){
			return null;
		}
		InstanceParameter ret=null;
		for (InstanceParameter parameter:parameters){
			if (key.equals(parameter.getKey())){
				ret=parameter;
				break;
			}
		}
		return ret;
	}

}