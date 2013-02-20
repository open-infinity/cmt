package org.openinfinity.cloud.application.admin.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.AvailabilityZone;
import org.openinfinity.cloud.util.LiferayService;
import org.openinfinity.cloud.util.LiferayServiceMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.portlet.MockResourceRequest;
import org.springframework.mock.web.portlet.MockResourceResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: tapantim
 * Date: 20.2.2013
 * Time: 8:10
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"/test-context.xml", "file:src/main/webapp/WEB-INF/cloudadmin-portlet.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CloudAdminControllerTest {

    @Autowired
    @Qualifier("cloudadminController")
    private CloudAdminController adminController;

    @Autowired
    @Qualifier("liferayService")
    private LiferayServiceMock liferayService;

    private  ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        liferayService.addUser("test", 1, "OPPYATOAS");
    }

    @Test
    public void testGetCloudZones() {
        try {
            MockResourceResponse response = new MockResourceResponse();
            adminController.getCloudZones(new MockResourceRequest(), response, 1);
            List<AvailabilityZone> zones = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<AvailabilityZone>>(){});
            assertEquals(1, zones.size());
            assertEquals("dev-cluster01", zones.iterator().next().getName());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testAddInstance() {

    }
}
