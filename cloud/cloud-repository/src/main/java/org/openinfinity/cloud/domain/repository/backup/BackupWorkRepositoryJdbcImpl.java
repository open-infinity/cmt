package org.openinfinity.cloud.domain.repository.backup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.openinfinity.cloud.domain.BackupOperation;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;


/**
 * JDBC implementation of BackupWorkRepository interface.
 * 
 * @author Timo Saarinen
 */
@Repository("backupWorkRepository")
public class BackupWorkRepositoryJdbcImpl implements BackupWorkRepository {

	public static final String TABLE_BACKUP_OPERATION = "backup_operation_tbl";
	public static final String COLUMN_ID = "backup_operation_id";
	public static final String COLUMN_OPERATION = "operation";
	public static final String COLUMN_UPDATED = "update_time";
	public static final String COLUMN_CREATED = "create_time";
	public static final String COLUMN_BACKUP_RULE_ID = "backup_rule_id";
	public static final String COLUMN_TARGET_CLUSTER_ID = "target_cluster_id";
	public static final String COLUMN_STATE = "state";

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public BackupWorkRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		if (ds == null) throw new NullPointerException("Data source is null!");
		jdbcTemplate = new JdbcTemplate(ds);
	}
	
	public List<BackupOperation> readBackupOperationsAfter(int id) {
		return jdbcTemplate.query("SELECT * FROM " + TABLE_BACKUP_OPERATION
				+ " WHERE " + COLUMN_ID + " > ? ORDER BY " + COLUMN_ID,
				new Object[] { new Integer(id) },
				new BackupOperatoionRowMapper());
	}

	public void writeBackupOperation(BackupOperation op) {
		if (op.getBackupRuleId() == -1) {
			jdbcTemplate.update("INSERT INTO " + TABLE_BACKUP_OPERATION + " ("
					+ COLUMN_OPERATION + ", " + COLUMN_UPDATED + ", "
					+ COLUMN_CREATED + ", " + COLUMN_BACKUP_RULE_ID + ", "
					+ COLUMN_TARGET_CLUSTER_ID + ", " + COLUMN_STATE
					+ ") VALUES (?, ?, ?, ?, ?, ?)",
					new Object[] { op.getOperation(), new java.util.Date(),
							new java.util.Date(), op.getBackupRuleId(), });
		} else {
			jdbcTemplate.update("UPDATE " + TABLE_BACKUP_OPERATION + " SET "
					+ COLUMN_OPERATION + " = ?, " + COLUMN_UPDATED + " = ?, "
					+ COLUMN_BACKUP_RULE_ID + " = ?, "
					+ COLUMN_TARGET_CLUSTER_ID + " = ?, " + COLUMN_STATE
					+ " = ?, " + ") WHERE " + COLUMN_ID + " = ?", new Object[] {
					op.getOperation(), op.getUpdated(), op.getBackupRuleId(),
					op.getTargetClusterId(), op.getState(), op.getId() });
		}
	}

	public BackupOperation readBackupOperation(int id) {
		List<BackupOperation> ops = jdbcTemplate.query("SELECT * FROM "
				+ TABLE_BACKUP_OPERATION + " WHERE " + COLUMN_ID + " = ?",
				new Object[] { new Integer(id) },
				new BackupOperatoionRowMapper());
		if (ops.size() == 1) {
			return ops.get(0);
		} else if (ops.size() == 0) {
			return null;
		} else {
			throw new RuntimeException("Unexpected result set. Read "
					+ ops.size() + " instead of one.");
		}
	}

	public boolean deleteBackupOperation(BackupOperation op) {
		int count = jdbcTemplate.update("DELETE FROM WHERE " + COLUMN_ID
				+ " = ?", new Object[] { op.getId() });
		return (count > 0);
	}

	class BackupOperatoionRowMapper implements RowMapper<BackupOperation> {
		public BackupOperation mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			BackupOperation op = new BackupOperation();
			op.setId(rs.getInt(COLUMN_ID));
			op.setOperation(rs.getString(COLUMN_OPERATION));
			op.setUpdated(rs.getDate(COLUMN_UPDATED));
			op.setBackupRuleId(rs.getInt(COLUMN_BACKUP_RULE_ID));
			op.setTargetClusterId(rs.getInt(COLUMN_TARGET_CLUSTER_ID));
			op.setState(rs.getString(COLUMN_STATE));
			return op;
		}
	}
}
