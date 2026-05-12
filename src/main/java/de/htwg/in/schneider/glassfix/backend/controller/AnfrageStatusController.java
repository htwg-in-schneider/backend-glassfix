package de.htwg.in.schneider.glassfix.backend.controller;

import org.springframework.web.bind.annotation.*;
import de.htwg.in.schneider.glassfix.backend.model.AnfrageStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anfrage-status")
public class AnfrageStatusController {
    @GetMapping
    public List<AnfrageStatus> getAnfrageStatus() {
        return Arrays.asList(AnfrageStatus.values());
    }

}
