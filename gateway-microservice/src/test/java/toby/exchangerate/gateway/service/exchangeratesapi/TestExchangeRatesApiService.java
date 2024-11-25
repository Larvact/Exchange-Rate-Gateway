package toby.exchangerate.gateway.service.exchangeratesapi;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.reactive.function.client.WebClient;
import toby.exchangerate.common.exception.api.ApiResponseException;
import toby.exchangerate.gateway.configuration.api.ApiKeyRetriever;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesRequest;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesResponse;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock()
class TestExchangeRatesApiService
{
    private static final String API_KEY = "test-api-key-123";
    private static final String USD_CURRENCY_CODE = "USD";
    private static final List<String> RESPONSE_CURRENCY_SYMBOLS = List.of("GBP", "JPY", "EUR");
    private static final String RESOURCE_NOT_FOUND_ERROR_RESPONSE = """
            {
              "success": false,
              "error": {
                "code": 404,
                "info": "The requested resource does not exist."
              }
            }""";
    private static final String USD_LATEST_CURRENCY_RESPONSE = """
            {
              "success": true,
              "timestamp": 1519296206,
              "base": "USD",
              "rates": {
                "GBP": 0.72007,
                "JPY": 107.346001,
                "EUR": 0.813399
              }
            }""";

    @Value("${wiremock.server.port}")
    private int wireMockPort;
    @Autowired
    private ExchangeRatesApiService exchangeRatesApiService;

    @BeforeEach
    void setup()
    {
        final var webClient = WebClient.builder().baseUrl("http://localhost:" + wireMockPort).build();
        final var apiKeyRetriever = Mockito.mock(ApiKeyRetriever.class);
        Mockito.when(apiKeyRetriever.getApiKey()).thenReturn(API_KEY);
        exchangeRatesApiService = new ExchangeRatesApiService(webClient, apiKeyRetriever);
    }

    @Test
    void notFoundResponseMocked_getLatestCurrencyExchangeRate_notFoundExceptionRaised()
    {
        stubFor((get(urlEqualTo(format("/v1/latest?access_key=%s&base=%s&symbols=%s", API_KEY, USD_CURRENCY_CODE, String.join(",", RESPONSE_CURRENCY_SYMBOLS))))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(RESOURCE_NOT_FOUND_ERROR_RESPONSE))));

        final var apiResponseException = assertThrows(ApiResponseException.class, () -> exchangeRatesApiService.getLatestCurrencyExchangeRate(new LatestCurrencyExchangeRatesRequest(USD_CURRENCY_CODE, RESPONSE_CURRENCY_SYMBOLS)));
    }

    @Test
    void baseUsd_getLatestCurrencyExchangeRate_okResponseReturned()
    {
        stubFor((get(urlEqualTo(format("/v1/latest?access_key=%s&base=%s&symbols=%s", API_KEY, USD_CURRENCY_CODE, String.join(",", RESPONSE_CURRENCY_SYMBOLS))))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(USD_LATEST_CURRENCY_RESPONSE))));

        final var latestCurrencyExchangeRatesResponse = exchangeRatesApiService.getLatestCurrencyExchangeRate(new LatestCurrencyExchangeRatesRequest(USD_CURRENCY_CODE, RESPONSE_CURRENCY_SYMBOLS));

        assertThat(latestCurrencyExchangeRatesResponse)
                .has(new Condition<>(response -> response.getCurrencyExchangeRates().size() == 3, "The response has 3 exchange rate values"))
                .has(new Condition<>(response -> nonNull(response.getTimestamp()), "Timestamp has been set"))
                .extracting(LatestCurrencyExchangeRatesResponse::getBaseCurrency)
                .isEqualTo(USD_CURRENCY_CODE);
    }
}