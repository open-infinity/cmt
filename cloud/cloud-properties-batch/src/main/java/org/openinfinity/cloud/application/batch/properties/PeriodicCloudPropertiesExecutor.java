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

package org.openinfinity.cloud.application.batch.properties;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class for running cloud properties patch process.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
public class PeriodicCloudPropertiesExecutor implements Daemon {

	private ClassPathXmlApplicationContext applicationContext = null;

	@Override
	public void destroy() {
		if (this.applicationContext != null) {
			this.applicationContext.close();
		}
	}

	@Override
	public void init(DaemonContext arg0) throws DaemonInitException, Exception {
		this.applicationContext = new ClassPathXmlApplicationContext("/cloud-properties-batch-context.xml");
	}

	@Override
	public void start() throws Exception {
		this.applicationContext.start();
	}

	@Override
	public void stop() throws Exception {
		if (this.applicationContext != null) {
			this.applicationContext.stop();
		}
	}

}