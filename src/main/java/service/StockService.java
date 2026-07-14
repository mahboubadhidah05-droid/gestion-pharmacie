package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;

public class StockService {

    private static final Logger LOGGER =
            Logger.getLogger(StockService.class.getName());

    private static final String MEDICAMENT_INEXISTANT =
            "Médicament introuvable : ID={0}";

    private static final String STOCK_MIS_A_JOUR =
            "Stock mis à jour pour le médicament ID={0}";


    private final MedicamentDAO medDAO;
    private final StockHistoriqueDAO histDAO;


    public StockService(
            MedicamentDAO medDAO,
            StockHistoriqueDAO histDAO) {

        this.medDAO = medDAO;
        this.histDAO = histDAO;
    }


    public void ajouterStock(
            int idMed,
            int quantite) {


        int stockActuel =
                medDAO.getStock(idMed);


        if (stockActuel == -1) {

            LOGGER.log(
                    Level.WARNING,
                    MEDICAMENT_INEXISTANT,
                    idMed
            );

            return;
        }


        int nouveauStock =
                stockActuel + quantite;


        mettreAJourStock(
                idMed,
                nouveauStock,
                quantite
        );


        LOGGER.log(
                Level.INFO,
                STOCK_MIS_A_JOUR,
                idMed
        );
    }


    private void mettreAJourStock(
            int idMed,
            int nouveauStock,
            int quantite) {


        medDAO.updateStock(
                idMed,
                nouveauStock
        );


        histDAO.ajouterHistorique(
                idMed,
                quantite
        );
    }
}