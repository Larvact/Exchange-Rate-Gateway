package toby.exchangerate.json.api.exchangerates.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ErrorDetail
{
    @JsonProperty("code")
    private int statusCode;

    @JsonProperty("info")
    private String description;
}
