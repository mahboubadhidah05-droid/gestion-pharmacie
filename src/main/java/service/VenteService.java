package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.VenteDAO;

public class VenteService {

    private static final Logger LOGGER =
            Logger.getLogger(VenteService.class.getName());

    private static final String STOCK_INSUFFISANT =
            "Stock insuffisant pour le médicament ID={0}";

    private static final String VENTE_OK =
            "Vente enregistrée et stock mis à jour. Médicament ID={0}";

    private static final String VENTE_ECHEC =
            "Échec de l'enregistrement de la vente. Stock non modifié. Médicament ID={0}";

    private final MedicamentDAO medDAO;
    private final VenteDAO venteDAO;
    private final StockHistoriqueDAO histDAO;

    public VenteService(
            MedicamentDAO medDAO,
            VenteDAO venteDAO,
            StockHistoriqueDAO histDAO) {
        this.medDAO = medDAO;
        this.venteDAO = venteDAO;
        this.histDAO = histDAO;
    }

    public void vendre(
            int idPh,
            int idCl,
            int idMed,
            int quantite) {

        int stock = medDAO.getStock(idMed);

        if (stock < quantite) {
            LOGGER.log(Level.WARNING, STOCK_INSUFFISANT, idMed);
            return;
        }

        enregistrerVente(idPh, idCl, idMed, quantite, stock);
    }

    private void enregistrerVente(
            int idPh,
            int idCl,
            int idMed,
            int quantite,
            int stock) {

        boolean venteReussie =
                venteDAO.enregistrerVente(idPh, idCl, idMed, quantite);

        if (!venteReussie) {
            LOGGER.log(Level.SEVERE, VENTE_ECHEC, idMed);
            return;
        }

        medDAO.updateStock(idMed, stock - quantite);
        histDAO.ajouterHistorique(idMed, -quantite);

        LOGGER.log(Level.INFO, VENTE_OK, idMed);

        verifierStockCritique(idMed);
    }

    private void verifierStockCritique(int idMed) {
        String alerte = medDAO.stockCritique(idMed);
        if (alerte != null) {
            LOGGER.warning(alerte);
        }
    }

    public void ventesParMedicament(int idMed) {
        venteDAO.ventesParMedicament(idMed);
    }

    public void ventesParClient(int idClient) {
        venteDAO.ventesParClient(idClient);
    }

    public void ventesParPeriode(
            String dateDebut,
            String dateFin) {
        venteDAO.ventesParPeriode(dateDebut, dateFin);
    }

    public boolean annulerVente(int idVente) {
        return venteDAO.annulerVente(idVente);
    }
}