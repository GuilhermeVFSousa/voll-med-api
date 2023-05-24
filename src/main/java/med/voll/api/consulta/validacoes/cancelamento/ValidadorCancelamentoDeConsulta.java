package med.voll.api.consulta.validacoes.cancelamento;

import med.voll.api.consulta.DTO.DadosCancelamentoConsultaDTO;

public interface ValidadorCancelamentoDeConsulta {
	
	void validar(DadosCancelamentoConsultaDTO dados);

}
