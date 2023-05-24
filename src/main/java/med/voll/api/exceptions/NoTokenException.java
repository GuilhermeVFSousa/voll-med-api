package med.voll.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class NoTokenException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	private HttpStatus status;
	private String message;

	public NoTokenException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}

	public NoTokenException() {
		this.status = HttpStatus.FORBIDDEN;
		this.message = "Token inválido ou Expirado!";
	}


	
	

}
