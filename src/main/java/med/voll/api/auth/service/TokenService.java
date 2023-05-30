package med.voll.api.auth.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.exceptions.JWTDecodeException;
import med.voll.api.exceptions.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import med.voll.api.auth.usuario.domain.Usuario;
import med.voll.api.exceptions.NoTokenException;

@Service
public class TokenService {
	
	@Value("${api.security.token.secret}")
	private String secret;
	
	public String gerarToken(Usuario usuario) throws TokenInvalidoException {

		try {
		    var algoritmo = Algorithm.HMAC256(secret);
			var id = usuario.getId();
			var nome = usuario.getNome();
			var email = usuario.getLogin();
			var superUser = usuario.isSuperUser();
			var ativo = usuario.isAtivo();
		    return JWT.create()
		    		.withIssuer("API Voll.med")
					.withSubject(email)
					.withPayload(Map.of(
							"id", id,
							"nome", nome,
							"ativo",ativo,
							"superUser", superUser))
		    		.withExpiresAt(dataExpiracao())
		    		.sign(algoritmo);
		} catch (JWTCreationException exception){
		    throw new TokenInvalidoException();
		}

	}
	
	public String getSubject(String tokenJWT) throws TokenInvalidoException {
		try {
			var algoritmo = Algorithm.HMAC256(secret);
			var jwtVerifier = JWT.require(algoritmo)
					.withIssuer("API Voll.med")
					.build();

			var decodedJWT = jwtVerifier.verify(tokenJWT);
			if (decodedJWT.getExpiresAt().before(new Date())) {
				throw new TokenInvalidoException();
			}

			return decodedJWT.getSubject();

		} catch (JWTVerificationException e){
		    throw new TokenInvalidoException();
		}
	}

	public Instant getExpiration(String tokenJWT) throws TokenInvalidoException {
		try {
			var algoritmo = Algorithm.HMAC256(secret);
			return JWT.require(algoritmo)
					// specify an specific claim validations
					.withIssuer("API Voll.med")
					.build()
					.verify(tokenJWT)
					.getExpiresAtAsInstant();

		} catch (JWTVerificationException exception){
			throw new TokenInvalidoException();
		}
	}

	private Instant dataExpiracao() {
		return LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00"));
	}

}
