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

package org.openinfinity.cloud.service.scaling;

import org.openinfinity.cloud.domain.Cluster;
import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.service.scaling.Enumerations.ScalingState;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * Service interface for automated provisioning business rules.
 * 
 * @author Ilkka Leinonen
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ScalingRuleService {

    public static enum ClusterStatus {
        IDLE,
        ERROR,
        UNDER_CONSTRUCTION,
    }

	/**
	 * Calculates scaling state for Periodic Autoscaler
	 */
    ScalingState applyScalingRule(float load, int clusterId, ScalingRule rule);

    /**
     * Calculates scaling state for Scheduled Autoscaler
     */
    ScalingState applyScalingRule(Timestamp samplingPeriodFrom, Timestamp samplingPeriodTo, Cluster cluster, ScalingRule rule);
	
	/**
	 * Stores the scaling rule into a repository.
	 */
	void store (ScalingRule scalingRule);
		
	/**
	 * Gets <code>org.openinfinity.cloud.domain.ScalingRule</code> based on the cluster id.
	 */
	ScalingRule getRule(int clusterId);

	/**
	 * Deletes scaling rule by id.
	 */
	void deleteByClusterId(int id);
	
	/**
	 * Loads all defined scaling rules for each organization and cluster.
	 */
	Collection<ScalingRule> loadAll();

	/**
	 * Stores original cluster size, clears scaleOut flag
	 */
	void storeScalingOutParameters(int numberOfMachines, int clusterId);
	
	/**
	 * Clears scaleIn flag
	 */
	void storeScalingInParameters(int clusterId);
	
	/**
     * Update job id.
     */
    void storeJobId(int clusterId, int jobId);
    
    
    void delete(int clusterId);

}
