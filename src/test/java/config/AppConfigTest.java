package config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import dao.ClientDAO;
import dao.CommandeDAO;
import dao.FournisseurDAO;
import dao.LotMedicamentDAO;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.UserDAO;
import dao.UtilisateurDAO;
import dao.UtilisateurGestionDAO;
import dao.VenteDAO;

class AppConfigTest {

    private final AppConfig appConfig =
            new AppConfig();

    @Test
    void doitCreerTousLesDaos() {

        assertNotNull(appConfig.clientDAO());
        assertNotNull(appConfig.commandeDAO());
        assertNotNull(appConfig.medicamentDAO());
        assertNotNull(appConfig.stockHistoriqueDAO());
        assertNotNull(appConfig.userDAO());
        assertNotNull(appConfig.utilisateurDAO());
        assertNotNull(appConfig.venteDAO());
        assertNotNull(appConfig.fournisseurDAO());
        assertNotNull(appConfig.notificationDAO());
        assertNotNull(appConfig.utilisateurGestionDAO());
        assertNotNull(appConfig.lotMedicamentDAO());
        assertNotNull(appConfig.cnamRapportDAO());
    }

    @Test
    void doitCreerTousLesServices() {

        ClientDAO clientDAO =
                appConfig.clientDAO();

        CommandeDAO commandeDAO =
                appConfig.commandeDAO();

        MedicamentDAO medicamentDAO =
                appConfig.medicamentDAO();

        StockHistoriqueDAO stockHistoriqueDAO =
                appConfig.stockHistoriqueDAO();

        UserDAO userDAO =
                appConfig.userDAO();

        UtilisateurDAO utilisateurDAO =
                appConfig.utilisateurDAO();

        VenteDAO venteDAO =
                appConfig.venteDAO();

        FournisseurDAO fournisseurDAO =
                appConfig.fournisseurDAO();

        UtilisateurGestionDAO utilisateurGestionDAO =
                appConfig.utilisateurGestionDAO();

        LotMedicamentDAO lotMedicamentDAO =
                appConfig.lotMedicamentDAO();

        assertNotNull(
                appConfig.authService(utilisateurDAO)
        );

        assertNotNull(
                appConfig.clientService(clientDAO)
        );

        assertNotNull(
                appConfig.medicamentService(
                        medicamentDAO,
                        stockHistoriqueDAO,
                        lotMedicamentDAO
                )
        );

        assertNotNull(
                appConfig.stockService(
                        medicamentDAO,
                        stockHistoriqueDAO
                )
        );

        assertNotNull(
                appConfig.commandeService(
                        commandeDAO,
                        medicamentDAO,
                        stockHistoriqueDAO,
                        lotMedicamentDAO
                )
        );

        assertNotNull(
                appConfig.venteService(
                        medicamentDAO,
                        venteDAO,
                        stockHistoriqueDAO,
                        lotMedicamentDAO
                )
        );

        assertNotNull(
                appConfig.userService(userDAO)
        );

        assertNotNull(
                appConfig.fournisseurService(fournisseurDAO)
        );

        assertNotNull(
                appConfig.utilisateurGestionService(utilisateurGestionDAO)
        );
    }

    @Test
    void doitCreerLesBeansEmail() {

        org.springframework.mail.javamail.JavaMailSenderImpl mailSender =
                appConfig.javaMailSender(
                        "test@gmail.com",
                        "mot-de-passe-test"
                );

        assertNotNull(mailSender);

        service.MailService mailService =
                appConfig.mailService(mailSender, "test@gmail.com");

        assertNotNull(mailService);

        service.AlerteStockService alerteStockService =
                appConfig.alerteStockService(
                        appConfig.medicamentService(
                                appConfig.medicamentDAO(),
                                appConfig.stockHistoriqueDAO(),
                                appConfig.lotMedicamentDAO()
                        ),
                        mailService,
                        appConfig.userDAO()
                );

        assertNotNull(alerteStockService);

        assertNotNull(
                appConfig.stockAlertScheduler(alerteStockService)
        );
    }
}