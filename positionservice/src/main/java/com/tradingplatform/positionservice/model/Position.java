package com.tradingplatform.positionservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_key", nullable = false)
    private String accountKey;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false,precision = 19,scale = 4)
    private BigDecimal quantity;

    @Column(name = "average_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal averageCost;

    @Column(name = "realized_pnl", nullable = false, precision = 19, scale = 4)
    private BigDecimal realizedPnl;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    protected Position(){}

    public Position(String accountKey, String symbol){
        this.accountKey = accountKey;
        this.symbol = symbol;
        this.quantity = BigDecimal.ZERO;
        this.averageCost = BigDecimal.ZERO;
        this.realizedPnl = BigDecimal.ZERO;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public BigDecimal getRealizedPnl() {
        return realizedPnl;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void applyFill(boolean isBuy, BigDecimal fillQuantity, BigDecimal fillPrice){

        BigDecimal signedFillQuantity = isBuy ? fillQuantity : fillQuantity.negate();

        if(this.quantity.signum() == 0){
            this.quantity = signedFillQuantity;
            this.averageCost = fillPrice;
        }
        else if(this.quantity.signum() == signedFillQuantity.signum()){
            BigDecimal existingNotional = this.quantity.abs().multiply(this.averageCost);
            BigDecimal fillNotional = signedFillQuantity.abs().multiply(fillPrice);

            BigDecimal newAbsQuantity = this.quantity.abs().add(fillQuantity);

            this.averageCost = existingNotional.add(fillNotional).divide(newAbsQuantity,4, RoundingMode.HALF_UP);
            this.quantity = this.quantity.add(signedFillQuantity);
        }
        else{
            BigDecimal closingQty = this.quantity.abs().min(fillQuantity);
            BigDecimal pnlPerUnit = fillPrice.subtract(this.averageCost).multiply(BigDecimal.valueOf(this.quantity.signum()));

            BigDecimal realizedThisFill = pnlPerUnit.multiply(closingQty);
            this.realizedPnl = realizedThisFill;

            BigDecimal remainingFillQty = fillQuantity.subtract(closingQty);

            if(remainingFillQty.signum() > 0){
                this.quantity = remainingFillQty.multiply(BigDecimal.valueOf(signedFillQuantity.signum()));
                this.averageCost = fillPrice;
            }
            else {
                this.quantity = this.quantity.add(signedFillQuantity);
                if(this.quantity.signum() == 0){
                    this.averageCost = BigDecimal.ZERO;
                }
            }
        }
        this.updatedAt = Instant.now();
    }
}
