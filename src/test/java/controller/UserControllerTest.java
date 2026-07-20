package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import service.UserService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(userService))
                .build();
    }


    private MockHttpSession sessionConnectee(String login, String role) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthController.ATTR_LOGIN, login);
        session.setAttribute(AuthController.ATTR_ROLE, role);
        return session;
    }


    @Test
    void profil_connecte_doitRetournerLeProfil() throws Exception {
        when(userService.getProfil("pharma"))
                .thenReturn(new String[]{"Ben Salah", "Ali"});

        mockMvc.perform(get("/api/utilisateurs/profil")
                        .session(sessionConnectee("pharma", "PHARMACIEN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("pharma"))
                .andExpect(jsonPath("$.nom").value("Ben Salah"))
                .andExpect(jsonPath("$.prenom").value("Ali"))
                .andExpect(jsonPath("$.role").value("PHARMACIEN"));
    }


    @Test
    void profil_sansSession_doitRetourner401() throws Exception {
        mockMvc.perform(get("/api/utilisateurs/profil"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void profil_utilisateurIntrouvable_doitRetourner404() throws Exception {
        when(userService.getProfil("fantome")).thenReturn(new String[0]);

        mockMvc.perform(get("/api/utilisateurs/profil")
                        .session(sessionConnectee("fantome", "PHARMACIEN")))
                .andExpect(status().isNotFound());
    }
}