package com.tradingplatform.positionservice.repository;

import com.tradingplatform.positionservice.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    Optional<Position> findByAccountKeyAndSymbol(String accountKey, String symbol);
}
