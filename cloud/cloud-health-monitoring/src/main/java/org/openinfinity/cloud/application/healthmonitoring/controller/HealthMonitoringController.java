/*
 * Copyright (c) 2012 the original author or authors.
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
package org.openinfinity.cloud.application.healthmonitoring.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openinfinity.cloud.domain.AbstractResponse;
import org.openinfinity.cloud.domain.GroupListResponse;
import org.openinfinity.cloud.domain.HealthStatusResponse;
import org.openinfinity.cloud.domain.MetricBoundariesResponse;
import org.openinfinity.cloud.domain.MetricNamesResponse;
import org.openinfinity.cloud.domain.MetricTypesResponse;
import org.openinfinity.cloud.domain.NodeListResponse;
import org.openinfinity.cloud.domain.NotificationResponse;
import org.openinfinity.cloud.service.healthmonitoring.HealthMonitoringService;
import org.openinfinity.cloud.service.healthmonitoring.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 * 
 * @author Ivan Bilechyk
 * @author Ilkka Leinonen
 * @author Timo Saarinen
 * @author Nishant Gupta
 */
@Controller(value = "healthMonitoringController")
@RequestMapping(value = "VIEW")
public class HealthMonitoringController {

    @Autowired
    private HealthMonitoringService healthMonitoringService;

    public static final class MetricResponse {
        public int responseStatus = AbstractResponse.STATUS_OK;
        public NodeListResponse listResponse;
        public GroupListResponse groupListResponse;
        public MetricTypesResponse typesResponse;
        public MetricNamesResponse namesResponse;

        private MetricResponse(NodeListResponse listResponse,
                               GroupListResponse groupListResponse,
                               MetricTypesResponse typesResponse,
                               MetricNamesResponse namesResponse) {
            this.listResponse = listResponse;
            this.groupListResponse = groupListResponse;
            this.typesResponse = typesResponse;
            this.namesResponse = namesResponse;
        }

    }

    @ResourceMapping("getMetricResponse")
    public void processMetricResponse(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
                                      @ModelAttribute("request") Request request)
            throws JsonGenerationException, JsonMappingException, IOException {
        MetricTypesResponse typesResponse = null;
        MetricNamesResponse namesResponse = null;
        NodeListResponse listResponse = null;
        GroupListResponse groupListResponse = null;
        AbstractResponse sources = null;
        if (Request.SOURCE_GROUP.equals(request.getSourceType())) {
            sources = groupListResponse = getGroupList();
            if (request.getSourceName() == null) {
                String sourceName = CollectionUtils.isEmpty(groupListResponse.getGroups()) ? null
                        : groupListResponse.getGroups().entrySet().iterator().next().getKey();
                request.setSourceName(sourceName);
            }
        } else {
            sources = listResponse = getHostList();
            if (request.getSourceName() == null) {
                String sourceName = CollectionUtils.isEmpty(listResponse.getActiveNodes()) ? null
                        : listResponse.getActiveNodes().get(0).getNodeName();
                request.setSourceName(sourceName);
            }
        }
        if (sources.getResponseStatus() != AbstractResponse.STATUS_OK) {
            // TODO: check why Content Type not set. Bug?
            new ObjectMapper().writeValue(resourceResponse.getWriter(), sources);
        } else {
            typesResponse = healthMonitoringService.getMetricTypes(request);
            // on initial request predefine metric name with the first available for selected type
            if (request.getMetricType() == null) {
                List<String> types = typesResponse.getMetricTypes();
                String metricType = CollectionUtils.isEmpty(types) ? null : types.get(0);
                request.setMetricType(metricType);
            }
            namesResponse = healthMonitoringService.getMetricNames(request);
            MetricResponse mr = new MetricResponse(listResponse, groupListResponse, typesResponse, namesResponse);
            writeObjectAsJson(resourceResponse, mr);
        }
    }

