package com.portfolio.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "alerts", indexes = @Index(name = "idx_alert_active", columnList = "active"))
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal targetPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Direction direction;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean active = true;

    public enum Direction { ABOVE, BELOW }

    public Alert() {}

    public Alert(String symbol, BigDecimal targetPrice, Direction direction, String email) {
        this.symbol = symbol.toUpperCase();
        this.targetPrice = targetPrice;
        this.direction = direction;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public BigDecimal getTargetPrice() { return targetPrice; }
    public Direction getDirection() { return direction; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
