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

package org.openinfinity.cloud.domain.repository.administrator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.Key;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Jdbc implementation of Key repository interface
 
 * @author Ossi Hämäläinen
 * @author Ilkka Leinonen
 * @author Juha-Matti Sironen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

@Repository("keyRepository")
public class KeyRepositoryJdbcImpl implements KeyRepository {
	
	private static final Logger LOG = Logger.getLogger(KeyRepositoryJdbcImpl.class.getName());
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public KeyRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource ds) {
		Assert.notNull(ds, "Please define datasource for key repository.");
		this.jdbcTemplate = new JdbcTemplate(ds);
	}
	
	@AuditTrail
	public void addKey(final Key key) {
		jdbcTemplate.update("insert into key_tbl (secret_key, key_fingerprint, key_name, instance_id) values (?,?,?,?)",
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, key.getSecret_key());
						ps.setString(2, key.getFingerprint());
						ps.setString(3, key.getName());
						ps.setInt(4, key.getInstanceId());
					}
				}
			);
	}

	@AuditTrail
	public Key getKeyForInstance(int instanceId) {
		List<Key> keyList = this.jdbcTemplate.query("select key_id, secret_key, key_fingerprint, key_name, instance_id from key_tbl where instance_id = ?", new Object[] {instanceId}, new KeyMapper());
		Key key = DataAccessUtils.singleResult(keyList);
		return key;
	}

	@AuditTrail
	public void removeKeyByInstanceId(int instanceId) {
		this.jdbcTemplate.execute("delete from key_tbl where instance_id = "+instanceId);

	}

	@AuditTrail
	public List<Key> getKeys() {
		List<Key> keyList = this.jdbcTemplate.query("select key_id, secret_key, key_fingerprint, key_name, instance_id from key_tbl", new KeyMapper());
		return keyList;
	}

	@AuditTrail
	public Key getKey(int id) {
		List<Key> keyList = this.jdbcTemplate.query("select key_id, secret_key, key_fingerprint, key_name, instance_id from key_tbl where key_id = ?", new Object[] {id}, new KeyMapper());
		Key key = DataAccessUtils.singleResult(keyList);
		return key;
	}

	@AuditTrail
	public void removeKey(int id) {
		// TODO Auto-generated method stub
	}

	private static final class KeyMapper implements RowMapper<Key> {
		public Key mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Key key = new Key();
			key.setId(rs.getInt("key_id"));
			key.setSecret_key(rs.getString("secret_key"));
			key.setFingerprint(rs.getString("key_fingerprint"));
			key.setName(rs.getString("key_name"));
			key.setInstanceId(rs.getInt("instance_id"));
			return key;
		}
	}	

}
