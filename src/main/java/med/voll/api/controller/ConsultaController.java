package med.voll.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.consulta.DadosCancelamentoConsulta;
import med.voll.api.service.AgendaDeConsultas;

@RestController
@RequestMapping("consultas")
@SecurityRequirement(name = "bearer-key")
public class ConsultaController {
	
	@Autowired
	private AgendaDeConsultas agendaDeConsultas;
	
	@PostMapping
	@Transactional
	public ResponseEntity<?> agendar(@RequestBody @Valid DadosAgendamentoConsulta dados) {
		var dto = agendaDeConsultas.agendar(dados);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping
    @Transactional
    public ResponseEntity<?> cancelar(@RequestBody @Valid DadosCancelamentoConsulta dados) {
		agendaDeConsultas.cancelar(dados);
        return ResponseEntity.noContent().build();
    }

}
