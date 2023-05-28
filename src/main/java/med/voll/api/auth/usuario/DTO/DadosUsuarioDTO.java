package med.voll.api.auth.usuario.DTO;

import jakarta.annotation.Nullable;

public record DadosUsuarioDTO(

		@Nullable
		Long id,
		String login,
		boolean superUser
		) {

}
