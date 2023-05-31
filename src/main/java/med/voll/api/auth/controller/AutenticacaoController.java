package med.voll.api.auth.controller;

import jakarta.validation.Valid;
import med.voll.api.auth.service.TokenService;
import med.voll.api.usuario.DTO.DadosAutenticacaoDTO;
import med.voll.api.usuario.domain.Usuario;
import med.voll.api.config.security.DadosTokenJWT;
import med.voll.api.exceptions.HttpErrorResponseException;
import med.voll.api.exceptions.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

	@Autowired
	private AuthenticationManager manager;

	@Autowired
	private TokenService tokenService; // token da nossa aplicação

	@PostMapping
	public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosAutenticacaoDTO dados) {
		try {
			var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
			var authentication = manager.authenticate(authenticationToken);
			var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
			var expiracao = tokenService.getExpiration(tokenJWT);
			return ResponseEntity.ok(new DadosTokenJWT(tokenJWT, expiracao));
		} catch (TokenInvalidoException e) {
			throw new HttpErrorResponseException(HttpStatus.FORBIDDEN, "Token inválido ou expirado!");
		} catch (AuthenticationException e) {
			throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Falha na autenticação");
		}
	}

}
