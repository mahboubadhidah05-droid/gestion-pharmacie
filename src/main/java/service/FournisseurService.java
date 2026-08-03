package service;

import java.util.List;

import dao.FournisseurDAO;
import dto.FournisseurResponse;

public class FournisseurService {

    private final FournisseurDAO dao;

    public FournisseurService(FournisseurDAO dao) {
        this.dao = dao;
    }

    public int ajouter(
            String nom,
            String telephone,
            String email,
            String adresse) {

        return dao.ajouterFournisseur(nom, telephone, email, adresse);
    }

    public List<FournisseurResponse> listerFournisseurs() {
        return dao.listerFournisseurs();
    }
}