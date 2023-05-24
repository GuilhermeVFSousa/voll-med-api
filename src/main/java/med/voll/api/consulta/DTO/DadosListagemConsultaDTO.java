package med.voll.api.consulta.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import med.voll.api.medico.DTO.DadosResumidosMedicoDTO;
import med.voll.api.paciente.DTO.DadosResumidosPacienteDTO;

import java.time.LocalDateTime;

public record DadosListagemConsultaDTO(

		Long id,
		@NotNull
		DadosResumidosMedicoDTO medico,
		
		DadosResumidosPacienteDTO paciente,

		LocalDateTime data,

		@JsonProperty("data_termino")
		LocalDateTime dataTermino

		) {

}
