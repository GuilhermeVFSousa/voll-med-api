package med.voll.api.consulta.validacoes.agendamento;

import med.voll.api.consulta.DTO.DadosAgendamentoConsultaDTO;

public interface ValidadorAgendamentoDeConsulta {

    void validar(DadosAgendamentoConsultaDTO dados);

}
