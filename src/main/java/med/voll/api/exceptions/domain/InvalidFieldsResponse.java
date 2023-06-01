package med.voll.api.exceptions.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvalidFieldsResponse {
    private HttpStatus status;
    private String timestamp;
    private List<Map<String, String>> message;

}
