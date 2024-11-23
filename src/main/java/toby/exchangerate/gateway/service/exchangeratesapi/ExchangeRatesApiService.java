package toby.exchangerate.gateway.service.exchangeratesapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import toby.exchangerate.gateway.service.ExchangeRatesService;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesRequest;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesResponse;

@Service
@Qualifier("exchangeRatesApiService")
public class ExchangeRatesApiService implements ExchangeRatesService
{
    @Override
    public LatestCurrencyExchangeRatesResponse getLatestCurrencyExchangeRate(final LatestCurrencyExchangeRatesRequest latestCurrencyExchangeRatesRequest)
    {
        return null;
    }
}
