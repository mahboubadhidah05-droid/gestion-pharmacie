package service;

import java.util.List;

import dao.UserDAO;
import dto.MedicamentResponse;

/**
 * Logique partagée de vérification des stocks critiques + envoi de
 * l'email d'alerte — utilisée à la fois par la tâche planifiée
 * quotidienne (StockAlertScheduler) et par le bouton de test manuel
 * (pour pouvoir démontrer la fonctionnalité sans attendre 8h du matin).
 */
public class AlerteStockService {

    private final MedicamentService medicamentService;
    private final MailService mailService;
    private final UserDAO userDAO;

    public AlerteStockService(
            MedicamentService medicamentService,
            MailService mailService,
            UserDAO userDAO) {

        this.medicamentService = medicamentService;
        this.mailService = mailService;
        this.userDAO = userDAO;
    }

    /**
     * Vérifie les stocks critiques et envoie l'email s'il y en a.
     *
     * @return le nombre de médicaments en stock critique trouvés
     *         (l'email n'est envoyé que si ce nombre est supérieur
     *         à 0 et qu'au moins un email de gestionnaire existe).
     */
    public int verifierEtEnvoyer() {

        List<MedicamentResponse> critiques =
                medicamentService.listerMedicaments()
                        .stream()
                        .filter(m -> m.stock() <= m.seuilCritique())
                        .toList();

        List<String> emails =
                userDAO.getEmailsGestionnaires();

        mailService.envoyerAlerteStockCritique(emails, critiques);

        return critiques.size();
    }
}