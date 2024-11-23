package toby.exchangerate.gateway.endpoints;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toby.exchangerate.gateway.service.ExchangeRatesService;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesRequest;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesResponse;

@RestController
@RequestMapping(path = "exchange-rates/v1")
public class ExchangeRatesResource
{
    private final ExchangeRatesService exchangeRatesService;

    public ExchangeRatesResource(@Qualifier("exchangeRatesApiService") final ExchangeRatesService exchangeRatesService)
    {
        this.exchangeRatesService = exchangeRatesService;
    }

    @PostMapping(path = "/latest")
    public LatestCurrencyExchangeRatesResponse postLatestCurrencyExchangeRate(@RequestBody final LatestCurrencyExchangeRatesRequest latestCurrencyExchangeRatesRequest)
    {
        return exchangeRatesService.getLatestCurrencyExchangeRate(latestCurrencyExchangeRatesRequest);
    }
}
