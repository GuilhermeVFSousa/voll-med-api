package med.voll.api.usuario.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.bind.DefaultValue;

public record DadosUsuarioComSenhaDTO(

		@Nullable
		Long id,
		@NotNull
		String login,

		@Nullable
		String password,
		@NotNull
		String nome,
		@Nullable
		String imagem,
		@DefaultValue(value = "false")
		boolean superUser,
		@DefaultValue(value = "true")
		boolean ativo
		) {

}
