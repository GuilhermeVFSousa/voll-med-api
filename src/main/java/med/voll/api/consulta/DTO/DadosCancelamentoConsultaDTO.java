package med.voll.api.consulta.DTO;

import jakarta.validation.constraints.NotNull;
import med.voll.api.consulta.MotivoCancelamento;

public record DadosCancelamentoConsultaDTO(
		@NotNull
        Long idConsulta,

        @NotNull
        MotivoCancelamento motivo
		
		) {

}
