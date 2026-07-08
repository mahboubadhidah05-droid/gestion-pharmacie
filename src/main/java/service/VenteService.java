package service;

import First_project.MedicamentDAO;
import First_project.StockHistoriqueDAO;
import First_project.VenteDAO;

public class VenteService {

    private final MedicamentDAO medDAO;
    private final VenteDAO venteDAO;
    private final StockHistoriqueDAO histDAO;

    public VenteService(MedicamentDAO medDAO, VenteDAO venteDAO, StockHistoriqueDAO histDAO) {
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
            System.out.println("✅ Vente enregistrée et stock mis à jour !");
        } else {
            System.out.println("❌ Stock insuffisant");
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
