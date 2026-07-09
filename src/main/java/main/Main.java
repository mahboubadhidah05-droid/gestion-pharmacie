package main;

import java.util.Scanner;

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

    private static final Scanner sc = new Scanner(System.in);
    private static String loginConnecte;

    public static void main(String[] args) {

        System.out.println("=== APPLICATION GESTION PHARMACIE ===");

        System.out.print("Login : ");
        loginConnecte = sc.nextLine();

        System.out.print("Mot de passe : ");
        String pwd = sc.nextLine();

        String role = AuthService.login(loginConnecte, pwd);

        switch (role) {

            case "PHARMACIEN":
                System.out.println("Connecté : Pharmacien");
                menuPharmacien();
                break;

            case "GESTIONNAIRE":
                System.out.println("Connecté : Gestionnaire");
                menuGestionnaire();
                break;

            default:
                System.out.println("Accès refusé !");
        }

        System.out.println("Fin de l'application.");
        sc.close();
    }


    // ================= MENU PHARMACIEN =================

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

            System.out.println("\n=== Menu Pharmacien ===");
            System.out.println("1- Enregistrer une vente");
            System.out.println("2- Consulter ventes par médicament");
            System.out.println("3- Consulter ventes par client");
            System.out.println("4- Consulter ventes par période");
            System.out.println("5- Annuler une vente");
            System.out.println("6- Consulter profil utilisateur");
            System.out.println("7- Créer un client");
            System.out.println("0- Déconnexion");

            System.out.print("Choix : ");

            choix = sc.nextInt();
            sc.nextLine();


            switch (choix) {

                case 1:

                    System.out.print("ID Pharmacien : ");
                    int idPh = sc.nextInt();

                    System.out.print("ID Client : ");
                    int idCl = sc.nextInt();

                    System.out.print("ID Médicament : ");
                    int idMed = sc.nextInt();

                    System.out.print("Quantité : ");
                    int qte = sc.nextInt();

                    sc.nextLine();

                    vservice.vendre(idPh, idCl, idMed, qte);

                    medDAO.stockCritique(idMed);

                    break;


                case 2:

                    System.out.print("ID Médicament : ");
                    idMed = sc.nextInt();
                    sc.nextLine();

                    vservice.ventesParMedicament(idMed);

                    break;


                case 3:

                    System.out.print("ID Client : ");
                    int idClient = sc.nextInt();
                    sc.nextLine();

                    vservice.ventesParClient(idClient);

                    break;


                case 4:

                    System.out.print("Date début : ");
                    String d1 = sc.next();

                    System.out.print("Date fin : ");
                    String d2 = sc.next();

                    sc.nextLine();

                    vservice.ventesParPeriode(d1, d2);

                    break;


                case 5:

                    System.out.print("ID Vente : ");
                    int idVente = sc.nextInt();

                    sc.nextLine();

                    vservice.annulerVente(idVente);

                    break;


                case 6:

                    UserService.consulterProfil(loginConnecte);

                    break;


                case 7:

                    System.out.print("Nom : ");
                    String nom = sc.nextLine();

                    System.out.print("Prénom : ");
                    String prenom = sc.nextLine();

                    System.out.print("Email : ");
                    String email = sc.nextLine();

                    System.out.print("Adresse : ");
                    String adresse = sc.nextLine();


                    clientService.creerClient(
                            nom,
                            prenom,
                            email,
                            adresse
                    );

                    break;


                case 0:

                    System.out.println("Déconnexion Pharmacien...");

                    break;


                default:

                    System.out.println("Choix invalide");

            }


        } while (choix != 0);

    }



    // ================= MENU GESTIONNAIRE =================


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


            System.out.println("\n=== Menu Gestionnaire ===");

            System.out.println("1- Ajouter un médicament");
            System.out.println("2- Mettre à jour le stock");
            System.out.println("3- Consulter stock critique");
            System.out.println("4- Créer une commande");
            System.out.println("5- Consulter historique stock");
            System.out.println("6- Consulter ventes par médicament");
            System.out.println("7- Consulter ventes par client");
            System.out.println("8- Consulter ventes par période");
            System.out.println("9- Consulter profil utilisateur");
            System.out.println("0- Déconnexion");


            System.out.print("Choix : ");

            choix = sc.nextInt();
            sc.nextLine();



            switch (choix) {


                case 1:

                    System.out.print("Nom : ");
                    String nom = sc.next();

                    System.out.print("Dosage : ");
                    String dosage = sc.next();

                    System.out.print("Stock initial : ");
                    int stock = sc.nextInt();

                    System.out.print("Prix : ");
                    double prix = sc.nextDouble();

                    System.out.print("Seuil critique : ");
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

                    System.out.print("ID médicament : ");
                    int idMed = sc.nextInt();

                    System.out.print("Nouvelle quantité : ");
                    int qte = sc.nextInt();

                    sc.nextLine();


                    stService.ajouterStock(idMed, qte);

                    break;



                case 3:

                    System.out.print("ID médicament : ");

                    idMed = sc.nextInt();

                    sc.nextLine();


                    medDAO.stockCritique(idMed);

                    break;



                case 4:

                    System.out.print("ID Gestionnaire : ");
                    int idGest = sc.nextInt();

                    System.out.print("ID Médicament : ");
                    idMed = sc.nextInt();

                    System.out.print("Quantité : ");
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

                    System.out.print("ID Médicament : ");

                    idMed = sc.nextInt();

                    sc.nextLine();


                    vservice.ventesParMedicament(idMed);

                    break;



                case 7:

                    System.out.print("ID Client : ");

                    int idClient = sc.nextInt();

                    sc.nextLine();


                    vservice.ventesParClient(idClient);

                    break;



                case 8:

                    System.out.print("Date début : ");

                    String d1 = sc.next();


                    System.out.print("Date fin : ");

                    String d2 = sc.next();


                    sc.nextLine();


                    vservice.ventesParPeriode(d1, d2);

                    break;



                case 9:

                    UserService.consulterProfil(loginConnecte);

                    break;



                case 0:

                    System.out.println("Déconnexion Gestionnaire...");

                    break;



                default:

                    System.out.println("Choix invalide");

            }


        } while (choix != 0);


    }

}
