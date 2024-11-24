package toby.exchangerate.json.api.exchangerates;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
public class ExchangeRatesLatestResponse
{
    @JsonProperty("success")
    private Boolean isSuccess;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("base")
    private String baseCurrency;

    @JsonProperty("rates")
    private Map<String, BigDecimal> currencyExchangeRates = new HashMap<>();
}
