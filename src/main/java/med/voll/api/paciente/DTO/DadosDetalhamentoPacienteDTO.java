package med.voll.api.paciente.DTO;

import med.voll.api.paciente.domain.Paciente;
import med.voll.api.endereco.domain.Endereco;

public record DadosDetalhamentoPacienteDTO(Long id, String nome, String email, String cpf, String telefone, Endereco endereco) {

    public DadosDetalhamentoPacienteDTO(Paciente paciente) {
        this(paciente.getId(), paciente.getNome(), paciente.getEmail(), paciente.getCpf(), paciente.getTelefone(), paciente.getEndereco());
    }
}
