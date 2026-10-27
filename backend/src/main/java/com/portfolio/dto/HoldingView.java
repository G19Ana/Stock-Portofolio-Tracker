package com.portfolio.dto;

import java.math.BigDecimal;

public record HoldingView(
        Long id,
        String symbol,
        BigDecimal shares,
        BigDecimal buyPrice,
        BigDecimal currentPrice,
        BigDecimal totalCost,
        BigDecimal marketValue,
        BigDecimal pnl,
        BigDecimal pnlPct,
        BigDecimal allocationPct
) {}
