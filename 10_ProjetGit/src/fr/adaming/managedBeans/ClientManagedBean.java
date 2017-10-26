package fr.adaming.managedBeans;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import fr.adaming.model.Client;
import fr.adaming.model.LigneDeCommande;
import fr.adaming.service.IClientService;

@ManagedBean(name = "cMB")
@SessionScoped
public class ClientManagedBean implements Serializable {

	@EJB
	IClientService clientServ;
<<<<<<< HEAD

=======
	
	@EJB
	ICommandeService commandeServ;
	
>>>>>>> branch 'master' of https://github.com/DenisKevin/Ecommerce.git
	Client client;

	HttpSession session;

	@PostConstruct
	public void init() {
		client = new Client();
	}

	public ClientManagedBean() {
		super();
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

<<<<<<< HEAD
	public String seConnecter() {
		// fonction de login du client, qui donne acces a la gestion du compte
		// personnel et des commandes
=======
	public String seConnecter(){
		// fonction de login du client, qui donne acces a la gestion du compte personnel et des commandes
>>>>>>> branch 'master' of https://github.com/DenisKevin/Ecommerce.git
		FacesContext context = FacesContext.getCurrentInstance();
		session = (HttpSession) context.getExternalContext().getSession(false);
<<<<<<< HEAD

		// On verifie que le client existe, et on recupere sa description
		// complete (avec id)
=======
		//On verifie que le client existe, et on recupere sa description complete (avec id)
>>>>>>> branch 'master' of https://github.com/DenisKevin/Ecommerce.git
		Client loginClient = clientServ.clientExists(client);

		if (loginClient != null) {
			this.client = loginClient;
			session.setAttribute("sessionClient", this.client);
			context.addMessage(null, new FacesMessage("Session de " + client.getNom() + " " + client.getPrenom()));
			return "commandes";
		} else {
			context.addMessage(null, new FacesMessage("Ce client n'existe pas, verifiez les identifiants"));
			return "login";
		}
	}

	public String seDeconnecter() {
		session.invalidate();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage("Vous avez été déconnecté avec succès"));
		return "accueil";
	}

	public String creerCompte() {
		// fonction de création de compte du client, à partir de ses
		// informations. éventuellement, seuls mail et password peuvent être
		// demandés pour commencer (option sign in/sign up)

		if (clientServ.clientExists(client) == null) {
			clientServ.createClient(client);
			return "accountInfo"; // renvoie vers l'ajout du reste des
									// informations. De là, l'annulation doit
									// etre possible, soit par simple
									// suppression, soit en ne créant pas le
									// compte à la ligne précédente mais à
									// l'aide d'une seconde option. ça demanderait de get la session meme sans connection d'un client. probablement nécessaire pour le panier de toute façon
		}
		return "accueil";
	}

}