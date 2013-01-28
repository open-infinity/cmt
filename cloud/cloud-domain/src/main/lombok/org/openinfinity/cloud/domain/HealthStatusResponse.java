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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openinfinity.cloud.domain.RrdValue;

/**
 * @author Vitali Kukresh
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 */
public class HealthStatusResponse extends AbstractResponse {

    private static final long serialVersionUID = -4202740833241267632L;

    private List<SingleHealthStatus> metrics = new ArrayList<SingleHealthStatus>();

    public List<SingleHealthStatus> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<SingleHealthStatus> metrics) {
        this.metrics = metrics;
    }

    /**
     * Container for single metric rrd data.
     * 
     * @author kukrevit
     */
    public static class SingleHealthStatus extends AbstractResponse {

        private static final long serialVersionUID = -7448666972970354257L;

        private String name;

        private Map<String, List<RrdValue>> values;

        /**
         * Default constructor.
         */
        public SingleHealthStatus() {
            super();
            values = new HashMap<String, List<RrdValue>>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, List<RrdValue>> getValues() {
            return values;
        }

        public void setValues(Map<String, List<RrdValue>> mapValues) {
            this.values = mapValues;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (name == null ? 0 : name.hashCode());
            result = prime * result + (values == null ? 0 : values.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SingleHealthStatus other = (SingleHealthStatus) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (values == null) {
                if (other.values != null) {
                    return false;
                }
            } else if (!values.equals(other.values)) {
                return false;
            }
            return true;
        }
    }
}
