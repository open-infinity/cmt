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

import java.util.List;

import org.openinfinity.cloud.domain.Node;

/**
 * @author Vitali Kukresh
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 */
public class NodeListResponse extends AbstractResponse {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4914566713392827725L;

    private List<Node> activeNodes;

    private List<Node> inactiveNodes;

    public List<Node> getActiveNodes() {
        return activeNodes;
    }

    public void setActiveNodes(List<Node> activeNodes) {
        this.activeNodes = activeNodes;
    }

    public List<Node> getInactiveNodes() {
        return inactiveNodes;
    }

    public void setInactiveNodes(List<Node> inactiveNodes) {
        this.inactiveNodes = inactiveNodes;
    }
}
