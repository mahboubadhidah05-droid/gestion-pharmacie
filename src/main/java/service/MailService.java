package service;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import dto.MedicamentResponse;

public class MailService {

    private final JavaMailSender mailSender;
    private final String expediteur;

    public MailService(JavaMailSender mailSender, String expediteur) {
        this.mailSender = mailSender;
        this.expediteur = expediteur;
    }

    public void envoyerAlerteStockCritique(
            List<String> destinataires,
            List<MedicamentResponse> critiques) {

        if (destinataires.isEmpty() || critiques.isEmpty()) {
            return;
        }

        StringBuilder corps = new StringBuilder();

        corps.append("Résumé quotidien des stocks critiques :\n\n");

        for (MedicamentResponse m : critiques) {

            corps.append("- ")
                    .append(m.nom())
                    .append(" (")
                    .append(m.dosage())
                    .append(") : ")
                    .append(m.stock())
                    .append(" unité(s) restante(s) — seuil critique : ")
                    .append(m.seuilCritique())
                    .append("\n");
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(expediteur);
        message.setTo(destinataires.toArray(new String[0]));
        message.setSubject(
                "Pharmacie — Alerte stock critique ("
                        + critiques.size()
                        + " médicament(s))"
        );
        message.setText(corps.toString());

        mailSender.send(message);
    }
}