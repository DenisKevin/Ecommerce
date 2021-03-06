package fr.adaming.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.adaming.dao.IProduitDao;
import fr.adaming.model.Categorie;
import fr.adaming.model.Produit;

@Service("prodService")
@Transactional
public class ProduitServiceImpl implements IProduitService {

	@Autowired
	private IProduitDao prodDao;

	@Override
	public Produit produitExists(Produit produit) {
		return prodDao.produitExists(produit);
	}

	@Override
	public Produit addProduit(Produit produit, Categorie categorie) {
		produit.setCategorie(categorie);
		return prodDao.addProduit(produit);
	}

	@Override
	public Produit updateProduit(Produit produit, Categorie categorie) {
		return prodDao.updateProduit(produit);
	}

	@Override
	public boolean deleteProduit(Produit produit, Categorie categorie) {
		return prodDao.deleteProduit(produit);
	}

	@Override
	public Produit getProduit(Produit produit) {
		return prodDao.getProduit(produit);
	}

	@Override
	public List<Produit> getProduitSelected() {
		return prodDao.getProduitSelected();
	}

	@Override
	public List<Produit> getProduitByKW(String[] keywords, boolean allKWs) {
		return prodDao.getProduitByKW(keywords, allKWs);
	}

	@Override
	public List<Produit> getProductByCategorie(Categorie categorie) {
		// TODO Auto-generated method stub
		return prodDao.getProductByCategorie(categorie);
	}

	@Override
	public List<Produit> getAllProduit() {

		return prodDao.getAllProduit();
	}

}
