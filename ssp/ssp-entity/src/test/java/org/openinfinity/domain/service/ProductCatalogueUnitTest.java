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
package org.openinfinity.domain.service;
/*
import static org.junit.Assert.assertFalse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.openinfinity.domain.entity.Product;
*/
/**
 * Unit test against product specification.
 * 
 * @author Ilkka Leinonen
 */

/*
public class ProductCatalogueUnitTest {

	private Collection<Product> products = new ArrayList<Product>();
	
	private ProductSpecification productSpecification;
	
	@Before
	public void setUp() {
		productSpecification = new ProductSpecification();
		products = new ArrayList<Product>();
		populateProducts();
	}
	
	private void populateProducts() {
		for (int index = 0 ; index < 10 ; index++) {
			Product product = new Product("Test Name " + index, "Test Description " + index, "Company");
			product.setId(new BigInteger(""+System.currentTimeMillis()));
			products.add(product);
		}
	}
	
	@Test
	public void givenKnownProductWhenCreatingNewProductThenBusinessRuleMustAcknowledgeDublicateItems() {
		Product product = new Product("Test Name", "Test Description", "Company");
		boolean expectedFalse = productSpecification.isNotEligibleForCreation(product, products);
		assertFalse(expectedFalse);
	}
	
}
*/