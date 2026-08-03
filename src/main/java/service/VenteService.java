package service;
import java.util.logging.Level;
import java.util.List;
import dto.VenteResponse;
import dto.LotResponse;
import dto.MedicamentVenteInfo;
import java.util.logging.Logger;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.VenteDAO;
import dao.LotMedicamentDAO;
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
    private final LotMedicamentDAO lotDAO;
    public VenteService(
            MedicamentDAO medDAO,
            VenteDAO venteDAO,
            StockHistoriqueDAO histDAO,
            LotMedicamentDAO lotDAO) {
        this.medDAO = medDAO;
        this.venteDAO = venteDAO;
        this.histDAO = histDAO;
        this.lotDAO = lotDAO;
    }
    public boolean vendre(
            int idPh,
            int idCl,
            int idMed,
            int quantite) {
        int stock = medDAO.getStock(idMed);
        if (stock < quantite) {
            LOGGER.log(Level.WARNING, STOCK_INSUFFISANT, idMed);
            return false;
        }

        MedicamentVenteInfo infos = medDAO.getInfosVente(idMed);

        double montantTotal =
                infos != null ? infos.prix() * quantite : 0.0;

        double montantRembourse =
                (infos != null && infos.conventionneCnam())
                        ? montantTotal * infos.tauxRemboursement()
                        : 0.0;

        double ticketModerateur = montantTotal - montantRembourse;

        return enregistrerVente(
                idPh, idCl, idMed, quantite, stock,
                montantRembourse, ticketModerateur
        );
    }
    private boolean enregistrerVente(
            int idPh,
            int idCl,
            int idMed,
            int quantite,
            int stock,
            double montantRembourse,
            double ticketModerateur) {
        boolean venteReussie =
                venteDAO.enregistrerVente(
                        idPh, idCl, idMed, quantite,
                        montantRembourse, ticketModerateur
                );
        if (!venteReussie) {
            LOGGER.log(Level.SEVERE, VENTE_ECHEC, idMed);
            return false;
        }
        medDAO.updateStock(idMed, stock - quantite);
        histDAO.ajouterHistorique(idMed, -quantite);
        deduireDesLotsFifo(idMed, quantite);
        LOGGER.log(Level.INFO, VENTE_OK, idMed);
        verifierStockCritique(idMed);
        return true;
    }
    /**
     * Déduit la quantité vendue des lots existants, en commençant par
     * celui qui périme le plus tôt (FIFO), en cascade sur plusieurs
     * lots si un seul ne suffit pas à couvrir la vente.
     */
    private void deduireDesLotsFifo(int idMed, int quantiteADeduire) {

        List<LotResponse> lots = lotDAO.getLotsDisponibles(idMed);
        int restant = quantiteADeduire;

        for (LotResponse lot : lots) {

            if (restant <= 0) {
                break;
            }

            int deductionSurCeLot = Math.min(lot.quantite(), restant);
            int nouvelleQuantite = lot.quantite() - deductionSurCeLot;

            lotDAO.mettreAJourQuantiteLot(lot.id(), nouvelleQuantite);

            restant -= deductionSurCeLot;
        }
    }
    private void verifierStockCritique(int idMed) {
        String alerte = medDAO.stockCritique(idMed);
        if (alerte != null) {
            LOGGER.warning(alerte);
        }
    }
    public List<VenteResponse> ventesParMedicament(int idMed) {
        return venteDAO.ventesParMedicament(idMed);
    }
    public List<VenteResponse> ventesParNomMedicament(String nomMedicament) {
        return venteDAO.ventesParNomMedicament(nomMedicament);
    }
    public List<VenteResponse> ventesParClient(int idClient) {
        return venteDAO.ventesParClient(idClient);
    }
    public List<VenteResponse> ventesParNomClient(String nom, String prenom) {
        return venteDAO.ventesParNomClient(nom, prenom);
    }
    public List<VenteResponse> ventesParPeriode(
            String dateDebut,
            String dateFin) {
        return venteDAO.ventesParPeriode(dateDebut, dateFin);
    }
    public boolean annulerVente(int idVente) {
        return venteDAO.annulerVente(idVente);
    }
}