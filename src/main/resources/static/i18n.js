/* ============================================================
   Gestion Pharmacie — moteur de traduction FR / EN
   ============================================================ */

const TRADUCTIONS = {

    /* ---------- Commun / header ---------- */
    "titreSite": { fr: "Gestion Pharmacie", en: "Pharmacy Management" },
    "verifApi": { fr: "Vérification de l'API…", en: "Checking API…" },
    "deconnexion": { fr: "Déconnexion", en: "Log out" },

    /* ---------- Navigation (sidebar) ---------- */
    "navTableauDeBord": { fr: "Tableau de bord", en: "Dashboard" },
    "navMedicaments": { fr: "Médicaments", en: "Medicines" },
    "navVentes": { fr: "Ventes", en: "Sales" },
    "navClients": { fr: "Clients", en: "Customers" },
    "navStockPharmacien": { fr: "Stock", en: "Stock" },
    "navCommandes": { fr: "Commandes", en: "Orders" },
    "navFournisseurs": { fr: "Fournisseurs", en: "Suppliers" },
    "navComptes": { fr: "Comptes", en: "Accounts" },
    "navHistorique": { fr: "Historique", en: "History" },
    "navRapports": { fr: "Rapports", en: "Reports" },
    "navParametres": { fr: "Paramètres", en: "Settings" },

    /* ---------- Tableau de bord (index.html) ---------- */
    "dashSousTitre": { fr: "Vue d'ensemble · API REST", en: "Overview · REST API" },
    "dashEtiquetteAcces": { fr: "ACCÈS", en: "ACCESS" },
    "dashBienvenue": { fr: "Bienvenue", en: "Welcome" },
    "dashIntro": {
        fr: "Toutes les statistiques ci-dessous utilisent les données réelles de ton stock et de tes ventes — rien n'est simulé.",
        en: "All the statistics below use the real data from your stock and your sales — nothing is simulated."
    },
    "kpiVentesJour": { fr: "Ventes aujourd'hui", en: "Sales today" },
    "kpiUnitesVenduesJour": { fr: "Unités vendues aujourd'hui", en: "Units sold today" },
    "etiquetteVentes": { fr: "VENTES", en: "SALES" },
    "evolutionVentes": { fr: "Évolution des ventes (7 derniers jours)", en: "Sales trend (last 7 days)" },
    "aucuneVentePeriode": { fr: "Aucune vente enregistrée sur cette période.", en: "No sales recorded for this period." },
    "etiquetteStock": { fr: "STOCK", en: "STOCK" },
    "kpiMedicamentsReferences": { fr: "Médicaments référencés", en: "Referenced medicines" },
    "kpiUnitesStock": { fr: "Unités en stock", en: "Units in stock" },
    "kpiValeurStock": { fr: "Valeur du stock", en: "Stock value" },
    "dinarsTunisiens": { fr: "Dinars tunisiens", en: "Tunisian dinars" },
    "kpiStockCritique": { fr: "Stock critique", en: "Critical stock" },
    "kpiBientotExpires": { fr: "Bientôt expirés", en: "Expiring soon" },
    "sousTexteExpires": { fr: "Sous 30 jours ou déjà périmés", en: "Within 30 days or already expired" },
    "repartitionStock": { fr: "Répartition du stock", en: "Stock breakdown" },
    "alertesStockCritique": { fr: "Alertes de stock critique", en: "Critical stock alerts" },
    "chargement": { fr: "Chargement…", en: "Loading…" },
    "lotsPerimesARetirer": { fr: "Lots périmés à retirer", en: "Expired batches to remove" },

    /* ---------- Boutons génériques ---------- */
    "btnActualiserListe": { fr: "Actualiser la liste", en: "Refresh list" },
    "btnModifier": { fr: "Modifier", en: "Edit" },
    "btnSupprimer": { fr: "Supprimer", en: "Delete" },
    "btnAnnuler": { fr: "Annuler", en: "Cancel" },
    "btnRechercher": { fr: "Rechercher", en: "Search" },
    "btnVerifier": { fr: "Vérifier", en: "Check" },
    "btnConsulter": { fr: "Consulter", en: "View" },

    /* ---------- Médicaments (medicaments.html) ---------- */
    "medSousTitre": { fr: "Ajout, stock, seuils critiques · API REST", en: "Adding, stock, critical thresholds · REST API" },
    "medTitrePage": { fr: "Médicaments", en: "Medicines" },
    "medDescriptionPage": { fr: "Ajout de nouveaux produits, consultation et mise à jour du stock, surveillance des seuils critiques.", en: "Adding new products, checking and updating stock, monitoring critical thresholds." },
    "medAjouterTitre": { fr: "Ajouter un médicament", en: "Add a medicine" },
    "champNom": { fr: "Nom", en: "Name" },
    "champDosage": { fr: "Dosage", en: "Dosage" },
    "champStockInitial": { fr: "Stock initial", en: "Initial stock" },
    "champPrix": { fr: "Prix (DT)", en: "Price (DT)" },
    "champSeuilCritique": { fr: "Seuil critique", en: "Critical threshold" },
    "champDatePeremptionFacultatif": { fr: "Date de péremption (facultatif)", en: "Expiration date (optional)" },
    "champConventionneCnam": { fr: "Conventionné CNAM", en: "CNAM covered" },
    "champTauxRemboursement": { fr: "Taux de remboursement (ex: 0.7 = 70%)", en: "Reimbursement rate (e.g. 0.7 = 70%)" },
    "btnAjouterAuStock": { fr: "Ajouter au stock", en: "Add to stock" },
    "medConsulterTitre": { fr: "Consulter un stock", en: "Check a stock" },
    "champNomMedicament": { fr: "Nom du médicament", en: "Medicine name" },
    "uniteEnStock": { fr: "unités en stock", en: "units in stock" },
    "medMajTitre": { fr: "Mettre à jour un stock", en: "Update a stock" },
    "champNouveauStock": { fr: "Nouveau stock", en: "New stock" },
    "btnMettreAJour": { fr: "Mettre à jour", en: "Update" },
    "medVerifierTitre": { fr: "Vérifier un médicament", en: "Check a medicine" },
    "medVerifierDescription": {
        fr: "Recherche par nom + dosage : stock critique et péremption vérifiés en une seule fois — pratique avec un grand catalogue.",
        en: "Search by name + dosage: critical stock and expiration checked at once — handy with a large catalogue."
    },
    "stockActuel": { fr: "Stock actuel", en: "Current stock" },
    "unitesPerimees": { fr: "Unités périmées", en: "Expired units" },
    "medRetirerPerimeTitre": { fr: "Retirer le stock périmé", en: "Remove expired stock" },
    "medRetirerPerimeDescription": {
        fr: "Passe le stock à zéro pour un médicament dont la date de péremption est dépassée, et enregistre le retrait dans l'historique. Refusé si le médicament n'est pas encore périmé.",
        en: "Sets stock to zero for a medicine past its expiration date, and logs the removal in the history. Refused if the medicine isn't expired yet."
    },
    "btnRetirerStockPerime": { fr: "Retirer le stock périmé", en: "Remove expired stock" },
    "medListeTitre": { fr: "Liste des médicaments", en: "Medicine list" },
    "colId": { fr: "ID", en: "ID" },
    "colNom": { fr: "Nom", en: "Name" },
    "colDosage": { fr: "Dosage", en: "Dosage" },
    "colStock": { fr: "Stock", en: "Stock" },
    "colPrixDT": { fr: "Prix (DT)", en: "Price (DT)" },
    "colSeuil": { fr: "Seuil", en: "Threshold" },
    "colPeremption": { fr: "Péremption", en: "Expiration" },

    /* ---------- Ventes (ventes.html) ---------- */
    "ventesSousTitre": { fr: "Enregistrement et consultation des ventes · API REST", en: "Recording and viewing sales · REST API" },
    "ventesTitrePage": { fr: "Ventes", en: "Sales" },
    "ventesDescriptionPage": {
        fr: "Enregistrement des ventes au comptoir et consultation de l'historique par médicament, client ou période.",
        en: "Recording counter sales and viewing history by medicine, customer or period."
    },
    "ventesEnregistrerTitre": { fr: "Enregistrer une vente", en: "Record a sale" },
    "champIdPharmacien": { fr: "ID Pharmacien", en: "Pharmacist ID" },
    "champIdClient": { fr: "ID Client", en: "Customer ID" },
    "champIdMedicament": { fr: "ID Médicament", en: "Medicine ID" },
    "champQuantite": { fr: "Quantité", en: "Quantity" },
    "btnEnregistrerVente": { fr: "Enregistrer la vente", en: "Record sale" },
    "ventesConsulterTitre": { fr: "Consulter les ventes", en: "View sales" },
    "champFiltrerPar": { fr: "Filtrer par", en: "Filter by" },
    "optMedicamentNom": { fr: "Médicament (nom)", en: "Medicine (name)" },
    "optClientNomPrenom": { fr: "Client (nom + prénom)", en: "Customer (name + surname)" },
    "optPeriode": { fr: "Période", en: "Period" },
    "champPrenom": { fr: "Prénom", en: "Surname" },
    "champDateDebut": { fr: "Date début", en: "Start date" },
    "champDateFin": { fr: "Date fin", en: "End date" },
    "btnRechercher": { fr: "Rechercher", en: "Search" },
    "colPharmacien": { fr: "Pharmacien", en: "Pharmacist" },
    "colClient": { fr: "Client", en: "Customer" },
    "colMedicament": { fr: "Médicament", en: "Medicine" },
    "colDate": { fr: "Date", en: "Date" },
    "colRembourseCnam": { fr: "Remboursé CNAM", en: "CNAM reimbursed" },
    "colTicketModerateur": { fr: "Ticket modérateur", en: "Co-payment" },
    "colActions": { fr: "Actions", en: "Actions" },
    "aucuneVenteTrouvee": { fr: "Aucune vente trouvée.", en: "No sales found." },

    /* ---------- Clients (clients.html) ---------- */
    "clientsSousTitre": { fr: "Gestion des clients · API REST", en: "Customer management · REST API" },
    "etiquetteClients": { fr: "CLIENTS", en: "CUSTOMERS" },
    "clientsTitrePage": { fr: "Clients", en: "Customers" },
    "clientsDescriptionPage": { fr: "Création et gestion des fiches clients rattachées aux ventes.", en: "Creating and managing customer records linked to sales." },
    "clientsCreerTitre": { fr: "Créer un client", en: "Create a customer" },
    "champEmail": { fr: "Email", en: "Email" },
    "champAdresse": { fr: "Adresse", en: "Address" },
    "champNumeroCnam": { fr: "Numéro CNAM (facultatif)", en: "CNAM number (optional)" },
    "btnCreerClient": { fr: "Créer le client", en: "Create customer" },

    /* ---------- Commandes (commandes.html) ---------- */
    "commandesSousTitre": { fr: "Réapprovisionnement fournisseur · API REST", en: "Supplier restocking · REST API" },
    "etiquetteCommandes": { fr: "COMMANDES", en: "ORDERS" },
    "commandesTitrePage": { fr: "Commandes", en: "Orders" },
    "commandesDescriptionPage": { fr: "Réapprovisionnement auprès des fournisseurs pour les médicaments en stock bas.", en: "Restocking from suppliers for low-stock medicines." },
    "commandesCreerTitre": { fr: "Créer une commande", en: "Create an order" },
    "champIdGestionnaire": { fr: "ID Gestionnaire", en: "Manager ID" },
    "champFournisseur": { fr: "Fournisseur", en: "Supplier" },
    "optNonPrecise": { fr: "— Non précisé —", en: "— Not specified —" },
    "champDatePeremptionLot": { fr: "Date de péremption du lot reçu (facultatif)", en: "Expiration date of received batch (optional)" },
    "btnCreerCommande": { fr: "Créer la commande", en: "Create order" },

    /* ---------- Fournisseurs (fournisseurs.html) ---------- */
    "fournisseursSousTitre": { fr: "Gestion des fournisseurs · API REST", en: "Supplier management · REST API" },
    "fournisseursTitrePage": { fr: "Fournisseurs", en: "Suppliers" },
    "fournisseursDescriptionPage": { fr: "Création et suivi des fournisseurs rattachés aux commandes de réapprovisionnement.", en: "Creating and tracking suppliers linked to restocking orders." },
    "fournisseursCreerTitre": { fr: "Créer un fournisseur", en: "Create a supplier" },
    "champTelephone": { fr: "Téléphone", en: "Phone" },
    "btnCreerFournisseur": { fr: "Créer le fournisseur", en: "Create supplier" },
    "fournisseursListeTitre": { fr: "Liste des fournisseurs", en: "Supplier list" },
    "colTelephone": { fr: "Téléphone", en: "Phone" },
    "colEmail": { fr: "Email", en: "Email" },
    "colAdresse": { fr: "Adresse", en: "Address" },
    "aucunFournisseurEnregistre": { fr: "Aucun fournisseur enregistré.", en: "No suppliers recorded." },

    /* ---------- Comptes (comptes.html) ---------- */
    "comptesSousTitre": { fr: "Gestion des comptes utilisateurs · API REST", en: "User account management · REST API" },
    "etiquetteComptes": { fr: "COMPTES", en: "ACCOUNTS" },
    "comptesTitrePage": { fr: "Comptes utilisateurs", en: "User accounts" },
    "comptesDescriptionPage": { fr: "Créer, modifier ou supprimer les comptes pharmacien et gestionnaire.", en: "Create, edit or delete pharmacist and manager accounts." },
    "comptesCreerTitre": { fr: "Créer un utilisateur", en: "Create a user" },
    "champLogin": { fr: "Login", en: "Login" },
    "champRole": { fr: "Rôle", en: "Role" },
    "rolePharmacien": { fr: "Pharmacien", en: "Pharmacist" },
    "roleGestionnaire": { fr: "Gestionnaire", en: "Manager" },
    "champMotDePasse": { fr: "Mot de passe", en: "Password" },
    "btnAnnulerModification": { fr: "Annuler la modification", en: "Cancel edit" },
    "comptesListeTitre": { fr: "Liste des utilisateurs", en: "User list" },
    "colPrenom": { fr: "Prénom", en: "Surname" },
    "colLogin": { fr: "Login", en: "Login" },
    "colRole": { fr: "Rôle", en: "Role" },

    /* ---------- Historique (historique.html) ---------- */
    "historiqueSousTitre": { fr: "Historique des mouvements de stock", en: "Stock movement history" },
    "etiquetteHistorique": { fr: "HISTORIQUE", en: "HISTORY" },
    "historiqueDescriptionPage": { fr: "Chaque ajout de médicament, vente ou commande enregistre automatiquement un mouvement ici.", en: "Every medicine addition, sale or order automatically logs a movement here." },
    "historiqueMouvementsTitre": { fr: "Mouvements de stock", en: "Stock movements" },
    "colIdMedicament": { fr: "ID Médicament", en: "Medicine ID" },
    "colMouvement": { fr: "Mouvement", en: "Movement" },
    "aucunMouvement": { fr: "Aucun mouvement enregistré pour l'instant.", en: "No movement recorded yet." },

    /* ---------- Rapports (rapports.html) ---------- */
    "rapportsSousTitre": { fr: "Statistiques et rapports d'activité", en: "Activity statistics and reports" },
    "etiquetteRapports": { fr: "RAPPORTS", en: "REPORTS" },
    "rapportsTitrePage": { fr: "Rapports", en: "Reports" },
    "rapportsDescriptionPage": { fr: "Statistiques calculées à partir des ventes réelles sur une période choisie.", en: "Statistics computed from real sales over a chosen period." },
    "rapportsChoisirPeriodeTitre": { fr: "Choisir une période", en: "Choose a period" },
    "btnGenererRapport": { fr: "Générer le rapport", en: "Generate report" },
    "aucuneVentePeriodeRapport": { fr: "Aucune vente sur cette période.", en: "No sales for this period." },
    "ventesSurPeriode": { fr: "Ventes sur la période", en: "Sales over the period" },
    "unitesVendues": { fr: "Unités vendues", en: "Units sold" },
    "chiffreAffairesEstime": { fr: "Chiffre d'affaires estimé", en: "Estimated revenue" },
    "topMedicamentsTitre": { fr: "Top médicaments vendus sur la période", en: "Top medicines sold over the period" },
    "detailParMedicamentTitre": { fr: "Détail par médicament", en: "Detail by medicine" },
    "colNombreVentes": { fr: "Nombre de ventes", en: "Number of sales" },
    "colUnitesVendues": { fr: "Unités vendues", en: "Units sold" },
    "etiquetteCnam": { fr: "CNAM", en: "CNAM" },
    "rapportCnamTitre": { fr: "Rapport CNAM", en: "CNAM report" },
    "rapportCnamDescription": {
        fr: "Suivi des remboursements CNAM sur une période (médicaments conventionnés uniquement).",
        en: "Tracking CNAM reimbursements over a period (covered medicines only)."
    },
    "btnGenererRapportCnam": { fr: "Générer le rapport CNAM", en: "Generate CNAM report" },
    "ventesConcerneesCnam": { fr: "Ventes concernées par la CNAM", en: "CNAM-related sales" },
    "totalRembourseCnam": { fr: "Total remboursé par la CNAM", en: "Total reimbursed by CNAM" },
    "totalTicketModerateur": { fr: "Total ticket modérateur (client)", en: "Total co-payment (customer)" },

    /* ---------- Paramètres (parametres.html) ---------- */
    "parametresSousTitre": { fr: "Configuration du compte et de l'application", en: "Account and application settings" },
    "etiquetteParametres": { fr: "PARAMÈTRES", en: "SETTINGS" },
    "parametresTitrePage": { fr: "Paramètres du compte", en: "Account settings" },
    "parametresDescriptionPage": {
        fr: "Gère ton mot de passe personnel. Chaque utilisateur ne peut modifier que son propre compte.",
        en: "Manage your personal password. Each user can only edit their own account."
    },
    "mesInformationsTitre": { fr: "Mes informations", en: "My information" },
    "btnEnregistrerModifications": { fr: "Enregistrer les modifications", en: "Save changes" },
    "changerMotDePasseTitre": { fr: "Changer mon mot de passe", en: "Change my password" },
    "champMotDePasseActuel": { fr: "Mot de passe actuel", en: "Current password" },
    "champNouveauMotDePasse": { fr: "Nouveau mot de passe", en: "New password" },
    "champConfirmerMotDePasse": { fr: "Confirmer le nouveau mot de passe", en: "Confirm new password" },
    "btnModifierMotDePasse": { fr: "Modifier le mot de passe", en: "Change password" },
    "champCodeBarre": { fr: "Code-barres (facultatif)", en: "Barcode (optional)" },
    "champCodeBarreScan": { fr: "Code-barres du médicament", en: "Medicine barcode" },
    "champCin": { fr: "CIN", en: "National ID (CIN)" },
    "optClientCin": { fr: "Client (CIN)", en: "Customer (National ID)" },
    "optMedicamentCodeBarre": { fr: "Médicament (code-barres)", en: "Medicine (barcode)" },
    "aucunMedicamentTrouve": { fr: "Aucun médicament trouvé.", en: "No medicine found." },
    "modeCodeBarre": { fr: "Code-barres", en: "Barcode" },
    "modeNomDosage": { fr: "Nom + dosage", en: "Name + dosage" },
    "champNomDosageMedicament": { fr: "Nom + dosage du médicament", en: "Medicine name + dosage" },
    "champForme": { fr: "Forme (facultatif)", en: "Form (optional)" },
    "champFabricant": { fr: "Fabricant (facultatif)", en: "Manufacturer (optional)" },
    "testerEmailTitre": { fr: "Test de l'alerte email", en: "Email alert test" },
    "testerEmailDescription": {
        fr: "Normalement envoyée automatiquement chaque jour à 8h aux gestionnaires si un médicament est en stock critique. Ce bouton déclenche la même vérification immédiatement, pratique pour tester sans attendre l'horaire réel.",
        en: "Normally sent automatically every day at 8am to managers if a medicine is in critical stock. This button triggers the same check immediately, handy for testing without waiting for the real schedule."
    },
    "btnTesterEmail": { fr: "Tester l'envoi maintenant", en: "Test sending now" },
    "stockPharmacienSousTitre": { fr: "Consultation du stock restant · API REST", en: "Remaining stock lookup · REST API" },
    "stockPharmacienTitrePage": { fr: "Stock", en: "Stock" },
    "stockPharmacienDescriptionPage": { fr: "Consultation du stock restant d'un médicament, en lecture seule.", en: "Read-only lookup of a medicine's remaining stock." }
};


