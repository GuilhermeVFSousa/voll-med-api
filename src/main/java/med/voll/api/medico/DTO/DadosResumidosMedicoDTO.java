package med.voll.api.medico.DTO;

import med.voll.api.medico.Especialidade;
import med.voll.api.medico.domain.Medico;

public record DadosResumidosMedicoDTO(
		String nome,
		String crm,
		Especialidade especialidade
		) {

	public DadosResumidosMedicoDTO(Medico medico) {
		this(
				medico.getNome(),
				medico.getCrm(),
				medico.getEspecialidade());
	}

}
