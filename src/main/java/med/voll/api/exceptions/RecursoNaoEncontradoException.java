package med.voll.api.exceptions;

import org.springframework.http.HttpStatus;

public class RecursoNaoEncontradoException extends RuntimeException {
    private final HttpStatus status;

    public RecursoNaoEncontradoException(HttpStatus status) {
        this.status = status;
    }

    public RecursoNaoEncontradoException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
