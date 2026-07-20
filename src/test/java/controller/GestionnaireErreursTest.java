package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import exception.AccesDonneesException;
import service.MedicamentService;

import java.sql.SQLException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GestionnaireErreursTest {

    @Mock
    private MedicamentService medicamentService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MedicamentController(medicamentService))
                .setControllerAdvice(new GestionnaireErreurs())
                .build();
    }


    @Test
    void accesDonneesException_doitRetourner500AvecMessagePropre() throws Exception {
        when(medicamentService.getStock(1)).thenThrow(
                new AccesDonneesException(
                        "Échec de la récupération du stock",
                        new SQLException("Connexion refusée")));

        mockMvc.perform(get("/api/medicaments/1/stock"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("Erreur interne : opération impossible sur la base de données"));
    }
}