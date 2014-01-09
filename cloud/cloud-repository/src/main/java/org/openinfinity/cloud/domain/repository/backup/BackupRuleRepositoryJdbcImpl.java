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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.LinkedList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.openinfinity.cloud.domain.BackupRule;

/**
 * Cluster backup schedule repository implementation for relational database
 * backend.
 * 
 * @author Timo Saarinen
 */
@Repository("backupRepository")
public class BackupRuleRepositoryJdbcImpl implements BackupRuleRepository {
	public static final String TABLE_BACKUP_RULE = "backup_rule_tbl";
	public static final String COLUMN_ID = "backup_rule_id";
	public static final String COLUMN_CLUSTER_ID = "cluster_id";
	public static final String COLUMN_ACTIVE = "active";
	public static final String COLUMN_MINUTES = "cron_minutes";
	public static final String COLUMN_HOURS = "cron_hours";
	public static final String COLUMN_DOM = "cron_day_of_month";
	public static final String COLUMN_MONTH = "cron_month";
	public static final String COLUMN_DOW = "cron_day_of_week";
	public static final String COLUMN_YEAR = "cron_year";

	private JdbcTemplate jdbcTemplate = null;

	@Autowired
	public BackupRuleRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		if (ds == null) throw new NullPointerException("Data source is null!");
		jdbcTemplate = new JdbcTemplate(ds);
	}
	
	public List<Integer> getBackupClusters() {
		final List<Integer> ids = new LinkedList<Integer>();
		jdbcTemplate.query("SELECT DISTINCT cluster_id FROM "
				+ TABLE_BACKUP_RULE, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				while (rs.next()) {
					ids.add(new Integer(rs.getInt(0)));
				}
			}
		});
		return ids;
	}

	public List<BackupRule> getClusterBackupRules(int cluster_id) {
		return jdbcTemplate.query("SELECT * FROM " + TABLE_BACKUP_RULE
				+ " WHERE " + COLUMN_CLUSTER_ID + " = ?",
				new Object[] { new Integer(cluster_id) },
				new BackupRuleRowMapper());
	}

	public boolean deleteClusterBackupRules(int cluster_id) {
		int r = jdbcTemplate.update("DELETE FROM " + TABLE_BACKUP_RULE
				+ " WHERE " + COLUMN_CLUSTER_ID + " = ?", new Integer(
				cluster_id));
		return r > 0;
	}

	public void createBackupRule(BackupRule rule) {
		jdbcTemplate.update(
				"INSERT INTO backup_rule_tbl (" + COLUMN_CLUSTER_ID + ", "
						+ COLUMN_ACTIVE + ", " + COLUMN_MINUTES + ", "
						+ COLUMN_HOURS + ", " + COLUMN_DOM + ", "
						+ COLUMN_MONTH + ", " + COLUMN_DOW + ", " + COLUMN_YEAR
						+ "" + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
				new Object[] { rule.getCluster_id(),
						rule.isActive() ? "yes" : "no", rule.getCronMinutes(),
						rule.getCronHours(), rule.getCronDayOfMonth(),
						rule.getCronMonth(), rule.getCronDayOfWeek(),
						rule.getCronYear() });
	}

	public boolean deleteBackupRule(BackupRule br) {
		int r = jdbcTemplate.update("DELETE FROM " + TABLE_BACKUP_RULE
				+ " WHERE " + COLUMN_ID + " = ?", new Integer(br.getId()));
		return r == 1;
	}

	public void updateBackupRule(BackupRule rule) {
		jdbcTemplate.update(
				"UPDATE " + TABLE_BACKUP_RULE + " SET " + COLUMN_CLUSTER_ID + " = ?, "
						+ COLUMN_ACTIVE + " = ?, " + COLUMN_MINUTES + " = ?, "
						+ COLUMN_HOURS + " = ?, " + COLUMN_DOM + " = ?, "
						+ COLUMN_MONTH + " = ?, " + COLUMN_DOW + " = ?, "
						+ COLUMN_YEAR + " = ?" + ") WHERE " + COLUMN_ID
						+ " = ?",
				new Object[] {
						rule.getCluster_id() >= 0 ? new Integer(rule
								.getCluster_id()) : null,
						rule.isActive() ? "yes" : "no", rule.getCronMinutes(),
						rule.getCronHours(), rule.getCronDayOfMonth(),
						rule.getCronMonth(), rule.getCronDayOfWeek(),
						rule.getCronYear(), rule.getId() });
	}

	class BackupRuleRowMapper implements RowMapper<BackupRule> {
		public BackupRule mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {
			BackupRule rule = new BackupRule();
			rule.setActive("yes".equalsIgnoreCase(resultSet.getString("active")));
			rule.setCluster_id(resultSet.getInt(COLUMN_CLUSTER_ID));
			rule.setId(resultSet.getInt(COLUMN_ID));
			rule.setCronMinutes(resultSet.getString(COLUMN_MINUTES));
			rule.setCronHours(resultSet.getString(COLUMN_HOURS));
			rule.setCronDayOfMonth(resultSet.getString(COLUMN_DOM));
			rule.setCronMonth(resultSet.getString(COLUMN_MONTH));
			rule.setCronDayOfWeek(resultSet.getString(COLUMN_DOW));
			rule.setCronYear(resultSet.getString(COLUMN_YEAR));
			return rule;
		}
	}
}
