package org.openinfinity.cloud.application.admin.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private static final String CTYPE_JBOSS_PORTAL_NAME = "jbossportal";
    private static final String CTYPE_JBOSS_SERVICE_NAME = "jbossservice";

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
            liferayService.mockUserWithOrganizations("OPPYATOAS");
            MockResourceResponse response = new MockResourceResponse();
            adminController.getCloudZones(new MockResourceRequest(), response, EUCA_ID);
            List<AvailabilityZone> zones = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<AvailabilityZone>>(){});
            assertEquals(1, zones.size());
            assertEquals("dev-cluster01", zones.get(0).getName());

            liferayService.mockUserWithOrganizations("TOAS", "OPPYATOAS");
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
            liferayService.mockUserWithOrganizations("OPPYATOAS");
            MockResourceResponse response = new MockResourceResponse();
            adminController.getCloudProviders(new MockResourceRequest(), response);
            List<CloudProvider> providers = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<CloudProvider>>(){});
            assertEquals(1, providers.size());
            CloudProvider provider = providers.get(0);
            assertEquals(EUCA_PROVIDER, provider.getName());
            assertEquals(1, provider.getId());

            // Test that only distinct providers are returned, providers are returned by id order
            liferayService.mockUserWithOrganizations("Tieto Finland", "OPPYATOAS", "TOAS");
            response = new MockResourceResponse();
            adminController.getCloudProviders(new MockResourceRequest(), response);
            providers = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<CloudProvider>>(){});
            assertEquals(2, providers.size());
            CloudProvider amazon = providers.get(0);
            assertEquals(AWS_PROVIDER, amazon.getName());
            assertEquals(0, amazon.getId());
            CloudProvider euca = providers.get(1);
            assertEquals(EUCA_PROVIDER, euca.getName());
            assertEquals(1, euca.getId());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetMachineTypes() {
        liferayService.mockUserWithOrganizations("OPPYATOAS");
        MockResourceResponse response = new MockResourceResponse();
        List<MachineType> machineTypes;
        try {
            adminController.getMachineTypes(new MockResourceRequest(), response);
            machineTypes = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<MachineType>>(){});
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
        liferayService.mockUserWithOrganizations("OPPYATOAS");
        MockResourceResponse response = new MockResourceResponse();
        List<ClusterType> clusterTypes;
        try {
            adminController.getClusterTypes(new MockResourceRequest(), response);
            clusterTypes = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<ClusterType>>(){});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(2, clusterTypes.size());
        ClusterType ct = clusterTypes.get(0);
        assertEquals(CTYPE_JBOSS_PORTAL_NAME, ct.getName());
        ct = clusterTypes.get(1);
        assertEquals(CTYPE_JBOSS_SERVICE_NAME, ct.getName());
    }


    @Test
    public void testAddJBossPortalInstance() {
        liferayService.mockUserWithOrganizations("OPPYATOAS");
        MockResourceResponse response = new MockResourceResponse();
        Map<String, String> requestMap = new HashMap<String, String>();

        requestMap.put("cloudtype", "1");
        requestMap.put("instancename", "inst1");
        requestMap.put("zone", EUCA_ZONE_NAME);
        requestMap.put("jbossportal", "true");
        requestMap.put("jbossportalclustersize", "1");
        requestMap.put("jbossportalmachinesize", "0");
        requestMap.put("jbossportalimagetype", "0");
        requestMap.put("jbossportalesb", "false");
        requestMap.put("jbossportalvolumesize", "0");
        requestMap.put("jbossportalsolr", "false");
        requestMap.put("jbossportalliveinstance", "true");
        requestMap.put("jbossportaldatasourceurl", "testurl");
        requestMap.put("jbossportaldatasourceuser", "testuser");
        requestMap.put("jbossportaldatasourcepassword", "testpassword");

        // TODO, UI shouldn't pass parameters for unselected platforms
        requestMap.put("jbossservice", "false");
        requestMap.put("jbossserviceclustersize", "0");
        requestMap.put("jbossservicemachinesize", "0");
        requestMap.put("jbossserviceesb", "false");
        requestMap.put("jbossservicevolumesize", "0");
        requestMap.put("jbossservicedatasourceurl", "");

        requestMap.put("jbosssolr", "false");
        requestMap.put("jbosssolrclustersize", "0");
        requestMap.put("jbosssolrmachinesize", "0");
        requestMap.put("jbosssolreesb", "false");
        requestMap.put("jbosssolrvolumesize", "0");
        requestMap.put("jbosssolrdatasourceurl", "");

        try {
            adminController.addInstance(new MockResourceRequest(), response, requestMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Instance inst1 = instanceRepository.getInstance("inst1");
        int inst1id = inst1.getInstanceId();
        assertEquals(EUCA_ID, inst1.getCloudType());
        assertEquals("inst1", inst1.getName());
        assertEquals(EUCA_ZONE_NAME, inst1.getZone());
        List<InstanceParameter> parameters = inst1.getParameters();
        assertEquals(4, parameters.size());
        int foundParams = 0;
        for (InstanceParameter p : parameters) {
            if ("portal_live".equals(p.getKey())) {
                assertEquals("true", p.getValue());
                ++foundParams;
            } else if ("portal_datasource_url".equals(p.getKey())) {
                assertEquals("testurl", p.getValue());
                ++foundParams;
            } else if ("portal_datasource_user".equals(p.getKey())) {
                assertEquals("testuser", p.getValue());
                ++foundParams;
            } else if ("portal_datasource_password".equals(p.getKey())) {
                assertEquals("testpassword", p.getValue());
                ++foundParams;
            } else {
                fail("JobPlatformParameters contains unknown parameter: " + p.getKey());
            }
        }
        assertEquals("expected job parameters do not match with actual", 4, foundParams);

        List<Job> jobs = jobRepository.getJobsForInstance(inst1id);
        assertEquals(1, jobs.size());
        Job inst1Job = jobs.get(0);
        assertEquals("jboss_portal_platform,1,0,0,null", inst1Job.getServices());

    }

    @Test
    public void testAddJBossPortalInstanceWithSolr() {
        liferayService.mockUserWithOrganizations("OPPYATOAS");
        MockResourceResponse response = new MockResourceResponse();
        Map<String, String> requestMap = new HashMap<String, String>();

        requestMap.put("cloudtype", "1");
        requestMap.put("instancename", "inst2");
        requestMap.put("zone", EUCA_ZONE_NAME);
        requestMap.put("jbossportal", "true");
        requestMap.put("jbossportalclustersize", "1");
        requestMap.put("jbossportalmachinesize", "0");
        requestMap.put("jbossportalimagetype", "0");
        requestMap.put("jbossportalesb", "false");
        requestMap.put("jbossportalvolumesize", "0");
        requestMap.put("jbossportalsolr", "true");
        requestMap.put("jbossportalliveinstance", "true");
        requestMap.put("jbossportaldatasourceurl", "testurl");
        requestMap.put("jbossportaldatasourceuser", "testuser");
        requestMap.put("jbossportaldatasourcepassword", "testpassword");

        requestMap.put("jbosssolr", "true");
        requestMap.put("jbosssolrclustersize", "1");
        requestMap.put("jbosssolrmachinesize", "0");
        requestMap.put("jbosssolreesb", "false");
        requestMap.put("jbosssolrvolumesize", "0");
        requestMap.put("jbosssolrdatasourceurl", "");
        requestMap.put("jbosssolrimagetype", "0");

        // TODO, UI shouldn't pass parameters for unselected platforms
        requestMap.put("jbossservice", "false");
        requestMap.put("jbossserviceclustersize", "0");
        requestMap.put("jbossservicemachinesize", "0");
        requestMap.put("jbossserviceesb", "false");
        requestMap.put("jbossservicevolumesize", "0");
        requestMap.put("jbossservicedatasourceurl", "");

        try {
            adminController.addInstance(new MockResourceRequest(), response, requestMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Instance inst2 = instanceRepository.getInstance("inst2");
        int inst2id = inst2.getInstanceId();
        assertEquals(EUCA_ID, inst2.getCloudType());
        assertEquals("inst2", inst2.getName());
        assertEquals(EUCA_ZONE_NAME, inst2.getZone());
        List<InstanceParameter> parameters = inst2.getParameters();
        assertEquals(5, parameters.size());
        int foundParams = 0;
        for (InstanceParameter p : parameters) {
            if ("portal_solr".equals(p.getKey())) {
                assertEquals("true", p.getValue());
                ++foundParams;
            } else if ("portal_live".equals(p.getKey())) {
                assertEquals("true", p.getValue());
                ++foundParams;
            } else if ("portal_datasource_url".equals(p.getKey())) {
                assertEquals("testurl", p.getValue());
                ++foundParams;
            } else if ("portal_datasource_user".equals(p.getKey())) {
                assertEquals("testuser", p.getValue());
                ++foundParams;
            } else if ("portal_datasource_password".equals(p.getKey())) {
                assertEquals("testpassword", p.getValue());
                ++foundParams;
            } else {
                fail("JobPlatformParameters contains unknown parameter: " + p.getKey());
            }
        }
        assertEquals("expected job parameters do not match with actual", 5, foundParams);

        List<Job> jobs = jobRepository.getJobsForInstance(inst2id);
        assertEquals(1, jobs.size());
        Job inst2Job = jobs.get(0);
        System.out.println(inst2Job.getServices());
        assertEquals("jboss_portal_platform,1,0,0,null,jboss_solr_platform,1,0,0,null", inst2Job.getServices());

    }
}
