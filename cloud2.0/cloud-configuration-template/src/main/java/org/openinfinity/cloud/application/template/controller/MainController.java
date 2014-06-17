package org.openinfinity.cloud.application.template.controller;

import com.liferay.portal.model.User;
import org.apache.log4j.Logger;
import org.openinfinity.cloud.common.annotation.Authenticated;
import org.openinfinity.cloud.common.web.LiferayService;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationTemplate;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationPackage;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationElementService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationTemplateService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationModuleService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationPackageService;
import org.openinfinity.cloud.util.collection.ListUtil;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("VIEW")
public class MainController extends AbstractController{

    private static final String GET_TEMPLATES = "getTemplates";
    private static final String GET_ELEMENTS = "getElements";
    private static final String GET_MODULES = "getModules";
    private static final String GET_PACKAGES = "getPackages";

    @Autowired
    private ConfigurationElementService elementService;

    @Autowired
    private ConfigurationTemplateService templateService;

    @Autowired
    private InstallationModuleService moduleService;

    @Autowired
    private InstallationPackageService packageService;

    @Autowired
    private LiferayService liferayService;

    public MainController() {
    }

    private static final Logger LOG = Logger.getLogger(MainController.class.getName());

    @RenderMapping
    public String showView(RenderRequest request, RenderResponse response) {
        User user = liferayService.getUser(request);
        if(user == null) {
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_USER_NOT_LOGGED_IN);
            return "notlogged";
        }
        return "main";
    }

    @Authenticated
    @ResourceMapping(GET_ELEMENTS)
    public void getElements(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
        try {
            List<ConfigurationElement> elements = (List<ConfigurationElement>)elementService.loadAll();
            createOnePageResponse(response, elements, page, rows);
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(GET_TEMPLATES)
    public void getTemplates(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows)
            throws Exception {
        try {
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            List<Long> organizationIds = liferayService.getOrganizationIds(user);
            List<ConfigurationTemplate> templates = templateService.loadAllForOrganizations(organizationIds);
            createOnePageResponse(response, templates, page, rows);
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_MODULES)
    public void getModules(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
        try {
            List<InstallationModule> modules = (List<InstallationModule>)moduleService.loadAll();
            createOnePageResponse(response, modules, page, rows);
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_PACKAGES)
    public void getPackages(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
        try {
            List<InstallationPackage> packages =  (List<InstallationPackage>)packageService.loadAll();
            createOnePageResponse(response, packages, page, rows);
        }
        catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    /*
     * Slices a subset from list, the result fits into one jqGrid page.
     */
    private <T> void createOnePageResponse(ResourceResponse response, List<T> items, int page, int rows) throws IOException {
        int records = items.size();
        int mod = records % rows;
        int totalPages = records / rows;
        if (mod > 0) totalPages++;
        List<T> itemsCopy = new LinkedList<T>();
        itemsCopy.addAll(items);
        List<T> onePage = ListUtil.sliceList(page, rows, itemsCopy);
        SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
    }

}
