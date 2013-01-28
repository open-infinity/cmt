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
package org.openinfinity.cloud.domain.repository.scaling;

import java.sql.Timestamp;
import java.util.Collection;

import org.openinfinity.cloud.domain.ScalingRule;

/**
 * Repository interface for storing scaling rules of the cluster.
 * 
 * @author Ilkka Leinonen
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ScalingRuleRepository {
	
	/**
	 * Stores  scaling rule autoscaling settings into repository. If  rule for a given cluster already exists,
	 * updates existing rule. Otherwise creates a new rule for the cluster.
	 */
	void store (ScalingRule scalingRule);
	
	/**
	 * Loads <code>org.openinfinity.cloud.domain.ScalingRule</code> based on the cluster id.
	 */
	ScalingRule loadByClusterId (int clusterId);
	
	/**
	 * Deletes scaling rule by id.
	 */
	void deleteByClusterId(int id);
	
	/**
	 * Loads all defined scaling rules for each organization and cluster.
	 */
	Collection<ScalingRule> loadAll();
	
	/**
	 * Stores original cluster size, changes state
	 */
	void storeStateScheduledScaling(int numberOfMachines, int clusterId);
	
	/**
	 * Changes state
	 */
	void storeStateScheduledUnScaling(int clusterId);
	
}
