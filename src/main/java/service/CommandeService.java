package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.CommandeDAO;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;

public class CommandeService {

    private static final Logger LOGGER =
            Logger.getLogger(CommandeService.class.getName());

    private static final String MEDICAMENT_INEXISTANT =
            "Commande refusée : médicament introuvable ID={0}";

    private static final int STOCK_INTROUVABLE = -1;


    private final CommandeDAO dao;
    private final MedicamentDAO medDAO;
    private final StockHistoriqueDAO histDAO;


    public CommandeService(CommandeDAO dao, MedicamentDAO medDAO, StockHistoriqueDAO histDAO) {
        this.dao = dao;
        this.medDAO = medDAO;
        this.histDAO = histDAO;
    }


    public boolean creerCommande(int idGest, int idMed, int qte) {

        int stockActuel = medDAO.getStock(idMed);

        if (stockActuel == STOCK_INTROUVABLE) {
            LOGGER.log(Level.WARNING, MEDICAMENT_INEXISTANT, idMed);
            return false;
        }

        dao.creerCommande(idGest, idMed, qte);
        medDAO.updateStock(idMed, stockActuel + qte);
        histDAO.ajouterHistorique(idMed, +qte);

        return true;
    }
}