package com.tradingplatform.positionservice.repository;

import com.tradingplatform.positionservice.model.ProcessedTradeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedTradeRepository extends JpaRepository<ProcessedTradeEvent, UUID> {


}
