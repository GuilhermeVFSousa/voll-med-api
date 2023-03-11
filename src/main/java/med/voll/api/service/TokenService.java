package med.voll.api.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import med.voll.api.domain.usuario.Usuario;
import med.voll.api.exceptions.NoTokenException;

@Service
public class TokenService {
	
	@Value("${api.security.token.secret}")
	private String secret;
	
	public String gerarToken(Usuario usuario) {

		try {
		    var algoritmo = Algorithm.HMAC256(secret);
		    return JWT.create()
		    		.withIssuer("API Voll.med")
		    		.withSubject(usuario.getLogin())
		    		.withClaim("id", usuario.getId())
		    		.withExpiresAt(dataExpiracao())
		    		.sign(algoritmo);
		} catch (JWTCreationException exception){
		    throw new RuntimeException("Erro ao gerar Token JWT", exception);
		}

	}
	
	public String getSubject(String tokenJWT) {
		try {
			var algoritmo = Algorithm.HMAC256(secret);
		    return JWT.require(algoritmo)
		        // specify an specific claim validations
		        .withIssuer("API Voll.med")
		        .build()
		        .verify(tokenJWT)
		        .getSubject();

		} catch (JWTVerificationException exception){
		    throw new NoTokenException("Token JWT inválido ou expirado!");
		}
	}
	
	public String getIdUser(String tokenJWT) {
		try {
			var algoritmo = Algorithm.HMAC256(secret);
		    return JWT.require(algoritmo)
		        // specify an specific claim validations
		        .withIssuer("API Voll.med")
		        .build()
		        .verify(tokenJWT)
		        .getClaim("id").toString();

		} catch (JWTVerificationException exception){
		    throw new NoTokenException("Token JWT inválido ou expirado!");
		}
	}

	private Instant dataExpiracao() {
		return LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.of("-03:00"));
	}

}
