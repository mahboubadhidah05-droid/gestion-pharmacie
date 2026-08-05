package config;

import org.springframework.scheduling.annotation.Scheduled;

import service.AlerteStockService;

public class StockAlertScheduler {

    private final AlerteStockService alerteStockService;

    public StockAlertScheduler(AlerteStockService alerteStockService) {
        this.alerteStockService = alerteStockService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verifierStocksCritiques() {
        alerteStockService.verifierEtEnvoyer();
    }
}