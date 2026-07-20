package controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import service.UserService;

@SpringBootTest(
        classes = PharmacieApplication.class
)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void profilSansSessionDoitRetourner401()
            throws Exception {

        mockMvc.perform(
                get("/api/utilisateurs/profil")
        )
        .andExpect(
                status().isUnauthorized()
        );
    }

    @Test
    void profilInexistantDoitRetourner404()
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

        when(
                userService.getProfil("pharma")
        ).thenReturn(
                new String[0]
        );

        mockMvc.perform(
                get("/api/utilisateurs/profil")
                        .session(session)
        )
        .andExpect(
                status().isNotFound()
        );
    }

    @Test
    void profilExistantDoitRetourner200()
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

        when(
                userService.getProfil("pharma")
        ).thenReturn(
                new String[]{
                        "Dupont",
                        "Jean"
                }
        );

        mockMvc.perform(
                get("/api/utilisateurs/profil")
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
                jsonPath("$.nom")
                        .value("Dupont")
        )
        .andExpect(
                jsonPath("$.prenom")
                        .value("Jean")
        )
        .andExpect(
                jsonPath("$.role")
                        .value("PHARMACIEN")
        );
    }
}