package service;

import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.LotMedicamentDAO;
import java.util.List;

import dto.LotResponse;
import dto.MedicamentResponse;
import dto.MedicamentVenteInfo;
import dto.NomMedicamentResponse;

public class MedicamentService {

    private final MedicamentDAO dao;
    private final StockHistoriqueDAO histDAO;
    private final LotMedicamentDAO lotDAO;

    public MedicamentService(
            MedicamentDAO dao,
            StockHistoriqueDAO histDAO,
            LotMedicamentDAO lotDAO) {
        this.dao = dao;
        this.histDAO = histDAO;
        this.lotDAO = lotDAO;
    }

    public void ajouter(
            String nom, String dosage, int stock, double prix,
            int seuil, String datePeremption,
            boolean conventionneCnam, double tauxRemboursement,
            String codeBarre) {

        int idExistant = dao.getIdMedicamentParNomEtDosage(nom, dosage);

        if (idExistant != -1) {

            int stockActuel = dao.getStock(idExistant);
            dao.updateStock(idExistant, stockActuel + stock);
            histDAO.ajouterHistorique(idExistant, stock);
            lotDAO.ajouterLot(idExistant, stock, datePeremption);
            return;
        }

        dao.ajouterMedicament(
                nom, dosage, stock, prix, seuil, datePeremption,
                conventionneCnam, tauxRemboursement, codeBarre
        );
        int idMed = dao.getIdMedicamentParNomEtDosage(nom, dosage);
        histDAO.ajouterHistorique(idMed, stock);
        lotDAO.ajouterLot(idMed, stock, datePeremption);
    }

    public void updateStock(int id, int qte) {
        dao.updateStock(id, qte);
    }

    public int getIdParNomEtDosage(String nom, String dosage) {
        return dao.getIdMedicamentParNomEtDosage(nom, dosage);
    }

    /**
     * Retrouve l'ID d'un médicament à partir de son code-barres
     * (utilisé lors d'une vente, après un scan).
     *
     * @return l'ID trouvé, ou -1 si aucun médicament ne correspond.
     */
    public int getIdParCodeBarre(String codeBarre) {
        return dao.getIdParCodeBarre(codeBarre);
    }

    public dto.MedicamentScanResponse getResumeParId(int id) {
        return dao.getResumeParId(id);
    }

    public MedicamentVenteInfo getInfosVente(int id) {
        return dao.getInfosVente(id);
    }

    /**
     * Liste enrichie : la date de péremption et la quantité périmée
     * sont recalculées depuis les vrais lots (pas depuis un champ
     * statique), pour rester cohérent avec le système de lots.
     */
    public List<MedicamentResponse> listerMedicaments() {

        List<MedicamentResponse> medicaments = dao.listerMedicaments();
        List<MedicamentResponse> corriges = new java.util.ArrayList<>();
        String aujourdhui = java.time.LocalDate.now().toString();

        for (MedicamentResponse med : medicaments) {

            String datePlusProche =
                    lotDAO.getDatePeremptionLaPlusProche(med.id());

            int quantitePerimee =
                    lotDAO.getQuantitePerimee(med.id(), aujourdhui);

            corriges.add(new MedicamentResponse(
                    med.id(),
                    med.nom(),
                    med.dosage(),
                    med.stock(),
                    med.prix(),
                    med.seuilCritique(),
                    datePlusProche,
                    quantitePerimee,
                    med.conventionneCnam(),
                    med.tauxRemboursement(),
                    med.codeBarre()
            ));
        }

        return corriges;
    }

    public List<NomMedicamentResponse> listerNoms() {
        return dao.listerNoms();
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

    /**
     * Retire (met à zéro) uniquement les lots dont la date de péremption
     * est dépassée, sans toucher aux lots encore valides du même
     * médicament, et enregistre le retrait dans l'historique.
     *
     * @return la quantité retirée (0 si aucun lot périmé n'avait de
     *         stock), ou un code d'erreur négatif : -1 médicament
     *         introuvable, -2 aucun lot périmé trouvé.
     */
    public int retirerStockPerime(String nom, String dosage) {

        int id = dao.getIdMedicamentParNomEtDosage(nom, dosage);

        if (id == -1) {
            return -1;
        }

        String aujourdhui = java.time.LocalDate.now().toString();

        List<LotResponse> lotsExpires =
                lotDAO.getLotsExpires(id, aujourdhui);

        if (lotsExpires.isEmpty()) {
            return -2;
        }

        int totalRetire = 0;

        for (LotResponse lot : lotsExpires) {

            totalRetire += lot.quantite();
            lotDAO.mettreAJourQuantiteLot(lot.id(), 0);
        }

        if (totalRetire > 0) {

            int stockActuel = dao.getStock(id);
            dao.updateStock(id, Math.max(0, stockActuel - totalRetire));
            histDAO.ajouterHistorique(id, -totalRetire);
        }

        return totalRetire;
    }

    /**
     * Vérification consolidée d'un médicament : stock critique et
     * péremption en une seule recherche, pratique pour un grand
     * catalogue où chercher séparément serait pénible.
     *
     * @return null si le médicament est introuvable.
     */
    public dto.VerifierMedicamentResponse verifier(
            String nom, String dosage) {

        int id = dao.getIdMedicamentParNomEtDosage(nom, dosage);

        if (id == -1) {
            return null;
        }

        int stock = dao.getStock(id);
        boolean critique = dao.stockCritique(id) != null;

        String aujourdhui = java.time.LocalDate.now().toString();

        int quantitePerimee = lotDAO.getQuantitePerimee(id, aujourdhui);

        String datePlusProche =
                lotDAO.getDatePeremptionLaPlusProche(id);

        boolean bientotPerime = false;

        if (quantitePerimee == 0 && datePlusProche != null) {

            String dans30Jours =
                    java.time.LocalDate.now().plusDays(30).toString();

            bientotPerime = datePlusProche.compareTo(dans30Jours) <= 0;
        }

        return new dto.VerifierMedicamentResponse(
                id,
                nom,
                dosage,
                stock,
                critique,
                quantitePerimee,
                datePlusProche,
                bientotPerime
        );
    }
}