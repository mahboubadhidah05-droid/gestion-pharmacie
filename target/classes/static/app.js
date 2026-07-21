/* ============================================================
   Gestion Pharmacie — appels à l'API REST
   ============================================================ */

/* Repli si ui.js n'a pas pu être chargé (404, etc.) :
   les actions continuent de fonctionner sans spinner ni toast. */
if (typeof definirChargement !== "function") {

    window.definirChargement = function (bouton, chargement) {

        if (bouton) {
            bouton.disabled = chargement;
        }
    };
}

if (typeof notifier !== "function") {

    window.notifier = function (message) {
        console.log(message);
    };
}

const API = "/api/medicaments";
const API_VENTES = "/api/ventes";
const API_CLIENTS = "/api/clients";


/* ============================================================
   Journal des appels API
   ============================================================ */

const journal = document.getElementById("journal");

function loguer(methode, url, statut, corps) {

    if (!journal) {
        return;
    }

    const vide = journal.querySelector(".journal-vide");

    if (vide) {
        vide.remove();
    }

    const heure = new Date().toLocaleTimeString("fr-FR");

    const classeStatut =
        statut >= 200 && statut < 300
            ? "statut-ok"
            : "statut-erreur";

    const ligne = document.createElement("p");

    ligne.className = "journal-ligne";

    ligne.innerHTML =
        `[${heure}] ` +
        `<span class="methode">${methode}</span> ` +
        `${url} ` +
        `<span class="${classeStatut}">→ ${statut}</span> ` +
        `${corps}`;

    journal.prepend(ligne);
}


/* ============================================================
   Appel générique à l'API
   ============================================================ */

async function appelerApi(methode, url, donnees) {

    const options = {
        method: methode
    };

    if (donnees) {

        options.headers = {
            "Content-Type": "application/json"
        };

        options.body = JSON.stringify(donnees);
    }

    try {

        const reponse = await fetch(url, options);

        if (reponse.status === 401) {

            window.location.href = "login.html";

            return {
                ok: false,
                statut: 401,
                donnees: null
            };
        }

        const texte = await reponse.text();

        loguer(
            methode,
            url,
            reponse.status,
            texte
        );

        let donneesRetour = null;

        if (texte) {
            donneesRetour = JSON.parse(texte);
        }

        return {
            ok: reponse.ok,
            statut: reponse.status,
            donnees: donneesRetour
        };

    } catch (erreur) {

        console.error("Erreur API :", erreur);

        loguer(
            methode,
            url,
            0,
            "Serveur injoignable"
        );

        return {
            ok: false,
            statut: 0,
            donnees: null
        };
    }
}


/* ============================================================
   Vérification de l'API
   ============================================================ */

(async function verifierApi() {

    const etat = document.getElementById("etatApi");
    const texte = document.getElementById("etatApiTexte");

    try {

        const reponse = await fetch("/api/auth/me");

        if (reponse.ok) {

            etat.classList.add("ok");

            texte.textContent =
                "API connectée";

        } else {

            etat.classList.add("erreur");

            texte.textContent =
                "Session non authentifiée";
        }

    } catch {

        etat.classList.add("erreur");

        texte.textContent =
            "API injoignable";
    }

})();


/* ============================================================
   AJOUTER UN MÉDICAMENT
   ============================================================ */

const formAjout =
    document.getElementById("formAjout");

if (formAjout) {

    formAjout.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f =
                new FormData(e.target);

            const resultat =
                await appelerApi(
                    "POST",
                    API,
                    {
                        nom: f.get("nom"),
                        dosage: f.get("dosage"),
                        stock: Number(f.get("stock")),
                        prix: Number(f.get("prix")),
                        seuil: Number(f.get("seuil"))
                    }
                );

            definirChargement(bouton, false);

            if (resultat.ok) {

                e.target.reset();

                notifier(
                    "Médicament ajouté avec succès."
                );

            } else {

                notifier(
                    "Erreur lors de l'ajout du médicament.",
                    "erreur"
                );
            }
        }
    );
}


/* ============================================================
   CONSULTER UN STOCK
   ============================================================ */

const formConsulter =
    document.getElementById(
        "formConsulter"
    );

