package main;

import java.util.Scanner;
import java.util.logging.Logger;

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

public class Main {

    private static final Logger LOGGER =
            Logger.getLogger(Main.class.getName());

    private static final String ID_CLIENT_MSG = "ID Client : ";
    private static final String ID_MEDICAMENT_MSG = "ID Médicament : ";
    private static final String CHOIX_MSG = "Choix : ";

    private static final Scanner sc = new Scanner(System.in);

    private static String loginConnecte;

    private static void afficher(String message) {
        LOGGER.info(message);
    }


    public static void main(String[] args) {

        afficher("=== APPLICATION GESTION PHARMACIE ===");

        afficher("Login : ");
        loginConnecte = sc.nextLine();

        afficher("Mot de passe : ");
        String pwd = sc.nextLine();


     
        UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
        AuthService authService = new AuthService(utilisateurDAO);   // utilisateurDAO, pas dao

     // 3. Remplacer l'appel statique par un appel sur l'instance
     String role="ECHEC";
     try {
         role = authService.login(loginConnecte, pwd);   // au lieu de AuthService.login(...)
     } catch (Exception e) {
         e.printStackTrace();}

        switch (role) {

            case "PHARMACIEN":
                afficher("Connecté : Pharmacien");
                menuPharmacien();
                break;


            case "GESTIONNAIRE":
                afficher("Connecté : Gestionnaire");
                menuGestionnaire();
                break;


            default:
                afficher("Accès refusé !");
                break;
        }


        afficher("Fin de l'application.");

        sc.close();
    }



    private static void menuPharmacien() {


        MedicamentDAO medDAO = new MedicamentDAO();

        ClientDAO clientDAO = new ClientDAO();

        ClientService clientService =
                new ClientService(clientDAO);


        VenteDAO venteDAO = new VenteDAO();

        StockHistoriqueDAO histDAO =
                new StockHistoriqueDAO();


        VenteService vservice =
                new VenteService(
                        medDAO,
                        venteDAO,
                        histDAO
                );


        UserService userService =
                new UserService(new UserDAO());


        int choix;


        do {

            afficher("\n=== Menu Pharmacien ===");

            afficher("1- Enregistrer une vente");
            afficher("2- Consulter ventes par médicament");
            afficher("3- Consulter ventes par client");
            afficher("4- Consulter ventes par période");
            afficher("5- Annuler une vente");
            afficher("6- Consulter profil utilisateur");
            afficher("7- Créer un client");
            afficher("0- Déconnexion");


            afficher(CHOIX_MSG);


            choix = sc.nextInt();
            sc.nextLine();



            switch (choix) {


                case 1:

                    afficher("ID Pharmacien : ");
                    int idPh = sc.nextInt();


                    afficher(ID_CLIENT_MSG);
                    int idCl = sc.nextInt();


                    afficher(ID_MEDICAMENT_MSG);
                    int idMed = sc.nextInt();


                    afficher("Quantité : ");
                    int qte = sc.nextInt();


                    sc.nextLine();


                    vservice.vendre(
                            idPh,
                            idCl,
                            idMed,
                            qte
                    );


                    medDAO.stockCritique(idMed);

                    break;
                case 2:

                    afficher(ID_MEDICAMENT_MSG);

                    int idMed1 = sc.nextInt();

                    sc.nextLine();


                    vservice.ventesParMedicament(idMed1);

                    break;



                case 3:

                    afficher(ID_CLIENT_MSG);

                    int idClient = sc.nextInt();

                    sc.nextLine();


                    vservice.ventesParClient(idClient);

                    break;



                case 4:

                    afficher("Date début : ");

                    String dateDebut = sc.next();


                    afficher("Date fin : ");

                    String dateFin = sc.next();


                    sc.nextLine();


                    vservice.ventesParPeriode(
                            dateDebut,
                            dateFin
                    );

                    break;



                case 5:

                    afficher("ID Vente : ");

                    int idVente = sc.nextInt();

                    sc.nextLine();


                    vservice.annulerVente(idVente);

                    break;



                case 6:

                    userService.consulterProfil(
                            loginConnecte
                    );

                    break;



                case 7:

                    afficher("Nom : ");
                    String nom = sc.nextLine();


                    afficher("Prénom : ");
                    String prenom = sc.nextLine();


                    afficher("Email : ");
                    String email = sc.nextLine();


                    afficher("Adresse : ");
                    String adresse = sc.nextLine();



                    clientService.creerClient(
                            nom,
                            prenom,
                            email,
                            adresse
                    );

                    break;



                case 0:

                    afficher(
                            "Déconnexion Pharmacien..."
                    );

                    break;



                default:

                    afficher(
                            "Choix invalide"
                    );
            }


        } while (choix != 0);

    }





