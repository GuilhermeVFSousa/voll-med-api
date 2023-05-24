package med.voll.api.consulta.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import med.voll.api.medico.Especialidade;

public record DadosAgendamentoConsultaDTO(
		Long idMedico,
		
		@NotNull
		Long idPaciente,
		
		@NotNull
		@Future
		LocalDateTime data,

		@NotNull
		Long duracao,

		@JsonProperty("data_termino")
		@Future
		LocalDateTime dataTermino,
		
		Especialidade especialidade
		) {

}
