package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dao.CnamRapportDAO;
import dto.CnamRapportResponse;

@RestController
@RequestMapping("/api/cnam")
public class CnamController {

    private final CnamRapportDAO cnamRapportDAO;

    public CnamController(CnamRapportDAO cnamRapportDAO) {
        this.cnamRapportDAO = cnamRapportDAO;
    }

    @GetMapping("/rapport")
    public ResponseEntity<CnamRapportResponse> genererRapport(
            @RequestParam String debut,
            @RequestParam String fin) {

        return ResponseEntity.ok(
                cnamRapportDAO.genererRapport(debut, fin)
        );
    }
}