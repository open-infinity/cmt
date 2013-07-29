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

package org.openinfinity.cloud.domain.repository.administrator;

import java.util.List;

import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Machine;

/**
 * Interface for Machine repository
 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public interface MachineRepository {

	void addMachine(Machine machine);
	List<Machine> getMachinesInCluster(int clusterId);
	List<Machine> getMachinesInClusterRunningAndReady(int clusterId);
	List<Machine> getMachinesInClusterExceptType(int clusterId, String machineType);
	boolean allMachinesConfigured(int clusterId);
	List<Machine> getMachinesNeedingConfigure();
	List<Machine> getBigDataMachinesNeedingConfigure();
	List<Machine> getMachinesNeedingUpdate(int cloudType);
	void updateMachineConfigure(int id, int configured); 
	Machine getMachineByInstanceId(String instanceId);
	Machine getClusterManagementMachine(int clusterId);
	void updateMachine(Machine machine);
	void removeMachine(int id);
	void removeMachine(String instanceId);
	Machine getMachine(int id);
	List<Machine> getMachines();
	List<Machine> getMachines(int offset, int rows, int instanceId);
	int getNumberOfMachines();
	int getNumberOfMachines(int instanceId);
	List<Machine> searchMachines(int projectId);
	Machine getMachine(String instanceId);
	void stopMachine(int id);
	void startMachine(int id);
	Machine getBigDataManager(int clusterId);
	
}
