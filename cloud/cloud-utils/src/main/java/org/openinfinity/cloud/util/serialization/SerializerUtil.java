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
package org.openinfinity.cloud.util.serialization;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.Writer;

/**
 * Util for handling JSON serialization with writers.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.0.0
 *
 */
public class SerializerUtil {

	public static void jsonSerialize(Writer writer, Object serializable) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(writer, serializable);
		} catch (Throwable t) {
		 throw new RuntimeException(t);	
		} 
	}
	
}
