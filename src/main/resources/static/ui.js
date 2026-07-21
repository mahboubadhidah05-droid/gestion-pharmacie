/* ============================================================
   Gestion Pharmacie — utilitaires d'interface partagés
   - Notifications (toasts)
   - État de chargement des boutons
   - Apparition en cascade des cartes ("rangement en rayon")
   ============================================================ */


/* ---------- Notifications ---------- */

function conteneurToasts() {

    let conteneur = document.querySelector(".toasts");

    if (!conteneur) {

        conteneur = document.createElement("div");
        conteneur.className = "toasts";
        conteneur.setAttribute("role", "status");
        conteneur.setAttribute("aria-live", "polite");

        document.body.appendChild(conteneur);
    }

    return conteneur;
}


/**
 * Affiche une notification temporaire.
 * @param {string} message
 * @param {"succes"|"erreur"} type
 */
function notifier(message, type = "succes") {

    const conteneur = conteneurToasts();

    const toast = document.createElement("div");

    toast.className =
        "toast" + (type === "erreur" ? " erreur" : "");

    const puce = document.createElement("span");
    puce.className = "toast-puce";

    const texte = document.createElement("span");
    texte.textContent = message;

    const fermer = document.createElement("button");
    fermer.className = "toast-fermer";
    fermer.setAttribute("aria-label", "Fermer la notification");
    fermer.textContent = "×";

    toast.appendChild(puce);
    toast.appendChild(texte);
    toast.appendChild(fermer);
    conteneur.appendChild(toast);

    const retirer = () => {

        toast.classList.add("sortie");

        setTimeout(() => {
            toast.remove();
        }, 200);
    };

    fermer.addEventListener("click", retirer);

    setTimeout(retirer, 4000);
}


/* ---------- État de chargement des boutons ---------- */

/**
 * Active/désactive l'état de chargement visuel d'un bouton
 * (spinner + désactivation) sans changer son texte dans le DOM.
 * @param {HTMLButtonElement} bouton
 * @param {boolean} chargement
 */
function definirChargement(bouton, chargement) {

    if (!bouton) {
        return;
    }

    bouton.disabled = chargement;
    bouton.classList.toggle("en-chargement", chargement);
}