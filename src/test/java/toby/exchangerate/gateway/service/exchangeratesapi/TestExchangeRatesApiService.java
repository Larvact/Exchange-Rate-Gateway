package toby.exchangerate.gateway.service.exchangeratesapi;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.web.reactive.function.client.WebClient;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesRequest;
import toby.exchangerate.json.api.exchangerates.latest.LatestCurrencyExchangeRatesResponse;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock()
class TestExchangeRatesApiService
{
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
    private ExchangeRatesApiService exchangeRatesApiService;

    @BeforeEach
    void setup()
    {
        final var webClient = WebClient.builder().baseUrl("http://localhost:" + wireMockPort).build();
        exchangeRatesApiService = new ExchangeRatesApiService(webClient);
    }

    @Test
    void baseUsd_getLatestCurrencyExchangeRate_okResponseReturned()
    {
        stubFor((get(urlEqualTo("/v1/latest?base=USD&symbols=GBP,JPY,EUR"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(USD_LATEST_CURRENCY_RESPONSE))));

        final var latestCurrencyExchangeRatesResponse = exchangeRatesApiService.getLatestCurrencyExchangeRate(new LatestCurrencyExchangeRatesRequest("USD", List.of("GBP", "JPY", "EUR")));

        assertThat(latestCurrencyExchangeRatesResponse)
                .has(new Condition<>(response -> response.getCurrencyExchangeRates().size() == 3, "The response has 3 exchange rate values"))
                .has(new Condition<>(response -> nonNull(response.getTimestamp()), "Timestamp has been set"))
                .extracting(LatestCurrencyExchangeRatesResponse::getIsSuccess, LatestCurrencyExchangeRatesResponse::getBaseCurrency)
                .containsExactly(true, "USD");
    }
}