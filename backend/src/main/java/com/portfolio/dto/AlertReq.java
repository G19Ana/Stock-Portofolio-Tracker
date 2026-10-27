package com.portfolio.dto;

import com.portfolio.entity.Alert;
import java.math.BigDecimal;

public record AlertReq(String symbol, BigDecimal targetPrice, Alert.Direction direction, String email) {}
