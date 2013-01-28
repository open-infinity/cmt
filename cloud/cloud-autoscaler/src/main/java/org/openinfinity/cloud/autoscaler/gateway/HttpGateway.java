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

package org.openinfinity.cloud.autoscaler.gateway;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openinfinity.core.util.ExceptionUtil;

/**
 * HTTP gateway util for accessing rest based services.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpGateway {
	
	private static HttpClient HTTP_CLIENT;
	
	static {
		HTTP_CLIENT = new HttpClient(new MultiThreadedHttpConnectionManager());
		HTTP_CLIENT.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
	}
	
	public static InputStream get(String url) {
		InputStream inputStream = null;
		GetMethod method = null;
		try {
			method = new GetMethod(url);
			method.setFollowRedirects(true);
			int statusCode = HTTP_CLIENT.executeMethod(method);
			if (statusCode == 200) {
				inputStream = method.getResponseBodyAsStream();
				return inputStream;
			} 
		} catch (Throwable throwable) {
				ExceptionUtil.throwSystemException(throwable);
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return inputStream;
	}

}
