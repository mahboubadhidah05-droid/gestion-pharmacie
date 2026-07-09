package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.VenteDAO;

public class VenteService {

    private static final Logger LOGGER =
            Logger.getLogger(VenteService.class.getName());

    private final MedicamentDAO medDAO;
    private final VenteDAO venteDAO;
    private final StockHistoriqueDAO histDAO;

    public VenteService(MedicamentDAO medDAO,
                        VenteDAO venteDAO,
                        StockHistoriqueDAO histDAO) {

        this.medDAO = medDAO;
        this.venteDAO = venteDAO;
        this.histDAO = histDAO;
    }

    public void vendre(int idPh, int idCl, int idMed, int qte) {

        int stock = medDAO.getStock(idMed);

        if (stock >= qte) {

            venteDAO.enregistrerVente(idPh, idCl, idMed, qte);

            medDAO.updateStock(idMed, stock - qte);

            histDAO.ajouterHistorique(idMed, -qte);

            LOGGER.log(Level.INFO,
                    "Vente enregistrée et stock mis à jour. Médicament ID={0}",
                    idMed);

        } else {

            LOGGER.log(Level.WARNING,
                    "Stock insuffisant pour le médicament ID={0}",
                    idMed);
        }
    }

    public void ventesParMedicament(int idMed) {
        venteDAO.ventesParMedicament(idMed);
    }

    public void ventesParClient(int idClient) {
        venteDAO.ventesParClient(idClient);
    }

    public void ventesParPeriode(String dateDebut, String dateFin) {
        venteDAO.ventesParPeriode(dateDebut, dateFin);
    }

    public void annulerVente(int idVente) {
        venteDAO.annulerVente(idVente);
    }
}