package de.htwg.in.schneider.glassfix.backend.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/anfrage")
public class AnfrageController {
    @GetMapping
    public List<String> getAllAnfragen() {
        return List.of(
                "Anfrage 1",
                "Anfrage 2"
        );
    }
}