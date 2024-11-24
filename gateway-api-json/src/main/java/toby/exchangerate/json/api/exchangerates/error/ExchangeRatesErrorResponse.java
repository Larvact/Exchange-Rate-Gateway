package toby.exchangerate.json.api.exchangerates.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ExchangeRatesErrorResponse
{
    @JsonProperty("success")
    private Boolean isSuccess;

    @JsonProperty("error")
    private ErrorDetail errorDetail;
}
