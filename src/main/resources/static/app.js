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
            traduire("serveurInjoignable")
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
                traduire("apiConnectee");

        } else {

            etat.classList.add("erreur");

            texte.textContent =
                traduire("sessionNonAuthentifiee");
        }

    } catch {

        etat.classList.add("erreur");

        texte.textContent =
            traduire("apiInjoignable");
    }

})();


/* ============================================================
   AJOUTER UN MÉDICAMENT
   ============================================================ */

const formAjout =
    document.getElementById("formAjout");

const checkConventionneCnam =
    document.getElementById("checkConventionneCnam");

if (checkConventionneCnam) {

    checkConventionneCnam.addEventListener(
        "change",
        (e) => {

            const label =
                document.getElementById("labelTauxRemboursement");

            label.hidden = !e.target.checked;

            if (!e.target.checked) {
                document.getElementById(
                    "inputTauxRemboursement"
                ).value = "";
            }
        }
    );
}

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
                        seuil: Number(f.get("seuil")),
                        datePeremption:
                            f.get("datePeremption") || null,
                        conventionneCnam:
                            f.get("conventionneCnam") === "on",
                        tauxRemboursement:
                            Number(f.get("tauxRemboursement")) || 0,
                        codeBarre:
                            f.get("codeBarre") || null
                    }
                );

            definirChargement(bouton, false);

            if (resultat.ok) {

                e.target.reset();

                notifier(
                    traduire("medicamentAjouteAvecSucces")
                );

            } else {

                notifier(
                    traduire("erreurAjoutMedicament"),
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

            const codeBarre = f.get("codeBarre");

            const resultat =
                await appelerApi(
                    "GET",
                    `${API}/stock?codeBarre=${encodeURIComponent(codeBarre)}`
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

            const codeBarre = f.get("codeBarre");

            const resultat =
                await appelerApi(
                    "PUT",
                    `${API}/stock?codeBarre=${encodeURIComponent(codeBarre)}`,
                    {
                        quantite:
                            Number(f.get("stock"))
                    }
                );

            definirChargement(bouton, false);

            if (resultat.ok) {

                e.target.reset();

                notifier(
                    traduire("stockMisAJourAvecSucces")
                );

            } else {

                notifier(
                    traduire("erreurMajStock"),
                    "erreur"
                );
            }
        }
    );
}


/* ============================================================
   VÉRIFIER LE SEUIL CRITIQUE
   ============================================================ */

const formVerifier =
    document.getElementById("formVerifier");

if (formVerifier) {

    formVerifier.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f = new FormData(e.target);
            const codeBarre = f.get("codeBarre");

            const resultat = await appelerApi(
                "GET",
                `${API}/verifier?codeBarre=${encodeURIComponent(codeBarre)}`
            );

            definirChargement(bouton, false);

            const alerte =
                document.getElementById("alerteVerifier");

            const bloc =
                document.getElementById("resultatVerifier");

            if (!resultat.ok) {

                bloc.hidden = true;
                alerte.hidden = false;
                alerte.textContent =
                    traduire("medicamentIntrouvable");
                alerte.classList.remove("ok");
                return;
            }

            alerte.hidden = true;
            bloc.hidden = false;

            const d = resultat.donnees;

            document.getElementById("verifierStock").textContent =
                d.stock;

            const carteCritique =
                document.getElementById("carteVerifierCritique");

            document.getElementById("verifierCritique").textContent =
                d.stockCritique ? traduire("oui") : traduire("non");

            carteCritique.classList.toggle(
                "kpi-alerte", d.stockCritique
            );

            const cartePerime =
                document.getElementById("carteVerifierPerime");

            document.getElementById("verifierPerime").textContent =
                `${d.quantitePerimee} / ${d.stock}`;

            cartePerime.classList.toggle(
                "kpi-alerte", d.quantitePerimee > 0
            );

            const infoDate =
                document.getElementById("verifierDateInfo");

            if (d.quantitePerimee > 0) {

                infoDate.textContent =
                    `${traduire("lotPerimeAncien")}${d.datePeremptionLaPlusProche}. `
                    + traduire("utiliseRetirerStockPerime");

            } else if (d.bientotPerime) {

                infoDate.textContent =
                    `${traduire("bientotPerimeMoinsDe30")}${d.datePeremptionLaPlusProche}${traduire("dansMoinsDe30Jours")}`;

            } else if (d.datePeremptionLaPlusProche) {

                infoDate.textContent =
                    `${traduire("prochainePeremption")}${d.datePeremptionLaPlusProche}.`;

            } else {

                infoDate.textContent =
                    traduire("aucuneDatePeremptionConnue");
            }
        }
    );
}



const formStockPerime =
    document.getElementById("formStockPerime");

