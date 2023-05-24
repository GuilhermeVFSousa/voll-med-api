package med.voll.api.medico.DTO;

import med.voll.api.endereco.domain.Endereco;
import med.voll.api.medico.Especialidade;
import med.voll.api.medico.domain.Medico;

public record DadosDetalhamentoMedicoDTO(
		Long id,
		String nome,
		String email,
		String crm,
		String telefone,
		Especialidade especialidade,
		Endereco endereco
		) {
	
	public DadosDetalhamentoMedicoDTO(Medico medico) {
		this(
				medico.getId(),
				medico.getNome(),
				medico.getEmail(),
				medico.getCrm(),
				medico.getTelefone(),
				medico.getEspecialidade(),
				medico.getEndereco()
				);
	}

}
