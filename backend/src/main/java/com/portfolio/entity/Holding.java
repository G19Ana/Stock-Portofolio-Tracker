package com.portfolio.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "holdings", indexes = @Index(name = "idx_holding_symbol", columnList = "symbol"))
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal shares;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal buyPrice;

    public Holding() {}

    public Holding(String symbol, BigDecimal shares, BigDecimal buyPrice) {
        this.symbol = symbol.toUpperCase();
        this.shares = shares;
        this.buyPrice = buyPrice;
    }

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol.toUpperCase(); }
    public BigDecimal getShares() { return shares; }
    public void setShares(BigDecimal shares) { this.shares = shares; }
    public BigDecimal getBuyPrice() { return buyPrice; }
    public void setBuyPrice(BigDecimal buyPrice) { this.buyPrice = buyPrice; }
}
