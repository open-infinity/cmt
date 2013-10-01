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
package org.openinfinity.ssp.domain.repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.domain.entity.User;
import org.openinfinity.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.Assert;

/**
 * User repository implementation.
 * 
 * @author Vedran Bartonicek
 */
public class UserRepositoryJdbcImpl implements UserRepository{

	private static final Logger LOG = Logger.getLogger(UserRepositoryJdbcImpl.class.getName());

	private JdbcTemplate jdbcTemplate;

    private DataSource dataSource;

    @Autowired
	public UserRepositoryJdbcImpl(@Qualifier("cloudDataSource") DataSource dataSource) {
		Assert.notNull(dataSource, "Please define datasource for instance repository.");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
	
	@AuditTrail
	public User create(final User user){
		SimpleJdbcInsert insert = new SimpleJdbcInsert(dataSource).withTableName("user_tbl").usingGeneratedKeyColumns("id");
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("username", user.getUsername());
		parameters.put("password", user.getPassword());
		parameters.put("first_name", user.getFirstName());
		parameters.put("last_name", user.getLastName());
		parameters.put("phone", user.getPhone());
		parameters.put("email", user.getEmail());
		parameters.put("address_line_1", user.getAddressLine1());
		parameters.put("address_line_2", user.getAddressLine2());
		parameters.put("city", user.getCity());
		parameters.put("state_province_region", user.getStateProvinceRegion());
		parameters.put("postalCode", user.getPostalCode());
		LOG.info(parameters.toString());
		Number id = insert.executeAndReturnKey(parameters);
		user.setId(BigInteger.valueOf((Long)id));
		return user;
	}

	@AuditTrail
	public void update(final User user) {
		jdbcTemplate.update("update user_tbl set username = ?, password = ?, first_name = ?, last_name = ?, phone = ?, email = ?, address_line_1 = ?,"
				+ " address_line_2 = ?, city = ?, state_province_region = ?, postalCode = ?", 
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, user.getUsername());
						ps.setString(2, user.getPassword());
						ps.setString(3, user.getFirstName());
						ps.setString(4, user.getLastName());
						ps.setString(5, user.getPhone());
						ps.setString(6, user.getEmail());
						ps.setString(7, user.getAddressLine1());
						ps.setString(8, user.getAddressLine2());
						ps.setString(9, user.getCity());
						ps.setString(10, user.getStateProvinceRegion());
						ps.setString(11, user.getPostalCode());
					}
				}
		);
	}
	
	@AuditTrail
	public Collection<User> loadAll(){
		return this.jdbcTemplate.query("select * from user_tbl", new UserWrapper());		
	}
	
	@AuditTrail
	public User loadById(BigInteger id){
		return this.jdbcTemplate.queryForObject("select * from user_tbl where user_id = ?", new Object[] { id }, new UserWrapper());
	}
	
	@AuditTrail
	public User loadByUsername(String username){
		return this.jdbcTemplate.queryForObject("select * from user_tbl where username = ?", new Object[] { username }, new UserWrapper());
	}
	
	@AuditTrail
	public BigInteger idByUsername(String username){
		return BigInteger.valueOf(this.jdbcTemplate.queryForInt("select user_id from user_tbl where username = ?", new Object[] { username }, new UserWrapper()));
	}

	@AuditTrail
	public void delete (User user){}
	
	private static final class UserWrapper implements RowMapper<User> {
		public User mapRow(ResultSet rs, int rowNumber) throws SQLException {
			User user = new User();
			user.setId(BigInteger.valueOf(rs.getInt("id")));
			user.setUsername(rs.getString("username"));
			user.setPassword(rs.getString("password"));
			user.setFirstName(rs.getString("first_name"));
			user.setLastName(rs.getString("last_name"));
			user.setPhone(rs.getString("phone"));
			user.setEmail(rs.getString("email"));
			user.setAddressLine1(rs.getString("address_line_1"));
			user.setAddressLine2(rs.getString("address_line_2"));
			user.setCity(rs.getString("city"));
			user.setStateProvinceRegion(rs.getString("state_province_region"));
			user.setPostalCode(rs.getString("postalCode"));
			return user;
		}
	}
}
