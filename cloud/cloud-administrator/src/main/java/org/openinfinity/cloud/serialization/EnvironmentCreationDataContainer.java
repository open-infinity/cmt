/*
 * Copyright (c) 2014 the original author or authors.
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

package org.openinfinity.cloud.serialization;

import java.util.Collection;

/**
 *  Container class for JSON data sent from client
 *
 *  Inner classes and members match the JSON data structure in the example below:
 *  requestData:{
 *      "environment": {"name":"a","type":"1","zone":"dev-pilvi1"},
 *      "configurations":[{
 *                          "element":{"id":1,"name":"ig","type":1},
 *                          "cluster":{"size":"1"},
 *                          "machine":{"size":"0"},
 *                          "replication":{"on":false,"cluster":{"size":0},"machine":{"size":0}},
 *                          "imageType":"ephemeral",
 *                          "ebs":{"on":false,"size":0},
 *                          "parameters":{"on":true,"keys":["-1"]}}]}
 *
 *
 * @author Vedran Bartonicek
 * @version 1.2.2
 * @since 1.2.2
 */

public class EnvironmentCreationDataContainer {

    public static class EnvironmentData {
        private String name;
        private int type;
        private String zone;

        public EnvironmentData(){};

        public EnvironmentData(String name, int type, String zone) {
            this.name = name;
            this.type = type;
            this.zone = zone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }
    }

    public static class ConfigurationData {
        public static class ElementData{
            private int id;
            private String name;
            private int type;

            public ElementData() {
            }

            public ElementData(int id, String name, int type) {
                this.id = id;
                this.name = name;
                this.type = type;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }
        }

        public static class ClusterConfigurationData{
            private int size;

            public ClusterConfigurationData(int size) {
                this.size = size;
            }

            public ClusterConfigurationData() {
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }
        }

        public static class MachineConfigurationData{
            private int size;

            public MachineConfigurationData(int size) {
                this.size = size;
            }

            public MachineConfigurationData() {
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }
        }

        public static class EbsDataContainer {
            private boolean on;
            private int size;

            public EbsDataContainer(boolean on, int size) {
                this.on = on;
                this.size = size;
            }

            public EbsDataContainer() {
            }

            public boolean isOn() {
                return on;
            }

            public void setOn(boolean on) {
                this.on = on;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }
        }

        public class ParametersDataContainer {
            private boolean on;
            private Collection<Integer> keys;

            public ParametersDataContainer(boolean on, Collection<Integer> keys) {
                this.on = on;
                this.keys = keys;
            }

            public ParametersDataContainer() {
            }

            public boolean isOn() {
                return on;
            }

            public void setOn(boolean on) {
                this.on = on;
            }

            public Collection<Integer> getKeys() {
                return keys;
            }

            public void setKeys(Collection<Integer> keys) {
                this.keys = keys;
            }
        }

        public static class ReplicationDataContainer {
            private boolean on;
            private EnvironmentCreationDataContainer.ConfigurationData.ClusterConfigurationData cluster;
            private EnvironmentCreationDataContainer.ConfigurationData.MachineConfigurationData machine;

            public ReplicationDataContainer(boolean on, EnvironmentCreationDataContainer.ConfigurationData.ClusterConfigurationData cluster, EnvironmentCreationDataContainer.ConfigurationData.MachineConfigurationData machine) {
                this.on = on;
                this.cluster = cluster;
                this.machine = machine;
            }

            public ReplicationDataContainer() {
            }

            public boolean isOn() {
                return on;
            }

            public void setOn(boolean on) {
                this.on = on;
            }

            public EnvironmentCreationDataContainer.ConfigurationData.ClusterConfigurationData getCluster() {
                return cluster;
            }

            public void setCluster(EnvironmentCreationDataContainer.ConfigurationData.ClusterConfigurationData cluster) {
                this.cluster = cluster;
            }

            public EnvironmentCreationDataContainer.ConfigurationData.MachineConfigurationData getMachine() {
                return machine;
            }

            public void setMachine(EnvironmentCreationDataContainer.ConfigurationData.MachineConfigurationData machine) {
                this.machine = machine;
            }
        }

        private ElementData element;
        private ClusterConfigurationData cluster;
        private MachineConfigurationData machine;
        private String imageType;
        private ReplicationDataContainer replication;
        private EbsDataContainer ebs;
        private ParametersDataContainer parameters;

        public ConfigurationData(ElementData element, ClusterConfigurationData cluster, MachineConfigurationData machine, String imageType, ReplicationDataContainer replication, EbsDataContainer ebs, ParametersDataContainer parameters) {
            this.element = element;
            this.cluster = cluster;
            this.machine = machine;
            this.imageType = imageType;
            this.replication = replication;
            this.ebs = ebs;
            this.parameters = parameters;
        }

        public ConfigurationData(){

        }

        public ElementData getElement() {
            return element;
        }

        public void setElement(ElementData element) {
            this.element = element;
        }

        public ClusterConfigurationData getCluster() {
            return cluster;
        }

        public void setCluster(ClusterConfigurationData cluster) {
            this.cluster = cluster;
        }

        public MachineConfigurationData getMachine() {
            return machine;
        }

        public void setMachine(MachineConfigurationData machine) {
            this.machine = machine;
        }

        public String getImageType() {
            return imageType;
        }

        public void setImageType(String imageType) {
            this.imageType = imageType;
        }

        public ReplicationDataContainer getReplication() {
            return replication;
        }

        public void setReplication(ReplicationDataContainer replication) {
            this.replication = replication;
        }

        public EbsDataContainer getEbs() {
            return ebs;
        }

        public void setEbs(EbsDataContainer ebs) {
            this.ebs = ebs;
        }

        public ParametersDataContainer getParameters() {
            return parameters;
        }

        public void setParameters(ParametersDataContainer parameters) {
            this.parameters = parameters;
        }
    }


    // Root level of JSON data

    private EnvironmentData environment;
    private Collection<ConfigurationData> configurations;

    public EnvironmentCreationDataContainer(){

    };

    public EnvironmentCreationDataContainer(EnvironmentData environment, Collection<ConfigurationData> configurations) {
        this.environment = environment;
        this.configurations = configurations;
    }

    public EnvironmentData getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentData environment) {
        this.environment = environment;
    }

    public Collection<ConfigurationData> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Collection<ConfigurationData> configurations) {
        this.configurations = configurations;
    }
}
