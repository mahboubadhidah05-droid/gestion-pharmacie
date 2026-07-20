package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import service.CommandeService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommandeControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private CommandeService commandeService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CommandeController(commandeService))
                .build();
    }


    @Test
    void creerCommande_reussie_doitRetourner201() throws Exception {
        when(commandeService.creerCommande(1, 2, 50)).thenReturn(true);

        mockMvc.perform(post("/api/commandes")
                        .contentType(CONTENT_TYPE)
                        .content("{\"idGestionnaire\":1,\"idMedicament\":2,\"quantite\":50}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Commande créée, stock mis à jour"));
    }


    @Test
    void creerCommande_medicamentIntrouvable_doitRetourner404() throws Exception {
        when(commandeService.creerCommande(1, 9999, 50)).thenReturn(false);

        mockMvc.perform(post("/api/commandes")
                        .contentType(CONTENT_TYPE)
                        .content("{\"idGestionnaire\":1,\"idMedicament\":9999,\"quantite\":50}"))
                .andExpect(status().isNotFound());
    }
}