if (formStockPerime) {

    formStockPerime.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f = new FormData(e.target);
            const codeBarre = f.get("codeBarre");

            const resultat = await appelerApi(
                "PUT",
                `${API}/stock-perime?codeBarre=${encodeURIComponent(codeBarre)}`
            );

            definirChargement(bouton, false);

            const alerte =
                document.getElementById("alerteStockPerime");

            alerte.hidden = false;

            if (resultat.ok) {

                alerte.textContent = resultat.donnees.message;
                alerte.classList.add("ok");
                e.target.reset();

            } else {

                alerte.textContent =
                    (resultat.donnees && resultat.donnees.message)
                        || traduire("erreurRetraitStockPerime");

                alerte.classList.remove("ok");
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
            traduire("impossibleChargerMedicaments"),
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

    if (resultat.donnees.length === 0) {

        table.hidden = true;

        let aucun = document.getElementById("aucunMedicament");

        if (!aucun) {

            aucun = document.createElement("p");
            aucun.id = "aucunMedicament";
            aucun.className = "alerte ok";
            aucun.textContent = traduire("aucunMedicamentEnregistre");
            table.insertAdjacentElement("afterend", aucun);
        }

        aucun.hidden = false;
        return;
    }

    const aucunMedExistant = document.getElementById("aucunMedicament");

    if (aucunMedExistant) {
        aucunMedExistant.hidden = true;
    }

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

            const celluleDate =
                document.createElement("td");

            if (med.quantitePerimee > 0) {

                celluleDate.className = "texte-perime";
                celluleDate.textContent =
                    `${med.quantitePerimee} / ${med.stock} ${traduire("unitesPerimees").toLowerCase()}`;

            } else if (med.datePeremption) {

                const aujourdhui =
                    new Date().toISOString().slice(0, 10);

                const dans30Jours =
                    new Date();

                dans30Jours.setDate(
                    dans30Jours.getDate() + 30
                );

                const limite =
                    dans30Jours.toISOString().slice(0, 10);

                celluleDate.textContent =
                    med.datePeremption;

                if (med.datePeremption <= limite) {

                    celluleDate.className =
                        "texte-bientot-perime";

                    celluleDate.textContent +=
                        traduire("bientotParenthese");
                }

            } else {

                celluleDate.textContent = "—";
            }

            ligne.appendChild(celluleDate);

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

            const hiddenId =
                document.getElementById("hiddenIdMedicamentVente");

            if (!hiddenId.value) {

                notifier(
                    traduire("scannerDabordUnMedicament"),
                    "erreur"
                );

                return;
            }

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

                document.getElementById(
                    "confirmationScanVente"
                ).textContent = "";

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
                    traduire("erreurEnregistrement");

                alerte.classList.remove(
                    "ok"
                );
            }
        }
    );
}


/* ============================================================
   SCAN CODE-BARRES — ENREGISTRER UNE VENTE
   ============================================================ */

const inputCodeBarreVente =
    document.getElementById("inputCodeBarreVente");

if (inputCodeBarreVente) {

    inputCodeBarreVente.addEventListener(
        "keydown",
        async (e) => {

            /* Un scanner USB tape le code puis appuie sur Entrée
               automatiquement — on intercepte cet Entrée pour faire
               la recherche, sans soumettre toute la vente tout de
               suite (la quantité n'a pas encore été saisie). */
            if (e.key !== "Enter") {
                return;
            }

            e.preventDefault();

            const code = inputCodeBarreVente.value.trim();

            const confirmation =
                document.getElementById("confirmationScanVente");

            const hiddenId =
                document.getElementById("hiddenIdMedicamentVente");

            if (!code) {
                return;
            }

            const resultat = await appelerApi(
                "GET",
                `/api/medicaments/code-barre/${encodeURIComponent(code)}`
            );

            if (!resultat.ok) {

                hiddenId.value = "";

                confirmation.textContent =
                    traduire("medicamentIntrouvablePourCeCode");

                confirmation.className = "kpi-sous-texte texte-perime";

                return;
            }

            const d = resultat.donnees;

            hiddenId.value = d.id;

            confirmation.textContent =
                `✓ ${d.nom} (${d.dosage}) — ${d.stock} `
                + traduire("uniteEnStock");

            confirmation.className =
                "kpi-sous-texte texte-mouvement-plus";

            /* Passe directement au champ quantité, comme au comptoir :
               scanner puis taper la quantité, sans manipuler la souris. */
            const champQuantite =
                document.querySelector('#formVente [name="quantite"]');

            if (champQuantite) {
                champQuantite.focus();
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

                const clientCin =
                    f.get("clientCin");

                if (!clientCin) {

                    return;
                }

                url =
                    `${API_VENTES}?clientCin=${encodeURIComponent(clientCin)}`;

            } else {

                const valeur =
                    f.get("valeur");

                if (!valeur) {
                    return;
                }

                const nomParametre =
                    filtre === "medicament" ? "codeBarre" : filtre;

                url =
                    `${API_VENTES}?${nomParametre}=${encodeURIComponent(valeur)}`;
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
                    traduire("erreurRechercheVentes"),
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
                            ),
                        vente.montantRembourse > 0
                            ? vente.montantRembourse.toFixed(2) + " DT"
                            : "—",
                        vente.ticketModerateur.toFixed(2) + " DT"
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
                                    traduire("venteAnnuleeAvecSucces")
                                );

                            } else {

                                notifier(
                                    traduire("erreurAnnulationVente"),
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
                            f.get("adresse"),

                        numeroCnam:
                            f.get("numeroCnam") || null,

                        cin:
                            f.get("cin")
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
                    + traduire("idParenthese")
                    + resultat.donnees.id
                    + ")";

                alerte.classList.add(
                    "ok"
                );

                e.target.reset();

            } else {

                alerte.textContent =
                    traduire("erreurCreation");

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
                    traduire("clientNexistePas");

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

            const valeurFournisseur =
                f.get("idFournisseur");

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
                            ),

                        idFournisseur:
                            valeurFournisseur
                                ? Number(valeurFournisseur)
                                : null,

                        datePeremption:
                            f.get("datePeremption") || null
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
                    traduire("erreurCreation");

                alerte.classList.remove(
                    "ok"
                );
            }
        }
    );
}

