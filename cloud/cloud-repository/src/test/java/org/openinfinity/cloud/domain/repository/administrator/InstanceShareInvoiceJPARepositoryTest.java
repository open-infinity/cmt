package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.cloud.domain.InstanceShareInvoice;
import org.openinfinity.cloud.domain.InstanceShare;
import org.openinfinity.cloud.domain.InstanceTbl;
import org.openinfinity.cloud.domain.repository.administrator.InstanceJPARepository;
import org.openinfinity.cloud.domain.repository.invoice.InstanceShareInvoiceRepository;
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
public class InstanceShareInvoiceJPARepositoryTest {
    
    
    @Autowired
    @Qualifier("cloudDataSource")
    DataSource ds;
    
    @Autowired
    InstanceJPARepository repository;
    
    @Autowired
    InstanceShareRepository instanceShareRepository;

    @Autowired
    InstanceShareInvoiceRepository instanceShareInvoiceRepository;
    
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
    
    private InstanceShareInvoice createInstanceShareInvoice(){
        InstanceShareInvoice instanceShareInvoice=new InstanceShareInvoice();
        instanceShareInvoice.setCreated(dateToSqlTimestamp(new Date()));
        instanceShareInvoice.setCreatedBy(1);
        
        return instanceShareInvoice;
    }

    @Transactional
    private InstanceTbl createInstanceAndSave(String instanceName){
        //Save
        InstanceTbl instance=createInstance(instanceName);
        InstanceShare instanceShare=createInstanceShare();
        instance.addInstanceShareTbl(instanceShare);
        instanceShare.setInstanceTbl(instance);
        
        return repository.saveAndFlush(instance);
        
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
        
        //associate each share with new InstanceShareInvoice
        for (InstanceShare instanceShare: instanceShares){
            
            InstanceShare instanceShare2 = instanceShareRepository.findOne(instanceShare.getId());
            InstanceShareInvoice invoice = createInstanceShareInvoice();
            invoice.setInstanceShare(instanceShare2);
            instanceShare2.addInstanceShareInvoice(invoice);
            instanceShareRepository.save(instanceShare);
        }
        
        //delete rows that this test case added
        for (InstanceShare instanceShare: instanceShares){
            InstanceShare instanceShare2 = instanceShareRepository.findOne(instanceShare.getId());
            for (InstanceShareInvoice instanceShareInvoice:instanceShare2.getInstanceShareInvoices()){
                instanceShareInvoiceRepository.delete(instanceShareInvoice);
            }
            instanceShareRepository.delete(instanceShare2);
        }
        
        repository.delete(instance);

    }

}
