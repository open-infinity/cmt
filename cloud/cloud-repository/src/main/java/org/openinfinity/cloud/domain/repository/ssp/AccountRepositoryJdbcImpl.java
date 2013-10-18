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
package org.openinfinity.cloud.domain.repository.ssp;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.cloud.domain.ssp.Account;
import org.openinfinity.core.annotation.AuditTrail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * Account repository implementation.
 * 
 * @author Vedran Bartonicek
 */
@Repository
public class AccountRepositoryJdbcImpl implements AccountRepository{

	private static final Logger LOG = Logger.getLogger(AccountRepositoryJdbcImpl.class.getName());

	private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    @Autowired
	public AccountRepositoryJdbcImpl(@Qualifier("sspDataSource") DataSource dataSource) {
		Assert.notNull(dataSource, "Please define datasource for instance repository.");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
	
	@AuditTrail
	public Account create(final Account account){
		SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("account").usingGeneratedKeyColumns("id");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("organization_id", account.getOrganizationId());
		parameters.put("name", account.getName());
		parameters.put("payment_status", account.getStatus());

		Number id = insert.executeAndReturnKey(parameters);
		account.setId(BigInteger.valueOf((Long)id));
		return account;
	}

	@AuditTrail
	public void update(final Account account) {
		jdbcTemplate.update("update account set organization_id = ?, name = ?, payment_status = ?", 
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, account.getOrganizationId().intValue());
						ps.setString(2, account.getName());
						ps.setInt(2, account.getStatus());
					}
				}
		);
	}
	
	@AuditTrail
	public Collection<Account> loadAll(){
		return this.jdbcTemplate.query("select * from account", new AccountWrapper());		
	}
	
	@AuditTrail
	public Account load(BigInteger id){
		return this.jdbcTemplate.queryForObject("select * from account where id = ?", new Object[] { id }, new AccountWrapper());
	}
		
	@AuditTrail
	public void delete (Account account){}
	
	private static final class AccountWrapper implements RowMapper<Account> {
		public Account mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Account account = new Account();
			account.setId(BigInteger.valueOf(rs.getInt("id")));
			account.setOrganizationId(BigInteger.valueOf(rs.getInt("organization_id")));
			account.setName(rs.getString("name"));
			account.setStatus(rs.getInt("status"));
			return account;
		}
	}

	@Override
	public Account loadByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}
}
