/* ============================================================
   Garde d'authentification de l'application
   - Vérifie la session HTTP
   - Redirige vers login.html si aucune session n'existe
   - Adapte les menus selon le rôle
   - Charge le profil utilisateur
   - Gère la déconnexion
   ============================================================ */

window.utilisateurConnecte = null;


/* ---------- Adaptation du menu selon le rôle ---------- */

function adapterMenu(role) {

    document.querySelectorAll("[data-role]")
        .forEach((element) => {

            if (element.dataset.role !== role) {
                element.classList.add("menu-cache");
            }
        });
}


/* ---------- Chargement du profil utilisateur ---------- */

function chargerProfil(userInfo) {

    fetch("/api/utilisateurs/profil")
        .then((reponse) => {

            if (!reponse.ok) {
                return null;
            }

            return reponse.json();
        })
        .then((profil) => {

            if (profil !== null) {

                userInfo.textContent =
                    profil.prenom
                    + " "
                    + profil.nom
                    + " · "
                    + profil.role;
            }
        })
        .catch(() => {

            /*
             * En cas d'échec, l'affichage
             * login · rôle est conservé.
             */
        });
}


/* ---------- Vérification de la session ---------- */

fetch("/api/auth/me")
    .then((reponse) => {

        if (!reponse.ok) {

            window.location.href =
                "login.html";

            return null;
        }

        return reponse.json();
    })
    .then((utilisateur) => {

        if (utilisateur === null) {
            return;
        }

        window.utilisateurConnecte =
            utilisateur;

        const userInfo =
            document.getElementById(
                "userInfo"
            );

        const btnLogout =
            document.getElementById(
                "btnLogout"
            );


        /* Affichage initial */

        if (userInfo !== null) {

            userInfo.textContent =
                utilisateur.login
                + " · "
                + utilisateur.role;
        }


        /* Affichage du bouton déconnexion */

        if (btnLogout !== null) {

            btnLogout.hidden = false;
        }


        /* Adaptation des menus */

        adapterMenu(
            utilisateur.role
        );


        /* Chargement du profil complet */

        if (userInfo !== null) {

            chargerProfil(
                userInfo
            );
        }


        /* Déconnexion */

        if (btnLogout !== null) {

            btnLogout.addEventListener(
                "click",
                () => {

                    fetch(
                        "/api/auth/logout",
                        {
                            method: "POST"
                        }
                    )
                    .finally(() => {

                        window.location.href =
                            "login.html";
                    });
                }
            );
        }
    })
    .catch(() => {

        window.location.href =
            "login.html";
    });