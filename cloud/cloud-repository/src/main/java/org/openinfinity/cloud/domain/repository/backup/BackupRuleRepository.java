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
package org.openinfinity.cloud.domain.repository.backup;

import java.util.List;

import org.openinfinity.cloud.domain.BackupRule;

/**
 * Cluster backup schedule repository interface. 
 * 
 * @author Timo Saarinen
 */
public interface BackupRuleRepository {
	/**
	 * Returns list of clusters to be backed up.
	 * @return List of cluster ids
	 */
	public List<Integer> getBackupClusters();
	
	/**
	 * Get all backup rules of the given cluster.
	 * 
	 * @param cluster_id Primary key of cluster_tbl
	 * @return List of BackupRule objects related to the given cluster.
	 */
	public List<BackupRule> getClusterBackupRules(int cluster_id);

	/**
	 * Delete all backup rules of the given cluster.
	 * 
	 * @param cluster_id Primary key of cluster_tbl
	 */
	public boolean deleteClusterBackupRules(int cluster_id);

	/**
	 * Add new backup rule for cluster.
	 * 
	 * @param br BackupRule to be created
	 */
	public void createBackupRule(BackupRule br);
	
	/**
	 * Delete backup rule.
	 * 
	 * @param br BackupRule to be deleted
	 */
	public boolean deleteBackupRule(BackupRule br);
	
	
	/**
	 * Update backup rule.
	 * 
	 * @param br BackupRule to be deleted
	 */
	public void updateBackupRule(BackupRule br);
}
