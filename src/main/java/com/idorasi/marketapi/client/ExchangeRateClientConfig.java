package com.idorasi.marketapi.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "exchange-rate-client")
public class ExchangeRateClientConfig {

    private String url;
    private String apiKey;

}
