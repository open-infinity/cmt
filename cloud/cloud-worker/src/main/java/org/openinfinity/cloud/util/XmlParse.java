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

import java.util.ArrayList;
import java.util.List;


/**
 * XmlParse xml parsing utility
 * @author Ossi Hämäläinen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public class XmlParse {
	public static List<String> getValues(String xml, String tag) throws WorkerException {
		String xmlString = new String(xml);
		List<String> l = new ArrayList<String>();
		String startTag = "<" + tag + ">";
		String endTag = "</" + tag + ">";

		int startTagIndex = xmlString.indexOf(startTag);

		while (startTagIndex != -1) {
			int endTagIndex = xmlString.indexOf(endTag);
			if ((endTagIndex == -1) || (endTagIndex < startTagIndex)) {
				throw new WorkerException("Error in xml parse");
			}
			String value = xmlString.substring((startTagIndex + startTag.length()), endTagIndex);
			l.add(value);
			try {
				xmlString = xmlString.substring(endTagIndex + endTag.length());
			} catch (Exception e) {
				xmlString = "";
			}
			startTagIndex = xmlString.indexOf(startTag);
		}
		return l;
	}
}
