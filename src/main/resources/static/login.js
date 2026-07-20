/* ============================================================
   Gestion Pharmacie — Page de connexion
   ============================================================ */

const formLogin = document.getElementById("formLogin");
const erreurLogin = document.getElementById("erreurLogin");


/* ---------- Vérifier si une session existe déjà ---------- */

fetch("/api/auth/me")
    .then((reponse) => {

        if (reponse.ok) {
            window.location.href = "index.html";
        }

    })
    .catch(() => {

        // Le serveur est inaccessible.
        // L'utilisateur reste sur la page de connexion.

    });


/* ---------- Connexion ---------- */

formLogin.addEventListener("submit", (evenement) => {

    evenement.preventDefault();

    const donnees = new FormData(formLogin);

    const login = donnees.get("login");
    const pwd = donnees.get("pwd");


    fetch("/api/auth/login", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({

            login: login,
            pwd: pwd

        })

    })

    .then((reponse) => {

        /* Connexion réussie */

        if (reponse.ok) {

            window.location.href = "index.html";

            return;
        }


        /* Identifiants incorrects */

        if (reponse.status === 401) {

            erreurLogin.textContent =
                "Identifiants incorrects. Veuillez réessayer.";

        }

        /* Autre erreur serveur */

        else {

            erreurLogin.textContent =
                "Erreur technique du serveur. Réessayez dans un instant.";

        }


        erreurLogin.hidden = false;

    })

    .catch(() => {

        erreurLogin.textContent =
            "Impossible de contacter le serveur.";

        erreurLogin.hidden = false;

    });

});