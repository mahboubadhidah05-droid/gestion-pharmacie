/* Garde d'authentification de l'application :
   redirige vers login.html si aucune session n'est ouverte,
   puis adapte les cartes visibles selon le rôle. */

window.utilisateurConnecte = null;


function adapterMenu(role) {

    document.querySelectorAll("[data-role]").forEach((element) => {

        if (element.dataset.role !== role) {
            element.classList.add("menu-cache");
        }
    });
}
/* Enrichit l'affichage avec le nom complet (profil). */
        fetch("/api/utilisateurs/profil")
            .then((r) => (r.ok ? r.json() : null))
            .then((profil) => {
                if (profil !== null) {
                    userInfo.textContent =
                        profil.prenom + " " + profil.nom
                        + " · " + profil.role;
                }
            })
            .catch(() => {
                /* En cas d'échec on garde l'affichage login · rôle. */
            });


fetch("/api/auth/me")
    .then((reponse) => {
        if (!reponse.ok) {
            window.location.href = "login.html";
            return null;
        }
        return reponse.json();
    })
    .then((utilisateur) => {
        if (utilisateur === null) {
            return;
        }

        window.utilisateurConnecte = utilisateur;

        const userInfo = document.getElementById("userInfo");
        const btnLogout = document.getElementById("btnLogout");

        userInfo.textContent =
            utilisateur.login + " · " + utilisateur.role;

        btnLogout.hidden = false;

        adapterMenu(utilisateur.role);

        btnLogout.addEventListener("click", () => {
            fetch("/api/auth/logout", { method: "POST" })
                .finally(() => {
                    window.location.href = "login.html";
                });
        });
    })
    .catch(() => {
        window.location.href = "login.html";
    });