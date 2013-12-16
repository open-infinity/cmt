package org.openinfinity.cloud.application.template.controller;

import com.liferay.portal.model.User;
import org.apache.log4j.Logger;
import org.openinfinity.cloud.annotation.Authenticated;
import org.openinfinity.cloud.comon.web.LiferayService;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationElement;
import org.openinfinity.cloud.domain.configurationtemplate.entity.ConfigurationTemplate;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationModule;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationPackage;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationElementService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.ConfigurationTemplateService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationModuleService;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationPackageService;
import org.openinfinity.cloud.util.collection.ListUtil;
import org.openinfinity.cloud.util.serialization.JsonDataWrapper;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.Collection;
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

    // TODO: these functions are the same. Generalization needed.

    @Authenticated
    @ResourceMapping(GET_ELEMENTS)
    public void getElements(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
        try {
            Collection<ConfigurationElement> templates = elementService.loadAll();
            int records = templates.size();
            int mod = records % rows;
            int totalPages = records / rows;
            if (mod > 0) totalPages++;
            List<ConfigurationElement> onePage = ListUtil.sliceList(page, rows, new LinkedList<ConfigurationElement>(templates));
            SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @ResourceMapping(GET_TEMPLATES)
    public void getTemplates(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows)
            throws Exception {
        try {
            LOG.debug("ENTER getTemplatesForUser, page=" + page + ",rows=" + rows);
            User user = liferayService.getUser(request, response);
            if (user == null) return;
            List<Long> organizationIds = liferayService.getOrganizationIds(user);
            List<ConfigurationTemplate> templates = templateService.loadAllForOrganizations(organizationIds);
            int records = templates.size();
            int mod = records % rows;
            int totalPages = records / rows;
            if (mod > 0) totalPages++;
            List<ConfigurationTemplate> onePage = ListUtil.sliceList(page, rows, new LinkedList<ConfigurationTemplate>(templates));
            SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_MODULES)
    public void getModules(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
        try {
            LOG.debug("ENTER getTemplatesForUser, page=" + page + ",rows=" + rows);
            Collection<InstallationModule> modules = moduleService.loadAll();
            LOG.debug("modules=" + modules);

            int records = modules.size();
            int mod = records % rows;
            int totalPages = records / rows;
            if (mod > 0) totalPages++;
            List<InstallationModule> onePage = ListUtil.sliceList(page, rows, new LinkedList<InstallationModule>(modules));
            LOG.debug("onePage=" + onePage);

            SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
            LOG.debug("EXIT");

        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(GET_PACKAGES)
    public void getPackages(ResourceRequest request, ResourceResponse response, @RequestParam("page") int page, @RequestParam("rows") int rows) throws Exception {
        try {
            LOG.debug("ENTER getTemplatesForUser, page=" + page + ",rows=" + rows);
            Collection<InstallationPackage> packages = packageService.loadAll();
            LOG.debug("packages=" + packages);

            int records = packages.size();
            int mod = records % rows;
            int totalPages = records / rows;
            if (mod > 0) totalPages++;
            List<InstallationPackage> onePage = ListUtil.sliceList(page, rows, new LinkedList<InstallationPackage>(packages));
            LOG.debug("onePage=" + onePage);

            SerializerUtil.jsonSerialize(response.getWriter(), new JsonDataWrapper(page, totalPages, records, onePage));
            LOG.debug("EXIT");

        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    // TODO: use me
    // Slices a subset from list, the result fits into one jqGgrid page.
    private <T> void sliceAndSerialize(ResourceResponse response, List<T> items, int page, int rows) throws IOException {
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
