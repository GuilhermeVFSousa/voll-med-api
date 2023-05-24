package med.voll.api.paciente.DTO;

import med.voll.api.paciente.domain.Paciente;

public record DadosListagemPacienteDTO(Long id, String nome, String email, String cpf) {

    public DadosListagemPacienteDTO(Paciente paciente) {
        this(paciente.getId(), paciente.getNome(), paciente.getEmail(), paciente.getCpf());
    }

}