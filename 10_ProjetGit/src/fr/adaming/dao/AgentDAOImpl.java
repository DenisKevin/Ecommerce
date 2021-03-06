package fr.adaming.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import fr.adaming.model.Agent;

@Stateless
public class AgentDAOImpl implements IAgentDAO{
	
	@PersistenceContext(unitName="PU")
	EntityManager em;

	@Override
	public Agent createAgent(Agent agent) {
		
		if(agentExists(agent)!= null){
			em.persist(agent);
			System.out.println("Agent créé: "+agent);
			return agent;
		} else {
			return null;
		}
	}

	@Override
	public Agent agentExists(Agent agent) {
		//Cette fonction "récupère" l'agent par son mail et password
		String req = "SELECT a FROM Agent a WHERE a.mail=:pMail AND a.password=:pPassword";
		
		Query query = em.createQuery(req);
		query.setParameter("pMail", agent.getMail());
		query.setParameter("pPassword", agent.getPassword());
		
		try{
			Agent outAgent = (Agent)query.getSingleResult();
			System.out.println("Agent existant: "+outAgent+"Envoyé depuis AgentDAOImpl.agentExists()");
			return outAgent;
		} catch (NoResultException nrex) {
			System.out.println(nrex);
			return null;
		}
	}

	@Override
	public Agent getAgent(Agent agent) {
		//Cette fonction ne récupère l'agent que par son ID
		
		Agent foundAgent = em.find(Agent.class, agent.getId());
		
		if (foundAgent!=null){
			System.out.println("Agent obtenu: "+agent);
			return foundAgent;
		} else {
			return null;
		}
		
	}

	@Override
	public Agent updateAgent(Agent agent) {
		// Cette fonction ne recupere l'agent que par son ID
		if(getAgent(agent)!=null){
			em.merge(agent);
			System.out.println("Agent modifié: "+agent);
			return agent;
		} else {
			return null;
		}
	}

	@Override
	public boolean deleteClient(Agent agent) {
		//Cette méthode récupère encore l'agent par son ID
		if(getAgent(agent)!=null){
			em.remove(agent);
			System.out.println("Agent supprimé: "+agent);
			return true;
		}else{
			return false;
		}
	}

	
}
