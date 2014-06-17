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
package org.openinfinity.cloud.domain;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Vitali Kukresh
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 */
@Data
@NoArgsConstructor
public class Notification implements Serializable {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private String severity;
    private String hostName;
    private Long time;
    private String plugin;
    private String type;
    private String typeInstance;
    private String datasource;
    private Double currentValue;
    private String message;
    private Long fileModificationTime;
    private String fileName;
    private Double warningMin;
    private Double warningMax;
    private Double failureMin;
    private Double FailureMax;

    /**
     * Formats {@link Notification} object into readable format.
     *
     * @return user friendly formated notification
     */
    public String format() {
        StringBuilder builder = new StringBuilder();
        builder.append("Severity: ").append(getSeverity()).append(LINE_SEPARATOR).append("Host: ")
                .append(getHostName()).append(LINE_SEPARATOR).append("Plugin: ").append(getPlugin())
                .append(LINE_SEPARATOR).append("Type: ").append(getType()).append(LINE_SEPARATOR)
                .append("TypeInstance: ").append(getTypeInstance()).append(LINE_SEPARATOR).append("Datasource: ")
                .append(getDatasource()).append(LINE_SEPARATOR).append("Value: ").append(getCurrentValue())
                .append(LINE_SEPARATOR).append("Time: ").append(getTime()).append(LINE_SEPARATOR);
        if (getFailureMin() != null) {
            builder.append(LINE_SEPARATOR).append("FailureMin: ").append(getFailureMin());
        }
        if (getFailureMax() != null) {
            builder.append(LINE_SEPARATOR).append("FailureMax: ").append(getFailureMax());
        }
        if (getWarningMin() != null) {
            builder.append(LINE_SEPARATOR).append("WarningMin: ").append(getWarningMin());
        }
        if (getWarningMax() != null) {
            builder.append(LINE_SEPARATOR).append("WarningMin: ").append(getWarningMax());
        }
        if (getMessage() != null) {
            builder.append(LINE_SEPARATOR).append("Message: ").append(getMessage());
        }
        return builder.toString();
    }

}
