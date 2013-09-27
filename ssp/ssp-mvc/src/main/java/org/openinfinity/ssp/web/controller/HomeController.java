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
package org.openinfinity.ssp.web.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.openinfinity.domain.entity.Account;
import org.openinfinity.domain.entity.User;
import org.openinfinity.ssp.web.model.AccountModel;
import org.openinfinity.ssp.web.model.SigninModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 * 
 * @author Vedran Bartonicek
 */
@Controller
@RequestMapping("/")
public class HomeController {
	
	@RequestMapping(method = RequestMethod.GET)
	public String signIn(Locale locale, Model model) {
		SigninModel signinModel = new SigninModel("username", "password");
		model.addAttribute("signinForm", signinModel);
		return "signin/signinForm";
	}	
	
	@RequestMapping(value="/signin", method = RequestMethod.POST)
	public String signInSubmit(Locale locale, Model modelMap) {
		User user = new User();
		Account account = new Account();
		AccountModel accountCreateModel = new AccountModel(user, account);
		modelMap.addAttribute("accountModel", accountCreateModel);
		return "service/view";
	}	
}
