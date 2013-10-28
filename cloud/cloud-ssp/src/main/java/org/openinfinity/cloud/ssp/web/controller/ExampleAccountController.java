package org.openinfinity.cloud.ssp.web.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/exampleaccount")
public class ExampleAccountController {

	private Map<Long, ExampleAccount> accounts = new ConcurrentHashMap<Long, ExampleAccount>();
	
	@RequestMapping(method=RequestMethod.GET)
	public String getCreateForm(Model model) {
		//model.addAttribute(new Account());

		model.addAttribute("exampleaccount", new ExampleAccount());
		return "exampleaccount/createForm";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String create(@Valid ExampleAccount account, BindingResult result) {
		if (result.hasErrors()) {
			return "exampleaccount/createForm";
		}
		this.accounts.put(account.assignId(), account);
		return "redirect:/exampleaccount/" + account.getId();
	}
	
	@RequestMapping(value="{id}", method=RequestMethod.GET)
	public String getView(@PathVariable Long id, Model model) {
		ExampleAccount account = this.accounts.get(id);
		if (account == null) {
			throw new ResourceNotFoundException(id);
		}
		model.addAttribute("exampleaccount", account);
		return "exampleaccount/view";
	}

}
