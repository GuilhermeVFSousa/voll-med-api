package med.voll.api.config.security;

import java.time.Instant;

public record DadosTokenJWT(
		String token,
		String user,
		Instant expiration
		) {

}
