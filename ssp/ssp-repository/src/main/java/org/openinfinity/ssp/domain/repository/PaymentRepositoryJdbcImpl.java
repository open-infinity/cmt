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
import java.util.ArrayList;
import java.util.Collection;

import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.openinfinity.domain.entity.Payment;

/**
 * Payment repository implementation.
 * 
 * @author Vedran Bartonicek
 */
public class PaymentRepositoryJdbcImpl {

	@Log
	@AuditTrail
	public Payment create(Payment product){
		return new Payment();
	}
	
	@Log
	@AuditTrail
	public void update(Payment product){}
	
	@Log
	@AuditTrail
	public Collection<Payment> loadAll(){
		return new ArrayList<Payment>();
	}
	
	@Log
	@AuditTrail
	public Payment loadById(BigInteger id){
		return new Payment();
	}
	
	@Log
	@AuditTrail
	public void delete (Payment product){}
}
