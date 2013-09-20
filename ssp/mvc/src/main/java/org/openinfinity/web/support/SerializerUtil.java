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
package org.openinfinity.web.support;

import java.io.Writer;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializer for JSON.
 * 
 * @author Ilkka Leinonen
 */
public class SerializerUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SerializerUtil.class);
	
	public static void jsonSerialize(Writer writer, Object serializable) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(writer, serializable);
		} catch (Throwable t) {
			LOGGER.error(t.getMessage());
		} 
	}
	
}
