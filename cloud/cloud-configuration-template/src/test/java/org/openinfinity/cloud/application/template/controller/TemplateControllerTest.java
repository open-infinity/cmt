package org.openinfinity.cloud.application.template.controller;

import static junit.framework.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.portlet.MockResourceRequest;
import org.springframework.mock.web.portlet.MockResourceResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.liferay.portal.model.User;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.openinfinity.cloud.domain.configurationtemplate.Template;
import org.openinfinity.cloud.service.liferay.LiferayService;
import org.openinfinity.cloud.application.template.controller.TemplateController;
import org.openinfinity.core.util.ExceptionUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

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
            templateController.getTemplatesForUser(request, response);
            System.out.println(response.getContentAsString());
            Set<Template> templates = objectMapper.readValue(
                   response.getContentAsString(),new TypeReference<Set<Template>>(){});
            assertEquals(1, templates.size()); 
        }
        catch (Exception e){
            ExceptionUtil.throwSystemException(e);   
        }
    }

}
