package toby.exchangerate.json.api.exchangerates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesResponse;

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

    @JsonIgnore
    public LatestCurrencyExchangeRatesResponse converttoLatestCurrencyExchangeRatesResponse()
    {
        final var latestCurrencyExchangeRates = new LatestCurrencyExchangeRatesResponse();
        latestCurrencyExchangeRates.setBaseCurrency(this.baseCurrency);
        latestCurrencyExchangeRates.setCurrencyExchangeRates(this.currencyExchangeRates);
        latestCurrencyExchangeRates.setTimestamp(this.timestamp);
        return latestCurrencyExchangeRates;
    }
}
