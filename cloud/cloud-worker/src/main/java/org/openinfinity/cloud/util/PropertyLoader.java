/*
 * Copyright (c) 2011 the original author or authors.
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

package org.openinfinity.cloud.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Property loader utility
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public class PropertyLoader {
	private static final Logger LOG = Logger.getLogger(PropertyLoader.class.getName());
	
	public static Properties getProperties(final String configLocation) {
		final Properties props = new Properties();
		try {
			final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configLocation);
			if(is != null) {
				props.load(is);
				is.close();
			}
		} catch (IOException e) {
			LOG.fatal("Can not load property file "+configLocation, e);
		}
		
		return props;
	}
}
