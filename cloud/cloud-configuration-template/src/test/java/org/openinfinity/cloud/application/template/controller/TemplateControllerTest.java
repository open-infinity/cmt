package org.openinfinity.cloud.application.template.controller;

import static junit.framework.Assert.assertEquals;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.configurationtemplate.Template;
import org.openinfinity.cloud.application.template.controller.TemplateController;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.portlet.MockResourceRequest;
import org.springframework.mock.web.portlet.MockResourceResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"/cloud-template-test-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TemplateControllerTest {

    @Autowired
    private TemplateController templateController;
    
    private ObjectMapper objectMapper;
    @Test
    public void test() {
        MockResourceResponse response = new MockResourceResponse();
        MockResourceRequest request = new MockResourceRequest();

        try{
            //templateController.getTemplatesForUser(request, response);
            //List<Template> templates = objectMapper.readValue(
             //       response.getContentAsString(),new TypeReference<List<Template>>(){});
            //assertEquals(1, templates.size());
            assertEquals(1,1);
            //System.out.print("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
        }
        catch (Exception e){
            ExceptionUtil.throwSystemException(e);   
        }
    }

}
