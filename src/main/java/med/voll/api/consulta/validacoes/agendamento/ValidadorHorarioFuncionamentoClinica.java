package med.voll.api.consulta.validacoes.agendamento;

import java.time.DayOfWeek;

import med.voll.api.consulta.DTO.DadosAgendamentoConsultaDTO;
import org.springframework.stereotype.Component;

import med.voll.api.exceptions.ValidacaoException;

@Component
public class ValidadorHorarioFuncionamentoClinica implements ValidadorAgendamentoDeConsulta {
	
	public void validar(DadosAgendamentoConsultaDTO dados) {
		
		var dataConsulta = dados.data();
		
		var domingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);

		var antesDaAberturaDaClinica = dataConsulta.getHour() < 7;
		
		var depoisDoEncerramentoDaClinica = dataConsulta.getHour() > 18;
		
		if (domingo || antesDaAberturaDaClinica || depoisDoEncerramentoDaClinica) {
		    throw new ValidacaoException("Consulta fora do horário de funcionamento da clínica");
		}
		
	}

}
