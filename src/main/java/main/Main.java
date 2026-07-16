package main;

import java.util.Scanner;
import java.util.logging.Level;
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
    private static final String DATE_DEBUT_MSG = "Date début : ";
    private static final String DATE_FIN_MSG =  "Date fin : ";
    private static final String QUANTITE_MSG = "Quantité : " ;
    private static final String NOM_MSG = "Nom : " ;
    private static final String PRENOM_MSG = "Prénom : " ;
    private static final String EMAIL_MSG = "Email : ";
    private static final String ADRESSE_MSG = "Adresse : ";

    private static final Scanner sc = new Scanner(System.in);

    private static String loginConnecte;

    private static void afficher(String message) {
        LOGGER.info(message);
    }
    private static int lireEntier(final String message) {
        afficher(message);
        int valeur = sc.nextInt();
        sc.nextLine();
        return valeur;
    }
    private static String lireTexte(final String message) {
        afficher(message);
        return sc.nextLine();
    }
    private static String lireDate(final String message) {
        afficher(message);
        return sc.next();
    }
    private static double lireDouble(final String message) {
        afficher(message);
        double valeur = sc.nextDouble();
        sc.nextLine();
        return valeur;
    }
    private static void consulterVentesParPeriode(final VenteService service) {
        String debut = lireDate(DATE_DEBUT_MSG);
        String fin = lireDate( DATE_FIN_MSG);
        sc.nextLine();
        service.ventesParPeriode(debut, fin);
    }
    private static void consulterProfil(final UserService service) {
        service.consulterProfil(loginConnecte);
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
    	 LOGGER.log(Level.SEVERE, "Erreur lors de la connexion", e);}

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
            afficher("4- Consulter ventes par période (format date : AAAA-MM-JJ)");
            afficher("5- Annuler une vente");
            afficher("6- Consulter profil utilisateur");
            afficher("7- Créer un client");
            afficher("0- Déconnexion");


            afficher(CHOIX_MSG);


            choix = sc.nextInt();
            sc.nextLine();


            switch (choix) {

            case 1: {
                final int idPh = lireEntier("ID Pharmacien : ");
                int idCl = lireEntier(ID_CLIENT_MSG);

                if (!clientDAO.existeClient(idCl)) {

                    afficher("Client introuvable. Merci de saisir ses informations :");

                    final String nom = lireTexte(NOM_MSG);
                    final String prenom = lireTexte(PRENOM_MSG);
                    final String email = lireTexte(EMAIL_MSG);
                    final String adresse = lireTexte(ADRESSE_MSG);

                    idCl = clientDAO.ajouterClient(nom, prenom, email, adresse);

                    if (idCl == -1) {
                        afficher("Erreur lors de la création du client. Vente annulée.");
                        break;
                    }

                    afficher("Client créé avec succès, ID=" + idCl);
                }

                final int idMed = lireEntier(ID_MEDICAMENT_MSG);
                final int qte = lireEntier(QUANTITE_MSG);

                vservice.vendre(idPh, idCl, idMed, qte);

                break;
            }


            case 2: {
                final int idMed = lireEntier(ID_MEDICAMENT_MSG);

                vservice.ventesParMedicament(idMed);

                break;
            }


            case 3: {
                final int idClient = lireEntier(ID_CLIENT_MSG);

                vservice.ventesParClient(idClient);

                break;
            }


            case 4: {

                consulterVentesParPeriode(vservice);

                break;
            }

            case 5: {
                final int idVente = lireEntier("ID Vente : ");

                boolean annulee = vservice.annulerVente(idVente);

                if (annulee) {
                    afficher("Vente annulée avec succès.");
                } else {
                    afficher("Aucune vente trouvée avec cet ID. Annulation impossible.");
                }

                break;
            }


            case 6: {

                consulterProfil(userService);

                break;
            }


            case 7: {

                final String nom = lireTexte(NOM_MSG);
                final String prenom = lireTexte(PRENOM_MSG);
                final String email = lireTexte(EMAIL_MSG);
                final String adresse = lireTexte(ADRESSE_MSG);

                clientService.creerClient(
                        nom,
                        prenom,
                        email,
                        adresse
                );

                break;
            }


            case 0: {

                afficher("Déconnexion Pharmacien...");

                break;
            }


            default: {

                afficher("Choix invalide");

                break;
            }
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

            case 1: {

                final String nom = lireTexte(NOM_MSG);
                final String dosage = lireTexte("Dosage : ");

                final int stock = lireEntier("Stock initial : ");
                final double prix = lireDouble("Prix : ");
                final int seuil = lireEntier("Seuil critique : ");

                medService.ajouter(
                        nom,
                        dosage,
                        stock,
                        prix,
                        seuil
                );

                break;
            }


            case 2: {

                final int idMed = lireEntier(ID_MEDICAMENT_MSG);
                final int qte = lireEntier("Nouvelle quantité : ");

                stService.ajouterStock(
                        idMed,
                        qte
                );

                break;
            }


            case 3: {

                final int idMed = lireEntier(ID_MEDICAMENT_MSG);

                String message = medService.stockCritique(idMed);

                if (message != null) {
                    afficher(message);
                } else {
                    afficher("Stock normal, aucun seuil critique atteint.");
                }

                break;
            }


            case 4: {

                final int idGest = lireEntier("ID Gestionnaire : ");
                final int idMed = lireEntier(ID_MEDICAMENT_MSG);
                final int qte = lireEntier(QUANTITE_MSG);

                cmdService.creerCommande(
                        idGest,
                        idMed,
                        qte
                );

                break;
            }


            case 5: {

                histDAO.afficherHistorique();

                break;
            }


            case 6: {

                final int idMed = lireEntier(ID_MEDICAMENT_MSG);

                vservice.ventesParMedicament(idMed);

                break;
            }


            case 7: {

                final int idClient = lireEntier(ID_CLIENT_MSG);

                vservice.ventesParClient(idClient);

                break;
            }


            case 8: {

                final String dateDebut = lireDate(DATE_DEBUT_MSG);
                final String dateFin = lireDate( DATE_FIN_MSG);

                vservice.ventesParPeriode(
                        dateDebut,
                        dateFin
                );

                break;
            }


            case 9: {

                consulterProfil(userService);

                break;
            }


            case 0: {

                afficher("Déconnexion Gestionnaire...");

                break;
            }


            default: {

                afficher("Choix invalide");

                break;
            }
        }


            } while (choix != 0);


        }

    }