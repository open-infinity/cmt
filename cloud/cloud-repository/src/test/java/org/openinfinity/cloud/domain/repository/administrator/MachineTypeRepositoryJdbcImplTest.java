package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.MachineType;
import org.openinfinity.cloud.domain.MachineTypeClusterTypeRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)

public class MachineTypeRepositoryJdbcImplTest {
	private int machineTypeIdSmall = 10;
	private int machineTypeIdMedium = 11;
	private int machineTypeIdLarge = 12;	
	private int clusterTypeIdPortal = 0;
	private int clusterTypeIdService = 0;
	private String orgName = "test organization";

	@Autowired
	@Qualifier("cloudDataSource")
	DataSource ds;
	
	@Autowired
	private MachineTypeRepository machineTypeRepository;
	
    @Before
    public void setUp() {
		SimpleJdbcInsert insert = new SimpleJdbcInsert(ds).withTableName("machine_type_tbl");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("id", machineTypeIdSmall);
		parameters.put("name", "small");
		parameters.put("spec", "small instance");
		insert.execute(parameters);

		parameters = new HashMap<String,Object>();
		parameters.put("id", machineTypeIdMedium);
		parameters.put("name", "medium");
		parameters.put("spec", "medium instance");
		insert.execute(parameters);

		parameters = new HashMap<String,Object>();
		parameters.put("id", machineTypeIdLarge);
		parameters.put("name", "large");
		parameters.put("spec", "large instance");
		insert.execute(parameters);		
		
		insert = new SimpleJdbcInsert(ds).withTableName("cluster_type_tbl").usingGeneratedKeyColumns("id");
		parameters = new HashMap<String,Object>();
		parameters.put("name", "portal");
		parameters.put("title", "portal server");
		Number newId = insert.executeAndReturnKey(parameters);
		clusterTypeIdPortal = newId.intValue();
		
		parameters = new HashMap<String,Object>();
		parameters.put("name", "service");
		parameters.put("title", "service server");
		newId = insert.executeAndReturnKey(parameters);
		clusterTypeIdService = newId.intValue();
		
		insert = new SimpleJdbcInsert(ds).withTableName("machine_type_cluster_type_rule_tbl");
		parameters = new HashMap<String,Object>();
		parameters.put("machine_type_id", machineTypeIdSmall);
		parameters.put("cluster_type_id", clusterTypeIdPortal);
		parameters.put("allowed", false);
		insert.execute(parameters);

		insert = new SimpleJdbcInsert(ds).withTableName("machine_type_cluster_type_rule_tbl");
		parameters = new HashMap<String,Object>();
		parameters.put("machine_type_id", machineTypeIdMedium);
		parameters.put("cluster_type_id", clusterTypeIdPortal);
		parameters.put("allowed", true);
		insert.execute(parameters);

		insert = new SimpleJdbcInsert(ds).withTableName("machine_type_cluster_type_rule_tbl");
		parameters = new HashMap<String,Object>();
		parameters.put("machine_type_id", machineTypeIdSmall);
		parameters.put("cluster_type_id", clusterTypeIdService);
		parameters.put("allowed", true);
		insert.execute(parameters);

		insert = new SimpleJdbcInsert(ds).withTableName("machine_type_cluster_type_rule_tbl");
		parameters = new HashMap<String,Object>();
		parameters.put("machine_type_id", machineTypeIdMedium);
		parameters.put("cluster_type_id", clusterTypeIdService);
		parameters.put("allowed", true);
		insert.execute(parameters);
		
		insert = new SimpleJdbcInsert(ds).withTableName("acl_machine_type_tbl");
		parameters = new HashMap<String,Object>();
		parameters.put("org_name", orgName);
		parameters.put("machine_type_id", machineTypeIdSmall);
		insert.execute(parameters);
		
		insert = new SimpleJdbcInsert(ds).withTableName("acl_machine_type_tbl");
		parameters = new HashMap<String,Object>();
		parameters.put("org_name", orgName);
		parameters.put("machine_type_id", machineTypeIdMedium);
		insert.execute(parameters);

		insert = new SimpleJdbcInsert(ds).withTableName("acl_cluster_type_tbl");
		parameters = new HashMap<String,Object>();
		parameters.put("org_name", orgName);
		parameters.put("cluster_id", clusterTypeIdPortal);
		insert.execute(parameters);

		insert = new SimpleJdbcInsert(ds).withTableName("acl_cluster_type_tbl");
		parameters = new HashMap<String,Object>();
		parameters.put("org_name", orgName);
		parameters.put("cluster_id", clusterTypeIdService);
		insert.execute(parameters);		
	}
	
    @After
    public void tearDown() {
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    	jdbcTemplate.execute("delete from acl_machine_type_tbl");
    	jdbcTemplate.execute("delete from acl_cluster_type_tbl");
    	jdbcTemplate.execute("delete from machine_type_cluster_type_rule_tbl");
    	jdbcTemplate.execute("delete from machine_type_tbl");
    	jdbcTemplate.execute("delete from cluster_type_tbl");
    }

	@Test
	public void testMachineTypeClusterTypeRuleRepository() {						
		List<String> userOrganizations = new ArrayList<String>();
		userOrganizations.add(orgName);
		Collection<MachineType> machineTypes = machineTypeRepository.getMachineTypes(userOrganizations);		
		assertEquals(machineTypes.size(), 2);
		
		for (MachineType machineType : machineTypes) {
			System.out.println(machineType.getName());
			List<MachineTypeClusterTypeRule> rules = machineType.getCompatibleClusterTypes();
			for (MachineTypeClusterTypeRule rule: rules) {
				System.out.println(rule.getClusterTypeId());
			}
			if (machineType.getName().equals("small")) {
				assertEquals(rules.size(), 1);
			}
			else if (machineType.getName().equals("medium")) {
				assertEquals(rules.size(), 2);
			}
			else {
				assertTrue("Invalid machineType received " + machineType.getName(), false);
			}
		}
	}
}