if (formConsulter) {

    formConsulter.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f =
                new FormData(e.target);

            const nom = f.get("nom");
            const dosage = f.get("dosage");

            const resultat =
                await appelerApi(
                    "GET",
                    `${API}/stock?nom=${encodeURIComponent(nom)}`
                    + `&dosage=${encodeURIComponent(dosage)}`
                );

            definirChargement(bouton, false);

            const bloc =
                document.getElementById(
                    "resultatStock"
                );

            const valeur =
                document.getElementById(
                    "stockValeur"
                );

            if (resultat.ok) {

                bloc.hidden = false;

                valeur.textContent =
                    resultat.donnees.stock;

            } else {

                bloc.hidden = false;

                valeur.textContent =
                    "introuvable";
            }
        }
    );
}


/* ============================================================
   METTRE À JOUR UN STOCK
   ============================================================ */

const formMaj =
    document.getElementById("formMaj");

if (formMaj) {

    formMaj.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f =
                new FormData(e.target);

            const nom = f.get("nom");
            const dosage = f.get("dosage");

            const resultat =
                await appelerApi(
                    "PUT",
                    `${API}/stock?nom=${encodeURIComponent(nom)}`
                    + `&dosage=${encodeURIComponent(dosage)}`,
                    {
                        quantite:
                            Number(f.get("stock"))
                    }
                );

            definirChargement(bouton, false);

            if (resultat.ok) {

                e.target.reset();

                notifier(
                    "Stock mis à jour avec succès."
                );

            } else {

                notifier(
                    "Erreur lors de la mise à jour du stock.",
                    "erreur"
                );
            }
        }
    );
}


/* ============================================================
   VÉRIFIER LE SEUIL CRITIQUE
   ============================================================ */

const formCritique =
    document.getElementById(
        "formCritique"
    );

if (formCritique) {

    formCritique.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f =
                new FormData(e.target);

            const nom = f.get("nom");
            const dosage = f.get("dosage");

            const resultat =
                await appelerApi(
                    "GET",
                    `${API}/stock-critique?nom=${encodeURIComponent(nom)}`
                    + `&dosage=${encodeURIComponent(dosage)}`
                );

            definirChargement(bouton, false);

            const alerte =
                document.getElementById(
                    "alerteCritique"
                );

            alerte.hidden = false;

            if (
                resultat.ok &&
                resultat.donnees.critique
            ) {

                alerte.textContent =
                    resultat.donnees.message;

                alerte.classList.remove(
                    "ok"
                );

            } else if (resultat.ok) {

                alerte.textContent =
                    "Stock au-dessus du seuil critique.";

                alerte.classList.add(
                    "ok"
                );

            } else {

                alerte.textContent =
                    "Vérification impossible.";

                alerte.classList.remove(
                    "ok"
                );
            }
        }
    );
}


/* ============================================================
   LISTE DES MÉDICAMENTS
   ============================================================ */

async function chargerMedicaments() {

    const bouton =
        document.getElementById(
            "btnListeMedicaments"
        );

    definirChargement(bouton, true);

    const resultat =
        await appelerApi(
            "GET",
            API
        );

    definirChargement(bouton, false);

    if (!resultat.ok) {

        notifier(
            "Impossible de charger la liste des médicaments.",
            "erreur"
        );

        return;
    }

    const table =
        document.getElementById(
            "tableMedicaments"
        );

    const corps =
        document.getElementById(
            "corpsTableMedicaments"
        );

    corps.innerHTML = "";

    resultat.donnees.forEach(
        (med) => {

            const ligne =
                document.createElement(
                    "tr"
                );

            if (
                med.stock <=
                med.seuilCritique
            ) {

                ligne.classList.add(
                    "ligne-critique"
                );
            }

            [
                med.id,
                med.nom,
                med.dosage,
                med.stock,
                Number(med.prix)
                    .toFixed(2),
                med.seuilCritique
            ].forEach(
                (valeur) => {

                    const cellule =
                        document.createElement(
                            "td"
                        );

                    cellule.textContent =
                        valeur;

                    ligne.appendChild(
                        cellule
                    );
                }
            );

            corps.appendChild(
                ligne
            );
        }
    );

    table.hidden = false;
}


const btnListeMedicaments =
    document.getElementById(
        "btnListeMedicaments"
    );

if (btnListeMedicaments) {

    btnListeMedicaments.addEventListener(
        "click",
        chargerMedicaments
    );
}


