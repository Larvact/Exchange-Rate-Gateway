package toby.exchangerate.gateway.service.exchangeratesapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import toby.exchangerate.gateway.configuration.api.ApiKeyRetriever;
import toby.exchangerate.gateway.configuration.api.exchangerates.ExchangeRatesApiKeyRetriever;
import toby.exchangerate.gateway.service.ExchangeRatesService;
import toby.exchangerate.json.api.exchangerates.ExchangeRatesLatestResponse;
import toby.exchangerate.json.api.exchangerates.error.ErrorDetail;
import toby.exchangerate.json.api.exchangerates.error.ExchangeRatesErrorResponse;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesRequest;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesResponse;

@Service
@Qualifier("exchangeRatesApiService")
@RequiredArgsConstructor
public class ExchangeRatesApiService implements ExchangeRatesService
{
    private static final String LATEST_CURRENCY_EXCHANGE_RATE_BASE_PATH = "/v1/latest?access_key=%s&base=%s&symbols=%s";

    private final WebClient webClient;
    private final ApiKeyRetriever apiKeyRetriever;

    @Override
    public LatestCurrencyExchangeRatesResponse getLatestCurrencyExchangeRate(final LatestCurrencyExchangeRatesRequest latestCurrencyExchangeRatesRequest)
    {
        return webClient.get()
                .uri(String.format(LATEST_CURRENCY_EXCHANGE_RATE_BASE_PATH, apiKeyRetriever.getApiKey(),latestCurrencyExchangeRatesRequest.getBaseCurrencySymbol(), String.join(",", latestCurrencyExchangeRatesRequest.getResponseCurrencySymbols())))
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode != HttpStatus.OK, response -> response.bodyToMono(ExchangeRatesErrorResponse.class)
                        .map(ExchangeRatesErrorResponse::getErrorDetail)
                        .map(ErrorDetail::convertFromErrorDetail))
                .bodyToMono(ExchangeRatesLatestResponse.class)
                .map(ExchangeRatesLatestResponse::converttoLatestCurrencyExchangeRatesResponse)
                .block();
    }
}
