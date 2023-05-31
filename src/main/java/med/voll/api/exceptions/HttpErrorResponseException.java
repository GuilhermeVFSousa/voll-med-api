package med.voll.api.exceptions;

import org.springframework.http.HttpStatus;

public class HttpErrorResponseException extends RuntimeException {
    private final HttpStatus status;

    public HttpErrorResponseException(HttpStatus status) {
        this.status = status;
    }

    public HttpErrorResponseException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
