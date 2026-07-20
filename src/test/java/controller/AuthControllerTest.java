package controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import main.PharmacieApplication;
import service.AuthService;

@SpringBootTest(
        classes = PharmacieApplication.class
)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void loginValideDoitRetourner200()
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
                                "application/json"
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
    void loginInvalideDoitRetourner401()
            throws Exception {

        when(
                authService.login(
                        "pharma",
                        "wrong"
                )
        ).thenReturn("ECHEC");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(
                                "application/json"
                        )
                        .content(
                                """
                                {
                                    "login": "pharma",
                                    "pwd": "wrong"
                                }
                                """
                        )
        )
        .andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void meSansSessionDoitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/api/auth/me")
        )
        .andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void meAvecSessionDoitRetourner200()
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

    @Test
    void logoutDoitRetourner204()
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
                post("/api/auth/logout")
                        .session(session)
        )
        .andExpect(
                status().isNoContent()
        );
    }
}