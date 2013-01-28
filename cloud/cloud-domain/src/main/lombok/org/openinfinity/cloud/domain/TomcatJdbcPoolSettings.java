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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.*;


/**
 * Connection pool related data. Null values are considered as default values.
 *
 * @see http://people.apache.org/~fhanik/jdbc-pool/jdbc-pool.html
 * 
 * @author Timo Saarinen
 */
public class TomcatJdbcPoolSettings {
	
	//
	// Common Attributes (null values are considered as default)
	//
	
	private Boolean defaultAutoCommit;
	private Boolean defaultReadOnly;
	private String defaultTransactionIsolation; // NONE, READ_COMMITTED, READ_UNCOMMITTED, REPEATABLE_READ, SERIALIZABLE
    
    private String defaultCatalog; // (bold)
    private String driverClassName; // (bold)
    private String username; // (bold)
    private String password; // (bold)
    
    @Size(min=1)
    private Integer maxActive; // default is 100
    @Size(min=1)
    private Integer maxIdle; // default is from maxActive
    @Size(min=1)
    private Integer minIdle; // default is from initialSize
    @Size(min=1)
    private Integer initialSize; // default 10
    private Integer maxWait; // default 30000 s
    
    private Boolean testOnBorrow;    
    private Boolean testOnRun;
    private Boolean testWhileIdle;
    
    private String validationQuery;
    private String validatorClassName;
    
    private Integer timeBetweenEvictionRunsMillis;
    private Integer numTestsPerEvictionRun;
    private Integer minEvictableIdleTimeMillis;
    
    private Boolean accessToUnderlyingConnectionAllowed;
    private Boolean removeAbandoned;
    private Integer removeAbandonedTimeout;
    private Boolean logAbandoned;
    private String connectionProperties;
    
    private Boolean poolPreparedStatements; // default: false
    @Size(min=1)
    private Integer maxOpenPreparedStatements;

    
	//
	// Tomcat JDBC Enhanced Attributes (null values are considered as default)
	//
    
	//private String initSQL; // default: null
	//private String jdbcInterceptors; // default: null
	private Long validationInterval; // default: 30000 ms
	private Boolean jmxEnabled; // default: true
	private Boolean fairQueue; // default: true
	private Integer abandonWhenPercentageFull; // default: 0
	private Long maxAge; // default: 0
	private Boolean useEquals; // default: true
	private Integer suspectTimeout; // default: 0
	private Boolean alternateUsernameAllowed; // default: false
	//private javax.sql.DataSource dataSource;
	//private String dataSourceJNDI;

	public TomcatJdbcPoolSettings() {
	}

	/**
	 * Returns list of string pairs, where the first member is name of field and the latter is value.
	 * @return
	 */
	public List<String[]> toParamsAndValues() {
		List<String[]> r = new LinkedList<String[]>();
		
		r.add(new String[] {"defaultAutoCommit", propToString(defaultAutoCommit)});
		r.add(new String[] {"defaultReadOnly", propToString(defaultReadOnly)});
		r.add(new String[] {"defaultTransactionIsolation", propToString(defaultTransactionIsolation)});
		r.add(new String[] {"defaultCatalog", propToString(defaultCatalog)});
		r.add(new String[] {"driverClassName", propToString(driverClassName)});
		
		r.add(new String[] {"username", propToString(username)});
		r.add(new String[] {"password", propToString(password)});

		r.add(new String[] {"maxActive", propToString(maxActive)});
		r.add(new String[] {"maxIdle", propToString(maxIdle)});
		r.add(new String[] {"minIdle", propToString(minIdle)});

		r.add(new String[] {"initialSize", propToString(initialSize)});
		r.add(new String[] {"maxWait", propToString(maxWait)});

		r.add(new String[] {"testOnBorrow", propToString(testOnBorrow)});
		r.add(new String[] {"testOnRun", propToString(testOnRun)});
		r.add(new String[] {"testWhileIdle", propToString(testWhileIdle)});

		r.add(new String[] {"validationQuery", propToString(validationQuery)});
		r.add(new String[] {"validatorClassName", propToString(validatorClassName)});

		r.add(new String[] {"timeBetweenEvictionRunsMillis", propToString(timeBetweenEvictionRunsMillis)});
		r.add(new String[] {"numTestsPerEvictionRun", propToString(numTestsPerEvictionRun)});
		r.add(new String[] {"minEvictableIdleTimeMillis", propToString(minEvictableIdleTimeMillis)});

		r.add(new String[] {"accessToUnderlyingConnectionAllowed", propToString(accessToUnderlyingConnectionAllowed)});
		r.add(new String[] {"removeAbandoned", propToString(removeAbandoned)});
		r.add(new String[] {"removeAbandonedTimeout", propToString(removeAbandonedTimeout)});
		r.add(new String[] {"logAbandoned", propToString(logAbandoned)});
		r.add(new String[] {"connectionProperties", propToString(connectionProperties)});
		r.add(new String[] {"poolPreparedStatements", propToString(poolPreparedStatements)});
		r.add(new String[] {"maxOpenPreparedStatements", propToString(maxOpenPreparedStatements)});
		r.add(new String[] {"validationInterval", propToString(validationInterval)});
		r.add(new String[] {"jmxEnabled", propToString(jmxEnabled)});
		r.add(new String[] {"fairQueue", propToString(fairQueue)});
		r.add(new String[] {"abandonWhenPercentageFull", propToString(abandonWhenPercentageFull)});
		r.add(new String[] {"maxAge", propToString(maxAge)});
		r.add(new String[] {"useEquals", propToString(useEquals)});
		r.add(new String[] {"suspectTimeout", propToString(suspectTimeout)});
		r.add(new String[] {"alternateUsernameAllowed", propToString(alternateUsernameAllowed)});
		
		return r;
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

	// ------------------------------------------------------------------------
	
	public Boolean getDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}

