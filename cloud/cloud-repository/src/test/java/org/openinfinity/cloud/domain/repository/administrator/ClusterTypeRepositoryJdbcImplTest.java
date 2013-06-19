package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.ClusterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ClusterTypeRepositoryJdbcImplTest {

	@Autowired
	private ClusterTypeRepository clusterTypeRepository;
	
	@Test
	public void testClusterTypeRepository() {
		List<String> params = new ArrayList<String>();
		params.add("TOAS");
		Collection<ClusterType> clusterTypes = clusterTypeRepository.getAvailableClusterTypes(params);
		
		ClusterType clusterType = clusterTypes.iterator().next();
		assertEquals(1, clusterType.getId());
		assertEquals(1, clusterType.getConfigurationId());
		assertEquals("ig", clusterType.getName());
		assertEquals("Identity Gateway", clusterType.getTitle());
		assertEquals(-1, clusterType.getDependency());
		assertEquals(1, clusterType.getMinMachines());
		assertEquals(12, clusterType.getMaxMachines());
		assertEquals(0, clusterType.getMinReplicationMachines());
		assertEquals(0, clusterType.getMaxReplicationMachines());
	}

}
