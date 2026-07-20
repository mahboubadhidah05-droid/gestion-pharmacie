/* ============================================================
   Gestion Pharmacie — appels à l'API REST
   Le HTML est servi par Spring Boot (dossier static/), donc
   même origine que l'API : pas de problème de CORS.
   L'authentification est gérée par auth.js (chargé avant).
   ============================================================ */

const API = "/api/medicaments";


/* ---------- Journal des appels ---------- */

const journal = document.getElementById("journal");

function loguer(methode, url, statut, corps) {
    const vide = journal.querySelector(".journal-vide");
    if (vide) vide.remove();

    const heure = new Date().toLocaleTimeString("fr-FR");
    const classeStatut = statut >= 200 && statut < 300 ? "statut-ok" : "statut-erreur";

    const ligne = document.createElement("p");
    ligne.className = "journal-ligne";
    ligne.innerHTML =
        `[${heure}] <span class="methode">${methode}</span> ${url} ` +
        `<span class="${classeStatut}">→ ${statut}</span> ${corps}`;

    journal.prepend(ligne);
}


/* ---------- Appel générique ---------- */

async function appelerApi(methode, url, donnees) {
    const options = { method: methode };

    if (donnees) {
        options.headers = { "Content-Type": "application/json" };
        options.body = JSON.stringify(donnees);
    }

    try {
        const reponse = await fetch(url, options);

        /* Session expirée : retour à la page de connexion. */
        if (reponse.status === 401) {
            window.location.href = "login.html";
            return { ok: false, statut: 401, donnees: null };
        }

        const texte = await reponse.text();

        loguer(methode, url, reponse.status, texte);

        return {
            ok: reponse.ok,
            statut: reponse.status,
            donnees: texte ? JSON.parse(texte) : null
        };

    } catch (erreur) {
        loguer(methode, url, 0, "Serveur injoignable");
        return { ok: false, statut: 0, donnees: null };
    }
}


/* ---------- État de l'API au chargement ---------- */

(async function verifierApi() {
    const etat = document.getElementById("etatApi");
    const texte = document.getElementById("etatApiTexte");

    try {
        // Un simple appel : toute réponse HTTP prouve que le serveur répond.
        await fetch("/api/auth/me");
        etat.classList.add("ok");
        texte.textContent = "API connectée";
    } catch {
        etat.classList.add("erreur");
        texte.textContent = "API injoignable";
    }
})();


/* ---------- Formulaire : ajouter ---------- */

document.getElementById("formAjout").addEventListener("submit", async (e) => {
    e.preventDefault();
    const f = new FormData(e.target);

    const resultat = await appelerApi("POST", API, {
        nom: f.get("nom"),
        dosage: f.get("dosage"),
        stock: Number(f.get("stock")),
        prix: Number(f.get("prix")),
        seuil: Number(f.get("seuil"))
    });

    if (resultat.ok) e.target.reset();
});


/* ---------- Formulaire : consulter le stock ---------- */

document.getElementById("formConsulter").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = new FormData(e.target).get("id");

    const resultat = await appelerApi("GET", `${API}/${id}/stock`);

    const bloc = document.getElementById("resultatStock");
    const valeur = document.getElementById("stockValeur");

    bloc.hidden = false;
    valeur.textContent = resultat.ok ? resultat.donnees.stock : "introuvable";
});


/* ---------- Formulaire : mettre à jour ---------- */

document.getElementById("formMaj").addEventListener("submit", async (e) => {
    e.preventDefault();
    const f = new FormData(e.target);
    const id = f.get("id");

    const resultat = await appelerApi("PUT", `${API}/${id}/stock`, {
        quantite: Number(f.get("stock"))
    });

    if (resultat.ok) e.target.reset();
});


/* ---------- Formulaire : seuil critique ---------- */

