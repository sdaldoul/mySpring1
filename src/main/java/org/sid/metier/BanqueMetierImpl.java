package org.sid.metier;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.util.Date;

import org.sid.dao.ClientRepository;
import org.sid.dao.CompteRepository;
import org.sid.dao.OperationRepository;
import org.sid.entities.Compte;
import org.sid.entities.CompteCourant;
import org.sid.entities.Operation;
import org.sid.entities.Retrait;
import org.sid.entities.Versement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor.OptimalPropertyAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BanqueMetierImpl implements IBanqueMetier {

	/* spring inject une implementation de cette interface */
	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private CompteRepository compteRepository;

	@Autowired
	private OperationRepository operationRepository;

	@Override
	public Compte consulterCompte(String codeCompte) {
		Compte cp = compteRepository.findOne(codeCompte);
		if (cp == null)
			throw new RuntimeException("Compte introuvable");
		return cp;
	}

	@Override
	public void verser(String codeCompte, double montant) {

		Compte cp = consulterCompte(codeCompte);
		Versement v = new Versement(new Date(), montant, cp);
		operationRepository.save(v);
		cp.setSolde(cp.getSolde() + montant);
		compteRepository.save(cp);

	}

	@Override
	public void retirer(String codeCompte, double montant) {

		Compte cp = consulterCompte(codeCompte);
		double facilitiesCaisse = 0;
		if (cp instanceof CompteCourant)
			facilitiesCaisse = ((CompteCourant) cp).getDecouvert();
		if (cp.getSolde() + facilitiesCaisse < montant)
			throw new RuntimeException("Solde insufissant");
		Retrait v = new Retrait(new Date(), montant, cp);
		operationRepository.save(v);
		cp.setSolde(cp.getSolde() - montant);
		compteRepository.save(cp);

	}

	@Override
	public void virement(String codeCompte1, String codeCompte2, double montant) {
		
		if (codeCompte1.equals(codeCompte2))
				throw new RuntimeException("Impossible le virement sur le meme compte");
		retirer(codeCompte1, montant);
		verser(codeCompte2, montant);

	}

	@Override
	public Page<Operation> listOperation(String codeCompte, int page, int size) {
		return operationRepository.listOperation(codeCompte, new PageRequest(page, size));
	}

}