	public Boolean getDefaultReadOnly() {
		return defaultReadOnly;
	}

	public void setDefaultReadOnly(Boolean defaultReadOnly) {
		this.defaultReadOnly = defaultReadOnly;
	}

	public String getDefaultTransactionIsolation() {
		return defaultTransactionIsolation;
	}

	public void setDefaultTransactionIsolation(String value) {
		if (value == null || "".equals(value)) {
			defaultTransactionIsolation = null;
		} else if ("NONE".equals(value) || 
				"READ_COMMITTED".equals(value) ||
				"READ_UNCOMMITTED".equals(value) ||
				"REPEATABLE_READ".equals(value) ||
				"SERIALIZABLE".equals(value)) 
		{
			defaultTransactionIsolation = value;
		} else {
			throw new IllegalArgumentException("Accepted values for Default Transaction Isolation are: NONE, READ_COMMITTED, READ_UNCOMMITTED, REPEATABLE_READ and SERIALIZABLE");
		}
	}

	public String getDefaultCatalog() {
		return defaultCatalog;
	}

	public void setDefaultCatalog(String defaultCatalog) {
		this.defaultCatalog = defaultCatalog;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}

	public Integer getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}

	public Integer getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}

	public Integer getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(Integer initialSize) {
		this.initialSize = initialSize;
	}

	public Integer getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}

	public Boolean getTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(Boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public Boolean getTestOnRun() {
		return testOnRun;
	}

	public void setTestOnRun(Boolean testOnRun) {
		this.testOnRun = testOnRun;
	}

	public Boolean getTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(Boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public String getValidatorClassName() {
		return validatorClassName;
	}

	public void setValidatorClassName(String validatorClassName) {
		this.validatorClassName = validatorClassName;
	}

	public Integer getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(
			Integer timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public Integer getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	public void setNumTestsPerEvictionRun(Integer numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public Integer getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public Boolean getAccessToUnderlyingConnectionAllowed() {
		return accessToUnderlyingConnectionAllowed;
	}

	public void setAccessToUnderlyingConnectionAllowed(
			Boolean accessToUnderlyingConnectionAllowed) {
		this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
	}

	public Boolean getRemoveAbandoned() {
		return removeAbandoned;
	}

	public void setRemoveAbandoned(Boolean removeAbandoned) {
		this.removeAbandoned = removeAbandoned;
	}

	public Integer getRemoveAbandonedTimeout() {
		return removeAbandonedTimeout;
	}

	public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
		this.removeAbandonedTimeout = removeAbandonedTimeout;
	}

	public Boolean getLogAbandoned() {
		return logAbandoned;
	}

	public void setLogAbandoned(Boolean logAbandoned) {
		this.logAbandoned = logAbandoned;
	}

	public String getConnectionProperties() {
		return connectionProperties;
	}

	public void setConnectionProperties(String connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

	public Boolean getPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public Integer getMaxOpenPreparedStatements() {
		return maxOpenPreparedStatements;
	}

	public void setMaxOpenPreparedStatements(Integer maxOpenPreparedStatements) {
		this.maxOpenPreparedStatements = maxOpenPreparedStatements;
	}

	public Long getValidationInterval() {
		return validationInterval;
	}

	public void setValidationInterval(Long validationInterval) {
		this.validationInterval = validationInterval;
	}

	public Boolean getJmxEnabled() {
		return jmxEnabled;
	}

	public void setJmxEnabled(Boolean jmxEnabled) {
		this.jmxEnabled = jmxEnabled;
	}

	public Boolean getFairQueue() {
		return fairQueue;
	}

	public void setFairQueue(Boolean fairQueue) {
		this.fairQueue = fairQueue;
	}

	public Integer getAbandonWhenPercentageFull() {
		return abandonWhenPercentageFull;
	}

	public void setAbandonWhenPercentageFull(Integer abandonWhenPercentageFull) {
		this.abandonWhenPercentageFull = abandonWhenPercentageFull;
	}

	public Long getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Long maxAge) {
		this.maxAge = maxAge;
	}

	public Boolean getUseEquals() {
		return useEquals;
	}

	public void setUseEquals(Boolean useEquals) {
		this.useEquals = useEquals;
	}

	public Integer getSuspectTimeout() {
		return suspectTimeout;
	}

	public void setSuspectTimeout(Integer suspectTimeout) {
		this.suspectTimeout = suspectTimeout;
	}

	public Boolean getAlternateUsernameAllowed() {
		return alternateUsernameAllowed;
	}

	public void setAlternateUsernameAllowed(Boolean alternateUsernameAllowed) {
		this.alternateUsernameAllowed = alternateUsernameAllowed;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TomcatJdbcPoolSettings [defaultAutoCommit=");
		builder.append(defaultAutoCommit);
		builder.append(", defaultReadOnly=");
		builder.append(defaultReadOnly);
		builder.append(", defaultTransactionIsolation=");
		builder.append(defaultTransactionIsolation);
		builder.append(", defaultCatalog=");
		builder.append(defaultCatalog);
		builder.append(", driverClassName=");
		builder.append(driverClassName);
		builder.append(", username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", maxActive=");
		builder.append(maxActive);
		builder.append(", maxIdle=");
		builder.append(maxIdle);
		builder.append(", minIdle=");
		builder.append(minIdle);
		builder.append(", initialSize=");
		builder.append(initialSize);
		builder.append(", maxWait=");
		builder.append(maxWait);
		builder.append(", testOnBorrow=");
		builder.append(testOnBorrow);
		builder.append(", testOnRun=");
		builder.append(testOnRun);
		builder.append(", testWhileIdle=");
		builder.append(testWhileIdle);
		builder.append(", validationQuery=");
		builder.append(validationQuery);
		builder.append(", validatorClassName=");
		builder.append(validatorClassName);
		builder.append(", timeBetweenEvictionRunsMillis=");
		builder.append(timeBetweenEvictionRunsMillis);
		builder.append(", numTestsPerEvictionRun=");
		builder.append(numTestsPerEvictionRun);
		builder.append(", minEvictableIdleTimeMillis=");
		builder.append(minEvictableIdleTimeMillis);
		builder.append(", accessToUnderlyingConnectionAllowed=");
		builder.append(accessToUnderlyingConnectionAllowed);
		builder.append(", removeAbandoned=");
		builder.append(removeAbandoned);
		builder.append(", removeAbandonedTimeout=");
		builder.append(removeAbandonedTimeout);
		builder.append(", logAbandoned=");
		builder.append(logAbandoned);
		builder.append(", connectionProperties=");
		builder.append(connectionProperties);
		builder.append(", poolPreparedStatements=");
		builder.append(poolPreparedStatements);
		builder.append(", maxOpenPreparedStatements=");
		builder.append(maxOpenPreparedStatements);
		builder.append(", validationInterval=");
		builder.append(validationInterval);
		builder.append(", jmxEnabled=");
		builder.append(jmxEnabled);
		builder.append(", fairQueue=");
		builder.append(fairQueue);
		builder.append(", abandonWhenPercentageFull=");
		builder.append(abandonWhenPercentageFull);
		builder.append(", maxAge=");
		builder.append(maxAge);
		builder.append(", useEquals=");
		builder.append(useEquals);
		builder.append(", suspectTimeout=");
		builder.append(suspectTimeout);
		builder.append(", alternateUsernameAllowed=");
		builder.append(alternateUsernameAllowed);
		builder.append("]");
		return builder.toString();
	}
}
