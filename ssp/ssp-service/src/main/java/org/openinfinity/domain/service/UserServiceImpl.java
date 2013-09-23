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
package org.openinfinity.domain.service;

import java.math.BigInteger;
import java.util.Collection;

import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.openinfinity.domain.entity.User;
import org.springframework.stereotype.Service;

/**
 * User service implementation.
 * 
 * @author Vedran Bartonicek
 */
@Service
public class UserServiceImpl implements UserService {

	@Override
	@Log
	@AuditTrail
	public User create(User product) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Log
	@AuditTrail
	public void update(User product) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Log
	@AuditTrail
	public Collection<User> loadAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Log
	@AuditTrail
	public User loadById(BigInteger id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Log
	@AuditTrail
	public void delete(User product) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Log
	@AuditTrail
	public User loadByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
