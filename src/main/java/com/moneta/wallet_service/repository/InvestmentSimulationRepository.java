package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.InvestmentSimulation;
import com.moneta.wallet_service.enums.SimulationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvestmentSimulationRepository extends JpaRepository<InvestmentSimulation, Long> {
    List<InvestmentSimulation> findByUserIdAndStatus(Long userId, SimulationStatus status);
}