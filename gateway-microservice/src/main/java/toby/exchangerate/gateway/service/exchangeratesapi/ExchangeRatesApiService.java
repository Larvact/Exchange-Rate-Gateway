package toby.exchangerate.gateway.service.exchangeratesapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import toby.exchangerate.gateway.configuration.api.ApiKeyRetriever;
import toby.exchangerate.gateway.service.ExchangeRatesService;
import toby.exchangerate.json.api.exchangerates.ExchangeRatesLatestResponse;
import toby.exchangerate.json.api.exchangerates.error.ErrorDetail;
import toby.exchangerate.json.api.exchangerates.error.ExchangeRatesErrorResponse;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesRequest;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesResponse;

@Service
@Qualifier("exchangeRatesApiService")
public class ExchangeRatesApiService implements ExchangeRatesService
{
    private final WebClient webClient;
    private final ApiKeyRetriever apiKeyRetriever;

    public ExchangeRatesApiService(final WebClient webClient, @Qualifier("exchangeRatesApiKeyRetriever") final ApiKeyRetriever apiKeyRetriever)
    {
        this.webClient = webClient;
        this.apiKeyRetriever = apiKeyRetriever;
    }

    @Override
    public LatestCurrencyExchangeRatesResponse getLatestCurrencyExchangeRate(final LatestCurrencyExchangeRatesRequest latestCurrencyExchangeRatesRequest)
    {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/latest")
                        .queryParam("access_key", apiKeyRetriever.getApiKey())
                        .queryParam("base", latestCurrencyExchangeRatesRequest.getBaseCurrencySymbol())
                        .queryParam("symbols", String.join(",", latestCurrencyExchangeRatesRequest.getResponseCurrencySymbols()))
                        .build()
                )
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode != HttpStatus.OK, response -> response.bodyToMono(ExchangeRatesErrorResponse.class)
                        .map(ExchangeRatesErrorResponse::getErrorDetail)
                        .map(ErrorDetail::convertFromErrorDetail))
                .bodyToMono(ExchangeRatesLatestResponse.class)
                .map(ExchangeRatesLatestResponse::converttoLatestCurrencyExchangeRatesResponse)
                .block();
    }
}
