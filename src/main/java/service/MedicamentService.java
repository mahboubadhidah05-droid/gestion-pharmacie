package service;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import java.util.List;

import dto.MedicamentResponse;

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

    public int getIdParNomEtDosage(String nom, String dosage) {
        return dao.getIdMedicamentParNomEtDosage(nom, dosage);
    }
    public List<MedicamentResponse> listerMedicaments() {
        return dao.listerMedicaments();
    }

    public int getStock(int id) {
        return dao.getStock(id);
    }

    public String stockCritique(int idMed) {
        String message = dao.stockCritique(idMed);
        if (message != null) {
            NotificationService.notifierStockCritique(message);
        }
        return message;
    }
}