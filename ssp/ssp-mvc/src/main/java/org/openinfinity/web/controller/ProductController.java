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
package org.openinfinity.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;

import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.openinfinity.core.aspect.ArgumentStrategy;
import org.openinfinity.core.exception.AbstractCoreException;
import org.openinfinity.core.exception.ApplicationException;
import org.openinfinity.core.exception.BusinessViolationException;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.domain.entity.Product;
import org.openinfinity.domain.service.ProductService;
import org.openinfinity.web.model.ProductModel;
import org.openinfinity.web.support.SerializerUtil;
import org.openinfinity.web.support.ServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * Product controller for m-v-c binding and service orchestration.
 * 
 * @author Ilkka Leinonen
 */
@Controller
@RequestMapping(value = "/productModel")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private Validator validator;
	
	@Autowired 
	ApplicationContext applicationContext;

	@Log
	@ExceptionHandler({SystemException.class, ApplicationException.class, BusinessViolationException.class})
	public void exceptionOccurred(AbstractCoreException abstractCoreException, HttpServletResponse response, Locale locale) {
		ProductModel productModel = new ProductModel();
		if (abstractCoreException.isErrorLevelExceptionMessagesIncluded()) {
			Collection<String> localizedErrorMessages = getLocalizedExceptionMessages(abstractCoreException.getErrorLevelExceptionIds(), locale);
			productModel.addErrorStatuses("errorLevelExceptions", localizedErrorMessages);
		}
		if (abstractCoreException.isWarningLevelExceptionMessagesIncluded())  {
			Collection<String> localizedErrorMessages = getLocalizedExceptionMessages(abstractCoreException.getWarningLevelExceptionIds(), locale);
			productModel.addErrorStatuses("warningLevelExceptions", localizedErrorMessages);
		}
		if (abstractCoreException.isInformativeLevelExceptionMessagesIncluded()) {
			Collection<String> localizedErrorMessages = getLocalizedExceptionMessages(abstractCoreException.getInformativeLevelExceptionIds(), locale);
			productModel.addErrorStatuses("informativeLevelExceptions", localizedErrorMessages);
		}
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		SerializerUtil.jsonSerialize(ServletUtil.getWriter(response), productModel.getErrorStatuses());
	}

	private Collection<String> getLocalizedExceptionMessages(Collection<String> localizedExceptionIds, Locale locale) {
		Collection<String> localizedErrorMessages = new ArrayList<String>();
		for (String uniqueId : localizedExceptionIds) {
			String message = applicationContext.getMessage(uniqueId, null, locale);
			localizedErrorMessages.add(message);	
		}
		return localizedErrorMessages;
	}
	
	@Log
	@AuditTrail(argumentStrategy=ArgumentStrategy.ALL)
	@RequestMapping(method = RequestMethod.GET)
	public String createNewProduct(Model model) {
		model.addAttribute(new ProductModel());
		return "product/editProduct";
	}
	
	@Log
	@AuditTrail(argumentStrategy=ArgumentStrategy.ALL) 
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> create(@Valid @RequestBody ProductModel productModel, HttpServletResponse response) {
		Set<ConstraintViolation<ProductModel>> failures = validator.validate(productModel);
		if (failures.isEmpty()) {
			Product product = productService.create(productModel.getProduct());
			return new ModelMap("id", product.getId());
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return getValidationMessages(failures);
		}
	}
	
	private Map<String, String> getValidationMessages(Set<ConstraintViolation<ProductModel>> failures) {
		Map<String, String> failureMessages = new HashMap<String, String>();
		for (ConstraintViolation<ProductModel> failure : failures) {
			failureMessages.put(failure.getPropertyPath().toString(), failure.getMessage());
		}
		return failureMessages;
	}
	
}
