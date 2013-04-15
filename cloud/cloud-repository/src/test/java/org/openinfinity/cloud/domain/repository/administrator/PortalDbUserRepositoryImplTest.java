package org.openinfinity.cloud.domain.repository.administrator;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations={"classpath*:META-INF/spring/repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PortalDbUserRepositoryImplTest {
		
    @Autowired
    private PortalDbUserRepository repository;	
	
	@Test
    public void dummy() {
    	
    }
    
	//This test works only from IDE and there must be valid tunnels configured to db.
	//@Test    
	public void test() {		
		String userid1 = "";
		String userid2 = "";
		String userid3 = "";
		
		System.out.println("Reserving new userid.");
		userid1 = repository.getNextFreeUserid();
		System.out.println("Reserved userid: " + userid1);
		assertTrue("Got invalid userid", userid1.startsWith("liferay"));
		
		System.out.println("Reserving new userid.");
		userid2 = repository.getNextFreeUserid();
		System.out.println("Reserved userid: " + userid2);
		assertTrue("Got invalid userid", userid2.startsWith("liferay"));
		assertTrue("Got Same userid twice " + userid1, !userid1.equals(userid2));
		
		System.out.println("Releasing userid: " + userid1);
		userid1 = repository.releaseUserid(userid1);
		System.out.println("Released userid: " + userid1);

		System.out.println("Releasing userid: " + userid2);
		userid2 = repository.releaseUserid(userid2);
		System.out.println("Released userid: " + userid2);
		
		System.out.println("Reserving new userid.");
		userid3 = repository.getNextFreeUserid();
		System.out.println("Reserved userid: " + userid3);
		assertTrue("Got invalid userid", userid3.startsWith("liferay"));

		System.out.println("Releasing userid: " + userid3);
		userid3 = repository.releaseUserid(userid3);
		System.out.println("Released userid: " + userid3);
		
		assertTrue("Should get released userid again, but got new one " + userid1 + " " + userid3, userid1.equals(userid3));		
	}
}
