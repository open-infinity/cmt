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

import java.util.Collection;
import java.util.List;

import org.openinfinity.cloud.domain.Key;
import org.openinfinity.cloud.domain.Machine;

/**
 * Machine service interface for handling virtual images inside a cloud environment
 * 
 * @author Ilkka Leinonen
 * @author Ossi Hämäläinen
 * @author Juha-Matti Sironen
 * @author Vedran Bartonicek
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */
public interface MachineService {

	static final int MACHINE_CONFIGURE_NOT_STARTED = 0;
	static final int MACHINE_CONFIGURE_STARTED = 1;
	static final int MACHINE_CONFIGURE_READY = 3;
	static final int MACHINE_CONFIGURE_ERROR = 5;
	
	static final int DATA_MACHINE_CONFIGURE_NOT_STARTED = 10;
	static final int DATA_MACHINE_CONFIGURE_STARTED = 11;
	
	static final int MACHINE_SIZE_SMALL = 0;
	static final int MACHINE_SIZE_MEDIUM = 1;
	static final int MACHINE_SIZE_LARGE = 2;
	
	Collection<Machine> getMachines();
	
	Collection<Machine> getMachines(int offset, int rows, int instanceId);
	
	Collection<Machine> getMachinesInCluster(int clusterId);
	
	Collection<Machine> getMachinesInClusterExceptType(int clusterId, String machineType);
	
    boolean allMachinesConfigured(int clusterId);
	
	Collection<Machine> getMachinesNeedingConfigure();
	
	Collection<Machine> getBigDataMachinesNeedingConfigure();
	
	Collection<Machine> getMachinesNeedingUpdate(int cloudType);
	
	int getNumberOfMachines();
	
	int getNumberOfMachines(int instanceId);
	
	void addMachine(Machine machine);
	
	void updateMachine(Machine machine);
	
	void updateMachineConfigure(int id, int configured);
	
	Machine getMachine(String instanceId);
	
	Machine getMachine(int id);
	
	void removeMachine(int id);
	
	void removeMachine(String instanceId);
	
	void stopMachine(int id);
	
	void startMachine(int id);
	
	Collection<Machine> searchMachines(int projectId);

	Machine getClusterManagementMachine(int clusterId);
	
}
