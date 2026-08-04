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
                                + "\"stock\":100,\"prix\":3.5,\"seuil\":10,"
                                + "\"datePeremption\":\"2026-12-31\"}"))
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
                List.of(new MedicamentResponse(
                        1, "Paracetamol", "500mg", 20, 2.5, 5,
                        "2026-12-31", 0, true, 0.7, "1234567890123"
                )));

        mockMvc.perform(get("/api/medicaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nom").value("Paracetamol"));
    }

    @Test
    void retirerStockPerime_reussi_doitRetourner200() throws Exception {

        when(medicamentService.retirerStockPerime("Paracetamol", "500mg"))
                .thenReturn(30);

        mockMvc.perform(put("/api/medicaments/stock-perime")
                        .param("nom", "Paracetamol")
                        .param("dosage", "500mg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Stock périmé retiré : 30 unité(s)."));
    }

    @Test
    void retirerStockPerime_introuvable_doitRetourner404() throws Exception {

        when(medicamentService.retirerStockPerime("Inconnu", "1g"))
                .thenReturn(-1);

        mockMvc.perform(put("/api/medicaments/stock-perime")
                        .param("nom", "Inconnu")
                        .param("dosage", "1g"))
                .andExpect(status().isNotFound());
    }

    @Test
    void retirerStockPerime_pasEncorePerime_doitRetourner400() throws Exception {

        when(medicamentService.retirerStockPerime("Paracetamol", "500mg"))
                .thenReturn(-2);

        mockMvc.perform(put("/api/medicaments/stock-perime")
                        .param("nom", "Paracetamol")
                        .param("dosage", "500mg"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifierMedicament_trouve_doitRetourner200() throws Exception {

        when(medicamentService.verifier("antafen", "100mg")).thenReturn(
                new dto.VerifierMedicamentResponse(
                        4, "antafen", "100mg", 160,
                        false, 0, "2027-02-24", false
                )
        );

        mockMvc.perform(get("/api/medicaments/verifier")
                        .param("nom", "antafen")
                        .param("dosage", "100mg"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("antafen"))
                .andExpect(jsonPath("$.stockCritique").value(false));
    }

    @Test
    void verifierMedicament_introuvable_doitRetourner404() throws Exception {

        when(medicamentService.verifier("Inconnu", "1g")).thenReturn(null);

        mockMvc.perform(get("/api/medicaments/verifier")
                        .param("nom", "Inconnu")
                        .param("dosage", "1g"))
                .andExpect(status().isNotFound());
    }

    @Test
    void scannerCodeBarre_trouve_doitRetourner200() throws Exception {

        when(medicamentService.getIdParCodeBarre("1234567890123"))
                .thenReturn(4);

        when(medicamentService.getResumeParId(4)).thenReturn(
                new dto.MedicamentScanResponse(4, "antafen", "100mg", 160)
        );

        mockMvc.perform(get("/api/medicaments/code-barre/1234567890123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("antafen"))
                .andExpect(jsonPath("$.stock").value(160));
    }

    @Test
    void scannerCodeBarre_introuvable_doitRetourner404() throws Exception {

        when(medicamentService.getIdParCodeBarre("0000000000000"))
                .thenReturn(-1);

        mockMvc.perform(get("/api/medicaments/code-barre/0000000000000"))
                .andExpect(status().isNotFound());
    }
}