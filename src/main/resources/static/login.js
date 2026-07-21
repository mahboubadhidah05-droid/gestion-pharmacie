/* Page de connexion */

const formLogin = document.getElementById("formLogin");
const erreurLogin = document.getElementById("erreurLogin");

/* Repli si ui.js n'a pas pu être chargé (404, etc.) :
   le formulaire continue de fonctionner sans le spinner. */
if (typeof definirChargement !== "function") {

    window.definirChargement = function (bouton, chargement) {

        if (bouton) {
            bouton.disabled = chargement;
        }
    };
}

formLogin.addEventListener("submit", async (evenement) => {

    evenement.preventDefault();

    const bouton = formLogin.querySelector("button");
    definirChargement(bouton, true);
    erreurLogin.hidden = true;

    const donnees = new FormData(formLogin);

    try {

        const reponse = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                login: donnees.get("login"),
                pwd: donnees.get("pwd")
            })
        });

        if (reponse.ok) {
            window.location.href = "/index.html";
            return;
        }

        if (reponse.status === 401) {
            erreurLogin.textContent =
                "Identifiants incorrects. Veuillez réessayer.";
        } else {
            erreurLogin.textContent =
                "Erreur technique du serveur.";
        }

        erreurLogin.hidden = false;
        definirChargement(bouton, false);

    } catch (erreur) {

        console.error(erreur);

        erreurLogin.textContent =
            "Impossible de contacter le serveur.";

        erreurLogin.hidden = false;
        definirChargement(bouton, false);
    }
});