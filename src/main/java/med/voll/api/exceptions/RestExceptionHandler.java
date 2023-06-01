package med.voll.api.exceptions;

import med.voll.api.exceptions.domain.InvalidFieldsResponse;
import med.voll.api.util.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(HttpErrorResponseException.class)
    public ResponseEntity<ErrorResponse> handleHttpErrorResponseException(HttpErrorResponseException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(PacienteNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handlePacienteNaoEncontradoException(HttpErrorResponseException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MedicoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleMedicoNaoEncontradoException(HttpErrorResponseException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ConsultaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleConsultaNaoEncontradoException(HttpErrorResponseException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ErrorResponse> handleValidacaoException(HttpErrorResponseException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        ErrorResponse errorResponse = new ErrorResponse((HttpStatus) e.getStatusCode(), e.getReason());
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InvalidFieldsResponse> handleValidationException(MethodArgumentNotValidException e) {
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

        var response = new InvalidFieldsResponse(
                HttpStatus.BAD_REQUEST,
                DateUtils.getTimestamp(),
                errors);

        return ResponseEntity.badRequest().body(response);
    }
}
