package med.voll.api.consulta.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import med.voll.api.consulta.domain.Consulta;

import java.time.LocalDateTime;
import java.util.Objects;

public record DadosDetalhamentoConsultaDTO(
		Long id,
		Long idMedico,
		Long idPaciente,
		LocalDateTime data,

		@JsonProperty("data_termino")
		LocalDateTime dataTermino
		) {

	public DadosDetalhamentoConsultaDTO(Consulta consulta) {
		this(consulta.getId(), consulta.getMedico().getId(), consulta.getPaciente().getId(), consulta.getData(), consulta.getDataTermino());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DadosDetalhamentoConsultaDTO that = (DadosDetalhamentoConsultaDTO) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
