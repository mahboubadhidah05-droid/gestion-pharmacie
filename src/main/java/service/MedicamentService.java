package service;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;

public class MedicamentService {

    private final MedicamentDAO dao;
    private final StockHistoriqueDAO histDAO;

    public MedicamentService(MedicamentDAO dao, StockHistoriqueDAO histDAO) {
        this.dao = dao;
        this.histDAO = histDAO;
    }

    public void ajouter(String nom, String dosage, int stock, double prix, int seuil) {
        dao.ajouterMedicament(nom, dosage, stock, prix, seuil);
        int idMed = dao.getIdMedicamentParNomEtDosage(nom, dosage);
        histDAO.ajouterHistorique(idMed, stock);
    }

    public void updateStock(int id, int qte) {
        dao.updateStock(id, qte);
    }

    public void stockCritique(int idMed) {
        String message = dao.stockCritique(idMed);
        if (message != null) {
            NotificationService.notifierStockCritique(message);
        }
    }
}
