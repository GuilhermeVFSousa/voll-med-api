package med.voll.api.consulta.validacoes.agendamento;

import java.time.Duration;
import java.time.LocalDateTime;

import med.voll.api.consulta.DTO.DadosAgendamentoConsultaDTO;
import org.springframework.stereotype.Component;

@Component("ValidadorHorarioAntecedenciaAgendamento")
public class ValidadorHorarioAntecedencia implements ValidadorAgendamentoDeConsulta {

    public void validar(DadosAgendamentoConsultaDTO dados) {
        var dataConsulta = dados.data();
        var agora = LocalDateTime.now();
        var diferencaEmMinutos = Duration.between(agora, dataConsulta).toMinutes();

        if (diferencaEmMinutos < 30) {
            throw new med.voll.api.exceptions.ValidacaoException("Consulta deve ser agendada com antecedência mínima de 30 minutos");
        }

    }
}