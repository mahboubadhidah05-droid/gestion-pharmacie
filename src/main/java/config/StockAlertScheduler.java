package config;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import dao.UserDAO;
import dto.MedicamentResponse;
import service.MailService;
import service.MedicamentService;

public class StockAlertScheduler {

    private final MedicamentService medicamentService;
    private final MailService mailService;
    private final UserDAO userDAO;

    public StockAlertScheduler(
            MedicamentService medicamentService,
            MailService mailService,
            UserDAO userDAO) {

        this.medicamentService = medicamentService;
        this.mailService = mailService;
        this.userDAO = userDAO;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verifierStocksCritiques() {
        executerVerification();
    }

    public int executerVerification() {

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