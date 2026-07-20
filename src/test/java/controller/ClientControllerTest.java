package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import service.ClientService;

import static org.mockito.ArgumentMatchers.anyString;
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
    void creerClient_reussi_doitRetourner201EtLId() throws Exception {
        when(clientService.creerClient("Testeur", "Web", "test@mail.com", "Tunis"))
                .thenReturn(12);

        mockMvc.perform(post("/api/clients")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Testeur\",\"prenom\":\"Web\","
                                + "\"email\":\"test@mail.com\",\"adresse\":\"Tunis\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(12));
    }


    @Test
    void creerClient_echec_doitRetourner500() throws Exception {
        when(clientService.creerClient(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(-1);

        mockMvc.perform(post("/api/clients")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"X\",\"prenom\":\"Y\","
                                + "\"email\":\"z@mail.com\",\"adresse\":\"T\"}"))
                .andExpect(status().isInternalServerError());
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
        when(clientService.existeClient(9999)).thenReturn(false);

        mockMvc.perform(get("/api/clients/9999/existe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(false));
    }
}