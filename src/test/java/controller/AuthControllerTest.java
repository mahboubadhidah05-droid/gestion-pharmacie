package controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import service.AuthService;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void loginDoitRetourner200SiIdentifiantsValides()
            throws Exception {

        when(
                authService.login(
                        "pharma",
                        "123"
                )
        ).thenReturn("PHARMACIEN");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                """
                                {
                                    "login": "pharma",
                                    "pwd": "123"
                                }
                                """
                        )
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$.login")
                        .value("pharma")
        )
        .andExpect(
                jsonPath("$.role")
                        .value("PHARMACIEN")
        );
    }

    @Test
    void loginDoitRetourner401SiIdentifiantsInvalides()
            throws Exception {

        when(
                authService.login(
                        "pharma",
                        "incorrect"
                )
        ).thenReturn("ECHEC");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                """
                                {
                                    "login": "pharma",
                                    "pwd": "incorrect"
                                }
                                """
                        )
        )
        .andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void logoutDoitRetourner204AvecSession()
            throws Exception {

        MockHttpSession session =
                new MockHttpSession();

        session.setAttribute(
                AuthController.ATTR_LOGIN,
                "pharma"
        );

        mockMvc.perform(
                post("/api/auth/logout")
                        .session(session)
        )
        .andExpect(
                status().isNoContent()
        );
    }

    @Test
    void logoutDoitRetourner204SansSession()
            throws Exception {

        mockMvc.perform(
                post("/api/auth/logout")
        )
        .andExpect(
                status().isNoContent()
        );
    }

    @Test
    void meDoitRetourner401SansSession()
            throws Exception {

        mockMvc.perform(
                get("/api/auth/me")
        )
        .andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void meDoitRetournerUtilisateurConnecte()
            throws Exception {

        MockHttpSession session =
                new MockHttpSession();

        session.setAttribute(
                AuthController.ATTR_LOGIN,
                "pharma"
        );

        session.setAttribute(
                AuthController.ATTR_ROLE,
                "PHARMACIEN"
        );

        mockMvc.perform(
                get("/api/auth/me")
                        .session(session)
        )
        .andExpect(
                status().isOk()
        )
        .andExpect(
                jsonPath("$.login")
                        .value("pharma")
        )
        .andExpect(
                jsonPath("$.role")
                        .value("PHARMACIEN")
        );
    }
}