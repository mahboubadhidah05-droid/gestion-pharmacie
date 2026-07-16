package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dao.ClientDAO;
import dao.CommandeDAO;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.UserDAO;
import dao.UtilisateurDAO;
import dao.VenteDAO;

import service.AuthService;
import service.ClientService;
import service.CommandeService;
import service.MedicamentService;
import service.StockService;
import service.UserService;
import service.VenteService;

@Configuration
public class AppConfig {

    // ---------- DAO ----------

    @Bean
    public ClientDAO clientDAO() {
        return new ClientDAO();
    }

    @Bean
    public CommandeDAO commandeDAO() {
        return new CommandeDAO();
    }

    @Bean
    public MedicamentDAO medicamentDAO() {
        return new MedicamentDAO();
    }

    @Bean
    public StockHistoriqueDAO stockHistoriqueDAO() {
        return new StockHistoriqueDAO();
    }

    @Bean
    public UserDAO userDAO() {
        return new UserDAO();
    }

    @Bean
    public UtilisateurDAO utilisateurDAO() {
        return new UtilisateurDAO();
    }

    @Bean
    public VenteDAO venteDAO() {
        return new VenteDAO();
    }

    // ---------- Services ----------

    @Bean
    public AuthService authService(UtilisateurDAO utilisateurDAO) {
        return new AuthService(utilisateurDAO);
    }

    @Bean
    public ClientService clientService(ClientDAO clientDAO) {
        return new ClientService(clientDAO);
    }

    @Bean
    public MedicamentService medicamentService(
            MedicamentDAO medicamentDAO,
            StockHistoriqueDAO stockHistoriqueDAO) {
        return new MedicamentService(medicamentDAO, stockHistoriqueDAO);
    }

    @Bean
    public StockService stockService(
            MedicamentDAO medicamentDAO,
            StockHistoriqueDAO stockHistoriqueDAO) {
        return new StockService(medicamentDAO, stockHistoriqueDAO);
    }

    @Bean
    public CommandeService commandeService(
            CommandeDAO commandeDAO,
            MedicamentDAO medicamentDAO,
            StockHistoriqueDAO stockHistoriqueDAO) {
        return new CommandeService(commandeDAO, medicamentDAO, stockHistoriqueDAO);
    }

    @Bean
    public VenteService venteService(
            MedicamentDAO medicamentDAO,
            VenteDAO venteDAO,
            StockHistoriqueDAO stockHistoriqueDAO) {
        return new VenteService(medicamentDAO, venteDAO, stockHistoriqueDAO);
    }

    @Bean
    public UserService userService(UserDAO userDAO) {
        return new UserService(userDAO);
    }
}