document.getElementById("formCritique").addEventListener("submit", async (e) => {
    e.preventDefault();
    const id = new FormData(e.target).get("id");

    const resultat = await appelerApi("GET", `${API}/${id}/stock-critique`);

    const alerte = document.getElementById("alerteCritique");
    alerte.hidden = false;

    if (resultat.ok && resultat.donnees.critique) {
        alerte.textContent = resultat.donnees.message;
        alerte.classList.remove("ok");
    } else if (resultat.ok) {
        alerte.textContent = "Stock au-dessus du seuil critique.";
        alerte.classList.add("ok");
    } else {
        alerte.textContent = "Vérification impossible (voir le journal).";
        alerte.classList.remove("ok");
    }
});
/* ---------- Liste des médicaments ---------- */

async function chargerMedicaments() {
    const resultat = await appelerApi("GET", API);

    if (!resultat.ok) return;

    const table = document.getElementById("tableMedicaments");
    const corps = document.getElementById("corpsTableMedicaments");

    corps.innerHTML = "";

    resultat.donnees.forEach((med) => {
        const ligne = document.createElement("tr");

        /* Ligne mise en évidence si le stock est au niveau critique */
        if (med.stock <= med.seuilCritique) {
            ligne.classList.add("ligne-critique");
        }

        [med.id, med.nom, med.dosage, med.stock,
         med.prix.toFixed(2), med.seuilCritique]
            .forEach((valeur) => {
                const cellule = document.createElement("td");
                cellule.textContent = valeur;
                ligne.appendChild(cellule);
            });

        corps.appendChild(ligne);
    });

    table.hidden = false;
}

