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

package org.openinfinity.cloud.domain.repository.scaling;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openinfinity.cloud.domain.ScalingRule;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * Implementation if RowMapper interface for scaling_rule_tbl.
 * 
 * @author Vedran Bartonicek
 * @version 1.0.0
 * @since 1.0.0
 */
@Component("scalingRuleRowMapper")
public class ScalingRuleRowMapper implements RowMapper<ScalingRule> { 
	public ScalingRule mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		ScalingRule scalingRule = new ScalingRule(
			resultSet.getInt("cluster_id"),
			resultSet.getBoolean("periodic"),
			resultSet.getBoolean("scheduled"),
			resultSet.getInt("scaling_state"),
			resultSet.getInt("max_machines"),
			resultSet.getInt("min_machines"),
			resultSet.getFloat("max_load"),
			resultSet.getFloat("min_load"),
			resultSet.getTimestamp("period_from"),
			resultSet.getTimestamp("period_to"),
			resultSet.getInt("size_new"),
			resultSet.getInt("size_original"),
			resultSet.getInt("job_id"));
		return scalingRule;
	}	
}