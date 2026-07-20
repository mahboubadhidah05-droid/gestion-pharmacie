package controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dto.MedicamentResponse;
import service.MedicamentService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MedicamentControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private MedicamentService medicamentService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MedicamentController(medicamentService))
                .build();
    }


    @Test
    void ajouterMedicament_doitRetourner201() throws Exception {
        mockMvc.perform(post("/api/medicaments")
                        .contentType(CONTENT_TYPE)
                        .content("{\"nom\":\"Doliprane\",\"dosage\":\"500mg\","
                                + "\"stock\":100,\"prix\":3.5,\"seuil\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Médicament créé avec succès"));
    }


    @Test
    void consulterStock_medicamentExiste_doitRetourner200EtLeStock() throws Exception {
        when(medicamentService.getStock(1)).thenReturn(4);

        mockMvc.perform(get("/api/medicaments/1/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stock").value(4));
    }


    @Test
    void consulterStock_medicamentIntrouvable_doitRetourner404() throws Exception {
        when(medicamentService.getStock(999)).thenReturn(-1);

        mockMvc.perform(get("/api/medicaments/999/stock"))
                .andExpect(status().isNotFound());
    }


    @Test
    void mettreAJourStock_doitRetourner200() throws Exception {
        mockMvc.perform(put("/api/medicaments/1/stock")
                        .contentType(CONTENT_TYPE)
                        .content("{\"quantite\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stock mis à jour avec succès"));
    }


    @Test
    void verifierStockCritique_stockCritique_doitRetournerLAlerte() throws Exception {
        when(medicamentService.stockCritique(1))
                .thenReturn("Médicament en stock critique : Paracetamol (ID 1) | Stock actuel = 2");

        mockMvc.perform(get("/api/medicaments/1/stock-critique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.critique").value(true));
    }


    @Test
    void verifierStockCritique_stockNormal_doitRetournerFalse() throws Exception {
        when(medicamentService.stockCritique(2)).thenReturn(null);

        mockMvc.perform(get("/api/medicaments/2/stock-critique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.critique").value(false))
                .andExpect(jsonPath("$.message").value("Stock normal"));
    }


    @Test
    void listerMedicaments_doitRetourner200EtLaListe() throws Exception {
        when(medicamentService.listerMedicaments()).thenReturn(
                List.of(new MedicamentResponse(1, "Paracetamol", "500mg", 20, 2.5, 5)));

        mockMvc.perform(get("/api/medicaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nom").value("Paracetamol"));
    }
}