    private static void menuGestionnaire() {


        MedicamentDAO medDAO =
                new MedicamentDAO();


        StockHistoriqueDAO histDAO =
                new StockHistoriqueDAO();



        MedicamentService medService =
                new MedicamentService(
                        medDAO,
                        histDAO
                );



        StockService stService =
                new StockService(
                        medDAO,
                        histDAO
                );



        CommandeDAO commandeDAO =
                new CommandeDAO();



        CommandeService cmdService =
                new CommandeService(
                        commandeDAO,
                        medDAO,
                        histDAO
                );



        VenteDAO venteDAO =
                new VenteDAO();



        VenteService vservice =
                new VenteService(
                        medDAO,
                        venteDAO,
                        histDAO
                );



        UserService userService =
                new UserService(
                        new UserDAO()
                );



        int choix;



        do {


            afficher("\n=== Menu Gestionnaire ===");


            afficher("1- Ajouter un médicament");
            afficher("2- Mettre à jour le stock");
            afficher("3- Consulter stock critique");
            afficher("4- Créer une commande");
            afficher("5- Consulter historique stock");
            afficher("6- Consulter ventes par médicament");
            afficher("7- Consulter ventes par client");
            afficher("8- Consulter ventes par période");
            afficher("9- Consulter profil utilisateur");
            afficher("0- Déconnexion");



            afficher(CHOIX_MSG);



            choix = sc.nextInt();

            sc.nextLine();



            switch (choix) {


                case 1:

                    afficher("Nom : ");

                    String nom = sc.next();


                    afficher("Dosage : ");

                    String dosage = sc.next();


                    afficher("Stock initial : ");

                    int stock = sc.nextInt();


                    afficher("Prix : ");

                    double prix = sc.nextDouble();


                    afficher("Seuil critique : ");

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

                        afficher(ID_MEDICAMENT_MSG);

                        int idMed = sc.nextInt();


                        afficher("Nouvelle quantité : ");

                        int qte = sc.nextInt();


                        sc.nextLine();


                        stService.ajouterStock(
                                idMed,
                                qte
                        );

                        break;



                    case 3:

                        afficher(ID_MEDICAMENT_MSG);

                        idMed = sc.nextInt();

                        sc.nextLine();


                        medDAO.stockCritique(idMed);

                        break;



                    case 4:

                        afficher("ID Gestionnaire : ");

                        int idGest = sc.nextInt();


                        afficher(ID_MEDICAMENT_MSG);

                        idMed = sc.nextInt();



                        afficher("Quantité : ");

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

                        afficher(ID_MEDICAMENT_MSG);

                        idMed = sc.nextInt();

                        sc.nextLine();


                        vservice.ventesParMedicament(
                                idMed
                        );

                        break;



                    case 7:

                        afficher(ID_CLIENT_MSG);

                        int idClient = sc.nextInt();

                        sc.nextLine();


                        vservice.ventesParClient(
                                idClient
                        );

                        break;



                    case 8:

                        afficher("Date début : ");

                        String dateDebut = sc.next();



                        afficher("Date fin : ");

                        String dateFin = sc.next();


                        sc.nextLine();



                        vservice.ventesParPeriode(
                                dateDebut,
                                dateFin
                        );

                        break;



                    case 9:

                        userService.consulterProfil(
                                loginConnecte
                        );

                        break;



                    case 0:

                        afficher(
                                "Déconnexion Gestionnaire..."
                        );

                        break;



                    default:

                        afficher(
                                "Choix invalide"
                        );
                }


            } while (choix != 0);


        }

    }