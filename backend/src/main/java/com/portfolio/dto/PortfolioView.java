package com.portfolio.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioView(
        BigDecimal totalCost,
        BigDecimal totalValue,
        BigDecimal totalPnl,
        BigDecimal totalPnlPct,
        List<HoldingView> holdings
) {}
