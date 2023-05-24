package med.voll.api.consulta.validacoes.agendamento;

import med.voll.api.consulta.DTO.DadosAgendamentoConsultaDTO;
import med.voll.api.paciente.repository.PacienteRepository;
import med.voll.api.exceptions.ValidacaoException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPacienteAtivo implements ValidadorAgendamentoDeConsulta {

    @Autowired
    private PacienteRepository repository;

    public void validar(DadosAgendamentoConsultaDTO dados) {
        var pacienteEstaAtivo = repository.findAtivoById(dados.idPaciente());
        if (!pacienteEstaAtivo) {
            throw new ValidacaoException("Consulta não pode ser agendada com paciente excluído");
        }
    }
}