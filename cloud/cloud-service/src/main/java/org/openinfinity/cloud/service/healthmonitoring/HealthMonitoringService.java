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

import org.openinfinity.cloud.domain.*;

import java.util.Date;
import java.util.Map;

/**
 * 
 * @author Ivan Bilechyk
 * @author Ilkka Leinonen
 * @author Timo Saarinen
 * @author Nishant Gupta
 */
public interface HealthMonitoringService {

    NodeListResponse getHostList();

    MetricTypesResponse getMetricTypes(Request request);

    MetricNamesResponse getMetricNames(Request request);

    /**
     * Provides the RRD data for period with regard to selected metric.
     *
     * @param sourceType  Type of source.
     * @param metricType  Metric type of registered host.
     * @param metricNames Metric names, separated by '+'.
     * @param startTime   Period start date-time.
     * @param endTime     Period end date-time.
     * @return {@link HealthStatusResponse}.
     */
    HealthStatusResponse getHealthStatus(String hostName, String sourceType, String metricType, String[] metricNames, Date startTime, Date endTime);

    HealthStatusResponse getClusterHealthStatus(Machine machine, String metricType, String[] metricNames, Date date);
    
    HealthStatusResponse getLatestAvalibaleClusterHealthStatus(Machine machine, String metricType, String[] metricNames, Date date);

    float getClusterLoad(Machine machine, String[] metricName, String metricType, String period);

    String getHealthStatus();

    /**
     * Provides boundaries for several metrics.
     *
     * @param metricType Metric type.
     * @return All available boundaries for every metric that belongs to selected metric type.
     */
    MetricBoundariesResponse getMetricBoundaries(String hostName,String metricType);

    GroupListResponse getGroupList();

    NotificationResponse getNotifications(Long lastUpdateTime, Long currentTimeMillis);
    
     Map<Integer,String> getClusterMasterMap();
}
