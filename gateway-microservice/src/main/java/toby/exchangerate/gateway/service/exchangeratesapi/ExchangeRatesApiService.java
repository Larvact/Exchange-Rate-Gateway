package toby.exchangerate.gateway.service.exchangeratesapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import toby.exchangerate.common.exception.api.ApiResponseException;
import toby.exchangerate.gateway.service.ExchangeRatesService;
import toby.exchangerate.json.api.exchangerates.ExchangeRatesLatestResponse;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesRequest;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesResponse;

import java.time.Instant;

@Service
@Qualifier("exchangeRatesApiService")
@RequiredArgsConstructor
public class ExchangeRatesApiService implements ExchangeRatesService
{
    private static final String LATEST_CURRENCY_EXCHANGE_RATE_BASE_PATH = "/v1/latest?base=%s&symbols=%s";

    private final WebClient webClient;

    @Override
    public LatestCurrencyExchangeRatesResponse getLatestCurrencyExchangeRate(final LatestCurrencyExchangeRatesRequest latestCurrencyExchangeRatesRequest)
    {
       final var apiResponse = webClient.get()
               .uri(String.format(LATEST_CURRENCY_EXCHANGE_RATE_BASE_PATH, latestCurrencyExchangeRatesRequest.getBaseCurrencySymbol(), String.join(",", latestCurrencyExchangeRatesRequest.getResponseCurrencySymbols())))
               .retrieve()
               .onStatus(httpStatusCode -> httpStatusCode != HttpStatus.OK, response -> Mono.error(() -> new ApiResponseException(null, null, HttpStatus.valueOf(response.statusCode().value()), Instant.now())))
               .bodyToMono(ExchangeRatesLatestResponse.class)
               .block();
       if(apiResponse == null)
       {
           throw new ApiResponseException("Third party API returned a null response.", null, HttpStatus.INTERNAL_SERVER_ERROR, Instant.now());
       }
       return convertFromExchangeRatesLatestResponse(apiResponse);
    }

    private static LatestCurrencyExchangeRatesResponse convertFromExchangeRatesLatestResponse(final ExchangeRatesLatestResponse apiResponse)
    {
        final var latestCurrencyExchangeRates = new LatestCurrencyExchangeRatesResponse();
        latestCurrencyExchangeRates.setBaseCurrency(apiResponse.getBaseCurrency());
        latestCurrencyExchangeRates.setCurrencyExchangeRates(apiResponse.getCurrencyExchangeRates());
        latestCurrencyExchangeRates.setTimestamp(apiResponse.getTimestamp());
        return latestCurrencyExchangeRates;
    }
}
