package med.voll.api.consulta.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import med.voll.api.consulta.DTO.*;
import med.voll.api.consulta.domain.Consulta;
import med.voll.api.consulta.repository.ConsultaRepository;
import med.voll.api.consulta.validacoes.agendamento.ValidadorAgendamentoDeConsulta;
import med.voll.api.consulta.validacoes.cancelamento.ValidadorCancelamentoDeConsulta;
import med.voll.api.exceptions.ConsultaNaoEncontradaException;
import med.voll.api.exceptions.MedicoNaoEncontradoException;
import med.voll.api.exceptions.PacienteNaoEncontradoException;
import med.voll.api.exceptions.ValidacaoException;
import med.voll.api.medico.DTO.DadosResumidosMedicoDTO;
import med.voll.api.medico.domain.Medico;
import med.voll.api.medico.repository.MedicoRepository;
import med.voll.api.paciente.DTO.DadosResumidosPacienteDTO;
import med.voll.api.paciente.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

	public List<DadosListagemConsultaDTO> listarConsultasPorMedicoEData(@NonNull Long id,
																		@NonNull String initialDate,
																		@Nullable String finalDate) {
		var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		var parsedInitialDate = LocalDateTime.parse(initialDate + "T00:00:00", formatter);
		LocalDateTime parsedFinalDate = null;
		parsedFinalDate = LocalDateTime.parse(Objects.requireNonNullElse(finalDate, initialDate) + "T23:59:00", formatter);
		return consultaRepository.listAllConsultasByDate(id, parsedInitialDate, parsedFinalDate)
				.stream()
				.map(this::converterDadosConsulta)
				.collect(Collectors.toList());
	}

	public DadosListagemConsultaDTO consultasPorId(@NonNull Long id) throws ConsultaNaoEncontradaException {
		try {
			var consulta = consultaRepository.getReferenceById(id);
			return converterDadosConsulta(consulta);
		} catch (EntityNotFoundException e) {
			throw new ConsultaNaoEncontradaException();
		}
	}

	public void editarDataConsulta(@NonNull Long id, @NotNull DadosEditarDataConsultaDTO dto) throws ConsultaNaoEncontradaException {
		try {
			var consulta = consultaRepository.getReferenceById(id);
			consulta.setData(dto.data());
			var dataTermino = dto.data().plusMinutes(dto.duracao());
			consulta.setDataTermino(dataTermino);
			consultaRepository.save(consulta);
		} catch (EntityNotFoundException e) {
			throw new ConsultaNaoEncontradaException();
		}
	}

	public void excluirConsulta(@NonNull Long id) throws ConsultaNaoEncontradaException {
		try {
			consultaRepository.deleteById(id);
		} catch (EntityNotFoundException e) {
			throw new ConsultaNaoEncontradaException();
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
			throw new ValidacaoException("O médico não está disponível nesta data e horário");
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
