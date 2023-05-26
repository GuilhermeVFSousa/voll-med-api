package med.voll.api.consulta.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import med.voll.api.medico.Especialidade;

import java.time.LocalDateTime;

public record DadosEditarDataConsultaDTO(
		
		@NotNull
		@Future
		LocalDateTime data,

		@NotNull
		Integer duracao
		) {

}
