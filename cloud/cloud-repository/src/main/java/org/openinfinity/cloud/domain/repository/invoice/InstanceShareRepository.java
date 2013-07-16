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

package org.openinfinity.cloud.domain.repository.invoice;

import java.util.Date;
import java.util.List;

import org.openinfinity.cloud.domain.InstanceShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Interface for InstanceShare repository
 
 * @author Pasi Kilponen
 * @version 1.0.0 Initial version
 * @since 1.0.0
 */

public interface InstanceShareRepository extends JpaRepository<InstanceShare, Long>{
    
    @Query("select u from InstanceShare u where u.instanceTbl.instanceId = ?1")
    List<InstanceShare> findByInstanceId(long instanceId);
    
    @Query("select u from InstanceShare u where u.instanceTbl.instanceId = ?1 and u.periodStart <= ?2 order by u.periodStart desc")
    List<InstanceShare> findByInstanceIdAndPeriodStart(long instanceId, Date periodStart);
    
}
