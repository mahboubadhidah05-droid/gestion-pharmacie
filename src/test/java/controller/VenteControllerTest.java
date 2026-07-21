package controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dto.VenteResponse;
import service.VenteService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VenteControllerTest {

    private static final String CONTENT_TYPE = "application/json";

    @Mock
    private VenteService venteService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new VenteController(venteService))
                .build();
    }


    @Test
    void enregistrerVente_reussie_doitRetourner201() throws Exception {
        when(venteService.vendre(1, 2, 3, 4)).thenReturn(true);

        mockMvc.perform(post("/api/ventes")
                        .contentType(CONTENT_TYPE)
                        .content("{\"idPharmacien\":1,\"idClient\":2,"
                                + "\"idMedicament\":3,\"quantite\":4}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Vente enregistrée avec succès"));
    }


    @Test
    void enregistrerVente_stockInsuffisant_doitRetourner409() throws Exception {
        when(venteService.vendre(1, 2, 3, 9999)).thenReturn(false);

        mockMvc.perform(post("/api/ventes")
                        .contentType(CONTENT_TYPE)
                        .content("{\"idPharmacien\":1,\"idClient\":2,"
                                + "\"idMedicament\":3,\"quantite\":9999}"))
                .andExpect(status().isConflict());
    }


    @Test
    void annulerVente_existante_doitRetourner200() throws Exception {
        when(venteService.annulerVente(7)).thenReturn(true);

        mockMvc.perform(delete("/api/ventes/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vente annulée avec succès"));
    }


    @Test
    void annulerVente_inexistante_doitRetourner404() throws Exception {
        when(venteService.annulerVente(999)).thenReturn(false);

        mockMvc.perform(delete("/api/ventes/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void ventesParMedicament_doitRetournerLaListe() throws Exception {
        when(venteService.ventesParNomMedicament("Amoxicilline")).thenReturn(
                List.of(new VenteResponse(6, 1, 6, 2, 15,
                        LocalDateTime.of(2026, 7, 15, 0, 0))));

        mockMvc.perform(get("/api/ventes").param("medicament", "Amoxicilline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(6))
                .andExpect(jsonPath("$[0].idMedicament").value(2));
    }


    @Test
    void ventesParClient_doitRetournerLaListe() throws Exception {
        when(venteService.ventesParClient(6)).thenReturn(List.of());

        mockMvc.perform(get("/api/ventes").param("client", "6"))
                .andExpect(status().isOk());
    }


    @Test
    void ventesParNomClient_doitRetournerLaListe() throws Exception {
        when(venteService.ventesParNomClient("Ben Ali", "Sami")).thenReturn(
                List.of(new VenteResponse(9, 1, 4, 2, 3,
                        LocalDateTime.of(2026, 7, 18, 0, 0))));

        mockMvc.perform(get("/api/ventes")
                        .param("clientNom", "Ben Ali")
                        .param("clientPrenom", "Sami"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9));
    }


    @Test
    void ventesParPeriode_doitRetournerLaListe() throws Exception {
        when(venteService.ventesParPeriode("2026-07-01", "2026-07-18"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/ventes")
                        .param("debut", "2026-07-01")
                        .param("fin", "2026-07-18"))
                .andExpect(status().isOk());
    }
}