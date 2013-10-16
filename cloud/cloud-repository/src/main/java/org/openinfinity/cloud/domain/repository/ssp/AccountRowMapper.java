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

package org.openinfinity.cloud.domain.repository.ssp;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openinfinity.cloud.domain.ScalingRule;
import org.openinfinity.cloud.domain.ssp.Account;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
@Component("accountRuleRowMapper")
public class AccountRowMapper implements RowMapper<Account> { 
	public Account mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		Account account = new Account();
		return account;
	}	
}