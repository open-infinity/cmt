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

package org.openinfinity.cloud.ssp.billing.invoicecreator;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openinfinity.cloud.domain.ssp.Account;
import org.openinfinity.cloud.service.administrator.ClusterService;
import org.openinfinity.cloud.service.administrator.InstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;

import static org.mockito.Mockito.when;

/**
 * Unit tests for Scheduled scaler.
 *
 * @author Vedran Bartonicek
 * @version 1.3.0
 * @since 1.3.0
 */

@ContextConfiguration(locations={"classpath*:META-INF/spring/cloud-autoscaler-test-unit-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class InvoiceCreatorItemProcessorTest {

    @InjectMocks
    @Autowired
    InvoiceCreatorItemProcessor itemProcessor;

    @Mock
    ClusterService mockClusterService;

    @Mock
    InstanceService mockInstanceService;

    @Mock
    Account mockAccount;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @Ignore
    public void simpleWriterTest() throws Exception {

        //insert into account (organization_id, name, state)
        //values(10687, 'test account', 1);

        when(mockAccount.getId()).thenReturn(BigInteger.valueOf(1));
        when(mockAccount.getName()).thenReturn("test account");
        when(mockAccount.getOrganizationId()).thenReturn(BigInteger.valueOf(10687));
        when(mockAccount.getState()).thenReturn(1);

        Assert.assertNotNull(itemProcessor.process(mockAccount));

        /*
        Cluster cluster = new Cluster();
        cluster.setInstanceId(1);
        cluster.setNumberOfMachines(10);
        when(mockClusterService.getCluster(1)).thenReturn(cluster);

        Instance instance = new Instance();
        instance.setCloudType(1);
        instance.setZone("whatever");
        when(mockInstanceService.getInstance(1)).thenReturn(instance);

        Assert.assertNotNull(itemProcessor.process(mockAccount));
        verify(scalingRuleService).storeScalingOutParameters(10,1);
        */
    }

}

