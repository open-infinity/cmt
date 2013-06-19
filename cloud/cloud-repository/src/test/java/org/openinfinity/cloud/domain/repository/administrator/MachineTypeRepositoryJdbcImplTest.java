package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.MachineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * The test initializes the H2 database with scripts sql/H2/create_tables.sql and sql/populate_tables.sql.
 * The system.properties configures the H2 jdbc url etc. Can be changed also to use MariaDB/MySQL.
 * 
 * Troubleshooting: if test fails. Check that the populate_tables.sql has correct values.
 * 
 * @author kytolesa
 *
 */
@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class MachineTypeRepositoryJdbcImplTest {

	@Autowired
	private MachineTypeRepository machineTypeRepository;
	
	@Test
	public void testMachineTypeRepository() {
		List<String> userOrganizations = new ArrayList<String>();
		userOrganizations.add("TOAS");
		Collection<MachineType> machineTypes = machineTypeRepository.getMachineTypes(userOrganizations);
		
		MachineType actual = machineTypes.iterator().next();
		assertEquals("Small", actual.getName());
		assertEquals("Cores: 1, RAM: 1GB, Disk: 10GB", actual.getSpecification());
	}
	
	@Test
	public void testMachineTypeRepositoryGetAll() {
		Collection<MachineType> machineTypes = machineTypeRepository.getMachineTypes();
		
		assertEquals(5, machineTypes.size());
		
		MachineType actual = machineTypes.iterator().next();
		assertEquals("Small", actual.getName());
		assertEquals("Cores: 1, RAM: 1GB, Disk: 10GB", actual.getSpecification());
	}
	
	@Test
	public void testMachineTypeRepositoryGetById() {
		MachineType machineType = machineTypeRepository.getMachineTypeById(0);
		
		assertEquals("Small", machineType.getName());
		assertEquals("Cores: 1, RAM: 1GB, Disk: 10GB", machineType.getSpecification());
	}

}
