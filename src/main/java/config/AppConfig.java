package config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;

import dao.ClientDAO;
import dao.CnamRapportDAO;
import dao.CommandeDAO;
import dao.FournisseurDAO;
import dao.LotMedicamentDAO;
import dao.MedicamentDAO;
import dao.NotificationDAO;
import dao.StockHistoriqueDAO;
import dao.UserDAO;
import dao.UtilisateurDAO;
import dao.UtilisateurGestionDAO;
import dao.VenteDAO;

import service.AlerteStockService;
import service.AuthService;
import service.ClientService;
import service.CommandeService;
import service.FournisseurService;
import service.MailService;
import service.MedicamentService;
import service.StockService;
import service.UserService;
import service.UtilisateurGestionService;
import service.VenteService;

@Configuration
@EnableScheduling
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

    @Bean
    public FournisseurDAO fournisseurDAO() {
        return new FournisseurDAO();
    }

    @Bean
    public NotificationDAO notificationDAO() {
        return new NotificationDAO();
    }

    @Bean
    public UtilisateurGestionDAO utilisateurGestionDAO() {
        return new UtilisateurGestionDAO();
    }

    @Bean
    public LotMedicamentDAO lotMedicamentDAO() {
        return new LotMedicamentDAO();
    }

    @Bean
    public CnamRapportDAO cnamRapportDAO() {
        return new CnamRapportDAO();
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
            StockHistoriqueDAO stockHistoriqueDAO,
            LotMedicamentDAO lotMedicamentDAO) {
        return new MedicamentService(medicamentDAO, stockHistoriqueDAO, lotMedicamentDAO);
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
            StockHistoriqueDAO stockHistoriqueDAO,
            LotMedicamentDAO lotMedicamentDAO) {
        return new CommandeService(
                commandeDAO, medicamentDAO, stockHistoriqueDAO, lotMedicamentDAO
        );
    }

    @Bean
    public VenteService venteService(
            MedicamentDAO medicamentDAO,
            VenteDAO venteDAO,
            StockHistoriqueDAO stockHistoriqueDAO,
            LotMedicamentDAO lotMedicamentDAO) {
        return new VenteService(
                medicamentDAO, venteDAO, stockHistoriqueDAO, lotMedicamentDAO
        );
    }

    @Bean
    public UserService userService(UserDAO userDAO) {
        return new UserService(userDAO);
    }

    @Bean
    public FournisseurService fournisseurService(FournisseurDAO fournisseurDAO) {
        return new FournisseurService(fournisseurDAO);
    }

    @Bean
    public UtilisateurGestionService utilisateurGestionService(
            UtilisateurGestionDAO utilisateurGestionDAO) {
        return new UtilisateurGestionService(utilisateurGestionDAO);
    }

    // ---------- Email ----------

    @Bean
    public JavaMailSenderImpl javaMailSender(
            @Value("${spring.mail.username}") String username,
            @Value("${spring.mail.password}") String password) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    @Bean
    public MailService mailService(
            JavaMailSenderImpl javaMailSender,
            @Value("${spring.mail.username}") String expediteur) {
        return new MailService(javaMailSender, expediteur);
    }

    @Bean
    public AlerteStockService alerteStockService(
            MedicamentService medicamentService,
            MailService mailService,
            UserDAO userDAO) {
        return new AlerteStockService(medicamentService, mailService, userDAO);
    }

    @Bean
    public StockAlertScheduler stockAlertScheduler(
            AlerteStockService alerteStockService) {
        return new StockAlertScheduler(alerteStockService);
    }
}