/* ============================================================
   ENREGISTRER UNE VENTE
   ============================================================ */

const formVente =
    document.getElementById(
        "formVente"
    );

if (formVente) {

    formVente.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f =
                new FormData(e.target);

            const resultat =
                await appelerApi(
                    "POST",
                    API_VENTES,
                    {
                        idPharmacien:
                            Number(
                                f.get(
                                    "idPharmacien"
                                )
                            ),

                        idClient:
                            Number(
                                f.get(
                                    "idClient"
                                )
                            ),

                        idMedicament:
                            Number(
                                f.get(
                                    "idMedicament"
                                )
                            ),

                        quantite:
                            Number(
                                f.get(
                                    "quantite"
                                )
                            )
                    }
                );

            definirChargement(bouton, false);

            const alerte =
                document.getElementById(
                    "alerteVente"
                );

            alerte.hidden = false;

            if (resultat.ok) {

                alerte.textContent =
                    resultat.donnees.message;

                alerte.classList.add(
                    "ok"
                );

                e.target.reset();

            } else if (
                resultat.statut === 409
            ) {

                alerte.textContent =
                    resultat.donnees.message;

                alerte.classList.remove(
                    "ok"
                );

            } else {

                alerte.textContent =
                    "Erreur lors de l'enregistrement.";

                alerte.classList.remove(
                    "ok"
                );
            }
        }
    );
}


/* ============================================================
   FILTRE DES VENTES
   ============================================================ */

const filtreVentes =
    document.getElementById(
        "filtreVentes"
    );

if (filtreVentes) {

    filtreVentes.addEventListener(
        "change",
        (e) => {

            const valeur = e.target.value;

            document.getElementById(
                "champValeur"
            ).hidden = valeur !== "medicament";

            document.getElementById(
                "champsClient"
            ).hidden = valeur !== "client";

            document.getElementById(
                "champsPeriode"
            ).hidden = valeur !== "periode";
        }
    );
}


/* ============================================================
   CONSULTER LES VENTES
   ============================================================ */

const formConsulterVentes =
    document.getElementById(
        "formConsulterVentes"
    );

if (formConsulterVentes) {

    formConsulterVentes.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");

            const f =
                new FormData(e.target);

            const filtre =
                f.get("filtre");

            let url;

            if (
                filtre ===
                "periode"
            ) {

                const debut =
                    f.get("debut");

                const fin =
                    f.get("fin");

                if (
                    !debut ||
                    !fin
                ) {

                    return;
                }

                url =
                    `${API_VENTES}?debut=${debut}&fin=${fin}`;

            } else if (
                filtre ===
                "client"
            ) {

                const clientNom =
                    f.get("clientNom");

                const clientPrenom =
                    f.get("clientPrenom");

                if (
                    !clientNom ||
                    !clientPrenom
                ) {

                    return;
                }

                url =
                    `${API_VENTES}?clientNom=${encodeURIComponent(clientNom)}`
                    + `&clientPrenom=${encodeURIComponent(clientPrenom)}`;

            } else {

                const valeur =
                    f.get("valeur");

                if (!valeur) {
                    return;
                }

                url =
                    `${API_VENTES}?${filtre}=${encodeURIComponent(valeur)}`;
            }

            definirChargement(bouton, true);

            const resultat =
                await appelerApi(
                    "GET",
                    url
                );

            definirChargement(bouton, false);

            if (!resultat.ok) {

                notifier(
                    "Erreur lors de la recherche des ventes.",
                    "erreur"
                );

                return;
            }

            const table =
                document.getElementById(
                    "tableVentes"
                );

            const corps =
                document.getElementById(
                    "corpsTableVentes"
                );

            const aucune =
                document.getElementById(
                    "aucuneVente"
                );

            corps.innerHTML = "";

            if (
                resultat.donnees.length ===
                0
            ) {

                table.hidden = true;

                aucune.hidden = false;

                return;
            }

            aucune.hidden = true;

            resultat.donnees.forEach(
                (vente) => {

                    const ligne =
                        document.createElement(
                            "tr"
                        );

                    [
                        vente.id,
                        vente.idPharmacien,
                        vente.idClient,
                        vente.idMedicament,
                        vente.quantite,
                        vente.dateVente
                            .replace(
                                "T",
                                " "
                            )
                    ].forEach(
                        (valeur) => {

                            const cellule =
                                document.createElement(
                                    "td"
                                );

                            cellule.textContent =
                                valeur;

                            ligne.appendChild(
                                cellule
                            );
                        }
                    );

                    const celluleActions =
                        document.createElement("td");

                    const btnAnnuler =
                        document.createElement("button");

                    btnAnnuler.type = "button";
                    btnAnnuler.textContent = "Annuler";
                    btnAnnuler.className = "bouton-danger-discret";

                    btnAnnuler.addEventListener(
                        "click",
                        async () => {

                            definirChargement(btnAnnuler, true);

                            const resultatAnnulation =
                                await appelerApi(
                                    "DELETE",
                                    `${API_VENTES}/${vente.id}`
                                );

                            definirChargement(btnAnnuler, false);

                            if (resultatAnnulation.ok) {

                                ligne.remove();

                                notifier(
                                    "Vente annulée avec succès."
                                );

                            } else {

                                notifier(
                                    "Erreur lors de l'annulation de la vente.",
                                    "erreur"
                                );
                            }
                        }
                    );

                    celluleActions.appendChild(btnAnnuler);
                    ligne.appendChild(celluleActions);

                    corps.appendChild(
                        ligne
                    );
                }
            );

            table.hidden = false;
        }
    );
}


