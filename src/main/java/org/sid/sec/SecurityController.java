package org.sid.sec;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecurityController {

	@RequestMapping(value = "login")
	public String login() {

		return "login";
	}

	/*
	 * probleme et solution problem = si tu log avec un user tu peux connecter
	 * solution tu ajoute
	 */
	@RequestMapping(value = "/")
	public String home() {
		return "redirect:/operations";
	}
	
	@RequestMapping(value = "/403")
	public String accessDenied() {
		return "403";
	}
}
