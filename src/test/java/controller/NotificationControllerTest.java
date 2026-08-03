package controller;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import config.StockAlertScheduler;
import dao.NotificationDAO;
import dto.NotificationResponse;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationDAO notificationDAO;

    @Mock
    private StockAlertScheduler stockAlertScheduler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new NotificationController(
                                notificationDAO,
                                stockAlertScheduler
                        )
                )
                .build();
    }

    @Test
    void lister_doitRetourner200EtLaListe() throws Exception {

        when(notificationDAO.lister()).thenReturn(
                List.of(new NotificationResponse(
                        1,
                        "Médicament en stock critique : Paracetamol",
                        Timestamp.valueOf("2026-07-23 10:00:00"),
                        false
                ))
        );

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].message")
                        .value("Médicament en stock critique : Paracetamol"));
    }

    @Test
    void declencherVerificationManuelle_doitRetourner200EtLeMessage() throws Exception {

        when(stockAlertScheduler.executerVerification()).thenReturn(3);

        mockMvc.perform(post("/api/notifications/verifier-stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Vérification effectuée : 3 médicament(s) en stock critique."));
    }
}