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

package org.openinfinity.cloud.application.deployer.batch;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class for running cloud deployer patch process.
 * 
 * @author Ilkka Leinonen
 * @version 1.0.0
 * @since 1.2.0
 */
public class PeriodicCloudDeployerExecutor {
	
	private static ApplicationContext APPLICATION_CONTEXT;
	
	private static boolean SHUTDOWN_FLAG = false;
	
	public static void main(String[] args) {
		APPLICATION_CONTEXT = new ClassPathXmlApplicationContext("/cloud-deployer-batch-context.xml", PeriodicCloudDeployerExecutor.class);

//		final Thread mainThread = Thread.currentThread();
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//		    public void run() {
//		    	SHUTDOWN_FLAG = false;
//		    	
//		        try {
//					mainThread.join();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		        ((AbstractApplicationContext) APPLICATION_CONTEXT).destroy();
//		    }
//		});
	
	}
	
}
