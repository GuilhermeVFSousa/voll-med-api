package med.voll.api.paciente.DTO;

import jakarta.validation.constraints.NotNull;
import med.voll.api.endereco.DTO.DadosEnderecoDTO;

public record DadosAtualizacaoPacienteDTO(
        @NotNull
        Long id,
        String nome,
        String telefone,
        DadosEnderecoDTO endereco) {
}