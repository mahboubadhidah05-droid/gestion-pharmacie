/* Page de connexion : vérifie les identifiants via l'API
   puis redirige vers l'application. */

const formLogin = document.getElementById("formLogin");
const erreurLogin = document.getElementById("erreurLogin");


/* Si l'utilisateur est déjà connecté, inutile de rester ici. */
fetch("/api/auth/me")
    .then((reponse) => {
        if (reponse.ok) {
            window.location.href = "index.html";
        }
    })
    .catch(() => {
        /* API injoignable : on reste sur la page de connexion. */
    });


formLogin.addEventListener("submit", (evenement) => {
    evenement.preventDefault();

    const donnees = new FormData(formLogin);

    fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            login: donnees.get("login"),
            pwd: donnees.get("pwd")
        })
    })
        .then((reponse) => {
            if (reponse.ok) {
                window.location.href = "index.html";
                return;
            }

            if (reponse.status === 401) {
                erreurLogin.textContent =
                    "Identifiants incorrects. Veuillez réessayer.";
            } else {
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
});		package controller;

		import org.junit.jupiter.api.BeforeEach;
		import org.junit.jupiter.api.Test;
		import org.junit.jupiter.api.extension.ExtendWith;
		import org.mockito.Mock;
		import org.mockito.junit.jupiter.MockitoExtension;
		import org.springframework.test.web.servlet.MockMvc;
		import org.springframework.test.web.servlet.setup.MockMvcBuilders;

		import service.AuthService;

		import static org.mockito.Mockito.when;
		import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
		import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
		import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
		import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

		@ExtendWith(MockitoExtension.class)
		class AuthControllerTest {

		    private static final String CONTENT_TYPE = "application/json";

		    @Mock
		    private AuthService authService;

		    private MockMvc mockMvc;


		    @BeforeEach
		    void setUp() {
		        mockMvc = MockMvcBuilders
		                .standaloneSetup(new AuthController(authService))
		                .build();
		    }


		    @Test
		    void login_identifiantsValides_doitRetourner200EtLeRole() throws Exception {
		        when(authService.login("pharma", "123")).thenReturn("PHARMACIEN");

		        mockMvc.perform(post("/api/auth/login")
		                        .contentType(CONTENT_TYPE)
		                        .content("{\"login\":\"pharma\",\"pwd\":\"123\"}"))
		                .andExpect(status().isOk())
		                .andExpect(jsonPath("$.login").value("pharma"))
		                .andExpect(jsonPath("$.role").value("PHARMACIEN"));
		    }


		    @Test
		    void login_identifiantsInvalides_doitRetourner401() throws Exception {
		        when(authService.login("pharma", "faux")).thenReturn("ECHEC");

		        mockMvc.perform(post("/api/auth/login")
		                        .contentType(CONTENT_TYPE)
		                        .content("{\"login\":\"pharma\",\"pwd\":\"faux\"}"))
		                .andExpect(status().isUnauthorized());
		    }


		    @Test
		    void me_sansSession_doitRetourner401() throws Exception {
		        mockMvc.perform(get("/api/auth/me"))
		                .andExpect(status().isUnauthorized());
		    }


		    @Test
		    void me_avecSession_doitRetournerLUtilisateur() throws Exception {
		        when(authService.login("gest", "133")).thenReturn("GESTIONNAIRE");

		        var session = mockMvc.perform(post("/api/auth/login")
		                        .contentType(CONTENT_TYPE)
		                        .content("{\"login\":\"gest\",\"pwd\":\"133\"}"))
		                .andExpect(status().isOk())
		                .andReturn()
		                .getRequest()
		                .getSession();

		        mockMvc.perform(get("/api/auth/me")
		                        .session((org.springframework.mock.web.MockHttpSession) session))
		                .andExpect(status().isOk())
		                .andExpect(jsonPath("$.login").value("gest"))
		                .andExpect(jsonPath("$.role").value("GESTIONNAIRE"));
		    }


		    @Test
		    void logout_doitRetourner204() throws Exception {
		        mockMvc.perform(post("/api/auth/logout"))
		                .andExpect(status().isNoContent());
		    }
		}