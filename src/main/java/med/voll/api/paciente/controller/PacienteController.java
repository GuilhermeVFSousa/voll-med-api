package med.voll.api.paciente.controller;

import jakarta.validation.Valid;
import med.voll.api.exceptions.CPFExistenteException;
import med.voll.api.exceptions.EmailExistenteException;
import med.voll.api.exceptions.HttpErrorResponseException;
import med.voll.api.exceptions.PacienteNaoEncontradoException;
import med.voll.api.paciente.DTO.DadosAtualizacaoPacienteDTO;
import med.voll.api.paciente.DTO.DadosCadastroPacienteDTO;
import med.voll.api.paciente.DTO.DadosDetalhamentoPacienteDTO;
import med.voll.api.paciente.DTO.DadosListagemPacienteDTO;
import med.voll.api.paciente.domain.Paciente;
import med.voll.api.paciente.repository.PacienteRepository;
import med.voll.api.paciente.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("pacientes")
@SecurityRequirement(name = "bearer-key")
public class PacienteController {

    @Autowired
    private PacienteService service;

    @Autowired
    private PacienteRepository repository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoPacienteDTO> cadastrar(@RequestBody @Valid DadosCadastroPacienteDTO dados, UriComponentsBuilder uriBuilder) {
        try {
            var paciente = service.cadastrarPaciente(dados);
            var dto = Paciente.domainToDadosDetalhamentoPacienteDTO(paciente);
            var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(paciente.getId()).toUri();
            return ResponseEntity.created(uri).body(dto);
        } catch (EmailExistenteException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "E-mail em uso");
        } catch (CPFExistenteException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "CPF em uso");
        }
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPacienteDTO>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemPacienteDTO::new);
        return ResponseEntity.ok(page);
    }
    
    @GetMapping("/inativos")
    public ResponseEntity<Page<DadosListagemPacienteDTO>> listarInativos(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoFalse(paginacao).map(DadosListagemPacienteDTO::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping({"/{id}", "/{id}/"})
    @Transactional
    public ResponseEntity<DadosDetalhamentoPacienteDTO> update(@NonNull @PathVariable Long id,
                                       @NonNull @RequestBody @Valid DadosDetalhamentoPacienteDTO dto) {
        try {
            var updatedPaciente = service.atualizarPaciente(id, dto);
            var newDto = Paciente.domainToDadosDetalhamentoPacienteDTO(updatedPaciente);

            return ResponseEntity.ok(newDto);
        } catch (PacienteNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Paciente não encontrado");
        } catch (EmailExistenteException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "E-mail em uso");
        } catch (CPFExistenteException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "CPF em uso");
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        var paciente = repository.getReferenceById(id);
        paciente.excluir();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoPacienteDTO> getPacienteById(@NonNull @Valid @PathVariable Long id) {
        try {
            var paciente = service.buscarPacientePorId(id);
            var dto = Paciente.domainToDadosDetalhamentoPacienteDTO(paciente);
            return ResponseEntity.ok(dto);
        } catch (PacienteNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Paciente não encontrado");
        }
    }


}

