package com.tradingplatform.surveillance_service.repository;

import com.tradingplatform.surveillance_service.model.SurveillanceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SurveillanceAlertRepository extends JpaRepository<SurveillanceAlert, UUID> {

    List<SurveillanceAlert> findTop50ByOrderByCreatedAtDesc();
}
