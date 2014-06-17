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
package org.openinfinity.cloud.domain.repository.connectionpool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.TomcatJdbcPoolSettings;
import org.springframework.stereotype.Service;

/**
 * Repository layer of Connection Pool Manager.
 * 
 * @author Timo Saarinen
 */
@Service(value="connectionPoolRepository")
public class ConnectionPoolRepositoryJdbcImpl implements ConnectionPoolRepository {

	static final String TJP = "tomcat-jdbc-pool";
	
	private static final Logger logger = Logger.getLogger(ConnectionPoolRepositoryJdbcImpl.class.getName());

	/*
 	 * create table CONNECTION_POOL (POOL_TYPE varchar(16) not null, PARAM_COLUMN varchar(64) not null, VALUE_COLUMN varchar(64)); 
 	 * create table CONNECTION_POOL_EXPORT (POOL_TYPE varchar(16) not null, XML_DATA text(8192) not null); 
	 */
	
	private static final String INSERT_SQL  = "insert into CONNECTION_POOL (POOL_TYPE, PARAM_COLUMN, VALUE_COLUMN) values (?, ?, ?)";
	private static final String UPDATE_SQL = "update CONNECTION_POOL SET VALUE_COLUMN = ? where POOL_TYPE = ? and PARAM_COLUMN = ?";
	private static final String COUNT_SQL  = "select count(*) from CONNECTION_POOL where POOL_TYPE = ? and PARAM_COLUMN = ?";
	private static final String LOAD_SQL   = "select PARAM_COLUMN, VALUE_COLUMN from CONNECTION_POOL where POOL_TYPE = ?";

	private static final String INSERT_EXPORT_SQL = "insert into CONNECTION_POOL_EXPORT (POOL_TYPE, XML_DATA) values (?, ?)";
	private static final String UPDATE_EXPORT_SQL = "update CONNECTION_POOL_EXPORT set XML_DATA = ? where POOL_TYPE = ?";
	private static final String COUNT_EXPORT_SQL  = "select count(*) from CONNECTION_POOL_EXPORT where POOL_TYPE = ?";

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ConnectionPoolRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
		Assert.notNull(dataSource, "Please define datasource for connection pool repository.");
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void store(TomcatJdbcPoolSettings s) {
		// Store params
		logger.debug("Storing values");
		for (String[] a : s.toParamsAndValues()) {
			String param = a[0];
			String value = a[1];
			storeParam(TJP, param, value);
		}
		
		// Generate XML ()
		logger.debug("Generating XML");
		StringBuilder xml = new StringBuilder();
		xml.append("<Resource");
		for (String[] a : s.toParamsAndValues()) {
			String param = a[0];
			String value = a[1];
			if (value != null && !"".equals(value)) {
				xml.append(" " + param + "=\"" + value.replaceAll("\"", "\\\"") + "\"");
			}
		}
		xml.append("/>");
		
		// Save XML
		logger.debug("Storing XML");
		int n = jdbcTemplate.queryForInt(COUNT_EXPORT_SQL, TJP);
		if (n == 0) {
			jdbcTemplate.update(INSERT_EXPORT_SQL, TJP, xml.toString());
		} else {
			jdbcTemplate.update(UPDATE_EXPORT_SQL, xml.toString(), TJP);
		}
	}

	/**
	 * Converts the parameter value to a form, which can be saved to database. 
	 */
	private String propToString(Object o) {
		if (o == null) {
			return null;
		} else {
			return o.toString();
		} 
	}

	/**
	 * Insert or update parameter to database.
	 */
	private void storeParam(String type, String param, String value) {
		int n = jdbcTemplate.queryForInt(COUNT_SQL, type, param);
		if (n == 0) {
			jdbcTemplate.update(INSERT_SQL, type, param, value);
		} else {
			jdbcTemplate.update(UPDATE_SQL, value, type, param);
		}
	}
	
