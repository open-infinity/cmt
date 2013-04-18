package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;

import java.util.List;
import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.MachineTypeClusterTypeRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class MachineTypeClusterTypeRuleRepositoryJdbcImplTest {

	@Autowired
	@Qualifier("cloudDataSource")
	DataSource ds;
	
	@Autowired
	private MachineTypeClusterTypeRuleRepository machineTypeClusterTypeRuleRepository;
	
	@Test
	public void testMachineTypeClusterTypeRuleRepository() {
		MachineTypeClusterTypeRule rule;
		rule = new MachineTypeClusterTypeRule();
		rule.setMachineTypeId(1);
		rule.setClusterTypeId(2);
		rule.setAllowed(true);
		machineTypeClusterTypeRuleRepository.addMachineTypeClusterTypeRule(rule);
		
		rule = new MachineTypeClusterTypeRule();
		rule.setMachineTypeId(1);
		rule.setClusterTypeId(3);
		rule.setAllowed(false);
		machineTypeClusterTypeRuleRepository.addMachineTypeClusterTypeRule(rule);

		rule = new MachineTypeClusterTypeRule();
		rule.setMachineTypeId(1);
		rule.setClusterTypeId(4);
		rule.setAllowed(true);
		machineTypeClusterTypeRuleRepository.addMachineTypeClusterTypeRule(rule);
		
		rule = new MachineTypeClusterTypeRule();
		rule.setMachineTypeId(2);
		rule.setClusterTypeId(4);
		rule.setAllowed(true);
		machineTypeClusterTypeRuleRepository.addMachineTypeClusterTypeRule(rule);
						
		List<MachineTypeClusterTypeRule> rules = machineTypeClusterTypeRuleRepository.getMachineTypeClusterTypeRules(1);		
		assertEquals(rules.size(), 2);
		assertEquals(rules.get(0).getMachineTypeId(), 1);
		assertEquals(rules.get(0).getClusterTypeId(), 2);
		assertEquals(rules.get(1).getMachineTypeId(), 1);
		assertEquals(rules.get(1).getClusterTypeId(), 4);
	}

}
