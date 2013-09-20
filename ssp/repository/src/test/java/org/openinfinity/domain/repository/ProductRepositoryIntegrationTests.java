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
package org.openinfinity.domain.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openinfinity.domain.entity.Product;
import org.openinfinity.domain.service.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Product Repository integration tests.
 * 
 * @Author Ilkka Leinonen 
 * 
 */
@ContextConfiguration(locations={"classpath*:**/test-repository-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductRepositoryIntegrationTests {

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ProductSpecification productionSpecification;
	
	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void givenKnownProductWhenStoringToBackendRepositoryThenProductMustBeFound() {
		Product expected = createProduct();
		Product product = productRepository.create(expected);
		Product actual = productRepository.loadById(product.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getCompany(), actual.getCompany());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertNotNull(actual.getId());		
	}

	private Product createProduct() {
		Product expected = new Product();
		expected.setCompany("TestComponany");
		expected.setDescription("Test Description");
		expected.setName("Test Name");
		return expected;
	}
	
//	@Test @Ignore
//	public void testFail() {
//		fail("Not yet implemented");
//	}	

}
