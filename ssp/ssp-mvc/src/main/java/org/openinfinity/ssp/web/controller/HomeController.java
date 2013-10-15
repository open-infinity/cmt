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

import java.util.Locale;

import org.openinfinity.domain.entity.Account;
import org.openinfinity.domain.entity.User;
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
	
	@RequestMapping(value="/signin", method = RequestMethod.GET)
	public String signIn(Locale locale, Model model) {
		return "signin";
	}	
	
	@RequestMapping(value="/signup", method = RequestMethod.GET)
	public String signUp(Locale locale, Model model) {
		User user = new User();
		model.addAttribute("userModel", user);
		return "user/new";
	}	
}