/* ---------- Messages dynamiques (app.js) ---------- */

Object.assign(TRADUCTIONS, {
    "apiConnectee": { fr: "API connectée", en: "API connected" },
    "apiInjoignable": { fr: "API injoignable", en: "API unreachable" },
    "sessionNonAuthentifiee": { fr: "Session non authentifiée", en: "Not authenticated" },
    "serveurInjoignable": { fr: "Serveur injoignable", en: "Server unreachable" },

    "aucunCompteEnregistre": { fr: "Aucun compte utilisateur enregistré.", en: "No user account recorded." },
    "aucunLotPerimeARetirer": { fr: "Aucun lot périmé à retirer.", en: "No expired batch to remove." },
    "aucunMedicamentStockCritique": { fr: "Aucun médicament en stock critique.", en: "No medicine with critical stock." },
    "aucunMedicamentEnregistre": { fr: "Aucun médicament enregistré pour l'instant.", en: "No medicine recorded yet." },
    "aucuneDatePeremptionConnue": { fr: "Aucune date de péremption connue pour le stock restant.", en: "No known expiration date for the remaining stock." },
    "clientNexistePas": { fr: "Ce client n'existe pas.", en: "This customer doesn't exist." },
    "creerLUtilisateur": { fr: "Créer l'utilisateur", en: "Create user" },
    "enregistrerLesModifications": { fr: "Enregistrer les modifications", en: "Save changes" },

    "erreurAjoutMedicament": { fr: "Erreur lors de l'ajout du médicament.", en: "Error adding the medicine." },
    "erreurAnnulationVente": { fr: "Erreur lors de l'annulation de la vente.", en: "Error cancelling the sale." },
    "erreurEnregistrement": { fr: "Erreur lors de l'enregistrement.", en: "Error saving." },
    "erreurCreationFournisseur": { fr: "Erreur lors de la création du fournisseur.", en: "Error creating the supplier." },
    "erreurCreation": { fr: "Erreur lors de la création.", en: "Error creating." },
    "erreurRapportCnam": { fr: "Erreur lors de la génération du rapport CNAM.", en: "Error generating the CNAM report." },
    "erreurRapport": { fr: "Erreur lors de la génération du rapport.", en: "Error generating the report." },
    "erreurMajStock": { fr: "Erreur lors de la mise à jour du stock.", en: "Error updating stock." },
    "erreurMaj": { fr: "Erreur lors de la mise à jour.", en: "Error updating." },
    "erreurRechercheVentes": { fr: "Erreur lors de la recherche des ventes.", en: "Error searching sales." },
    "erreurSuppression": { fr: "Erreur lors de la suppression.", en: "Error deleting." },
    "erreurChangementMotDePasse": { fr: "Erreur lors du changement de mot de passe.", en: "Error changing password." },
    "erreurRetraitStockPerime": { fr: "Erreur lors du retrait du stock périmé.", en: "Error removing expired stock." },

    "impossibleChargerHistorique": { fr: "Impossible de charger l'historique.", en: "Unable to load history." },
    "impossibleChargerComptes": { fr: "Impossible de charger la liste des comptes.", en: "Unable to load account list." },
    "impossibleChargerFournisseurs": { fr: "Impossible de charger la liste des fournisseurs.", en: "Unable to load supplier list." },
    "impossibleChargerMedicaments": { fr: "Impossible de charger la liste des médicaments.", en: "Unable to load medicine list." },
    "indisponiblePourRole": { fr: "Indisponible pour votre rôle", en: "Unavailable for your role" },
    "laisserVideNePasChanger": { fr: "Laisser vide pour ne pas changer", en: "Leave empty to keep unchanged" },
    "motsDePasseNeCorrespondentPas": { fr: "Le nouveau mot de passe et sa confirmation ne correspondent pas.", en: "The new password and its confirmation don't match." },
    "motDePasseChamp": { fr: "Mot de passe", en: "Password" },
    "medicamentAjouteAvecSucces": { fr: "Médicament ajouté avec succès.", en: "Medicine added successfully." },
    "medicamentIntrouvable": { fr: "Médicament introuvable.", en: "Medicine not found." },
    "nouveauMotDePasseChamp": { fr: "Nouveau mot de passe", en: "New password" },
    "statistiquesIndisponiblesRole": { fr: "Statistiques indisponibles pour votre rôle.", en: "Statistics unavailable for your role." },
    "stockCritiqueLabel": { fr: "Stock critique", en: "Critical stock" },
    "stockMisAJourAvecSucces": { fr: "Stock mis à jour avec succès.", en: "Stock updated successfully." },
    "stockNormalLabel": { fr: "Stock normal", en: "Normal stock" },
    "unitesVenduesLabel": { fr: "Unités vendues", en: "Units sold" },
    "utilisateurSupprimeAvecSucces": { fr: "Utilisateur supprimé avec succès.", en: "User deleted successfully." },
    "venteAnnuleeAvecSucces": { fr: "Vente annulée avec succès.", en: "Sale cancelled successfully." },
    "bientotParenthese": { fr: "(bientôt)", en: "(soon)" },
    "perimeParenthese": { fr: "(périmé)", en: "(expired)" },
    "idParenthese": { fr: " (ID : ", en: " (ID: " },
    "utiliseRetirerStockPerime": {
        fr: "Utilise \"Retirer le stock périmé\" pour le retirer.",
        en: "Use \"Remove expired stock\" to remove it."
    },
    "placeholderMotDePasseRegles": { fr: "8 caractères min., lettres + chiffres", en: "8 chars min., letters + numbers" },
    "unitesPerimeesSurTotal": { fr: "unités périmées", en: "expired units" },
    "lotPerimeAncien": { fr: "Lot périmé le plus ancien : ", en: "Oldest expired batch: " },
    "bientotPerimeMoinsDe30": { fr: "Bientôt périmé : ", en: "Expiring soon: " },
    "dansMoinsDe30Jours": { fr: " (dans moins de 30 jours).", en: " (in less than 30 days)." },
    "prochainePeremption": { fr: "Prochaine péremption : ", en: "Next expiration: " },
    "stockPerimeRetire": { fr: "Stock périmé retiré : ", en: "Expired stock removed: " },
    "uniteSingulier": { fr: " unité(s).", en: " unit(s)." },
    "ceMedicamentNestPasEncorePerime": { fr: "Ce médicament n'est pas encore périmé.", en: "This medicine isn't expired yet." },
    "oui": { fr: "Oui", en: "Yes" },
    "non": { fr: "Non", en: "No" },
    "pharmacienLabel": { fr: "Pharmacien", en: "Pharmacist" },
    "gestionnaireLabel": { fr: "Gestionnaire", en: "Manager" },
    "scannerDabordUnMedicament": {
        fr: "Scanne d'abord un médicament avant d'enregistrer la vente.",
        en: "Scan a medicine first before recording the sale."
    },
    "medicamentIntrouvablePourCeCode": {
        fr: "Médicament introuvable pour ce code-barres.",
        en: "No medicine found for this barcode."
    }
});


