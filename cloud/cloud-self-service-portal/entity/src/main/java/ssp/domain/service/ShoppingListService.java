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
package ssp.domain.service;

import java.util.Collection;

import ssp.domain.entity.Catalogue;
import ssp.domain.entity.Product;
import ssp.domain.entity.ShoppingList;

/**
 * Business service for shoppinglist.
 * 
 * @author Ilkka Leinonen
 */
public interface ShoppingListService extends AbstractCrudServiceInterface<ShoppingList>{
	
	Collection<Catalogue> queryByName(String name);
	
	Product addProductToShoppingList(Product product);
	
	Product updateProductToShoppingList(Product product);
	
	void deleteProductFromShoppingList(Product product);

}
