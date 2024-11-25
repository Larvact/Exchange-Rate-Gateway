package toby.exchangerate.gateway.configuration.api.exchangerates;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import toby.exchangerate.gateway.configuration.api.ApiKeyRetriever;

@Component
@Qualifier("ExchangeRatesApiKeyRetriever")
@Getter
public class ExchangeRatesApiKeyRetriever implements ApiKeyRetriever
{
    @Value("exchange.rates.api.key")
    private String apiKey;
}
