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

package org.openinfinity.cloud.service.administrator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.domain.Machine;
import org.openinfinity.cloud.domain.repository.administrator.MachineRepository;
import org.openinfinity.core.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Machine service implementation for handling virtual images inside a cloud environment.
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Service("machineService")
public class MachineServiceImpl implements MachineService {
	
	@Autowired
	@Qualifier("machineRepository")
	private MachineRepository machineRepository;
	
	@Log
	public void addMachine(Machine machine) {
		machineRepository.addMachine(machine);
	}
	
	@Log
	public void updateMachine(Machine machine) {
		machineRepository.updateMachine(machine);
	}

	@Log
	public Machine getMachine(String instanceId) {
		return machineRepository.getMachine(instanceId);
	}

	@Log
	public Machine getMachine(int id) {
		return machineRepository.getMachine(id);
	}

	@Log
	public List<Machine> getMachines() {
		return machineRepository.getMachines();
	}
	
	@Log
	public List<Machine> getMachines(int offset, int rows, int instanceId) {
		return machineRepository.getMachines(offset, rows, instanceId);
	}
	
	@Log
	public List<Machine> getMachinesInCluster(int clusterId) {
		return machineRepository.getMachinesInCluster(clusterId);
	}

	@Log
	public List<Machine> getMachinesInClusterExceptType(int clusterId, String machineType) {
        return machineRepository.getMachinesInClusterExceptType(clusterId, machineType);
    } 
	
	@Log
	public void removeMachine(int id) {
		machineRepository.removeMachine(id);
	}

	@Log
	public List<Machine> searchMachines(int projectId) {
		if(projectId == 0) {
			List<Machine> temp = machineRepository.getMachines();
			if(temp == null) {
				return new ArrayList<Machine>();
			} else {
				return temp;
			}
		} else {
			return machineRepository.searchMachines(projectId);
		}
	}
 	
	@Log
	public void startMachine(int id) {
		machineRepository.startMachine(id);
	}

	@Log
	public void stopMachine(int id) {
		machineRepository.stopMachine(id);
	}

	@Log
	public int getNumberOfMachines() {
		return machineRepository.getNumberOfMachines();
	}
	
	@Log
	public int getNumberOfMachines(int instanceId) {
		return machineRepository.getNumberOfMachines(instanceId);
	}

	@Log
	public void removeMachine(String instanceId) {
		machineRepository.removeMachine(instanceId);	
	}

	@Log
	public Machine getClusterManagementMachine(int clusterId) {
		return machineRepository.getClusterManagementMachine(clusterId);
	}

	@Log
	public void updateMachineConfigure(int id, int configured) {
		machineRepository.updateMachineConfigure(id, configured);
	}

	@Log
	public Collection<Machine> getMachinesNeedingConfigure() {
		return machineRepository.getMachinesNeedingConfigure();
	}

	@Log
	public Collection<Machine> getBigDataMachinesNeedingConfigure() {
		return machineRepository.getBigDataMachinesNeedingConfigure();
	}

	@Log
	public Collection<Machine> getMachinesNeedingUpdate(int cloudType) {
		return machineRepository.getMachinesNeedingUpdate(cloudType);
	}

   @Log
    public boolean allMachinesConfigured(int clusterId) {
       return machineRepository.allMachinesConfigured(clusterId);
    } 

}
