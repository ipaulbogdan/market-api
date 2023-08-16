package com.idorasi.marketapi.client;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;
import com.idorasi.marketapi.dto.ExchangeRateDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@AllArgsConstructor
public class ExchangeRateClient {

    private static final String API_KEY_PARAM = "apikey";
    private static final String BASE_CURRENCY_PARAM = "base_currency";

    private ExchangeRateClientConfig exchangeRateClientConfig;

    public Map<Currency, BigDecimal> getRate(Currency currency) {
        var url = UriComponentsBuilder.fromHttpUrl(exchangeRateClientConfig.getUrl())
                .queryParam(API_KEY_PARAM, exchangeRateClientConfig.getApiKey())
                .queryParam(BASE_CURRENCY_PARAM, currency.getCurrencyCode())
                .build()
                .toString();

        var response = new RestTemplate().getForEntity(url, ExchangeRateDto.class);

        return Objects.requireNonNull(response.getBody()).data();
    }
}
