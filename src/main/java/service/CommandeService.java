package service;

import dao.CommandeDAO;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;

public class CommandeService {

    private final CommandeDAO dao;
    private final MedicamentDAO medDAO;
    private final StockHistoriqueDAO histDAO;

    public CommandeService(CommandeDAO dao, MedicamentDAO medDAO, StockHistoriqueDAO histDAO) {
        this.dao = dao;
        this.medDAO = medDAO;
        this.histDAO = histDAO;
    }

    public void creerCommande(int idGest, int idMed, int qte) {
        dao.creerCommande(idGest, idMed, qte);
        int stockActuel = medDAO.getStock(idMed);
        medDAO.updateStock(idMed, stockActuel + qte);
        histDAO.ajouterHistorique(idMed, +qte);
    }
}