/* ============================================================
   TABLEAU DE BORD
   ============================================================ */

const kpiTotalMedicaments =
    document.getElementById("kpiTotalMedicaments");

if (kpiTotalMedicaments) {

    (async function chargerTableauDeBord() {

        const resultat =
            await appelerApi("GET", API);

        const kpiUnites =
            document.getElementById("kpiUnitesStock");

        const kpiValeur =
            document.getElementById("kpiValeurStock");

        const kpiCritique =
            document.getElementById("kpiStockCritique");

        const kpiExpires =
            document.getElementById("kpiBientotExpires");

        const listeAlertes =
            document.getElementById("listeAlertesDashboard");

        if (!resultat.ok) {

            kpiTotalMedicaments.textContent = "—";
            kpiUnites.textContent = "—";
            kpiValeur.textContent = "—";
            kpiCritique.textContent = "—";

            if (kpiExpires) {
                kpiExpires.textContent = "—";
            }

            if (listeAlertes) {

                listeAlertes.innerHTML =
                    '<p class="kpi-sous-texte">'
                    + traduire("statistiquesIndisponiblesRole")
                    + "</p>";
            }

            return;
        }

        const medicaments = resultat.donnees;

        const totalUnites =
            medicaments.reduce(
                (somme, m) => somme + m.stock,
                0
            );

        const valeurStock =
            medicaments.reduce(
                (somme, m) => somme + (m.stock * m.prix),
                0
            );

        const critiques =
            medicaments.filter(
                (m) => m.stock <= m.seuilCritique
            );

        if (kpiExpires) {

            const dans30Jours = new Date();
            dans30Jours.setDate(dans30Jours.getDate() + 30);
            const limite = dans30Jours.toISOString().slice(0, 10);

            const concernes =
                medicaments.filter(
                    (m) => m.quantitePerimee > 0
                        || (m.datePeremption && m.datePeremption <= limite)
                );

            kpiExpires.textContent = concernes.length;
        }

        kpiTotalMedicaments.textContent =
            medicaments.length;

        kpiUnites.textContent = totalUnites;
        kpiValeur.textContent = valeurStock.toFixed(2);
        kpiCritique.textContent = critiques.length;

        if (listeAlertes) {

            if (critiques.length === 0) {

                listeAlertes.innerHTML =
                    '<p class="aucune-alerte">'
                    + traduire("aucunMedicamentStockCritique")
                    + "</p>";

            } else {

                listeAlertes.innerHTML = "";

                critiques.forEach((m) => {

                    const item =
                        document.createElement("div");

                    item.className = "item-alerte";

                    item.innerHTML =
                        `<span><strong>${m.nom}</strong> (${m.dosage})</span>`
                        + `<span class="quantite">${m.stock} / ${traduire("champSeuilCritique")} ${m.seuilCritique}</span>`;

                    listeAlertes.appendChild(item);
                });
            }
        }

        const listePeremption =
            document.getElementById("listeAlertesPeremption");

        if (listePeremption) {

            const perimes =
                medicaments.filter((m) => m.quantitePerimee > 0);

            if (perimes.length === 0) {

                listePeremption.innerHTML =
                    '<p class="aucune-alerte">'
                    + traduire("aucunLotPerimeARetirer")
                    + "</p>";

            } else {

                listePeremption.innerHTML = "";

                perimes.forEach((m) => {

                    const item =
                        document.createElement("div");

                    item.className = "item-alerte";

                    item.innerHTML =
                        `<span><strong>${m.nom}</strong> (${m.dosage})</span>`
                        + `<span class="quantite">${m.quantitePerimee} / ${m.stock} ${traduire("unitesPerimees").toLowerCase()}</span>`;

                    listePeremption.appendChild(item);
                });
            }
        }

        const canvasStock =
            document.getElementById("graphiqueStock");

        if (canvasStock && typeof Chart !== "undefined") {

            const rupture =
                medicaments.filter((m) => m.stock === 0).length;

            const critiqueNonNul =
                critiques.length - rupture;

            const normal =
                medicaments.length - critiques.length;

            const texteCentre = {
                id: "texteCentre",
                afterDraw(chart) {

                    const { ctx, chartArea } = chart;

                    if (!chartArea) {
                        return;
                    }

                    const x = (chartArea.left + chartArea.right) / 2;
                    const y = (chartArea.top + chartArea.bottom) / 2;

                    ctx.save();
                    ctx.textAlign = "center";
                    ctx.textBaseline = "middle";

                    ctx.font = "700 22px 'IBM Plex Mono', monospace";
                    ctx.fillStyle = "#123526";
                    ctx.fillText(String(medicaments.length), x, y - 8);

                    ctx.font = "600 11px Inter, sans-serif";
                    ctx.fillStyle = "#55677A";
                    ctx.fillText("Total", x, y + 14);

                    ctx.restore();
                }
            };

            new Chart(canvasStock, {
                type: "doughnut",
                data: {
                    labels: [traduire("stockNormalLabel"), traduire("stockCritiqueLabel"), "Rupture"],
                    datasets: [{
                        data: [normal, critiqueNonNul, rupture],
                        backgroundColor: ["#1E8A5C", "#B4690E", "#C0293B"],
                        borderWidth: 3,
                        borderColor: "#FFFFFF",
                        hoverOffset: 6
                    }]
                },
                options: {
                    responsive: true,
                    cutout: "68%",
                    plugins: {
                        legend: { position: "bottom", labels: { boxWidth: 10, padding: 14 } }
                    }
                },
                plugins: [texteCentre]
            });
        }
    })();
}


