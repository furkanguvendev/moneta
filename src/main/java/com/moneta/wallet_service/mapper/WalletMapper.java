package com.moneta.wallet_service.mapper;

import com.moneta.wallet_service.dto.request.WalletRequest;
import com.moneta.wallet_service.dto.response.WalletResponse;
import com.moneta.wallet_service.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public Wallet toEntity(WalletRequest request) {
        if (request == null) return null;

        Wallet wallet = new Wallet();
        wallet.setName(request.name());
        wallet.setBalance(request.balance());
        wallet.setCurrency(request.currency());
        return wallet;
    }

    public WalletResponse toResponse(Wallet entity) {
        if (entity == null) return null;

        String ownerName = entity.getUser() != null ? entity.getUser().getUserName() : null;

        return new WalletResponse(
                entity.getId(),
                entity.getName(),
                entity.getBalance(),
                entity.getCurrency(),
                ownerName,
                entity.getCreatedAt()
        );
    }
}