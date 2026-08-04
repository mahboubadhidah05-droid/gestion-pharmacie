package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import service.ClientService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private ClientService clientService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ClientController(clientService))
                .build();
    }

    @Test
    void creerClient_reussi_doitRetourner201() throws Exception {

        when(
                clientService.creerClient(
                        "Ben Ali", "Sami", "sami@exemple.com",
                        "Rue de la Paix", "CNAM12345", "12345678"
                )
        ).thenReturn(3);

        mockMvc.perform(post("/api/clients")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Ben Ali\",\"prenom\":\"Sami\","
                                + "\"email\":\"sami@exemple.com\","
                                + "\"adresse\":\"Rue de la Paix\","
                                + "\"numeroCnam\":\"CNAM12345\","
                                + "\"cin\":\"12345678\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.message")
                        .value("Client créé avec succès"));
    }

    @Test
    void creerClient_sansNumeroCnamNiCin_reussi_doitRetourner201() throws Exception {

        when(
                clientService.creerClient(
                        "Nom", "Prenom", "email@exemple.com",
                        "Adresse", null, null
                )
        ).thenReturn(5);

        mockMvc.perform(post("/api/clients")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Nom\",\"prenom\":\"Prenom\","
                                + "\"email\":\"email@exemple.com\","
                                + "\"adresse\":\"Adresse\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void creerClient_echec_doitRetourner500() throws Exception {

        when(
                clientService.creerClient(
                        "Nom", "Prenom", "email@exemple.com",
                        "Adresse", null, null
                )
        ).thenReturn(-1);

        mockMvc.perform(post("/api/clients")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Nom\",\"prenom\":\"Prenom\","
                                + "\"email\":\"email@exemple.com\","
                                + "\"adresse\":\"Adresse\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Erreur lors de la création du client"));
    }

    @Test
    void existeClient_existant_doitRetournerTrue() throws Exception {

        when(clientService.existeClient(1)).thenReturn(true);

        mockMvc.perform(get("/api/clients/1/existe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(true));
    }

    @Test
    void existeClient_inexistant_doitRetournerFalse() throws Exception {

        when(clientService.existeClient(999)).thenReturn(false);

        mockMvc.perform(get("/api/clients/999/existe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(false));
    }
}