/* ============================================================
   TABLEAU DE BORD — VENTES (pharmacien)
   ============================================================ */

const kpiVentesJour =
    document.getElementById("kpiVentesJour");

if (kpiVentesJour) {

    (async function chargerDashboardVentes() {

        const aujourdhui =
            new Date().toISOString().slice(0, 10);

        const septJours = new Date();
        septJours.setDate(septJours.getDate() - 6);
        const ilYA7Jours = septJours.toISOString().slice(0, 10);

        const kpiUnitesVendues =
            document.getElementById("kpiUnitesVenduesJour");

        const resultatSemaine =
            await appelerApi(
                "GET",
                `${API_VENTES}?debut=${ilYA7Jours}&fin=${aujourdhui}`
            );

        if (!resultatSemaine.ok) {

            kpiVentesJour.textContent = "—";

            if (kpiUnitesVendues) {
                kpiUnitesVendues.textContent = "—";
            }

            return;
        }

        const ventes = resultatSemaine.donnees;

        const ventesAujourdhui =
            ventes.filter(
                (v) => v.dateVente.slice(0, 10) === aujourdhui
            );

        kpiVentesJour.textContent =
            ventesAujourdhui.length;

        if (kpiUnitesVendues) {

            kpiUnitesVendues.textContent =
                ventesAujourdhui.reduce(
                    (somme, v) => somme + v.quantite,
                    0
                );
        }

        /* Regroupement par jour pour le graphique en ligne */
        const joursOrdonnes = [];

        for (let i = 6; i >= 0; i--) {

            const jour = new Date();
            jour.setDate(jour.getDate() - i);
            joursOrdonnes.push(jour.toISOString().slice(0, 10));
        }

        const quantitesParJour =
            joursOrdonnes.map((jour) => {

                return ventes
                    .filter((v) => v.dateVente.slice(0, 10) === jour)
                    .reduce((somme, v) => somme + v.quantite, 0);
            });

        const canvasVentes =
            document.getElementById("graphiqueVentes");

        const zoneVide =
            document.getElementById("graphiqueVentesVide");

        const totalPeriode =
            quantitesParJour.reduce((a, b) => a + b, 0);

        if (canvasVentes && typeof Chart !== "undefined") {

            if (totalPeriode === 0 && zoneVide) {

                canvasVentes.hidden = true;
                zoneVide.hidden = false;

            } else {

                new Chart(canvasVentes, {
                    type: "line",
                    data: {
                        labels: joursOrdonnes.map(
                            (j) => j.slice(5).replace("-", "/")
                        ),
                        datasets: [{
                            label: traduire("unitesVenduesLabel"),
                            data: quantitesParJour,
                            borderColor: "#1E8A5C",
                            backgroundColor: "rgba(30, 138, 92, 0.12)",
                            tension: 0.3,
                            fill: true,
                            pointRadius: 3
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            y: { beginAtZero: true, ticks: { precision: 0 } }
                        }
                    }
                });
            }
        }
    })();
}


/* ============================================================
   FOURNISSEURS
   ============================================================ */

const API_FOURNISSEURS = "/api/fournisseurs";

const formFournisseur =
    document.getElementById("formFournisseur");

if (formFournisseur) {

    formFournisseur.addEventListener(
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
                    API_FOURNISSEURS,
                    {
                        nom: f.get("nom"),
                        telephone: f.get("telephone"),
                        email: f.get("email"),
                        adresse: f.get("adresse")
                    }
                );

            definirChargement(bouton, false);

            const alerte =
                document.getElementById(
                    "alerteFournisseur"
                );

            alerte.hidden = false;

            if (resultat.ok) {

                alerte.textContent =
                    resultat.donnees.message
                    + traduire("idParenthese")
                    + resultat.donnees.id
                    + ")";

                alerte.classList.add("ok");

                e.target.reset();

            } else {

                alerte.textContent =
                    traduire("erreurCreationFournisseur");

                alerte.classList.remove("ok");
            }
        }
    );
}


