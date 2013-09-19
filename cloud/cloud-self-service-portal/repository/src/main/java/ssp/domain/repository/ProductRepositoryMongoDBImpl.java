
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
 */package ssp.domain.repository;

import java.math.BigInteger;
import java.util.Collection;

import ssp.core.annotation.AuditTrail;
import ssp.core.annotation.Log;
import ssp.domain.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * Product repository implementation.
 * 
 * @author Ilkka Leinonen
 */
@Repository
public class ProductRepositoryMongoDBImpl implements ProductRepository {

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Log
	@AuditTrail
	public Product create(Product product) {
		mongoTemplate.save(product);
		return product;
	}
	
	@Log
	@AuditTrail
	public void update(Product product) {
		Query query = new Query(Criteria.where("id").is(product.getId()));
		Update update = new Update();
		mongoTemplate.upsert(query, update, product.getClass());
	}
	
	@Log
	@AuditTrail
	public Collection<Product> loadAll() {
		return mongoTemplate.findAll(Product.class);
	}
	
	@Log
	@AuditTrail
	public Product loadById(BigInteger id) {
		Query query = new Query(Criteria.where("id").is(id));
		return mongoTemplate.findById(query, Product.class);
	}
	
	@Log
	@AuditTrail
	public Collection<Product> loadByName(String name) {
		Query query = new Query(Criteria.where("name").is(name));
		return mongoTemplate.find(query, Product.class);
	}
	
	@Log
	@AuditTrail
	public void delete (Product product) {
		Query query = new Query(Criteria.where("id").is(product.getId()));
		mongoTemplate.remove(query, product.getClass());
	}
	
}
