/*
 * Copyright (c) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openinfinity.cloud.application.template.controller;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.openinfinity.cloud.common.annotation.Authenticated;
import org.openinfinity.cloud.domain.configurationtemplate.entity.InstallationPackage;
import org.openinfinity.cloud.service.configurationtemplate.entity.api.InstallationPackageService;
import org.openinfinity.cloud.util.http.HttpCodes;
import org.openinfinity.cloud.util.serialization.SerializerUtil;
import org.openinfinity.core.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.math.BigInteger;

/**
 * Spring portlet controller for handling Installation Package requests.
 *
 * Handles requests from "Main view, Installation package tab" and requests from "Package dialog"
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */
@Controller
@RequestMapping("VIEW")
public class PackageController extends AbstractController{

    private static final String CREATE_PACKAGE = "createPackage";
    private static final String GET_PACKAGE = "getPackage";
	private static final String EDIT_PACKAGE = "editPackage";
    private static final String DELETE_PACKAGE = "deletePackage";

    private static final Logger LOG = Logger.getLogger(PackageController.class.getName());


    @Autowired
    private InstallationPackageService packageService;

    @Authenticated
    @ResourceMapping(GET_PACKAGE)
    public void getPackage(ResourceRequest request, ResourceResponse response, @RequestParam("packageId") int packageId) throws Exception {
        try {
            SerializerUtil.jsonSerialize(response.getWriter(), packageService.load(BigInteger.valueOf(packageId)));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

    @Authenticated
    @ResourceMapping(EDIT_PACKAGE)
    public void editPackage(ResourceRequest request, ResourceResponse response, @RequestParam("package") String packageData){
        try {
            // TODO bean me up
            ObjectMapper mapper = new ObjectMapper();
            InstallationPackage pkg = mapper.readValue(packageData, InstallationPackage.class);
            LOG.debug("InstallationPackage:" + pkg);
            packageService.update(pkg);
        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

    @Authenticated
    @ResourceMapping(CREATE_PACKAGE)
    public void createPackage(ResourceRequest request, ResourceResponse response, @RequestParam("package") String packageData){
        try {
            packageService.create(new ObjectMapper().readValue(packageData, InstallationPackage.class));
        } catch (Exception e) {
            e.printStackTrace();
            response.setProperty(ResourceResponse.HTTP_STATUS_CODE, HttpCodes.HTTP_ERROR_CODE_SERVER_ERROR);
        }
    }

    @Authenticated
    @ResourceMapping(DELETE_PACKAGE)
    public void deletePackage(ResourceRequest request, ResourceResponse response, @RequestParam("id") int packageId) throws Exception {
        try{
            packageService.delete(BigInteger.valueOf(packageId));
        } catch (Exception e) {
            ExceptionUtil.throwSystemException(e);
        }
    }

}