package med.voll.api.medico.controller;

import med.voll.api.medico.*;
import med.voll.api.medico.DTO.DadosAtualizacaoMedicoDTO;
import med.voll.api.medico.DTO.DadosCadastroMedicoDTO;
import med.voll.api.medico.DTO.DadosListagemMedicoDTO;
import med.voll.api.medico.domain.Medico;
import med.voll.api.medico.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    @Transactional
    public ResponseEntity<?> cadastrar(@RequestBody @Valid DadosCadastroMedicoDTO dados, UriComponentsBuilder uriComponentsBuilder) {
    	var medico = new Medico(dados);
    	
        medicoRepository.save(medico);
        
        var uri = uriComponentsBuilder.path("medicos/{id}").buildAndExpand(medico.getId()).toUri();
        
        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedicoDTO(medico));
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
    
    @PutMapping
    @Transactional
    public ResponseEntity<?> atualizar(@RequestBody @Valid DadosAtualizacaoMedicoDTO dados) {
    	var medico = medicoRepository.getReferenceById(dados.id());
    	medico.atualizarInformacoes(dados);
    	
    	return ResponseEntity.ok(new DadosDetalhamentoMedicoDTO(medico));
    }
    
	/*
	 * @DeleteMapping("/{id}")
	 * 
	 * @Transactional public void excluir(@PathVariable Long id) {
	 * medicoRepository.deleteById(id); }
	 */
    
    // exclusão lógica
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> excluir(@PathVariable Long id) {
    	var medico = medicoRepository.getReferenceById(id);
    	medico.inativar();

    	return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<?> detalhar(@PathVariable Long id) {
    	var medico = medicoRepository.getReferenceById(id);

    	return ResponseEntity.ok(new DadosDetalhamentoMedicoDTO(medico));
    }

}
