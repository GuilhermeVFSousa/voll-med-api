package med.voll.api.medico.DTO;

import med.voll.api.medico.Especialidade;
import med.voll.api.medico.domain.Medico;

public record DadosListagemMedicoDTO(
		Long id,
		String nome, 
		String email, 
		String crm, 
		Especialidade especialidade
		) {
	
	public DadosListagemMedicoDTO(Medico medico) {
		this(
				medico.getId(),
				medico.getNome(),
				medico.getEmail(),
				medico.getCrm(),
				medico.getEspecialidade());
	}

}