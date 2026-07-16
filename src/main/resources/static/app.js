/* ============================================================
   Gestion Pharmacie — appels à l'API REST
   Le HTML est servi par Spring Boot (dossier static/), donc
   même origine que l'API : pas de problème de CORS.
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
        // Un simple appel : un 200 ou un 404 prouve que le serveur répond.
        const reponse = await fetch(`${API}/1/stock`);
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
        stock: Number(f.get("stock"))
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