async function chargerFournisseurs() {

    const bouton =
        document.getElementById(
            "btnListeFournisseurs"
        );

    definirChargement(bouton, true);

    const resultat =
        await appelerApi(
            "GET",
            API_FOURNISSEURS
        );

    definirChargement(bouton, false);

    const table =
        document.getElementById(
            "tableFournisseurs"
        );

    const aucun =
        document.getElementById(
            "aucunFournisseur"
        );

    if (!resultat.ok) {

        notifier(
            traduire("impossibleChargerFournisseurs"),
            "erreur"
        );

        return;
    }

    const corps =
        document.getElementById(
            "corpsTableFournisseurs"
        );

    corps.innerHTML = "";

    if (resultat.donnees.length === 0) {

        table.hidden = true;
        aucun.hidden = false;
        return;
    }

    aucun.hidden = true;

    resultat.donnees.forEach(
        (fournisseur) => {

            const ligne =
                document.createElement(
                    "tr"
                );

            [
                fournisseur.id,
                fournisseur.nom,
                fournisseur.telephone || "—",
                fournisseur.email || "—",
                fournisseur.adresse || "—"
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


const btnListeFournisseurs =
    document.getElementById(
        "btnListeFournisseurs"
    );

if (btnListeFournisseurs) {

    btnListeFournisseurs.addEventListener(
        "click",
        chargerFournisseurs
    );
}


/* ============================================================
   MENU DÉROULANT FOURNISSEUR (page Commandes)
   ============================================================ */

const selectFournisseur =
    document.getElementById("selectFournisseur");

if (selectFournisseur) {

    (async function chargerOptionsFournisseurs() {

        const resultat =
            await appelerApi(
                "GET",
                API_FOURNISSEURS
            );

        if (!resultat.ok) {
            return;
        }

        resultat.donnees.forEach((fournisseur) => {

            const option =
                document.createElement("option");

            option.value = fournisseur.id;
            option.textContent = fournisseur.nom;

            selectFournisseur.appendChild(option);
        });
    })();
}


/* ============================================================
   HISTORIQUE DES MOUVEMENTS DE STOCK
   ============================================================ */

const API_HISTORIQUE = "/api/stock/historique";

async function chargerHistorique() {

    const bouton =
        document.getElementById("btnListeHistorique");

    definirChargement(bouton, true);

    const resultat =
        await appelerApi("GET", API_HISTORIQUE);

    definirChargement(bouton, false);

    if (!resultat.ok) {

        notifier(
            traduire("impossibleChargerHistorique"),
            "erreur"
        );

        return;
    }

    const table =
        document.getElementById("tableHistorique");

    const aucun =
        document.getElementById("aucunHistorique");

    const corps =
        document.getElementById("corpsTableHistorique");

    corps.innerHTML = "";

    if (resultat.donnees.length === 0) {

        table.hidden = true;
        aucun.hidden = false;
        return;
    }

    aucun.hidden = true;

    /* Les plus récents en premier */
    const historiqueTrie =
        [...resultat.donnees].reverse();

    historiqueTrie.forEach((mouvement) => {

        const ligne =
            document.createElement("tr");

        const celluleId =
            document.createElement("td");

        celluleId.textContent =
            mouvement.idMedicament;

        const celluleQuantite =
            document.createElement("td");

        const positif =
            mouvement.quantite >= 0;

        celluleQuantite.textContent =
            (positif ? "+" : "") + mouvement.quantite;

        celluleQuantite.className =
            positif ? "texte-mouvement-plus" : "texte-mouvement-moins";

        const celluleDate =
            document.createElement("td");

        celluleDate.textContent =
            (mouvement.dateModification || "")
                .toString()
                .replace("T", " ");

        ligne.appendChild(celluleId);
        ligne.appendChild(celluleQuantite);
        ligne.appendChild(celluleDate);

        corps.appendChild(ligne);
    });

    table.hidden = false;
}


const btnListeHistorique =
    document.getElementById("btnListeHistorique");

if (btnListeHistorique) {

    btnListeHistorique.addEventListener(
        "click",
        chargerHistorique
    );
}



/* ============================================================
   RAPPORTS
   ============================================================ */

const formRapport =
    document.getElementById("formRapport");

if (formRapport) {

    let graphiqueTopMedicamentsInstance = null;

    formRapport.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f = new FormData(e.target);
            const debut = f.get("debut");
            const fin = f.get("fin");

            const resultatVentes =
                await appelerApi(
                    "GET",
                    `${API_VENTES}?debut=${debut}&fin=${fin}`
                );

            const rapportVide =
                document.getElementById("rapportVide");

            const kpiGrille =
                document.getElementById("kpiGrilleRapport");

            const carteGraphique =
                document.getElementById("carteGraphiqueRapport");

            const carteTableau =
                document.getElementById("carteTableauRapport");

            if (!resultatVentes.ok) {

                definirChargement(bouton, false);

                notifier(
                    traduire("erreurRapport"),
                    "erreur"
                );

                return;
            }

            const ventes = resultatVentes.donnees;

            if (ventes.length === 0) {

                definirChargement(bouton, false);

                kpiGrille.style.display = "none";
                carteGraphique.style.display = "none";
                carteTableau.style.display = "none";
                rapportVide.hidden = false;

                return;
            }

            rapportVide.hidden = true;

            /* Essai de récupérer les médicaments pour les noms + prix
               (réservé au Gestionnaire ; échec silencieux sinon) */
            const resultatMedicaments =
                await appelerApi("GET", API);

            definirChargement(bouton, false);

            const medicamentsParId = {};

            if (resultatMedicaments.ok) {

                resultatMedicaments.donnees.forEach((m) => {
                    medicamentsParId[m.id] = m;
                });
            }

            /* Agrégation par médicament */
            const parMedicament = {};

            ventes.forEach((v) => {

                if (!parMedicament[v.idMedicament]) {

                    parMedicament[v.idMedicament] = {
                        nombreVentes: 0,
                        unites: 0
                    };
                }

                parMedicament[v.idMedicament].nombreVentes += 1;
                parMedicament[v.idMedicament].unites += v.quantite;
            });

            const totalVentes = ventes.length;

            const totalUnites =
                ventes.reduce(
                    (somme, v) => somme + v.quantite,
                    0
                );

            document.getElementById("kpiNombreVentes")
                .textContent = totalVentes;

            document.getElementById("kpiUnitesVendues")
                .textContent = totalUnites;

            const kpiCA =
                document.getElementById("kpiChiffreAffaires");

            const kpiCANote =
                document.getElementById("kpiChiffreAffairesNote");

            if (resultatMedicaments.ok) {

                const chiffreAffaires =
                    ventes.reduce((somme, v) => {

                        const med = medicamentsParId[v.idMedicament];

                        return somme + (med ? med.prix * v.quantite : 0);
                    }, 0);

                kpiCA.textContent = chiffreAffaires.toFixed(2);
                kpiCANote.textContent = traduire("dinarsTunisiens");

            } else {

                kpiCA.textContent = "—";
                kpiCANote.textContent =
                    traduire("indisponiblePourRole");
            }

            kpiGrille.style.display = "grid";

            /* Tri du plus vendu au moins vendu */
            const medicamentsTries =
                Object.entries(parMedicament)
                    .sort(
                        (a, b) => b[1].unites - a[1].unites
                    );

            const nomMedicament = (id) => {

                const med = medicamentsParId[id];

                return med
                    ? `${med.nom} (${med.dosage})`
                    : `Médicament #${id}`;
            };

            /* Tableau détaillé */
            const corpsTableau =
                document.getElementById("corpsTableRapport");

            corpsTableau.innerHTML = "";

            medicamentsTries.forEach(([id, stats]) => {

                const ligne =
                    document.createElement("tr");

                [
                    nomMedicament(id),
                    stats.nombreVentes,
                    stats.unites
                ].forEach((valeur) => {

                    const cellule =
                        document.createElement("td");

                    cellule.textContent = valeur;
                    ligne.appendChild(cellule);
                });

                corpsTableau.appendChild(ligne);
            });

            carteTableau.style.display = "block";

            /* Graphique top médicaments (5 premiers) */
            const top5 = medicamentsTries.slice(0, 5);

            const canvas =
                document.getElementById("graphiqueTopMedicaments");

            if (canvas && typeof Chart !== "undefined") {

                if (graphiqueTopMedicamentsInstance) {
                    graphiqueTopMedicamentsInstance.destroy();
                }

                graphiqueTopMedicamentsInstance = new Chart(canvas, {
                    type: "bar",
                    data: {
                        labels: top5.map(([id]) => nomMedicament(id)),
                        datasets: [{
                            label: traduire("unitesVenduesLabel"),
                            data: top5.map(([, stats]) => stats.unites),
                            backgroundColor: "#1E8A5C",
                            borderRadius: 6
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            y: { beginAtZero: true, ticks: { precision: 0 } }
                        }
                    }
                });

                carteGraphique.style.display = "block";
            }
        }
    );
}


/* ============================================================
   COMPTES UTILISATEURS
   ============================================================ */

const API_COMPTES = "/api/comptes";

const formCompte =
    document.getElementById("formCompte");

if (formCompte) {

    let compteEnEdition = null;

    const titreForm =
        document.getElementById("titreFormCompte");

    const btnSubmit =
        document.getElementById("btnSubmitCompte");

    const btnAnnuler =
        document.getElementById("btnAnnulerEditionCompte");

    const selectRole =
        document.getElementById("selectRoleCompte");

    const inputPwd =
        document.getElementById("inputPwdCompte");

    const labelPwd =
        document.getElementById("labelMotDePasseCompte");

    function reinitialiserFormCompte() {

        compteEnEdition = null;

        formCompte.reset();

        titreForm.textContent = traduire("comptesCreerTitre");
        btnSubmit.textContent = traduire("creerLUtilisateur");
        btnAnnuler.hidden = true;

        selectRole.disabled = false;
        inputPwd.required = true;
        labelPwd.firstChild.textContent = traduire("motDePasseChamp");
        inputPwd.placeholder =
            traduire("placeholderMotDePasseRegles");
    }

    function passerEnEdition(compte) {

        compteEnEdition = {
            id: compte.id,
            role: compte.role
        };

        formCompte.nom.value = compte.nom;
        formCompte.prenom.value = compte.prenom;
        formCompte.login.value = compte.login;
        selectRole.value = compte.role;
        selectRole.disabled = true;

        inputPwd.value = "";
        inputPwd.required = false;
        labelPwd.firstChild.textContent =
            traduire("nouveauMotDePasseChamp");
        inputPwd.placeholder =
            traduire("laisserVideNePasChanger");

        titreForm.textContent =
            `Modifier ${compte.nom} ${compte.prenom}`;

        btnSubmit.textContent = traduire("enregistrerLesModifications");
        btnAnnuler.hidden = false;

        formCompte.scrollIntoView({ behavior: "smooth" });
    }

    btnAnnuler.addEventListener(
        "click",
        reinitialiserFormCompte
    );

    formCompte.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button[type=submit]");
            definirChargement(bouton, true);

            const f = new FormData(e.target);
            const alerte = document.getElementById("alerteCompte");

            let resultat;

            if (compteEnEdition) {

                resultat = await appelerApi(
                    "PUT",
                    `${API_COMPTES}/${compteEnEdition.role}/${compteEnEdition.id}`,
                    {
                        nom: f.get("nom"),
                        prenom: f.get("prenom"),
                        login: f.get("login"),
                        pwd: f.get("pwd") || null
                    }
                );

            } else {

                resultat = await appelerApi(
                    "POST",
                    API_COMPTES,
                    {
                        nom: f.get("nom"),
                        prenom: f.get("prenom"),
                        login: f.get("login"),
                        pwd: f.get("pwd"),
                        role: f.get("role")
                    }
                );
            }

            definirChargement(bouton, false);

            alerte.hidden = false;

            if (resultat.ok) {

                alerte.textContent =
                    resultat.donnees.message;

                alerte.classList.add("ok");

                reinitialiserFormCompte();
                chargerComptes();

            } else {

                alerte.textContent =
                    (resultat.donnees && resultat.donnees.message)
                        || traduire("erreurEnregistrement");

                alerte.classList.remove("ok");
            }
        }
    );

    window.passerEnEditionCompte = passerEnEdition;
}


async function chargerComptes() {

    const bouton =
        document.getElementById("btnListeComptes");

    definirChargement(bouton, true);

    const resultat =
        await appelerApi("GET", API_COMPTES);

    definirChargement(bouton, false);

    if (!resultat.ok) {

        notifier(
            traduire("impossibleChargerComptes"),
            "erreur"
        );

        return;
    }

    const table =
        document.getElementById("tableComptes");

    const corps =
        document.getElementById("corpsTableComptes");

    corps.innerHTML = "";

    if (resultat.donnees.length === 0) {

        table.hidden = true;

        let aucun = document.getElementById("aucunCompte");

        if (!aucun) {

            aucun = document.createElement("p");
            aucun.id = "aucunCompte";
            aucun.className = "alerte ok";
            aucun.textContent = traduire("aucunCompteEnregistre");
            table.insertAdjacentElement("afterend", aucun);
        }

        aucun.hidden = false;
        return;
    }

    const aucunCompteExistant = document.getElementById("aucunCompte");

    if (aucunCompteExistant) {
        aucunCompteExistant.hidden = true;
    }

    resultat.donnees.forEach((compte) => {

        const ligne =
            document.createElement("tr");

        [
            compte.nom,
            compte.prenom,
            compte.login,
            compte.role === "PHARMACIEN" ? traduire("pharmacienLabel") : traduire("gestionnaireLabel")
        ].forEach((valeur) => {

            const cellule =
                document.createElement("td");

            cellule.textContent = valeur;
            ligne.appendChild(cellule);
        });

        const celluleActions =
            document.createElement("td");

        const btnModifier =
            document.createElement("button");

        btnModifier.type = "button";
        btnModifier.textContent = "Modifier";
        btnModifier.className = "bouton-danger-discret";
        btnModifier.style.borderColor = "var(--vert)";
        btnModifier.style.color = "var(--vert)";
        btnModifier.style.marginRight = "6px";

        btnModifier.addEventListener("click", () => {
            window.passerEnEditionCompte(compte);
        });

        const btnSupprimer =
            document.createElement("button");

        btnSupprimer.type = "button";
        btnSupprimer.textContent = "Supprimer";
        btnSupprimer.className = "bouton-danger-discret";

        btnSupprimer.addEventListener("click", async () => {

            definirChargement(btnSupprimer, true);

            const resultatSuppression = await appelerApi(
                "DELETE",
                `${API_COMPTES}/${compte.role}/${compte.id}`
            );

            definirChargement(btnSupprimer, false);

            if (resultatSuppression.ok) {

                ligne.remove();

                notifier(traduire("utilisateurSupprimeAvecSucces"));

            } else {

                notifier(
                    traduire("erreurSuppression"),
                    "erreur"
                );
            }
        });

        celluleActions.appendChild(btnModifier);
        celluleActions.appendChild(btnSupprimer);
        ligne.appendChild(celluleActions);

        corps.appendChild(ligne);
    });

    table.hidden = false;
}


const btnListeComptes =
    document.getElementById("btnListeComptes");

if (btnListeComptes) {

    btnListeComptes.addEventListener(
        "click",
        chargerComptes
    );
}


/* ============================================================
   PARAMÈTRES — MES INFORMATIONS (nom, prénom, email)
   ============================================================ */

const formMesInfos =
    document.getElementById("formMesInfos");

if (formMesInfos) {

    (async function chargerMesInfos() {

        const resultat = await appelerApi("GET", "/api/mon-compte");

        if (!resultat.ok) {
            return;
        }

        document.getElementById("inputNomInfos").value =
            resultat.donnees.nom || "";

        document.getElementById("inputPrenomInfos").value =
            resultat.donnees.prenom || "";

        document.getElementById("inputLoginInfos").value =
            resultat.donnees.login || "";

        document.getElementById("inputEmailInfos").value =
            resultat.donnees.email || "";
    })();

    formMesInfos.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f = new FormData(e.target);

            const resultat = await appelerApi(
                "PUT",
                "/api/mon-compte/infos",
                {
                    nom: f.get("nom"),
                    prenom: f.get("prenom"),
                    email: f.get("email") || null
                }
            );

            definirChargement(bouton, false);

            const alerte = document.getElementById("alerteMesInfos");
            alerte.hidden = false;

            if (resultat.ok) {

                alerte.textContent = resultat.donnees.message;
                alerte.classList.add("ok");

            } else {

                alerte.textContent =
                    (resultat.donnees && resultat.donnees.message)
                        || traduire("erreurMaj");

                alerte.classList.remove("ok");
            }
        }
    );
}


