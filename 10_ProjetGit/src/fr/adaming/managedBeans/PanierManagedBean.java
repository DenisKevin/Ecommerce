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
	
	private LigneDeCommande ligneCommande;
	
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

	public LigneDeCommande getLigneCommande() {
		return ligneCommande;
	}

	public void setLigneCommande(LigneDeCommande ligneCommande) {
		this.ligneCommande = ligneCommande;
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
			boolean produitPresent = false;
			List<Produit> listeP = panier.getListProduits();
			
			ligneCommande = panier.getLigneByProduit(reqProduit);
			
			if(ligneCommande!=null){
				//lignePanier.incrementerQuantite();
				ligneCommande.getTotal();
			}else{
				panier.getListeLignes().add(new LigneDeCommande(1, reqProduit.getPrix(), reqProduit, panier));
				System.out.println("-------------------------------------" +ligneCommande);
			}
		session.setAttribute("panier", panier);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ce produit n'existe pas!"));
		}	
	}
	
	public void enleverProduit(){

		Produit reqProduit = produitServ.getProduit(produit);
		if (reqProduit!=null){
			boolean produitPresent = true;
			List<Produit> listeP = panier.getListProduits();
			
			LigneDeCommande lignePanier = panier.getLigneByProduit(reqProduit);
			
			if(lignePanier!=null){
				lignePanier.decrementerQuantite();
				lignePanier.getTotal();
				if(lignePanier.getQuantite()==0){
					listeP.remove(lignePanier);
				}
			}
		session.setAttribute("panier", panier);
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ce produit n'existe pas!"));
		}	
	}
	
	public void viderPanier(){
		
		if(panier.getListeLignes().size()!=0){
			panier.viderPanier();
		}
		session.setAttribute("panier", panier);
	}
	
	public String passerCommande(){
		client = (Client)session.getAttribute("sessionClient");
		if(client!=null){
			return "orderPage";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Inscription ou login nécessaire pour enregistrer une commande"));
			return "accueil";
		}
	}
	
	public String enregistrerCommande(){
		client = (Client)session.getAttribute("sessionClient");
		if(client!=null){
			panier.setClient(client);
			Commande outPanier = commandeServ.createCommande(panier);
			if (outPanier!=null){
				panier = new Commande(new Date(), client);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Commande validée"));
				return "commandesClient";
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Commande échouée, veuillez rééssayer"));
				return "orderPage";
			}
		} else {
			return "accueil";
		}
	}
	
}
