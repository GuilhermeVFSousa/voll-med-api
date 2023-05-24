package med.voll.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleRecursoNaoEncontradoException(RecursoNaoEncontradoException e) {
        ErroResponse erroResponse = new ErroResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(erroResponse);
    }

    @ExceptionHandler(PacienteNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handlePacienteNaoEncontradoException(RecursoNaoEncontradoException e) {
        ErroResponse erroResponse = new ErroResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(erroResponse);
    }

    @ExceptionHandler(MedicoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleMedicoNaoEncontradoException(RecursoNaoEncontradoException e) {
        ErroResponse erroResponse = new ErroResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(erroResponse);
    }

    @ExceptionHandler(ConsultaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleConsultaNaoEncontradoException(RecursoNaoEncontradoException e) {
        ErroResponse erroResponse = new ErroResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(erroResponse);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErroResponse> handleValidacaoException(RecursoNaoEncontradoException e) {
        ErroResponse erroResponse = new ErroResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(erroResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> handleResponseStatusException(ResponseStatusException e) {
        ErroResponse erroResponse = new ErroResponse((HttpStatus) e.getStatusCode(), e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(erroResponse);
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<?> handleResponseStatusTokenInvalido(TokenInvalidoException e) {
        var responseError = Map.of("error", "Falha na autenticação");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<Map<String, String>>>> handleValidationException(MethodArgumentNotValidException e) {
        List<Map<String, String>> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("campo", error.getField());
                    errorDetails.put("error", error.getDefaultMessage());
                    return errorDetails;
                })
                .collect(Collectors.toList());
        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }
}
