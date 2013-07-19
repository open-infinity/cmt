package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceTbl;
import org.openinfinity.cloud.domain.repository.administrator.InstanceJPARepository;
import org.openinfinity.cloud.domain.repository.invoice.InstanceShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

//@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context-mysql.xml"})
@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class InstanceJPARepositoryTest {
    
    
    @Autowired
    @Qualifier("cloudDataSource")
    DataSource ds;
    
    @Autowired
    InstanceJPARepository repository;
    
    @Autowired
    InstanceShareRepository instanceShareRepository;
    
    private InstanceTbl createInstance(String instanceName){
        InstanceTbl instance=new InstanceTbl();
        instance.setInstanceName(instanceName);
        instance.setOrganizationId(1);
        instance.setInstanceStatus("Starting");
        instance.setCloudType(1);
        //instance.setInstanceId(instanceId++);
        instance.setUserId(1);
        
        return instance;
    }
    
    public static java.sql.Timestamp dateToSqlTimestamp(java.util.Date date){
        if (date==null){
            return null;
        }
        return new java.sql.Timestamp(date.getTime());

    }

    
    private InstanceShare createInstanceShare(){
        InstanceShare instanceShare=new InstanceShare();
        Timestamp created=dateToSqlTimestamp(new Date());
        instanceShare.setCreated(created);
        int createdBy=1;
        instanceShare.setCreatedBy(createdBy);
        instanceShare.setModified(created);
        instanceShare.setModifiedBy(1);
        instanceShare.setPeriodStart(new Date());
        return instanceShare;
    }

    @Transactional
    private InstanceTbl createInstanceAndSave(String instanceName){
        //Save
        InstanceTbl instance=createInstance(instanceName);
        
        InstanceShare instanceShare = createInstanceShare();
        instanceShare.setInstanceTbl(instance);
        instance.addInstanceShareTbl(instanceShare);
        
        //return repository.saveAndFlush(instance);
        return repository.save(instance);
        
    }

    @Test
    @Transactional
    @Rollback(value=false)
    public void testInstanceTbl() {
        assertNotNull(repository);
        
        //record count of instances before adding new
        int countOfInstances = repository.findAll().size();
        
        //create one new instance
        InstanceTbl savedInstance = createInstanceAndSave("New Instance");
         
        //check that instances count was added by one
        List<InstanceTbl> instances = repository.findAll();
        assertEquals(countOfInstances+1, instances.size());
        
        //find instance that was created by this test
        InstanceTbl instance = repository.findOne(savedInstance.getInstanceId());
        
        //there should be exactly one instance share created by this test
        assertEquals(1,instance.getInstanceShareTbls().size());
        
        //find shares by instance id
        List<InstanceShare> instanceShares = instanceShareRepository.findByInstanceId(instance.getInstanceId());
        assertEquals(instance.getInstanceId(), instanceShares.get(0).getInstanceTbl().getInstanceId());
        
        for (InstanceShare instanceShare2: instanceShares){
            assertNotNull(instanceShareRepository.findOne(instanceShare2.getId()));
            //remove instance share
            instanceShareRepository.delete(instanceShare2);
        }
        
        //remove instance that this test case added
        repository.delete(instance);

    }

}