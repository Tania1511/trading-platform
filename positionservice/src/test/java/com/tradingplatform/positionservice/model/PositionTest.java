package com.tradingplatform.positionservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class PositionTest {

    private Position position;

    @BeforeEach
    void setup(){
        position = new Position("trader-1","AAPL");
    }

    @Test
    void freshPosition_startsFlatWithZeroEverything(){

        assertThat(position.getQuantity()).isEqualByComparingTo("0");
        assertThat(position.getAverageCost()).isEqualByComparingTo("0");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("0");
    }

    @Test
    void firstBuy_OpensLongPosition_atFillPrice(){
        position.applyFill(true,new BigDecimal("10"), new BigDecimal("150.00"));
        assertThat(position.getQuantity()).isEqualByComparingTo("10");
        assertThat(position.getAverageCost()).isEqualByComparingTo("150.00");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("0");
    }

    @Test
    void secondBuyAtDifferentPrice_recomputeWeightedAverageCost(){
        position.applyFill(true,new BigDecimal("10"), new BigDecimal("150.00"));
        position.applyFill(true,new BigDecimal("5"), new BigDecimal("160.00"));
        assertThat(position.getQuantity()).isEqualByComparingTo("15");
        assertThat(position.getAverageCost()).isEqualByComparingTo("153.3333");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("0");
    }

    @Test
    void partialSell_reduceQuantity_avgCostUnchanged_realizesProfit(){
        position.applyFill(true,new BigDecimal("10"), new BigDecimal("150.00"));
        position.applyFill(false,new BigDecimal("4"), new BigDecimal("160.00"));
        assertThat(position.getQuantity()).isEqualByComparingTo("6");
        assertThat(position.getAverageCost()).isEqualByComparingTo("150.00");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("40.00");
    }

    @Test
    void partialSell_atLossBelowAvgCost_realizesNegativePnl(){
        position.applyFill(true,new BigDecimal("10"), new BigDecimal("150.00"));
        position.applyFill(false,new BigDecimal("4"), new BigDecimal("140.00"));
        assertThat(position.getQuantity()).isEqualByComparingTo("6");
        assertThat(position.getAverageCost()).isEqualByComparingTo("150.00");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("-40.00");
    }

    @Test
    void exactFullClose_bringsPositonToExactlyFlat_avgCostResetsToZero(){
        position.applyFill(true,new BigDecimal("10"), new BigDecimal("150.00"));
        position.applyFill(false,new BigDecimal("10"), new BigDecimal("155.00"));
        assertThat(position.getQuantity()).isEqualByComparingTo("0");
        assertThat(position.getAverageCost()).isEqualByComparingTo("0");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("50.00");
    }

    @Test
    void sellLargerThanCurrentLong_flipsToShort_withFreshCostBasisOnTheFlippedPortion(){
        position.applyFill(true,new BigDecimal("10"), new BigDecimal("150.00"));
        position.applyFill(false,new BigDecimal("15"), new BigDecimal("155.00"));
        assertThat(position.getQuantity()).isEqualByComparingTo("-5");
        assertThat(position.getAverageCost()).isEqualByComparingTo("155.00");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("50.00");
    }

    @Test
    void shortPosition_buyBackAtLowerPrice_realizesProfit(){
        position.applyFill(false,new BigDecimal("10"), new BigDecimal("150.00"));
        assertThat(position.getQuantity()).isEqualByComparingTo("-10");
        assertThat(position.getAverageCost()).isEqualByComparingTo("150.00");

        position.applyFill(true,new BigDecimal("10"), new BigDecimal("140.00"));

        assertThat(position.getQuantity()).isEqualByComparingTo("0");
        assertThat(position.getAverageCost()).isEqualByComparingTo("0");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("100.00");
    }

    @Test
    void addingToExistingShort_recomputeWeightedAverageCost(){
        position.applyFill(false,new BigDecimal("10"), new BigDecimal("150.00"));
        position.applyFill(false,new BigDecimal("5"), new BigDecimal("140.00"));
        assertThat(position.getQuantity()).isEqualByComparingTo("-15");
        assertThat(position.getAverageCost()).isEqualByComparingTo("146.6667");
        assertThat(position.getRealizedPnl()).isEqualByComparingTo("0");
    }
}
