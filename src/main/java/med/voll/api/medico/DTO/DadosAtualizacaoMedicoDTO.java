package med.voll.api.medico.DTO;

import jakarta.validation.constraints.NotNull;
import med.voll.api.endereco.DTO.DadosEnderecoDTO;

public record DadosAtualizacaoMedicoDTO(
		
		@NotNull
		Long id,
		String nome,
		String telefone,
		DadosEnderecoDTO endereco
		) {

}
