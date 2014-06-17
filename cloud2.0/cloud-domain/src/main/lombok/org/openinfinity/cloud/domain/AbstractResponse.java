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

/**
 * @author Vitali Kukresh
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 */
public class AbstractResponse implements Serializable {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -152516404305670937L;

    public static final int STATUS_OK = 0;
    public static final int STATUS_RRD_FAIL = 1;
    public static final int STATUS_METRIC_FAIL = 2;
    public static final int STATUS_NODE_FAIL = 3;
    public static final int STATUS_PARAM_FAIL = 4;

    private int responseStatus;

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }
}