document.getElementById("btnListeMedicaments")
    .addEventListener("click", chargerMedicaments);
	/* ---------- Ventes (pharmacien) ---------- */

	const API_VENTES = "/api/ventes";


	/* Enregistrer une vente */

	document.getElementById("formVente").addEventListener("submit", async (e) => {
	    e.preventDefault();
	    const f = new FormData(e.target);

	    const resultat = await appelerApi("POST", API_VENTES, {
	        idPharmacien: Number(f.get("idPharmacien")),
	        idClient: Number(f.get("idClient")),
	        idMedicament: Number(f.get("idMedicament")),
	        quantite: Number(f.get("quantite"))
	    });

	    const alerte = document.getElementById("alerteVente");
	    alerte.hidden = false;

	    if (resultat.ok) {
	        alerte.textContent = resultat.donnees.message;
	        alerte.classList.add("ok");
	        e.target.reset();
	    } else if (resultat.statut === 409) {
	        alerte.textContent = resultat.donnees.message;
	        alerte.classList.remove("ok");
	    } else {
	        alerte.textContent = "Erreur lors de l'enregistrement (voir le journal).";
	        alerte.classList.remove("ok");
	    }
	});


	/* Annuler une vente */

	document.getElementById("formAnnulation").addEventListener("submit", async (e) => {
	    e.preventDefault();
	    const id = new FormData(e.target).get("id");

	    const resultat = await appelerApi("DELETE", `${API_VENTES}/${id}`);

	    const alerte = document.getElementById("alerteAnnulation");
	    alerte.hidden = false;

	    if (resultat.ok) {
	        alerte.textContent = resultat.donnees.message;
	        alerte.classList.add("ok");
	        e.target.reset();
	    } else if (resultat.statut === 404) {
	        alerte.textContent = "Aucune vente trouvée avec cet ID.";
	        alerte.classList.remove("ok");
	    } else {
	        alerte.textContent = "Erreur lors de l'annulation (voir le journal).";
	        alerte.classList.remove("ok");
	    }
	});


	/* Consulter les ventes : bascule Valeur / Période selon le filtre */

	document.getElementById("filtreVentes").addEventListener("change", (e) => {
	    const periode = e.target.value === "periode";

	    document.getElementById("champValeur").hidden = periode;
	    document.getElementById("champsPeriode").hidden = !periode;
	});


	/* Consulter les ventes : recherche et affichage */

	document.getElementById("formConsulterVentes").addEventListener("submit", async (e) => {
	    e.preventDefault();
	    const f = new FormData(e.target);
	    const filtre = f.get("filtre");

	    let url;

	    if (filtre === "periode") {
	        const debut = f.get("debut");
	        const fin = f.get("fin");
	        if (!debut || !fin) return;
	        url = `${API_VENTES}?debut=${debut}&fin=${fin}`;
	    } else {
	        const valeur = f.get("valeur");
	        if (!valeur) return;
	        url = `${API_VENTES}?${filtre}=${valeur}`;
	    }

	    const resultat = await appelerApi("GET", url);

	    if (!resultat.ok) return;

	    const table = document.getElementById("tableVentes");
	    const corps = document.getElementById("corpsTableVentes");
	    const aucune = document.getElementById("aucuneVente");

	    corps.innerHTML = "";

	    if (resultat.donnees.length === 0) {
	        table.hidden = true;
	        aucune.hidden = false;
	        return;
	    }

	    aucune.hidden = true;

	    resultat.donnees.forEach((vente) => {
	        const ligne = document.createElement("tr");

	        [vente.id, vente.idPharmacien, vente.idClient,
	         vente.idMedicament, vente.quantite,
	         vente.dateVente.replace("T", " ")]
	            .forEach((valeur) => {
	                const cellule = document.createElement("td");
	                cellule.textContent = valeur;
	                ligne.appendChild(cellule);
	            });

	        corps.appendChild(ligne);
	    });

	    table.hidden = false;
	});
	/* ---------- Clients (pharmacien) ---------- */

	const API_CLIENTS = "/api/clients";


	/* Créer un client */

	document.getElementById("formClient").addEventListener("submit", async (e) => {
	    e.preventDefault();
	    const f = new FormData(e.target);

	    const resultat = await appelerApi("POST", API_CLIENTS, {
	        nom: f.get("nom"),
	        prenom: f.get("prenom"),
	        email: f.get("email"),
	        adresse: f.get("adresse")
	    });

	    const alerte = document.getElementById("alerteClient");
	    alerte.hidden = false;

	    if (resultat.ok) {
	        alerte.textContent =
	            resultat.donnees.message + " (ID : " + resultat.donnees.id + ")";
	        alerte.classList.add("ok");
	        e.target.reset();
	    } else {
	        alerte.textContent = "Erreur lors de la création (voir le journal).";
	        alerte.classList.remove("ok");
	    }
	});


	/* Vérification du client à la saisie dans le formulaire de vente,
	   comme dans le menu CLI : on prévient si le client n'existe pas. */

	document.querySelector("#formVente [name='idClient']")
	    .addEventListener("blur", async (e) => {

	        const id = e.target.value;
	        if (!id) return;

	        const resultat = await appelerApi(
	            "GET",
	            `${API_CLIENTS}/${id}/existe`
	        );

	        const alerte = document.getElementById("alerteVente");

	        if (resultat.ok && !resultat.donnees.existe) {
	            alerte.hidden = false;
	            alerte.textContent =
	                "Ce client n'existe pas. Créez-le d'abord avec la carte « Créer un client ».";
	            alerte.classList.remove("ok");
	        } else if (resultat.ok) {
	            alerte.hidden = true;
	        }
	    });
		/* ---------- Commandes (gestionnaire) ---------- */

		document.getElementById("formCommande").addEventListener("submit", async (e) => {
		    e.preventDefault();
		    const f = new FormData(e.target);

		    const resultat = await appelerApi("POST", "/api/commandes", {
		        idGestionnaire: Number(f.get("idGestionnaire")),
		        idMedicament: Number(f.get("idMedicament")),
		        quantite: Number(f.get("quantite"))
		    });

		    const alerte = document.getElementById("alerteCommande");
		    alerte.hidden = false;

		    if (resultat.ok) {
		        alerte.textContent = resultat.donnees.message;
		        alerte.classList.add("ok");
		        e.target.reset();
		        chargerMedicaments();
		    } else if (resultat.statut === 404) {
		        alerte.textContent = resultat.donnees.message;
		        alerte.classList.remove("ok");
		    } else {
		        alerte.textContent = "Erreur lors de la création (voir le journal).";
		        alerte.classList.remove("ok");
		    }
		});