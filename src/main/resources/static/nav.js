/* ============================================================
   Gestion Pharmacie — barre de navigation principale
   Générée une seule fois ici et injectée dans chaque page,
   pour éviter de dupliquer le HTML de navigation partout.
   ============================================================ */

(function () {

    const ICONES = {
        tableau:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="9"/><rect x="14" y="3" width="7" height="5"/><rect x="14" y="12" width="7" height="9"/><rect x="3" y="16" width="7" height="5"/></svg>',
        medicaments:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3.5" y="8.5" width="17" height="7" rx="3.5" transform="rotate(-45 12 12)"/><line x1="8.5" y1="12" x2="12" y2="15.5"/></svg>',
        ventes:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="20" r="1.4"/><circle cx="18" cy="20" r="1.4"/><path d="M2 3h2l2.2 12.2a2 2 0 0 0 2 1.8h8.7a2 2 0 0 0 2-1.6L21 8H6"/></svg>',
        clients:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="3.6"/><path d="M4.5 20c1-4 4-6 7.5-6s6.5 2 7.5 6"/></svg>',
        commandes:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 7l9-4 9 4-9 4-9-4Z"/><path d="M3 7v10l9 4 9-4V7"/><path d="M12 11v10"/></svg>',
        fournisseurs:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="8" width="13" height="9"/><path d="M14 11h4l3 3v3h-7z"/><circle cx="6" cy="19" r="1.6"/><circle cx="17.5" cy="19" r="1.6"/></svg>',
        comptes:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="9" cy="7.5" r="3"/><path d="M3.5 19c0.8-3.2 3-5 5.5-5s4.7 1.8 5.5 5"/><circle cx="17.5" cy="8.5" r="2.2"/><path d="M15.5 12c1.8 0.2 3.4 1.6 4 4.3"/></svg>',
        historique:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="8.5"/><path d="M12 7.5V12l3 2"/></svg>',
        rapports:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="20" x2="5" y2="12"/><line x1="12" y1="20" x2="12" y2="6"/><line x1="19" y1="20" x2="19" y2="15"/></svg>',
        notifications:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 9a6 6 0 0 1 12 0c0 5 2 6 2 6H4s2-1 2-6Z"/><path d="M10 19a2 2 0 0 0 4 0"/></svg>',
        parametres:
            '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 13.5a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.6v.2a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.6 1.7 1.7 0 0 0-1.9.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.9 1.7 1.7 0 0 0-1.6-1H4a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.6-1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.9.3H10a1.7 1.7 0 0 0 1-1.6V4a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.6 1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.9v.1a1.7 1.7 0 0 0 1.6 1H20a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.6 1Z"/></svg>'
    };

    const PAGES = [
        { href: "index.html", label: "Tableau de bord", cle: "navTableauDeBord", icone: "tableau" },
        { href: "medicaments.html", label: "Médicaments", cle: "navMedicaments", icone: "medicaments", role: "GESTIONNAIRE" },
        { href: "ventes.html", label: "Ventes", cle: "navVentes", icone: "ventes", role: "PHARMACIEN" },
        { href: "stock-pharmacien.html", label: "Stock", cle: "navStockPharmacien", icone: "medicaments", role: "PHARMACIEN" },
        { href: "clients.html", label: "Clients", cle: "navClients", icone: "clients", role: "PHARMACIEN" },
        { href: "commandes.html", label: "Commandes", cle: "navCommandes", icone: "commandes", role: "GESTIONNAIRE" },
        { href: "fournisseurs.html", label: "Fournisseurs", cle: "navFournisseurs", icone: "fournisseurs", role: "GESTIONNAIRE" },
        { href: "comptes.html", label: "Comptes", cle: "navComptes", icone: "comptes", role: "GESTIONNAIRE" },
        { href: "historique.html", label: "Historique", cle: "navHistorique", icone: "historique" },
        { href: "rapports.html", label: "Rapports", cle: "navRapports", icone: "rapports" },
        { href: "parametres.html", label: "Paramètres", cle: "navParametres", icone: "parametres" }
    ];

    const CLE_STOCKAGE = "navReduit";

    const ICONE_REDUIRE =
        '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="14 6 8 12 14 18"/></svg>';

    function pageActuelle() {

        const segment = window.location.pathname
            .split("/")
            .pop();

        return segment && segment.length > 0
            ? segment
            : "index.html";
    }

    function estReduit() {

        try {
            return localStorage.getItem(CLE_STOCKAGE) === "true";
        } catch {
            return false;
        }
    }

    function definirReduit(nav, reduit) {

        nav.classList.toggle("reduit", reduit);

        try {
            localStorage.setItem(CLE_STOCKAGE, reduit ? "true" : "false");
        } catch {
            /* stockage indisponible : l'état ne sera simplement pas mémorisé */
        }
    }

    function construireNav() {

        const entete = document.querySelector(".topbar");

        if (!entete) {
            return;
        }

        const actuelle = pageActuelle();
        const reduit = estReduit();

        const nav = document.createElement("nav");
        nav.className = "nav-principal" + (reduit ? " reduit" : "");
        nav.setAttribute("aria-label", "Navigation principale");

        const boutonReduire = document.createElement("button");
        boutonReduire.type = "button";
        boutonReduire.className = "nav-bouton-reduire";
        boutonReduire.setAttribute(
            "aria-label",
            "Réduire ou agrandir le menu"
        );
        boutonReduire.innerHTML = ICONE_REDUIRE;

        boutonReduire.addEventListener("click", () => {
            definirReduit(nav, !nav.classList.contains("reduit"));
        });

        const conteneur = document.createElement("div");
        conteneur.className = "nav-liens";

        PAGES.forEach((page) => {

            const lien = document.createElement("a");

            lien.href = page.href;
            lien.className =
                "nav-lien" +
                (page.href === actuelle ? " actif" : "");

            lien.dataset.tooltip = page.label;
            lien.setAttribute("data-i18n-tooltip", page.cle);

            if (page.role) {
                lien.dataset.role = page.role;
            }

            lien.innerHTML =
                ICONES[page.icone] +
                `<span data-i18n="${page.cle}">${page.label}</span>`;

            if (page.href === actuelle) {
                lien.setAttribute("aria-current", "page");
            }

            conteneur.appendChild(lien);
        });

        nav.appendChild(boutonReduire);
        nav.appendChild(conteneur);

        entete.insertAdjacentElement("afterend", nav);
    }

    construireNav();

    /*
     * Aligne dynamiquement le haut de la barre latérale sur la vraie
     * hauteur du header, plutôt qu'une valeur fixe devinée — évite le
     * chevauchement si le header change de hauteur (nouveaux boutons,
     * texte plus long, etc.).
     */
    function ajusterPositionNav() {

        const header = document.querySelector(".topbar");
        const barre = document.querySelector(".nav-principal");

        if (!header || !barre) {
            return;
        }

        const hauteur = header.offsetHeight;

        barre.style.top = hauteur + "px";
    }

    ajusterPositionNav();

    window.addEventListener("resize", ajusterPositionNav);

    if (window.ResizeObserver) {

        const observateurHeader =
            new ResizeObserver(ajusterPositionNav);

        const header = document.querySelector(".topbar");

        if (header) {
            observateurHeader.observe(header);
        }
    }

})();