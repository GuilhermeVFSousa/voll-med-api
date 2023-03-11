package med.voll.api.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class TratadorDeExceptions {
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<?> tratarErro404() {
		return ResponseEntity.notFound().build();
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> tratarErro400(MethodArgumentNotValidException ex) {
		var erros = ex.getFieldErrors();
		return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> tratarErroDeLogin(BadCredentialsException ex) {
		var erros = ex.getMessage();
		return ResponseEntity.status(403).body(new DadosErroLogin(erros));
	}
	
	@ExceptionHandler(NoTokenException.class)
	public ResponseEntity<?> tratarErroSemToken(NoTokenException ex) {
		var erros = ex.getMessage();
		return ResponseEntity.status(403).body(new DadosErroLogin(erros));
	}
	
	@ExceptionHandler(ValidacaoException.class)
	public ResponseEntity<?> tratarErroRegraDeNegocio(ValidacaoException ex) {
		var erros = ex.getMessage();
		return ResponseEntity.badRequest().body(new DadosErroLogin(erros));
	}
	
	
	private record DadosErroValidacao(String campo, String mensagem) {
		
		
		public DadosErroValidacao(FieldError erro) {
			this(erro.getField(), erro.getDefaultMessage());
		}
		
		public DadosErroValidacao(String campo, String mensagem) {
			this.campo = campo;
			this.mensagem = mensagem;
		}
	}
	
	private record DadosErroLogin(String error) {
		
		
		public DadosErroLogin(String error) {
			this.error = error;
		}
	}

}
