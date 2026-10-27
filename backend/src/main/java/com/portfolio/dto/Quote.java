package com.portfolio.dto;

import java.math.BigDecimal;

public record Quote(String symbol, BigDecimal price, BigDecimal change) {}
