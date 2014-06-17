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
 * Information about node. This class has a natural ordering that is inconsistent with equals.
 * 
 * @author Vitali Kukresh
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 */
@Data
@NoArgsConstructor
public class Node implements Serializable, Comparable<Node> {

    private String ipAddress;

    private String nodeName;

    private String groupName;

    @Override
    public int compareTo(Node o) {
        return this.nodeName.compareTo(o.nodeName);
    }

}
