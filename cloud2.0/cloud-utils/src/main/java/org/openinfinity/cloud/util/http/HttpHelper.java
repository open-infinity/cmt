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
package org.openinfinity.cloud.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * 
 * @author Ivan Bilechyk
 * @author Ilkka Leinonen
 * @author Timo Saarinen
 * @author Nishant Gupta
 * @author Vedran Bartonicek
 */
public class HttpHelper {

    private static final String EXCEPTION_WHILE_EXECUTING_HTTP_REQUEST = "Exception while executing http request";
    private static final Logger LOG = Logger.getLogger(HttpHelper.class.getName());

    public static String executeHttpRequest(HttpClient client, String url) {
        HttpUriRequest request = new HttpGet(url);

        try {
            HttpResponse response = client.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            }
        } catch (ClientProtocolException e) {
            LOG.warn(EXCEPTION_WHILE_EXECUTING_HTTP_REQUEST +"----" +e.getMessage());
        } catch (IOException e) {
            LOG.warn(EXCEPTION_WHILE_EXECUTING_HTTP_REQUEST +"----" +e.getMessage());
        } catch (Exception e) {
            LOG.warn(EXCEPTION_WHILE_EXECUTING_HTTP_REQUEST +"----" +e.getMessage());
        }
        return null;
    }
}
