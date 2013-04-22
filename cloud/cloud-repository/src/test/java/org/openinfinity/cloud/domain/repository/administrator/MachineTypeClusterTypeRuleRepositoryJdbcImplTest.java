package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.MachineTypeClusterTypeRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class MachineTypeClusterTypeRuleRepositoryJdbcImplTest {
	private static int machineTypeIdSmall = 10;
	private static int machineTypeIdMedium = 11;
	private static int clusterTypeIdPortal = 0;
	private static int clusterTypeIdService = 0;
	private static int clusterTypeIdDatabase = 0;

	@Autowired
	@Qualifier("cloudDataSource")
	DataSource ds;
	
	@Autowired
	private MachineTypeClusterTypeRuleRepository machineTypeClusterTypeRuleRepository;
	
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
		
		insert = new SimpleJdbcInsert(ds).withTableName("cluster_type_tbl").usingGeneratedKeyColumns("id");
		parameters = new HashMap<String,Object>();
		parameters.put("name", "portal");
		parameters.put("title", "portal server");
		Number newId = insert.executeAndReturnKey(parameters);
		System.out.println("Portal server id: " + newId);
		clusterTypeIdPortal = newId.intValue();
		
		parameters = new HashMap<String,Object>();
		parameters.put("name", "service");
		parameters.put("title", "service server");
		newId = insert.executeAndReturnKey(parameters);
		System.out.println("Service server id: " + newId);
		clusterTypeIdService = newId.intValue();
		
		parameters = new HashMap<String,Object>();
		parameters.put("name", "database");
		parameters.put("title", "database server");
		newId = insert.executeAndReturnKey(parameters);
		System.out.println("Database server id: " + newId);
		clusterTypeIdDatabase = newId.intValue();		
	}
	
    @After
    public void tearDown() {
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    	jdbcTemplate.execute("delete from machine_type_cluster_type_rule_tbl");
    	jdbcTemplate.execute("delete from machine_type_cluster_type_rule_tbl");
    	jdbcTemplate.execute("delete from machine_type_tbl");
    	jdbcTemplate.execute("delete from cluster_type_tbl");    	
    }

	@Test
	public void testMachineTypeClusterTypeRuleRepository() {
		MachineTypeClusterTypeRule rule;
		rule = new MachineTypeClusterTypeRule();
		rule.setMachineTypeId(machineTypeIdSmall);
		rule.setClusterTypeId(clusterTypeIdPortal);
		rule.setAllowed(true);
		machineTypeClusterTypeRuleRepository.addMachineTypeClusterTypeRule(rule);
		
		rule = new MachineTypeClusterTypeRule();
		rule.setMachineTypeId(machineTypeIdSmall);
		rule.setClusterTypeId(clusterTypeIdDatabase);
		rule.setAllowed(false);
		machineTypeClusterTypeRuleRepository.addMachineTypeClusterTypeRule(rule);

		rule = new MachineTypeClusterTypeRule();
		rule.setMachineTypeId(machineTypeIdSmall);
		rule.setClusterTypeId(clusterTypeIdService);
		rule.setAllowed(true);
		machineTypeClusterTypeRuleRepository.addMachineTypeClusterTypeRule(rule);
		
		rule = new MachineTypeClusterTypeRule();
		rule.setMachineTypeId(machineTypeIdMedium);
		rule.setClusterTypeId(clusterTypeIdService);
		rule.setAllowed(true);
		machineTypeClusterTypeRuleRepository.addMachineTypeClusterTypeRule(rule);
						
		List<MachineTypeClusterTypeRule> rules = machineTypeClusterTypeRuleRepository.getMachineTypeClusterTypeRules(machineTypeIdSmall);		
		assertEquals(rules.size(), 2);
		assertEquals(rules.get(0).getMachineTypeId(), machineTypeIdSmall);
		assertEquals(rules.get(0).getClusterTypeId(), clusterTypeIdPortal);
		assertEquals(rules.get(1).getMachineTypeId(), machineTypeIdSmall);
		assertEquals(rules.get(1).getClusterTypeId(), clusterTypeIdService);
	}

}
