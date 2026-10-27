package com.portfolio.dto;

import java.math.BigDecimal;

public record HoldingReq(String symbol, BigDecimal shares, BigDecimal buyPrice) {}
