package org.openinfinity.cloud.application.template.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.portlet.MockResourceRequest;
import org.springframework.mock.web.portlet.MockResourceResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.liferay.portal.model.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import org.openinfinity.cloud.service.liferay.LiferayService;
import org.openinfinity.cloud.application.template.controller.TemplateController;
import org.openinfinity.core.util.ExceptionUtil;

/**
 * Tests for TemplateController
 * 
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@ContextConfiguration(locations={"/cloud-template-test-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TemplateControllerTest {

    @InjectMocks
    @Autowired
    @Qualifier("configurationTemplateController")
    private TemplateController templateController;
    
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
    public void testGetAllTemplatesForUser() {     
        MockResourceResponse response = new MockResourceResponse();
        MockResourceRequest request = new MockResourceRequest();
        
        List <Long> organizationIds = new LinkedList<Long>();
        organizationIds.add((long) 1);
        organizationIds.add((long) 2);
        
        when(mockUser.getLastName()).thenReturn("Bartonicek");
        when(mockLiferayService.getUser(request, response)).thenReturn(mockUser);  
        when(mockLiferayService.getOrganizationIds(mockUser)).thenReturn(organizationIds);  
            
        try{
            templateController.getTemplatesForUser(request, response, 1, 1);
            JsonNode rootNode = objectMapper.readValue(response.getContentAsString(), 
                                             JsonNode.class);
            
            int templatesExpected = 1;
            int rowsFound = 0;        
            for (@SuppressWarnings("unused") JsonNode node : rootNode.path("rows")) {
                ++rowsFound;
            }
            assertEquals(templatesExpected, rowsFound); 
        }
        catch (Exception e){
            ExceptionUtil.throwSystemException(e);   
        }
    }

}
