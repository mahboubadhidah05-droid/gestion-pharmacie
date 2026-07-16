package controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dao.StockHistoriqueDAO;
import dto.StockHistoriqueResponse;

@RestController
@RequestMapping("/api/stock")
public class StockHistoriqueController {

    private final StockHistoriqueDAO stockHistoriqueDAO;

    public StockHistoriqueController(StockHistoriqueDAO stockHistoriqueDAO) {
        this.stockHistoriqueDAO = stockHistoriqueDAO;
    }

    @GetMapping("/historique")
    public ResponseEntity<List<StockHistoriqueResponse>> historique() {
        return ResponseEntity.ok(stockHistoriqueDAO.afficherHistorique());
    }
}