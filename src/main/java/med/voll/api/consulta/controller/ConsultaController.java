package med.voll.api.consulta.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import med.voll.api.consulta.DTO.DadosAgendamentoConsultaDTO;
import med.voll.api.consulta.DTO.DadosCancelamentoConsultaDTO;
import med.voll.api.consulta.DTO.DadosEditarDataConsultaDTO;
import med.voll.api.consulta.DTO.DadosListagemConsultaDTO;
import med.voll.api.consulta.service.ConsultaService;
import med.voll.api.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("consultas")
@SecurityRequirement(name = "bearer-key")
public class ConsultaController {

	@Autowired
	private ConsultaService consultaService;

	@GetMapping({"", "/"})
	public ResponseEntity<List<DadosListagemConsultaDTO>>getAll() {
		return ResponseEntity.ok().body(consultaService.listarConsultas());
	}

	@GetMapping({"/medico/{id}", "/medico/{id}/"})
	public ResponseEntity<List<DadosListagemConsultaDTO>>getAllByMedico(@PathVariable Long id) {
		return ResponseEntity.ok().body(consultaService.listarConsultasPorMedico(id));
	}

	@GetMapping({"/{id}", "/{id}/"})
	public ResponseEntity<DadosListagemConsultaDTO>getConsultaById(@PathVariable Long id) {
		DadosListagemConsultaDTO response = null;
		try {
			response = consultaService.consultasPorId(id);
		} catch (ConsultaNaoEncontradaException e) {
			throw new RecursoNaoEncontradoException(HttpStatus.NOT_FOUND, "Consulta não encontrada");
		}
		return ResponseEntity.ok().body(response);
	}

	@PutMapping({"/{id}", "/{id}/"})
	public ResponseEntity<?>editConsultaDate(@PathVariable Long id, @RequestBody @Valid @NotNull DadosEditarDataConsultaDTO dados)
			throws MethodArgumentNotValidException {
		try {
			consultaService.editarDataConsulta(id, dados);
			return ResponseEntity.ok().build();
		} catch (ConsultaNaoEncontradaException e) {
			throw new RecursoNaoEncontradoException(HttpStatus.NOT_FOUND, "Consulta não encontrada");
		}
	}

	@DeleteMapping({"/{id}", "/{id}/"})
	public ResponseEntity<?> excluirConsulta(@PathVariable @NonNull Long id) {
		try {
			consultaService.excluirConsulta(id);
			return ResponseEntity.noContent().build();
		} catch (ConsultaNaoEncontradaException e) {
			throw new RecursoNaoEncontradoException(HttpStatus.NOT_FOUND, "Consulta não encontrada");
		}
	}

		@PostMapping
		@Transactional
		public ResponseEntity<?> agendar(@RequestBody @Valid @NotNull DadosAgendamentoConsultaDTO dados)
    throws MethodArgumentNotValidException {
			var authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (authentication == null)
				throw new UsuarioEncontradoException();
			var email = ((UserDetails)authentication).getUsername();
			try {
				var dto = consultaService.agendar(dados);
				return ResponseEntity.ok(dto);
			} catch (PacienteNaoEncontradoException e) {
				throw new RecursoNaoEncontradoException(HttpStatus.NOT_FOUND, "ID do paciente informado não existe");
			} catch (MedicoNaoEncontradoException e) {
				throw new RecursoNaoEncontradoException(HttpStatus.NOT_FOUND, "ID do médico informado não existe");
			} catch (UsuarioEncontradoException e) {
				throw new RecursoNaoEncontradoException(HttpStatus.NOT_FOUND, "O usuário não existe");
			}catch (ValidacaoException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		}

		@DeleteMapping
    @Transactional
    public ResponseEntity<?> cancelar(@RequestBody @Valid DadosCancelamentoConsultaDTO dados) {
			consultaService.cancelar(dados);
        return ResponseEntity.noContent().build();
    }

}
