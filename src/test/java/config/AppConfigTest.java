package config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import dao.ClientDAO;
import dao.CommandeDAO;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.UserDAO;
import dao.UtilisateurDAO;
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

        assertNotNull(
                appConfig.authService(utilisateurDAO)
        );

        assertNotNull(
                appConfig.clientService(clientDAO)
        );

        assertNotNull(
                appConfig.medicamentService(
                        medicamentDAO,
                        stockHistoriqueDAO
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
                        stockHistoriqueDAO
                )
        );

        assertNotNull(
                appConfig.venteService(
                        medicamentDAO,
                        venteDAO,
                        stockHistoriqueDAO
                )
        );

        assertNotNull(
                appConfig.userService(userDAO)
        );
    }
}