/* ============================================================
   PARAMÈTRES — CHANGER SON PROPRE MOT DE PASSE
   ============================================================ */

const formMotDePasse =
    document.getElementById("formMotDePasse");

if (formMotDePasse) {

    formMotDePasse.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const f = new FormData(e.target);
            const alerte = document.getElementById("alerteMotDePasse");

            const pwdNouveau = f.get("pwdNouveau");
            const pwdConfirmation = f.get("pwdConfirmation");

            if (pwdNouveau !== pwdConfirmation) {

                alerte.hidden = false;
                alerte.textContent =
                    traduire("motsDePasseNeCorrespondentPas");
                alerte.classList.remove("ok");
                return;
            }

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const resultat = await appelerApi(
                "PUT",
                "/api/mon-compte/mot-de-passe",
                {
                    pwdActuel: f.get("pwdActuel"),
                    pwdNouveau: pwdNouveau
                }
            );

            definirChargement(bouton, false);

            alerte.hidden = false;

            if (resultat.ok) {

                alerte.textContent =
                    resultat.donnees.message;

                alerte.classList.add("ok");

                e.target.reset();

            } else {

                alerte.textContent =
                    (resultat.donnees && resultat.donnees.message)
                        || traduire("erreurChangementMotDePasse");

                alerte.classList.remove("ok");
            }
        }
    );
}


/* ============================================================
   RAPPORT CNAM
   ============================================================ */

const formRapportCnam =
    document.getElementById("formRapportCnam");

if (formRapportCnam) {

    formRapportCnam.addEventListener(
        "submit",
        async (e) => {

            e.preventDefault();

            const bouton = e.target.querySelector("button");
            definirChargement(bouton, true);

            const f = new FormData(e.target);
            const debut = f.get("debut");
            const fin = f.get("fin");

            const resultat = await appelerApi(
                "GET",
                `/api/cnam/rapport?debut=${debut}&fin=${fin}`
            );

            definirChargement(bouton, false);

            if (!resultat.ok) {

                notifier(
                    traduire("erreurRapportCnam"),
                    "erreur"
                );

                return;
            }

            const d = resultat.donnees;

            document.getElementById("cnamNombreVentes").textContent =
                d.nombreVentesConcernees;

            document.getElementById("cnamTotalRembourse").textContent =
                d.totalMontantRembourse.toFixed(2);

            document.getElementById("cnamTotalTicket").textContent =
                d.totalTicketModerateur.toFixed(2);

            document.getElementById("kpiGrilleCnam").style.display = "grid";
        }
    );
}