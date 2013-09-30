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
package org.openinfinity.domain.entity;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.openinfinity.core.annotation.NotScript;

/**
 * SSP User entity.
 * 
 * @author Vedran Bartonicek
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude={"id"})
public class User {
	
	@NotScript @NotNull @NonNull
	private BigInteger id;
	
	@NotScript @NotNull @NonNull
	private BigInteger accountId;
	
	@NotScript @NotNull @NonNull
	private String username;
	
	@NotScript @NotNull @NonNull
	private String password;
	
	@NotScript @NotNull @NonNull
	private String firstName;
	
	@NotScript @NotNull @NonNull
	private String lastName;
	
	@NotScript @NotNull @NonNull
	private String phone;
		
	@NotScript @NotNull @NonNull
	private String email;
	
	@NotScript @NotNull @NonNull
	private String addressLine1;
	
	@NotScript @NotNull @NonNull
	private String addressLine2;
	
	@NotScript @NotNull @NonNull
	private String city;
	
	@NotScript @NotNull @NonNull
	private String stateProvinceRegion;
	
	@NotScript @NotNull @NonNull
	private String postalCode;	
	
}
