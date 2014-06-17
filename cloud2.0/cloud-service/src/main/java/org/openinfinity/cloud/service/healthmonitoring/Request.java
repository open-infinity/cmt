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

import java.util.Date;

/**
 * 
 * @author Ivan Bilechyk
 * @author Ilkka Leinonen
 * @author Timo Saarinen
 */
public class Request {
    public static final String SOURCE_HOST = "host";
    public static final String SOURCE_GROUP = "group";

    private String sourceName;
    private String sourceType = SOURCE_HOST;
    private String metricType;
    private String[] metricNames;
    private Date startTime;
    private Date endTime;
    private Long step;

    public Request() {
    }

    public Request(String sourceName) {
        this.sourceName = sourceName;
    }

    public Request(String sourceName, String metricType) {
        this.sourceName = sourceName;
        this.metricType = metricType;
    }

    public Request(String sourceName, String sourceType, String metricType, String[] metricNames,
                   Date startTime, Date endTime, long step) {
        this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.metricType = metricType;
        this.metricNames = metricNames;
        this.startTime = startTime;
        this.endTime = endTime;
        this.step = step;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String[] getMetricNames() {
        return metricNames;
    }

    public void setMetricNames(String[] metricNames) {
        this.metricNames = metricNames;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getStep() {
        return step;
    }

    public void setStep(Long step) {
        this.step = step;
    }
}
