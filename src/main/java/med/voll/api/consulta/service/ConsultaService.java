package med.voll.api.consulta.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import med.voll.api.consulta.DTO.DadosAgendamentoConsultaDTO;
import med.voll.api.consulta.DTO.DadosListagemConsultaDTO;
import med.voll.api.consulta.domain.Consulta;
import med.voll.api.consulta.repository.ConsultaRepository;
import med.voll.api.consulta.validacoes.cancelamento.ValidadorCancelamentoDeConsulta;
import med.voll.api.consulta.DTO.DadosCancelamentoConsultaDTO;
import med.voll.api.consulta.DTO.DadosDetalhamentoConsultaDTO;
import med.voll.api.exceptions.RegraNegocioException;
import med.voll.api.medico.DTO.DadosResumidosMedicoDTO;
import med.voll.api.paciente.DTO.DadosResumidosPacienteDTO;
import med.voll.api.exceptions.MedicoNaoEncontradoException;
import med.voll.api.exceptions.PacienteNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import med.voll.api.consulta.validacoes.agendamento.ValidadorAgendamentoDeConsulta;
import med.voll.api.medico.domain.Medico;
import med.voll.api.medico.repository.MedicoRepository;
import med.voll.api.paciente.repository.PacienteRepository;
import med.voll.api.exceptions.ValidacaoException;

@Service
public class ConsultaService {

	@Autowired
	private ConsultaRepository consultaRepository;

	@Autowired
	private MedicoRepository medicoRepository;

	@Autowired
	private PacienteRepository pacienteRepository;

	@Autowired
	private List<ValidadorAgendamentoDeConsulta> validadores;

	@Autowired
	private List<ValidadorCancelamentoDeConsulta> validadoresCancelamento;

	public List<DadosListagemConsultaDTO> listarConsultas() {
		return consultaRepository.listAllConsultas()
				.stream()
				.map(this::converterDadosConsulta)
				.collect(Collectors.toList());
	}

	public List<DadosListagemConsultaDTO> listarConsultasPorMedico(@NonNull Long id) {
		return consultaRepository.listAllConsultasByMedicoId(id)
				.stream()
				.map(this::converterDadosConsulta)
				.collect(Collectors.toList());
	}

	public DadosListagemConsultaDTO consultasPorId(@NonNull Long id) throws RegraNegocioException {
		try {
			var consulta = consultaRepository.getReferenceById(id);
			return converterDadosConsulta(consulta);
		} catch (EntityNotFoundException e) {
			throw new RegraNegocioException();
		}
	}

	public DadosDetalhamentoConsultaDTO agendar(DadosAgendamentoConsultaDTO dados)
		throws PacienteNaoEncontradoException, MedicoNaoEncontradoException, ValidacaoException {

		if (!pacienteRepository.existsById(dados.idPaciente())) {
			throw new PacienteNaoEncontradoException();
		}

		if (dados.idMedico() != null && !medicoRepository.existsById(dados.idMedico())) {
			throw new MedicoNaoEncontradoException();
		}

		var dataTermino = dados.data().plusMinutes(dados.duracao());


		//VALIDAÇÕES
		validadores.forEach(v -> v.validar(dados));

		var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
		var medico = escolherMedico(dados, dataTermino);
		if (medico == null && dados.idMedico() == null) {
			throw new ValidacaoException("Não há medicos disponíveis nesta data e horário");
		}
		if (medico == null) {
			throw new ValidacaoException("O médico não está disponíveis nesta data e horário");
		}

		var consulta = new Consulta(null, medico, paciente, dados.data(), dataTermino, null);

		consultaRepository.save(consulta);

		return new DadosDetalhamentoConsultaDTO(consulta);
	}

	public void cancelar(DadosCancelamentoConsultaDTO dados) {
	if (!consultaRepository.existsById(dados.idConsulta())) {
		throw new ValidacaoException("Id da consulta informado não existe!");
	}

	validadoresCancelamento.forEach(v -> v.validar(dados));

	var consulta = consultaRepository.getReferenceById(dados.idConsulta());
	consulta.cancelar(dados.motivo());
	}

	private Medico escolherMedico(DadosAgendamentoConsultaDTO dados, LocalDateTime dataTermino) {

		if (dados.idMedico() != null) {
			return medicoRepository.escolherMedicoLivreNaData(dados.idMedico(), dados.data(), dataTermino);
		}

		if (dados.especialidade() == null) {
			throw new ValidacaoException("Especialidade é obrigatória quando o Médico não for escolhido");
		}

		return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.data());
	}

	private DadosListagemConsultaDTO converterDadosConsulta(Consulta consulta) {
		return new DadosListagemConsultaDTO(
				consulta.getId(),
				new DadosResumidosMedicoDTO(
						consulta.getMedico().getNome(),
						consulta.getMedico().getCrm(),
						consulta.getMedico().getEspecialidade()),
				new DadosResumidosPacienteDTO(
						consulta.getPaciente().getNome(),
						consulta.getPaciente().getCpf()),
				consulta.getData(),
				consulta.getDataTermino());
	}

}
