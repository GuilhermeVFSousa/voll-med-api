package med.voll.api.paciente.DTO;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import med.voll.api.paciente.domain.Paciente;
import med.voll.api.endereco.domain.Endereco;

public record DadosDetalhamentoPacienteDTO(
        @Nullable
        Long id,
        @NotNull
        String nome,
        @NotNull
        String email,
        @NotBlank
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}")
        String cpf,
        @NotNull
        String telefone,
        @NotNull
        Endereco endereco) {

    public DadosDetalhamentoPacienteDTO(Paciente paciente) {
        this(paciente.getId(), paciente.getNome(), paciente.getEmail(), paciente.getCpf(), paciente.getTelefone(), paciente.getEndereco());
    }
}
