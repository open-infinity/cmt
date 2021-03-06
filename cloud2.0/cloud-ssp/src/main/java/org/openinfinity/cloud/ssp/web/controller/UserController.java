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
package org.openinfinity.cloud.ssp.web.controller;

import java.math.BigInteger;
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

import org.openinfinity.cloud.service.ssp.AccountService;
import org.openinfinity.cloud.ssp.web.model.AccountModel;
import org.openinfinity.cloud.ssp.web.model.AccountSampleModel;
import org.openinfinity.cloud.ssp.web.support.SerializerUtil;
import org.openinfinity.cloud.ssp.web.support.ServletUtil;
import org.openinfinity.core.annotation.AuditTrail;
import org.openinfinity.core.annotation.Log;
import org.openinfinity.core.aspect.ArgumentStrategy;
import org.openinfinity.core.exception.AbstractCoreException;
import org.openinfinity.core.exception.ApplicationException;
import org.openinfinity.core.exception.BusinessViolationException;
import org.openinfinity.core.exception.SystemException;
import org.openinfinity.cloud.domain.ssp.Account;
import org.openinfinity.cloud.domain.ssp.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * Spring MVC Controller
 * 
 * @author Vedran Bartonicek
 */


@Controller
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private Validator validator;
	
	@Autowired 
	ApplicationContext applicationContext;

	@Log
	@ExceptionHandler({SystemException.class, ApplicationException.class, BusinessViolationException.class})
	public void exceptionOccurred(AbstractCoreException abstractCoreException, HttpServletResponse response, Locale locale) {
		AccountSampleModel accountModel = new AccountSampleModel();
		if (abstractCoreException.isErrorLevelExceptionMessagesIncluded()) {
			Collection<String> localizedErrorMessages = getLocalizedExceptionMessages(abstractCoreException.getErrorLevelExceptionIds(), locale);
			accountModel.addErrorStatuses("errorLevelExceptions", localizedErrorMessages);
		}
		if (abstractCoreException.isWarningLevelExceptionMessagesIncluded())  {
			Collection<String> localizedErrorMessages = getLocalizedExceptionMessages(abstractCoreException.getWarningLevelExceptionIds(), locale);
			accountModel.addErrorStatuses("warningLevelExceptions", localizedErrorMessages);
		}
		if (abstractCoreException.isInformativeLevelExceptionMessagesIncluded()) {
			Collection<String> localizedErrorMessages = getLocalizedExceptionMessages(abstractCoreException.getInformativeLevelExceptionIds(), locale);
			accountModel.addErrorStatuses("informativeLevelExceptions", localizedErrorMessages);
		}
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		SerializerUtil.jsonSerialize(ServletUtil.getWriter(response), accountModel.getErrorStatuses());
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
	@RequestMapping(value="{id}", method = RequestMethod.GET)
	public String getAccountById(@PathVariable Long id, Model model) {
		// TODO get Account by ID from DB.
		User user = new User();
		model.addAttribute("userModel", user);
		return "user/view";
	}
	
	// TODO: implement me
	@Log
	@AuditTrail(argumentStrategy=ArgumentStrategy.ALL)
	@RequestMapping(method = RequestMethod.PUT)
	public String editService(Model model) {
		return "user/view";
	}
	
	@Log
	@AuditTrail(argumentStrategy=ArgumentStrategy.ALL)
	@RequestMapping(value="/user", method = RequestMethod.POST)
	public String submitService(@Valid User user, BindingResult result) {
		if (result.hasErrors()) {
			return "user/new";
		}
		// TODO: store to DB, set Id to user.
		// TODO: perhaps view creation ins better from here, not GET handler 
		// return "redirect:/user/" + user.getId();
		user.setId(BigInteger.valueOf(1));
		return "redirect:/invoicecreator/" + user.getId();
	}
		
	
	private Map<String, String> getValidationMessages(Set<ConstraintViolation<AccountSampleModel>> failures) {
		Map<String, String> failureMessages = new HashMap<String, String>();
		for (ConstraintViolation<AccountSampleModel> failure : failures) {
			failureMessages.put(failure.getPropertyPath().toString(), failure.getMessage());
		}
		return failureMessages;
	}
	
}
