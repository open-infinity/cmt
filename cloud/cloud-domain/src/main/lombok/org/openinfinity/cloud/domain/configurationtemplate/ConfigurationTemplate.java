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
package org.openinfinity.cloud.domain.configurationtemplate;

import org.openinfinity.core.annotation.NotScript;

import lombok.ToString;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.Override;
import java.math.BigInteger;

/**
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class ConfigurationTemplate implements Comparable{
    
    @NonNull
    @NotScript
    private int id;
    
    @NonNull
    @NotScript
    private String name;
    
    @NonNull
    @NotScript
    private String description;

    public ConfigurationTemplate(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public int compareTo(Object o) {
        return this.id - ((ConfigurationTemplate)o).getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurationTemplate)) return false;
        ConfigurationTemplate that = (ConfigurationTemplate) o;
        if (id != that.id) return false;
        return true;
    }

}
