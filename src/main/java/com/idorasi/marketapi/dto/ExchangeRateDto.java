package com.idorasi.marketapi.dto;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

public record ExchangeRateDto(Map<Currency, BigDecimal> data) {
}
