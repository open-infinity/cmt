package org.openinfinity.cloud.application.admin.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.application.admin.controller.CloudAdminController;
import org.openinfinity.cloud.domain.*;
import org.openinfinity.cloud.domain.repository.administrator.InstanceRepository;
import org.openinfinity.cloud.domain.repository.administrator.JobRepository;
import org.openinfinity.cloud.util.LiferayServiceMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.portlet.MockResourceRequest;
import org.springframework.mock.web.portlet.MockResourceResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: tapantim
 * Date: 20.2.2013
 * Time: 8:10
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"/cloud-admin-test-config.xml", "file:src/main/webapp/WEB-INF/cloudadmin-portlet.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CloudAdminControllerTest {

    private static final String AWS_PROVIDER = "Amazon";
    private static final String EUCA_PROVIDER = "Eucalyptus";

    private static final int AWS_ID = 0;
    private static final int EUCA_ID = 1;

    private static final String EUCA_ZONE_NAME = "dev-cluster01";
    private static final String AWS_ZONE1_NAME = "aws-cluster01";
    private static final String AWS_ZONE2_NAME = "aws-cluster02";

    private static final String MTYPE_SMALL_NAME = "Small";
    private static final String MTYPE_MEDIUM_NAME = "Medium";
    private static final String MTYPE_LARGE_NAME = "Large";
    private static final String MTYPE_XLARGE_NAME = "XLarge";
    private static final String MTYPE_XXLARGE_NAME = "XXLarge";

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("cloudadminController")
    private CloudAdminController adminController;

    @Autowired
    @Qualifier("liferayService")
    private LiferayServiceMock liferayService;

    private  ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetCloudZones() {
        try {
            liferayService.mockUserWithOrganizations("TOAS");
            MockResourceResponse response = new MockResourceResponse();
            adminController.getCloudZones(new MockResourceRequest(), response, EUCA_ID);
            List<AvailabilityZone> zones = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<AvailabilityZone>>(){});
            assertEquals(1, zones.size());
            assertEquals("dev-cluster01", zones.get(0).getName());

            liferayService.mockUserWithOrganizations("TOAS");
            response = new MockResourceResponse();
            adminController.getCloudZones(new MockResourceRequest(), response, AWS_ID);
            zones = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<AvailabilityZone>>(){});
            assertEquals(2, zones.size());
            assertEquals(AWS_ZONE1_NAME, zones.get(0).getName());
            assertEquals(AWS_ZONE2_NAME, zones.get(1).getName());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetCloudProviders() {
        try {
            // Test that only resources specified in acl are returned
            liferayService.mockUserWithOrganizations("TOAS");
            MockResourceResponse response = new MockResourceResponse();
            adminController.getCloudProviders(new MockResourceRequest(), response);
            List<CloudProvider> providers = objectMapper.readValue(response.getContentAsString(),
                                            new TypeReference<List<CloudProvider>>(){});
            assertEquals(2, providers.size());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetMachineTypes() {
        liferayService.mockUserWithOrganizations("TOAS");
        MockResourceResponse response = new MockResourceResponse();
        List<MachineType> machineTypes;
        try {
            adminController.getMachineTypes(new MockResourceRequest(), response);
            machineTypes = objectMapper.readValue(response.getContentAsString(), 
                           new TypeReference<List<MachineType>>(){});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(5, machineTypes.size());
        MachineType mt = machineTypes.get(0);
        assertEquals(MTYPE_SMALL_NAME, mt.getName());
        mt = machineTypes.get(4);
        assertEquals(MTYPE_XXLARGE_NAME, mt.getName());
    }

    @Test
    public void testGetClusterTypes() {
        liferayService.mockUserWithOrganizations("TOAS");
        MockResourceResponse response = new MockResourceResponse();
        List<ClusterType> clusterTypes;
        try {
            adminController.getClusterTypes(new MockResourceRequest(), response);
            clusterTypes = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<ClusterType>>(){});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(9, clusterTypes.size());
    }
}