	@Override
	public TomcatJdbcPoolSettings load() {
		TomcatJdbcPoolSettings s = new TomcatJdbcPoolSettings();
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(LOAD_SQL, TJP);
		for (Map a : rows) {
			Object param = a.get("PARAM_COLUMN");
			Object value_object = a.get("VALUE_COLUMN");
			if (value_object instanceof String) {
				String value = (String) value_object;
				if ("connectionProperties".equals(param)) {
					s.setConnectionProperties(value);
				} else if ("defaultCatalog".equals(param)) {
					s.setDefaultCatalog(value);
				} else if ("defaultTransactionIsolation".equals(param)) {
					s.setDefaultTransactionIsolation(value);
				} else if ("driverClassName".equals(param)) {
					s.setDriverClassName(value);
				} else if ("password".equals(param)) {
					s.setPassword(value);
				} else if ("username".equals(param)) {
					s.setUsername(value);
				} else if ("validationQuery".equals(param)) {
					s.setValidationQuery(value);
				} else if ("validatorClassName".equals(param)) {
					s.setValidatorClassName(value);
				} else if ("abandonWhenPercentageFull".equals(param)) {
					s.setAbandonWhenPercentageFull(new Integer(value));
				} else if ("accessToUnderlyingConnectionAllowed".equals(param)) {
					s.setAccessToUnderlyingConnectionAllowed(new Boolean(value));
				} else if ("alternateUsernameAllowed".equals(param)) {
					s.setAlternateUsernameAllowed(new Boolean(value));
				} else if ("defaultAutoCommit".equals(param)) {
					s.setDefaultAutoCommit(new Boolean(value));
				} else if ("defaultReadOnly".equals(param)) {
					s.setDefaultReadOnly(new Boolean(value));
				} else if ("fairQueue".equals(param)) {
					s.setFairQueue(new Boolean(value));
				} else if ("initialSize".equals(param)) {
					s.setInitialSize(new Integer(value));
				} else if ("jmxEnabled".equals(param)) {
					s.setJmxEnabled(new Boolean(value));
				} else if ("logAbandoned".equals(param)) {
					s.setLogAbandoned(new Boolean(value));
				} else if ("maxActive".equals(param)) {
					s.setMaxActive(new Integer(value));
				} else if ("maxAge".equals(param)) {
					s.setMaxAge(new Long(value));
				} else if ("maxIdle".equals(param)) {
					s.setMaxIdle(new Integer(value));
				} else if ("maxOpenPreparedStatements".equals(param)) {
					s.setMaxOpenPreparedStatements(new Integer(value));
				} else if ("maxWait".equals(param)) {
					s.setMaxWait(new Integer(value));
				} else if ("minEvictableIdleTimeMillis".equals(param)) {
					s.setMinEvictableIdleTimeMillis(new Integer(value));
				} else if ("minIdle".equals(param)) {
					s.setMinIdle(new Integer(value));
				} else if ("numTestsPerEvictionRun".equals(param)) {
					s.setNumTestsPerEvictionRun(new Integer(value));
				} else if ("poolPreparedStatements".equals(param)) {
					s.setPoolPreparedStatements(new Boolean(value));
				} else if ("removeAbandoned".equals(param)) {
					s.setRemoveAbandoned(new Boolean(value));
				} else if ("removeAbandonedTimeout".equals(param)) {
					s.setRemoveAbandonedTimeout(new Integer(value));
				} else if ("suspectTimeout".equals(param)) {
					s.setSuspectTimeout(new Integer(value));
				} else if ("testOnBorrow".equals(param)) {
					s.setTestOnBorrow(new Boolean(value));
				} else if ("testOnRun".equals(param)) {
					s.setTestOnRun(new Boolean(value));
				} else if ("testWhileIdle".equals(param)) {
					s.setTestWhileIdle(new Boolean(value));
				} else if ("timeBetweenEvictionRunsMillis".equals(param)) {
					s.setTimeBetweenEvictionRunsMillis(new Integer(value));
				} else if ("useEquals".equals(param)) {
					s.setUseEquals(new Boolean(value));
				} else if ("validationInterval".equals(param)) {
					s.setValidationInterval(new Long(value));
				} else {
					logger.warn("Unknown connection pool parameter '" + param + "' with value '" + value + "'");
				}
			}
		}
		
		return s;
	}
	
}
