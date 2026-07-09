package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;

public class StockService {

    private static final Logger LOGGER =
            Logger.getLogger(StockService.class.getName());

    private final MedicamentDAO medDAO;
    private final StockHistoriqueDAO histDAO;

    public StockService(MedicamentDAO medDAO, StockHistoriqueDAO histDAO) {
        this.medDAO = medDAO;
        this.histDAO = histDAO;
    }

    public void ajouterStock(int idMed, int qte) {

        int stockActuel = medDAO.getStock(idMed);

        if (stockActuel == -1) {
            LOGGER.log(Level.WARNING,
                    "Médicament introuvable : ID={0}",
                    idMed);
            return;
        }

        int nouveauStock = stockActuel + qte;

        medDAO.updateStock(idMed, nouveauStock);
        histDAO.ajouterHistorique(idMed, qte);

        LOGGER.log(Level.INFO,
                "Stock mis à jour pour le médicament ID={0}",
                idMed);
    }
}