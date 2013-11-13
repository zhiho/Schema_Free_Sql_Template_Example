package com.irelandken.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.irelandken.service.SecurityService;


@Controller
public class LoginController
{
	@Autowired
	SecurityService securityService;
	
	@RequestMapping("/login.htm")
	public String login(@RequestParam String username,@RequestParam String password,ModelMap model)
	{
		if(securityService.login(username, password)) {
			
			
			Map<String, Object> user = securityService.getUseinfo(username);
			
			model.put("user", user);
			
			return "/home.jsp";
		}
		
		return "redirect:/login.html";
	}
}
