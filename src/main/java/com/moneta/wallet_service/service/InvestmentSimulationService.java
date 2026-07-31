package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.SimulationCloseRequest;
import com.moneta.wallet_service.dto.request.SimulationRequest;
import com.moneta.wallet_service.entity.InvestmentSimulation;
import java.util.List;

public interface InvestmentSimulationService {
    List<InvestmentSimulation> getActiveSimulations(Long userId);
    InvestmentSimulation createSimulation(Long userId, SimulationRequest request);
    InvestmentSimulation closeOrCancelSimulation(Long id, Long userId, SimulationCloseRequest request);
    void deleteSimulationById(Long id);
}