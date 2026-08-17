package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.SimulationCloseRequest;
import com.moneta.wallet_service.dto.request.SimulationRequest;
import com.moneta.wallet_service.dto.response.SimulationResponse;
import com.moneta.wallet_service.entity.InvestmentSimulation;

import java.util.List;

public interface InvestmentSimulationService {
    List<SimulationResponse> getActiveSimulations(Long userId);
    SimulationResponse createSimulation(Long userId, SimulationRequest request);
    SimulationResponse closeOrCancelSimulation(Long id, Long userId, SimulationCloseRequest request);
    InvestmentSimulation getSimulationEntityById(Long id);
    void deleteSimulationById(Long id);
}