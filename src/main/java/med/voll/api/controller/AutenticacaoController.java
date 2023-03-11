package med.voll.api.controller;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import med.voll.api.exceptions.NoTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import med.voll.api.config.security.DadosTokenJWT;
import med.voll.api.domain.usuario.DadosAutenticacao;
import med.voll.api.domain.usuario.Usuario;
import med.voll.api.service.TokenService;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {
	
	@Autowired
	private AuthenticationManager manager;
	
	@Autowired
	private TokenService tokenService; // token da nossa aplicação
	
	@PostMapping
	public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
		try {
			var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
			var authentication =  manager.authenticate(authenticationToken);
			var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
			var userDoToken = dados.login();
			return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, userDoToken));
		} catch (JWTCreationException e) {
			throw new NoTokenException("Token inválido ou expirado!");
		} catch (TokenExpiredException e) {
			throw new NoTokenException("Token inválido ou expirado!");
		}
	}

}
