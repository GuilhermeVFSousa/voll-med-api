package med.voll.api.medico.controller;

import med.voll.api.exceptions.*;
import med.voll.api.medico.*;
import med.voll.api.medico.DTO.DadosAtualizacaoMedicoDTO;
import med.voll.api.medico.DTO.DadosCadastroMedicoDTO;
import med.voll.api.medico.DTO.DadosListagemMedicoDTO;
import med.voll.api.medico.domain.Medico;
import med.voll.api.medico.repository.MedicoRepository;
import med.voll.api.medico.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.medico.DTO.DadosDetalhamentoMedicoDTO;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("medicos")
@SecurityRequirement(name = "bearer-key")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private MedicoService service;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosDetalhamentoMedicoDTO> cadastrar(@NonNull @RequestBody @Valid DadosCadastroMedicoDTO dados,
                                       @NonNull UriComponentsBuilder uriComponentsBuilder) {
       try {
           var medico = service.cadastrarMedico(dados);
           var dto = Medico.domainToDadosDetalhamentoMedico(medico);
           var uri = uriComponentsBuilder.path("medicos/{id}").buildAndExpand(medico.getId()).toUri();
           return ResponseEntity.created(uri).body(dto);

       } catch (EmailExistenteException e) {
           throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "E-mail em uso");
       } catch (CRMExistenteException e) {
           throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "CRM em uso");
       }
    }
    
    @GetMapping
    public ResponseEntity<Page<DadosListagemMedicoDTO>> listar(@PageableDefault(size = 1000, sort = {"nome"}) Pageable pageable) {
        var  page = medicoRepository.findAllByAtivoTrue(pageable).map(DadosListagemMedicoDTO::new);
        return ResponseEntity.ok(page);
    }
    @GetMapping("/inativos")
    public ResponseEntity<Page<DadosListagemMedicoDTO>>listarInativos(@PageableDefault(size = 10, sort = {"nome"}) Pageable pageable) {
        var page = medicoRepository.findAllByAtivoFalse(pageable).map(DadosListagemMedicoDTO::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/especialidades")
    public ResponseEntity<List<String>> listarEspecialidades() {
        var especialidades = Arrays.stream(Especialidade.values()).map(Especialidade::getNome).toList();
        return ResponseEntity.ok(especialidades);
    }
    
    @PutMapping({"/{id}", "/{id}/"})
    public ResponseEntity<DadosDetalhamentoMedicoDTO> update(@NonNull @PathVariable Long id,
                                                             @NonNull @RequestBody @Valid DadosDetalhamentoMedicoDTO dados) {
       try {
           var updatedMedico = service.atualizarMedico(id, dados);
           var dto = Medico.domainToDadosDetalhamentoMedico(updatedMedico);

           return ResponseEntity.ok(dto);

       } catch (MedicoNaoEncontradoException e) {
           throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Médico não encontrado");
       } catch (EmailExistenteException e) {
           throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "E-mail em uso");
       } catch (CRMExistenteException e) {
           throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "CRM em uso");
       }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@NonNull @Valid @PathVariable Long id) {
        try {
            service.excluirMedico(id);
            return ResponseEntity.noContent().build();
        } catch (MedicoNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Médico não encontrado");
        } catch (DadosVinculadosException e) {
            throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Médico com consultas agendadas não pode ser excluído");
        }
    }
    
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoMedicoDTO> getMedicoById(@PathVariable Long id) {
        try {
            var medico = service.buscarMedicoPorId(id);
            var dto = Medico.domainToDadosDetalhamentoMedico(medico);
            return ResponseEntity.ok(dto);
        } catch (MedicoNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Médico não encontrado");
        }
    }

}
