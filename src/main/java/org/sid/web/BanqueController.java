package org.sid.web;

import java.util.Date;

import org.sid.entities.Client;
import org.sid.entities.Compte;
import org.sid.entities.CompteCourant;
import org.sid.entities.Operation;
import org.sid.metier.IBanqueMetier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BanqueController {

	@Autowired
	private IBanqueMetier banqueMetier;

	@RequestMapping("/operations")
	public String index() {
		return "comptes";
	}

	@RequestMapping("/consulterCompte") /*
										 * parameter par default en utilisant
										 * request param, ça permet d'attribue
										 * un nom pour parametre url qu'on veut
										 * recuperer
										 */
	public String consulter(Model model, String codeCompte, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size) {

		model.addAttribute("codeCompte", codeCompte);
		try {
			// Client c1 = new Client("Slaheddine",
			// "daldoul.slaheddine@gmail.com");
			// Compte cp = new CompteCourant("c1", new Date(), 9000d, c1,
			// 6000d);
			Compte cp = banqueMetier.consulterCompte(codeCompte);
			Page<Operation> pageOperations = banqueMetier.listOperation(codeCompte, page, size);
			model.addAttribute("listOperations", pageOperations.getContent());
			int[] pages = new int[pageOperations.getTotalPages()];
			model.addAttribute("pages", pages);
			model.addAttribute("compte", cp);
		} catch (Exception e) {
			model.addAttribute("exception", e);
		}
		return "comptes";
	}

	@RequestMapping(value = "/saveOperation", method = RequestMethod.POST)
	public String saveOperation(Model model, String typeOperation, String codeCompte, double montant,
			String codeCompte2) {

		try {
			if (typeOperation.equals("VERS")) {
				banqueMetier.verser(codeCompte, montant);
			} else if (typeOperation.equals("RET")) {
				banqueMetier.retirer(codeCompte, montant);

			} else if (typeOperation.equals("VIR")) {
				banqueMetier.virement(codeCompte, codeCompte2, montant);

			}
		} catch (Exception e) {
			model.addAttribute("exception", e);
			return "redirect:/consulterCompte?codeCompte=" + codeCompte + "&error=" + e.getMessage();
		}

		/*
		 * a cause de redirection les donne qu'on a stocke dans le modele on
		 * peux plus les afficher alors model.addAttribute("exception", e); est
		 * perdue
		 */
		return "redirect:/consulterCompte?codeCompte=" + codeCompte;
	}
}