    @ModelAttribute("metricTypes")
    public MetricTypesResponse getMetricTypes(@ModelAttribute("request") Request request) {
        if (!StringUtils.isBlank(request.getSourceName())) {
            return healthMonitoringService.getMetricTypes(request);
        }
        return null;
    }

    @ModelAttribute("metricNames")
    public MetricNamesResponse getMetricNames(@ModelAttribute("request") Request request) {
        if (!StringUtils.isBlank(request.getSourceName()) && !StringUtils.isBlank(request.getMetricType())) {
            return healthMonitoringService.getMetricNames(request);
        }
        return null;
    }

    @ModelAttribute("hostList")
    public NodeListResponse getHostList() {
        return healthMonitoringService.getHostList();
    }

    @ModelAttribute("groupList")
    public GroupListResponse getGroupList() {
        return healthMonitoringService.getGroupList();
    }

    @RenderMapping
    public ModelAndView showView(ModelAndView modelView) {
        modelView.setView("mainview");
        return modelView;
    }

    @ResourceMapping("getNodeList")
    public void processGetNodeList(ResourceRequest request, ResourceResponse response)
            throws JsonGenerationException, JsonMappingException, IOException {
        NodeListResponse source = healthMonitoringService.getHostList();
        writeNodeListJson(source, response);
    }

    @ResourceMapping("getGroupList")
    public void processGetGroupList(ResourceRequest request, ResourceResponse response)
            throws JsonGenerationException, JsonMappingException, IOException {
        GroupListResponse source = healthMonitoringService.getGroupList();
        writeObjectAsJson(response, source);
    }

    @ResourceMapping("getHealthStatusResponse")
    public void processHealthStatusResponse(ResourceRequest request, ResourceResponse response,
                                            @RequestParam(required = true) String sourceName,
                                            @RequestParam(defaultValue = Request.SOURCE_HOST) String sourceType,
                                            @RequestParam(required = true) String metricType,
                                            @RequestParam(required = true) String[] metricNames,
                                            @RequestParam(required = false) Long startTimeStamp,
                                            @RequestParam(required = false) Long endTimeStamp)
            throws JsonGenerationException, JsonMappingException, IOException {
        Date startTime = new Date();
        Date endTime = startTime;
        if (startTimeStamp != null) {
            startTime = new Date(startTimeStamp);
        }
        if (endTimeStamp != null) {
            endTime = new Date(endTimeStamp);
        }
        HealthStatusResponse status = healthMonitoringService.getHealthStatus(sourceName, sourceType, metricType,
                metricNames, startTime, endTime);
        writeObjectAsJson(response, status);
    }

    @ResourceMapping("getHealthBoundariesResponse")
	// Nishant: Changed to add the sourceName parameter in the method call.
    public void processHealthBoundariesResponse(ResourceRequest request, ResourceResponse response,
    											@RequestParam(required = true) String sourceName,
                                                @RequestParam(required = true) String metricType)
            throws JsonGenerationException, JsonMappingException, IOException {
        MetricBoundariesResponse status = healthMonitoringService.getMetricBoundaries(sourceName,metricType);
        writeObjectAsJson(response, status);
    }

    private void writeNodeListJson(NodeListResponse source, ResourceResponse response)
            throws JsonGenerationException, JsonMappingException, IOException {
        writeObjectAsJson(response, source);
    }

    @ResourceMapping("getNotifications")
    public void getNotifications(ResourceRequest request, ResourceResponse response,
                                 @RequestParam(required = false) Long lastUpdateTime)
            throws JsonGenerationException, JsonMappingException, IOException {
        NotificationResponse notificationResponse = healthMonitoringService.getNotifications(lastUpdateTime,
                System.currentTimeMillis());
        writeObjectAsJson(response, notificationResponse);
    }

    private static void writeObjectAsJson(ResourceResponse response, Object object) throws IOException,
            JsonGenerationException, JsonMappingException {
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), object);
    }
}
