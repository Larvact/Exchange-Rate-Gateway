package toby.exchangerate.gateway.service.exchangeratesapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import toby.exchangerate.common.exception.api.ApiResponseException;
import toby.exchangerate.gateway.service.ExchangeRatesService;
import toby.exchangerate.json.api.exchangerates.ExchangeRatesLatestResponse;
import toby.exchangerate.json.api.exchangerates.error.ErrorDetail;
import toby.exchangerate.json.api.exchangerates.error.ExchangeRatesErrorResponse;
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
        return webClient.get()
                .uri(String.format(LATEST_CURRENCY_EXCHANGE_RATE_BASE_PATH, latestCurrencyExchangeRatesRequest.getBaseCurrencySymbol(), String.join(",", latestCurrencyExchangeRatesRequest.getResponseCurrencySymbols())))
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode != HttpStatus.OK, response -> response.bodyToMono(ExchangeRatesErrorResponse.class)
                        .map(ExchangeRatesErrorResponse::getErrorDetail)
                        .map(ExchangeRatesApiService::convertFromErrorDetail))
                .bodyToMono(ExchangeRatesLatestResponse.class)
                .map(ExchangeRatesApiService::convertFromExchangeRatesLatestResponse)
                .block();
    }

    private static LatestCurrencyExchangeRatesResponse convertFromExchangeRatesLatestResponse(final ExchangeRatesLatestResponse apiResponse)
    {
        final var latestCurrencyExchangeRates = new LatestCurrencyExchangeRatesResponse();
        latestCurrencyExchangeRates.setBaseCurrency(apiResponse.getBaseCurrency());
        latestCurrencyExchangeRates.setCurrencyExchangeRates(apiResponse.getCurrencyExchangeRates());
        latestCurrencyExchangeRates.setTimestamp(apiResponse.getTimestamp());
        return latestCurrencyExchangeRates;
    }

    private static ApiResponseException convertFromErrorDetail(final ErrorDetail errorDetail)
    {
        return new ApiResponseException(errorDetail.getDescription(), null, HttpStatus.valueOf(errorDetail.getStatusCode()), Instant.now());
    }
}
