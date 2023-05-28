package med.voll.api.config.security;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import med.voll.api.exceptions.NoTokenException;
import med.voll.api.exceptions.TokenInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.auth.usuario.repository.UsuarioRepository;
import med.voll.api.auth.service.TokenService;

@Component
public class SecurityFilter extends OncePerRequestFilter {
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		var tokenJWT = recuperarToken(request);
			try {
				if (tokenJWT != null) {
					String subject = null;
					subject = tokenService.getSubject(tokenJWT);
					var usuario = usuarioRepository.findByLogin(subject);
					var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} else {
					response.setStatus(HttpStatus.FORBIDDEN.value());
				}
			} catch (TokenInvalidoException e) {
				var responseError = Map.of("error", "Falha na autenticação");
				var objectMapper = new ObjectMapper();
				response.setStatus(HttpStatus.FORBIDDEN.value());
				response.getWriter().write(objectMapper.writeValueAsString(responseError));
			}

		filterChain.doFilter(request, response);
	}

	private String recuperarToken(HttpServletRequest request) {
		var authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null) {
			return authorizationHeader.replace("Bearer ", "");
		}
		return null;

	}
	
	

}