/* ============================================================
   CRÉER UN CLIENT
   ============================================================ */

const formClient =
    document.getElementById(
        "formClient"
    );

if (formClient) {

    formClient.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f =
                new FormData(e.target);

            const resultat =
                await appelerApi(
                    "POST",
                    API_CLIENTS,
                    {
                        nom:
                            f.get("nom"),

                        prenom:
                            f.get("prenom"),

                        email:
                            f.get("email"),

                        adresse:
                            f.get("adresse")
                    }
                );

            definirChargement(bouton, false);

            const alerte =
                document.getElementById(
                    "alerteClient"
                );

            alerte.hidden = false;

            if (resultat.ok) {

                alerte.textContent =
                    resultat.donnees.message
                    + " (ID : "
                    + resultat.donnees.id
                    + ")";

                alerte.classList.add(
                    "ok"
                );

                e.target.reset();

            } else {

                alerte.textContent =
                    "Erreur lors de la création.";

                alerte.classList.remove(
                    "ok"
                );
            }
        }
    );
}


/* ============================================================
   VÉRIFICATION D'UN CLIENT
   ============================================================ */

const champClient =
    document.querySelector(
        "#formVente [name='idClient']"
    );

if (champClient) {

    champClient.addEventListener(
        "blur",
        async (e) => {

            const id =
                e.target.value;

            if (!id) {
                return;
            }

            const resultat =
                await appelerApi(
                    "GET",
                    `${API_CLIENTS}/${id}/existe`
                );

            const alerte =
                document.getElementById(
                    "alerteVente"
                );

            if (
                resultat.ok &&
                !resultat.donnees.existe
            ) {

                alerte.hidden = false;

                alerte.textContent =
                    "Ce client n'existe pas.";

                alerte.classList.remove(
                    "ok"
                );

            } else if (
                resultat.ok
            ) {

                alerte.hidden = true;
            }
        }
    );
}


/* ============================================================
   CRÉER UNE COMMANDE
   ============================================================ */

const formCommande =
    document.getElementById(
        "formCommande"
    );

if (formCommande) {

    formCommande.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f =
                new FormData(e.target);

            const resultat =
                await appelerApi(
                    "POST",
                    "/api/commandes",
                    {
                        idGestionnaire:
                            Number(
                                f.get(
                                    "idGestionnaire"
                                )
                            ),

                        idMedicament:
                            Number(
                                f.get(
                                    "idMedicament"
                                )
                            ),

                        quantite:
                            Number(
                                f.get(
                                    "quantite"
                                )
                            )
                    }
                );

            definirChargement(bouton, false);

            const alerte =
                document.getElementById(
                    "alerteCommande"
                );

            alerte.hidden = false;

            if (resultat.ok) {

                alerte.textContent =
                    resultat.donnees.message;

                alerte.classList.add(
                    "ok"
                );

                e.target.reset();

                chargerMedicaments();

            } else {

                alerte.textContent =
                    "Erreur lors de la création.";

                alerte.classList.remove(
                    "ok"
                );
            }
        }
    );
}