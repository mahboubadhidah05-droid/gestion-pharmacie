package controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dto.NomMedicamentResponse;
import service.MedicamentService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NomsMedicamentsControllerTest {

    @Mock
    private MedicamentService medicamentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new NomsMedicamentsController(medicamentService))
                .build();
    }

    @Test
    void listerNoms_doitRetourner200EtLaListe() throws Exception {

        when(medicamentService.listerNoms()).thenReturn(
                List.of(new NomMedicamentResponse("Paracetamol", "500mg"))
        );

        mockMvc.perform(get("/api/noms-medicaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Paracetamol"))
                .andExpect(jsonPath("$[0].dosage").value("500mg"));
    }
}