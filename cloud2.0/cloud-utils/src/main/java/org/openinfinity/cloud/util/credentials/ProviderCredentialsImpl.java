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
package org.openinfinity.cloud.util.credentials;

import org.jets3t.service.security.ProviderCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

/**
 * Provider credentials extension for accessing infrastructure services.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@Profile("jets3t")
@ImportResource("classpath:/META-INF/spring/cloud-infrastructure-configuration.xml")
public class ProviderCredentialsImpl extends ProviderCredentials {

	
//	public ProviderCredentialsImpl(@Value("${eucaaccesskeyid}") String accessKey, @Value("${eucasecretkey}") String secretKey) {
//		super(accessKey, secretKey);
//	}
	
	public ProviderCredentialsImpl(String accessKey, String secretKey) {
		super(accessKey, secretKey);
	}

	@Override
	protected String getTypeName() {
		return "";
	}

	@Override
	protected String getVersionPrefix() {
		return V3_KEYS_DELIMITER;
	}
	
	
}
