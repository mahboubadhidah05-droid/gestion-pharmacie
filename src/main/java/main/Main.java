package main;

import java.util.Scanner;
import java.util.logging.Logger;

import dao.ClientDAO;
import dao.CommandeDAO;
import dao.MedicamentDAO;
import dao.StockHistoriqueDAO;
import dao.VenteDAO;

import service.AuthService;
import service.ClientService;
import service.CommandeService;
import service.MedicamentService;
import service.StockService;
import service.UserService;
import service.VenteService;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final String ID_CLIENT_MSG = "ID Client : ";
    private static final String ID_MEDICAMENT_MSG = "ID Médicament : ";
    private static final String CHOIX_MSG = "Choix : ";

    private static final Scanner sc = new Scanner(System.in);
    private static String loginConnecte;

    public static void main(String[] args) {

        LOGGER.info("=== APPLICATION GESTION PHARMACIE ===");

        LOGGER.info("Login : ");
        loginConnecte = sc.nextLine();

        LOGGER.info("Mot de passe : ");
        String pwd = sc.nextLine();

        String role = AuthService.login(loginConnecte, pwd);

        switch (role) {

            case "PHARMACIEN":
                LOGGER.info("Connecté : Pharmacien");
                menuPharmacien();
                break;

            case "GESTIONNAIRE":
                LOGGER.info("Connecté : Gestionnaire");
                menuGestionnaire();
                break;

            default:
                LOGGER.warning("Accès refusé !");
        }

        LOGGER.info("Fin de l'application.");
        sc.close();
    }


    private static void menuPharmacien() {

        MedicamentDAO medDAO = new MedicamentDAO();

        ClientDAO clientDAO = new ClientDAO();
        ClientService clientService = new ClientService(clientDAO);

        VenteDAO venteDAO = new VenteDAO();

        StockHistoriqueDAO histDAO = new StockHistoriqueDAO();

        VenteService vservice =
                new VenteService(
                        medDAO,
                        venteDAO,
                        histDAO
                );

        int choix;

        do {

            LOGGER.info("\n=== Menu Pharmacien ===");
            LOGGER.info("1- Enregistrer une vente");
            LOGGER.info("2- Consulter ventes par médicament");
            LOGGER.info("3- Consulter ventes par client");
            LOGGER.info("4- Consulter ventes par période");
            LOGGER.info("5- Annuler une vente");
            LOGGER.info("6- Consulter profil utilisateur");
            LOGGER.info("7- Créer un client");
            LOGGER.info("0- Déconnexion");

            LOGGER.info(CHOIX_MSG);

            choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {

                case 1:

                    LOGGER.info("ID Pharmacien : ");
                    int idPh = sc.nextInt();

                    LOGGER.info(ID_CLIENT_MSG);
                    int idCl = sc.nextInt();

                    LOGGER.info(ID_MEDICAMENT_MSG);
                    int idMed = sc.nextInt();

                    LOGGER.info("Quantité : ");
                    int qte = sc.nextInt();

                    sc.nextLine();

                    vservice.vendre(idPh, idCl, idMed, qte);

                    medDAO.stockCritique(idMed);

                    break;


                case 2:

                    LOGGER.info(ID_MEDICAMENT_MSG);
                    idMed = sc.nextInt();
                    sc.nextLine();

                    vservice.ventesParMedicament(idMed);

                    break;


                case 3:

                    LOGGER.info(ID_CLIENT_MSG);
                    int idClient = sc.nextInt();
                    sc.nextLine();

                    vservice.ventesParClient(idClient);

                    break;


                case 4:

                    LOGGER.info("Date début : ");
                    String d1 = sc.next();

                    LOGGER.info("Date fin : ");
                    String d2 = sc.next();

                    sc.nextLine();

                    vservice.ventesParPeriode(d1, d2);

                    break;


                case 5:

                    LOGGER.info("ID Vente : ");
                    int idVente = sc.nextInt();

                    sc.nextLine();

                    vservice.annulerVente(idVente);

                    break;


                case 6:

                    UserService.consulterProfil(loginConnecte);

                    break;


                case 7:

                    LOGGER.info("Nom : ");
                    String nom = sc.nextLine();

                    LOGGER.info("Prénom : ");
                    String prenom = sc.nextLine();

                    LOGGER.info("Email : ");
                    String email = sc.nextLine();

                    LOGGER.info("Adresse : ");
                    String adresse = sc.nextLine();

                    clientService.creerClient(
                            nom,
                            prenom,
                            email,
                            adresse
                    );

                    break;


                case 0:

                    LOGGER.info("Déconnexion Pharmacien...");

                    break;


                default:

                    LOGGER.warning("Choix invalide");

            }

        } while (choix != 0);

    }


    private static void menuGestionnaire() {

        MedicamentDAO medDAO = new MedicamentDAO();

        StockHistoriqueDAO histDAO = new StockHistoriqueDAO();

        MedicamentService medService =
                new MedicamentService(medDAO, histDAO);

        StockService stService =
                new StockService(medDAO, histDAO);

        CommandeDAO commandeDAO = new CommandeDAO();

        CommandeService cmdService =
                new CommandeService(
                        commandeDAO,
                        medDAO,
                        histDAO
                );

        VenteDAO venteDAO = new VenteDAO();

        VenteService vservice =
                new VenteService(
                        medDAO,
                        venteDAO,
                        histDAO
                );


        int choix;

        do {

            LOGGER.info("\n=== Menu Gestionnaire ===");
            LOGGER.info("1- Ajouter un médicament");
            LOGGER.info("2- Mettre à jour le stock");
            LOGGER.info("3- Consulter stock critique");
            LOGGER.info("4- Créer une commande");
            LOGGER.info("5- Consulter historique stock");
            LOGGER.info("6- Consulter ventes par médicament");
            LOGGER.info("7- Consulter ventes par client");
            LOGGER.info("8- Consulter ventes par période");
            LOGGER.info("9- Consulter profil utilisateur");
            LOGGER.info("0- Déconnexion");

            LOGGER.info(CHOIX_MSG);

            choix = sc.nextInt();
            sc.nextLine();


            switch (choix) {

                case 1:

                    LOGGER.info("Nom : ");
                    String nom = sc.next();

                    LOGGER.info("Dosage : ");
                    String dosage = sc.next();

                    LOGGER.info("Stock initial : ");
                    int stock = sc.nextInt();

                    LOGGER.info("Prix : ");
                    double prix = sc.nextDouble();

                    LOGGER.info("Seuil critique : ");
                    int seuil = sc.nextInt();

                    sc.nextLine();

                    medService.ajouter(
                            nom,
                            dosage,
                            stock,
                            prix,
                            seuil
                    );

                    break;


                case 2:

                    LOGGER.info(ID_MEDICAMENT_MSG);
                    int idMed = sc.nextInt();

                    LOGGER.info("Nouvelle quantité : ");
                    int qte = sc.nextInt();

                    sc.nextLine();

                    stService.ajouterStock(idMed, qte);

                    break;


                case 3:

                    LOGGER.info(ID_MEDICAMENT_MSG);

                    idMed = sc.nextInt();

                    sc.nextLine();

                    medDAO.stockCritique(idMed);

                    break;


                case 4:

                    LOGGER.info("ID Gestionnaire : ");
                    int idGest = sc.nextInt();

                    LOGGER.info(ID_MEDICAMENT_MSG);
                    idMed = sc.nextInt();

                    LOGGER.info("Quantité : ");
                    qte = sc.nextInt();

                    sc.nextLine();

                    cmdService.creerCommande(
                            idGest,
                            idMed,
                            qte
                    );

                    break;


                case 5:

                    histDAO.afficherHistorique();

                    break;


                case 6:

                    LOGGER.info(ID_MEDICAMENT_MSG);

                    idMed = sc.nextInt();

                    sc.nextLine();

                    vservice.ventesParMedicament(idMed);

                    break;


                case 7:

                    LOGGER.info(ID_CLIENT_MSG);

                    int idClient = sc.nextInt();

                    sc.nextLine();

                    vservice.ventesParClient(idClient);

                    break;


                case 8:

                    LOGGER.info("Date début : ");

                    String d1 = sc.next();

                    LOGGER.info("Date fin : ");

                    String d2 = sc.next();

                    sc.nextLine();

                    vservice.ventesParPeriode(d1, d2);

                    break;


                case 9:

                    UserService.consulterProfil(loginConnecte);

                    break;


                case 0:

                    LOGGER.info("Déconnexion Gestionnaire...");

                    break;


                default:

                    LOGGER.warning("Choix invalide");
            }

        } while (choix != 0);

    }

}
