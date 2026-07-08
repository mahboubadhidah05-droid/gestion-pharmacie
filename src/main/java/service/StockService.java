package service;

import First_project.MedicamentDAO;
import First_project.StockHistoriqueDAO;

public class StockService {

    private final MedicamentDAO medDAO;
    private final StockHistoriqueDAO histDAO;

    public StockService(MedicamentDAO medDAO, StockHistoriqueDAO histDAO) {
        this.medDAO = medDAO;
        this.histDAO = histDAO;
    }

    public void ajouterStock(int idMed, int qte) {
        int stockActuel = medDAO.getStock(idMed);
        if (stockActuel == -1) {
            System.out.println("❌ Médicament introuvable");
            return;
        }
        int nouveauStock = stockActuel + qte;
        medDAO.updateStock(idMed, nouveauStock);
        histDAO.ajouterHistorique(idMed, qte);
        System.out.println("✅ Stock mis à jour");
    }
}