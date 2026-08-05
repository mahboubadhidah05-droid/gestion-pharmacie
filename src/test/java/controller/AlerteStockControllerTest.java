package controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import service.AlerteStockService;

class AlerteStockControllerTest {

    private AlerteStockService alerteStockService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        alerteStockService = mock(AlerteStockService.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AlerteStockController(alerteStockService))
                .build();
    }

    @Test
    void testerEmail_avecCritiques_doitConfirmerEnvoi() throws Exception {

        when(alerteStockService.verifierEtEnvoyer()).thenReturn(3);

        mockMvc.perform(post("/api/alertes/tester-email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Email envoyé pour 3 médicament(s) en stock critique."));
    }

    @Test
    void testerEmail_sansCritiques_doitDireQueRienNaEteEnvoye() throws Exception {

        when(alerteStockService.verifierEtEnvoyer()).thenReturn(0);

        mockMvc.perform(post("/api/alertes/tester-email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Aucun médicament en stock critique actuellement — aucun email envoyé."));
    }
}