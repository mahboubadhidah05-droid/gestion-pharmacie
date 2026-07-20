package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dao.StockHistoriqueDAO;
import dto.StockHistoriqueResponse;

class StockHistoriqueControllerTest {

    private StockHistoriqueDAO stockHistoriqueDAO;

    private StockHistoriqueController controller;

    @BeforeEach
    void setUp() {

        stockHistoriqueDAO =
                mock(StockHistoriqueDAO.class);

        controller =
                new StockHistoriqueController(
                        stockHistoriqueDAO
                );
    }

    @Test
    void doitRetournerHistoriqueAvecStatutOk() {

        StockHistoriqueResponse historique =
                new StockHistoriqueResponse(
                        1,
                        50,
                        Timestamp.valueOf(
                                LocalDateTime.of(
                                        2026,
                                        7,
                                        20,
                                        10,
                                        30
                                )
                        )
                );

        List<StockHistoriqueResponse> historiqueList =
                List.of(historique);

        when(stockHistoriqueDAO.afficherHistorique())
                .thenReturn(historiqueList);

        ResponseEntity<List<StockHistoriqueResponse>> response =
                controller.historique();

        assertNotNull(response);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                historiqueList,
                response.getBody()
        );

        verify(
                stockHistoriqueDAO
        ).afficherHistorique();
    }
}