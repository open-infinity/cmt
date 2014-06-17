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

package org.openinfinity.cloud.domain;

import java.io.Serializable;

import org.openinfinity.core.annotation.NotScript;

import lombok.Data;

/**
 * Domain class is defining cluster backup schedule(s).
 * The cron fields follow the common cron syntax, used in
 * Unix Cron, CrontTrigger of Quartz Scheduler and Spring Batch.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Cron#CRON_expression">Cron expressions</a>
 * @see <a href="http://quartz-scheduler.org/documentation/quartz-2.2.x/tutorials/crontrigger">Quartz CronTrigger</a>
 *
 * @author Timo Saarinen
 */
@Data
public class BackupRule implements Serializable {
	
	@NotScript
	private int id;
	
	@NotScript
	private int cluster_id;

	@NotScript
	private boolean active;
	
	@NotScript
	private String cronMinutes;
	
	@NotScript
	private String cronHours;
	
	@NotScript
	private String cronDayOfMonth;
	
	@NotScript
	private String cronMonth;
	
	@NotScript
	private String cronDayOfWeek;
	
	@NotScript
	private String cronYear;
}
