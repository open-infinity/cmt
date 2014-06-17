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

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Property manager utility
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public class PropertyManager {
	private static final Logger LOG = Logger.getLogger(PropertyManager.class.getName());
	private static final String PROPERTY_SOURCE = "cloudadminworker.properties";
	private static Properties props = PropertyLoader.getProperties(PROPERTY_SOURCE);
	
	private PropertyManager() {
		
	}
	
	public static String getProperty(final String key) {
		String value = null;
		if(key != null) {
			value = props.getProperty(key);
		
			if(value == null) {
				LOG.error("Can't find value for property key: "+key+" from "+PROPERTY_SOURCE);
			} else {
				LOG.debug("Property manager: key : "+key+", "+value);
			}
		}
		if(value != null) {
			return value.trim();
		} else {
			return value;
		}
	}
	
	public static String getProperty(final String key, final String defaultValue) {
		String value = null;
		if(key != null) {
			value = props.getProperty(key);
			
			if(value == null) {
				LOG.warn("Can't find value for property key: "+key+" from "+PROPERTY_SOURCE+". Using default value "+defaultValue);
				value = defaultValue;
			} else {
				LOG.debug("Property manager: key: "+key+", "+value);
			}	
		}
		if(value != null) {
			return value.trim();
		} else {
			return value;
		}
	}
}