let langueActuelle =
    localStorage.getItem("langue") || "fr";


function traduire(cle) {

    const entree = TRADUCTIONS[cle];

    if (!entree) {
        return cle;
    }

    return entree[langueActuelle] || entree.fr;
}


function appliquerTraduction() {

    document.documentElement.setAttribute("lang", langueActuelle);

    document.querySelectorAll("[data-i18n]").forEach((element) => {

        const cle = element.getAttribute("data-i18n");
        const texte = traduire(cle);

        /*
         * Ne modifie que le premier nœud de texte direct de l'élément,
         * sans jamais toucher à ses éléments enfants (ex: un <input>
         * placé à l'intérieur d'un <label data-i18n="...">).
         * Écraser tout le textContent détruirait ces enfants.
         */
        let noeudTexte = null;

        for (const enfant of element.childNodes) {

            if (enfant.nodeType === Node.TEXT_NODE) {
                noeudTexte = enfant;
                break;
            }
        }

        if (noeudTexte) {
            noeudTexte.textContent = texte;
        } else {
            element.textContent = texte;
        }
    });

    document.querySelectorAll("[data-i18n-placeholder]").forEach((element) => {

        const cle = element.getAttribute("data-i18n-placeholder");
        element.setAttribute("placeholder", traduire(cle));
    });

    document.querySelectorAll("[data-i18n-title]").forEach((element) => {

        const cle = element.getAttribute("data-i18n-title");
        element.setAttribute("title", traduire(cle));
    });

    document.querySelectorAll("[data-i18n-tooltip]").forEach((element) => {

        const cle = element.getAttribute("data-i18n-tooltip");
        element.setAttribute("data-tooltip", traduire(cle));
    });
}


const btnToggleLangue =
    document.getElementById("btnToggleLangue");

if (btnToggleLangue) {

    btnToggleLangue.textContent =
        langueActuelle === "fr" ? "EN" : "FR";

    btnToggleLangue.addEventListener("click", () => {

        langueActuelle =
            langueActuelle === "fr" ? "en" : "fr";

        localStorage.setItem("langue", langueActuelle);

        btnToggleLangue.textContent =
            langueActuelle === "fr" ? "EN" : "FR";

        appliquerTraduction();

        if (typeof rafraichirTraductionDynamique === "function") {
            rafraichirTraductionDynamique();
        }
    });
}


document.addEventListener("DOMContentLoaded", appliquerTraduction);