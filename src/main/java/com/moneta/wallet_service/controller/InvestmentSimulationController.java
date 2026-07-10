package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.SimulationCloseRequest;
import com.moneta.wallet_service.dto.request.SimulationRequest;
import com.moneta.wallet_service.entity.InvestmentSimulation;
import com.moneta.wallet_service.service.InvestmentSimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simulations")
@RequiredArgsConstructor
public class InvestmentSimulationController {

    private final InvestmentSimulationService simulationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvestmentSimulation>> getActiveSimulations(@PathVariable Long userId) {
        return ResponseEntity.ok(simulationService.getActiveSimulations(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<InvestmentSimulation> createSimulation(
            @PathVariable Long userId,
            @RequestBody SimulationRequest request) {
        return ResponseEntity.ok(simulationService.createSimulation(userId, request));
    }

    @PutMapping("/{id}/close/user/{userId}")
    public ResponseEntity<InvestmentSimulation> closeSimulation(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestBody SimulationCloseRequest request) {
        return ResponseEntity.ok(simulationService.closeOrCancelSimulation(id, userId, request));
    }
}