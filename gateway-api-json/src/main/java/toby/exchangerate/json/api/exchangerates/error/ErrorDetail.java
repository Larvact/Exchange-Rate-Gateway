package toby.exchangerate.json.api.exchangerates.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import toby.exchangerate.common.exception.api.ApiResponseException;

import java.time.Instant;

@NoArgsConstructor
@Data
public class ErrorDetail
{
    @JsonProperty("code")
    private int statusCode;

    @JsonProperty("info")
    private String description;


    public ApiResponseException convertFromErrorDetail()
    {
        return new ApiResponseException(this.getDescription(), null, HttpStatus.valueOf(this.getStatusCode()), Instant.now());
    }
}
