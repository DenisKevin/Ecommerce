package fr.adaming.managedBeans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import fr.adaming.dao.ICommandeDAO;
import fr.adaming.model.Client;
import fr.adaming.model.Commande;
import fr.adaming.model.LigneDeCommande;
import fr.adaming.model.Produit;
import fr.adaming.service.ICommandeService;
import fr.adaming.service.IProduitService;

@ManagedBean(name="panierMB")
@SessionScoped
public class PanierManagedBean implements Serializable {

	@EJB
	private ICommandeService commandeServ;
	
	@EJB
	private IProduitService produitServ;
	
	private Produit produit;
	
	private Commande panier;
	
	private Commande commande;
	
	private Client client;
	
	private HttpSession session ;
	
	@PostConstruct
	public void init(){
		// On obtient une session pour conserver le panier au cours de la navigation
		session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		// On cherche à obtenir le client actuel depuis la session, si il existe (ie session déjà créée par clientMB)
		client = (Client)session.getAttribute("sessionClient");
		// Si le client existe, le panier (Commande) lui est attribué
		if(client!=null){
			//la création de panier, qui est une Commande, ne passe pas par serv afin de ne pas l'enregistrer (pour le moment)
			panier = new Commande(new Date(), client);
		} else {
			// Si ce n'est pas le cas, Client vide qui sera revérifié en cas de requete d'enregistrement de commande
			panier = new Commande(new Date(), new Client());
		}
		//Le panier est enregistré dans la session
		session.setAttribute("panier", panier);
	}
	
	public Produit getProduit() {
		return produit;
	}

	public void setProduit(Produit produit) {
		this.produit = produit;
	}

	public Commande getPanier() {
		return panier;
	}

	public void setPanier(Commande panier) {
		this.panier = panier;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Commande getCommande() {
		return commande;
	}

	public void setCommande(Commande commande) {
		this.commande = commande;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public void ajouterProduit(){
		// ajoute le produit donné dans un form, et vérifie si le produit est déjà dans le panier
		Produit reqProduit = produitServ.getProduit(produit);
		if (reqProduit!=null){
			//on verifie si le produit est déjà présent, on part du principe que non(false)
			boolean produitPresent = false;
			//on cherche si une ligne contient le produit a rajouter, le nom ("designation") ET l'id ("idproduit") doivent correspondre.
			//normalement c'est le cas vu qu'on est passé par produitServ...
			LigneDeCommande ligneCommande = panier.getLigneByProduit(reqProduit);
			//si on trouve une ligne qui correspond, on incremente la quantite et on met a jour le total
			if(ligneCommande!=null){
				ligneCommande.incrementerQuantite();
				ligneCommande.getTotal(); // Note: Commande.getTotal() existe: une méthode qui calcule le total des totaux, sans stocker le résultat.
			}else{
				//sinon, on rajoute une nouvelle ligne
				panier.getListeLignes().add(new LigneDeCommande(1, reqProduit.getPrix(), reqProduit, panier));
			}
		//on renvoie le panier dans la session
		session.setAttribute("panier", panier);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ce produit n'existe pas!"));
		}	
	}
	
	public void enleverProduit(){
		//meme chose que ajouter, mais à l'envers, avec différence si la ligne devient vide
		Produit reqProduit = produitServ.getProduit(produit);
		if (reqProduit!=null){
			boolean produitPresent = true; //Ici, on part du principe que le produit est bien dans le panier, mais on vérifie
			LigneDeCommande lignePanier = panier.getLigneByProduit(reqProduit);
			
			if(lignePanier!=null){
				lignePanier.decrementerQuantite();
				lignePanier.getTotal();
				if(lignePanier.getQuantite()==0){
					//si la quantite de la ligne est 0 après soustraction, on vire la ligne
					panier.getListeLignes().remove(lignePanier);
				}
			}
		session.setAttribute("panier", panier);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ce produit n'existe pas!"));
		}	
	}
	
	public void viderPanier(){
		//on vire tout
		if(panier.getListeLignes().size()!=0){
			panier.viderPanier();
		}
		session.setAttribute("panier", panier);
	}
	
	public String passerCommande(){
		//commande a faire dans managesbean séparé éventuellement, écrite en dessous sinon. Il faut juste une page intermédiare avec les infos de paiement une vérification de numéro de CB correct...
		client = (Client)session.getAttribute("sessionClient");
		if(client!=null){
			//redirection vers une page de confirmation, A CONDITION que le client soit connecté
			return "orderPage";
		} else {
			//si pas de client connecté, on redirige vers la homepage (ici accueil, à changer vers la nouvelle) pour que la connection soit faite
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Inscription ou login nécessaire pour enregistrer une commande"));
			return "accueil";
		}
	}
	
	public String enregistrerCommande(){
		client = (Client)session.getAttribute("sessionClient");
		// on vérifie encore si le client est loggé
		if(client!=null){
			
			//on affecte le client connecté au panier, avant d'en faire une commande
			panier.setClient(client);
			
			//on cree la commande
			Commande outPanier = commandeServ.createCommande(panier);
			if (outPanier!=null){
				//si réussite de la création, on vide le panier et on renvoie à la page de récapitulatif des commandes
				panier = new Commande(new Date(), client);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Commande validée"));
				return "commandesClient";
			} else {
				//sinon, retour a la page de confirmation
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Commande échouée, veuillez rééssayer"));
				return "orderPage";
			}
		} else {
			return "accueil";
		}
	}
	
}
