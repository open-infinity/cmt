package org.openinfinity.cloud.application.admin.controller;

import java.util.LinkedList;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.portlet.MockResourceRequest;
import org.springframework.mock.web.portlet.MockResourceResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.auth.AWSCredentials;
import com.liferay.portal.model.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import org.openinfinity.cloud.service.administrator.AuthorizationRoutingService;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.openinfinity.cloud.service.administrator.JobService;
import org.openinfinity.cloud.service.administrator.MachineService;
import org.openinfinity.cloud.service.liferay.LiferayService;
import org.openinfinity.cloud.service.scaling.ScalingRuleService;
import org.openinfinity.cloud.application.admin.controller.ClusterController;
import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.Job;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.core.util.ExceptionUtil;

/**
 * Tests for ClusterController
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@ContextConfiguration(locations={"/cluster-controller-test-config.xml", "file:src/main/webapp/WEB-INF/cloudadmin-portlet.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ClusterControllerTest {

    @InjectMocks
    @Autowired
    @Qualifier("clusterController")
    private ClusterController clusterController;
    
    @Autowired
	@Qualifier("jobService")
	private JobService jobService;
	
	@Autowired
	@Qualifier("scalingRuleService")
	private ScalingRuleService scalingRuleService;
	
	@Autowired
	@Qualifier("clusterService")
	private ClusterService clusterService;
    
    @Mock
    private LiferayService mockLiferayService;
    
    @Mock
    private User mockUser;
    	
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    private  ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    public void testScaleClusterManualScaleNewSizeNotEqualCurrentSize() {     
        MockResourceResponse response = new MockResourceResponse();
        MockResourceRequest request = new MockResourceRequest(); 
        
        List <Long> organizationIds = new LinkedList<Long>();
        organizationIds.add((long) 1);
        organizationIds.add((long) 2);
        
        when(mockUser.getLastName()).thenReturn("lastname");
        when(mockLiferayService.getUser(request, response)).thenReturn(mockUser);  
        when(mockLiferayService.getOrganizationIds(mockUser)).thenReturn(organizationIds);

        // Request manual scaling, with new size[20] != current cluster size[10]
		try {
			clusterController.scaleCluster(
					request,
					response,
					1, 				// clusterId
					true, 			// periodicScalingOn
					true, 			// scheduledScalingOn
					200, 			// maxNumberOfMachinesPerCluster
					1, 				// minNumberOfMachinesPerCluster
					(float) 0.9, 	// maxLoad
					(float) 0.1, 	// minLoad
					0, 				// periodFrom
					0,              // periodTo
					5,              // scheduledClusterSize
					true,           // manualScaling
					20              // manualScalingNewSize
					);
			
			// Make sure that job is correctly created
			Job job = jobService.getJob(1);
			assertNotNull(job);
        	assertEquals("scale_cluster", job.getJobType());
            assertEquals("1,20", job.getServices()); 
            
            // Make sure that scaling rule is created
            ScalingRule scalingRule = scalingRuleService.getRule(1);
            assertEquals(200, scalingRule.getMaxNumberOfMachinesPerCluster());
            
			// Simulate request to scale cluster to size 10, and change 
			// periodic and scheduled scaling settings
			clusterController.scaleCluster(
					request,
					response,
					1, 				// clusterId
					false, 			// periodicScalingOn
					false, 			// scheduledScalingOn
					200, 			// maxNumberOfMachinesPerCluster
					1, 				// minNumberOfMachinesPerCluster
					(float) 0.9, 	// maxLoad
					(float) 0.1, 	// minLoad
					0, 				// periodFrom
					0,              // periodTo
					5,              // scheduledClusterSize
					true,           // manualScaling
					10              // manualScalingNewSize
					);
			
			// Make sure that no new job is created
			job = jobService.getJob(2);
			assertNull(job);
			
			// Make sure that scaling rule got updated
			ScalingRule scalingRuleFinal = scalingRuleService.getRule(1);
            assertEquals(false, scalingRuleFinal.isPeriodicScalingOn());
            assertEquals(false, scalingRuleFinal.isScheduledScalingOn());            
        }
        catch (Exception e){
            ExceptionUtil.throwSystemException(e);   
        }
    }
}
