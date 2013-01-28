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
package org.openinfinity.cloud.service.healthmonitoring;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Ivan Bileichyk
 * @author Ilkka Leinonen
 * @author Timo Saarinen
 */

@Component
public class RequestBuilder {

    // plus character
    public static final String METRIC_NAME_DELIMITER = "%2B";

    private String hostName;
    private int port;
    private String protocol;
    private String contextPath;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String buildHostListRequest(Request request) {
        StringBuilder builder = new StringBuilder(getHostPart());
        builder.append("/hostlist");
        return builder.toString();
    }

    public String buildGroupListRequest(Request request) {
        StringBuilder builder = new StringBuilder(getHostPart());
        builder.append("/grouplist");
        return builder.toString();
    }

    public String buildMetricTypesRequest(Request request) {
        StringBuilder builder = new StringBuilder(getHostPart());
        if (Request.SOURCE_GROUP.equals(request.getSourceType())) {
            builder.append("/groupmetrictypes")
                    .append("?")
                    .append("groupName=");
        } else {
            builder.append("/metrictypes")
                    .append("?")
                    .append("hostName=");
        }
        builder.append(request.getSourceName());
        return builder.toString();
    }

    public String buildMetricNamesRequest(Request request) {
        StringBuilder builder = new StringBuilder(getHostPart());
        if (Request.SOURCE_GROUP.equals(request.getSourceType())) {
            builder.append("/groupmetricnames")
                    .append("?")
                    .append("groupName=");
        } else {
            builder.append("/metricnames")
                    .append("?")
                    .append("hostName=");
        }
        builder.append(request.getSourceName())
                .append("&metricType=")
                .append(request.getMetricType());
        return builder.toString();
    }

    public String buildMetricBoundariesRequest(Request request) {
        StringBuilder builder = new StringBuilder(getHostPart());
        builder.append("/metricboundaries")
                .append("?")
                .append("metricType=")
                .append(request.getMetricType());
        return builder.toString();
    }

    public String buildHealthStatusRequest(Request request) {
        StringBuilder builder = new StringBuilder(getHostPart());
        String joinedMetricNames = StringUtils.join(request.getMetricNames(), METRIC_NAME_DELIMITER);
        if (Request.SOURCE_GROUP.equals(request.getSourceType())) {
            builder.append("/grouphealthstatus")
                    .append("?")
                    .append("groupName=");
        } else {
            builder.append("/healthstatus")
                    .append("?")
                    .append("hostName=");
        }
        builder
                .append(request.getSourceName())
                .append("&metricType=")
                .append(request.getMetricType())
                .append("&metricNames=")
                .append(joinedMetricNames)
                .append("&startTime=")
                .append(request.getStartTime().getTime())
                .append("&endTime=")
                .append(request.getEndTime().getTime())
                .append("&step=")
                .append(request.getStep());
        return builder.toString();
    }

    private String getHostPart() {
        StringBuilder builder = new StringBuilder(50);
        builder.append(protocol)
                .append(hostName)
                .append(":")
                .append(port)
                .append("/")
                .append(contextPath);
        return builder.toString();
    }

    public String buildNotificationsRequest(Long startTime, Long endTime) {
        StringBuilder builder = new StringBuilder(getHostPart());
        builder.append("/notifications")
                .append("?");
        if (startTime != null) {
            builder.append("startTime=").append(startTime).append("&");
        }
        if (endTime != null) {
            builder.append("endTime=").append(endTime);
        }
        return builder.toString();
    }
}
