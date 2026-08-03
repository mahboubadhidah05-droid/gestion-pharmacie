package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.CommandeDAO;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.LotMedicamentDAO;

public class CommandeService {

    private static final Logger LOGGER =
            Logger.getLogger(CommandeService.class.getName());

    private static final String MEDICAMENT_INEXISTANT =
            "Commande refusée : médicament introuvable ID={0}";

    private static final int STOCK_INTROUVABLE = -1;


    private final CommandeDAO dao;
    private final MedicamentDAO medDAO;
    private final StockHistoriqueDAO histDAO;
    private final LotMedicamentDAO lotDAO;


    public CommandeService(
            CommandeDAO dao,
            MedicamentDAO medDAO,
            StockHistoriqueDAO histDAO,
            LotMedicamentDAO lotDAO) {
        this.dao = dao;
        this.medDAO = medDAO;
        this.histDAO = histDAO;
        this.lotDAO = lotDAO;
    }


    public boolean creerCommande(
            int idGest,
            int idMed,
            int qte,
            Integer idFournisseur,
            String datePeremption) {

        int stockActuel = medDAO.getStock(idMed);

        if (stockActuel == STOCK_INTROUVABLE) {
            LOGGER.log(Level.WARNING, MEDICAMENT_INEXISTANT, idMed);
            return false;
        }

        dao.creerCommande(idGest, idMed, qte, idFournisseur);
        medDAO.updateStock(idMed, stockActuel + qte);
        histDAO.ajouterHistorique(idMed, +qte);
        lotDAO.ajouterLot(idMed, qte, datePeremption);

        return true;
    }
}