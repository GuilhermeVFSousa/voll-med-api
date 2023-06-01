package med.voll.api.paciente.domain;

import jakarta.persistence.*;
import lombok.*;
import med.voll.api.paciente.DTO.DadosAtualizacaoPacienteDTO;
import med.voll.api.paciente.DTO.DadosCadastroPacienteDTO;
import med.voll.api.endereco.domain.Endereco;
import med.voll.api.paciente.DTO.DadosDetalhamentoPacienteDTO;

@Table(name = "pacientes")
@Entity(name = "Paciente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Paciente {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;

    private String telefone;

    private String cpf;

    @Embedded
    private Endereco endereco;

    private Boolean ativo;

    public Paciente(DadosCadastroPacienteDTO dados) {
        this.ativo = true;
        this.nome = dados.nome();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.cpf = dados.cpf();
        this.endereco = new Endereco(dados.endereco());
    }

    public void atualizarInformacoes(DadosAtualizacaoPacienteDTO dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
        if (dados.endereco() != null) {
            this.endereco.atualizarInformacoes(dados.endereco());
        }

    }

    public void excluir() {
        this.ativo = false;
    }

    public static DadosDetalhamentoPacienteDTO domainToDadosDetalhamentoPacienteDTO(Paciente paciente) {
        return new DadosDetalhamentoPacienteDTO(
                paciente.getId() != null ? paciente.getId() : null,
                paciente.getNome(),
                paciente.getEmail(),
                paciente.getCpf(),
                paciente.getTelefone(),
                paciente.getEndereco()

        );
    }
    public static Paciente dadosDetalhamentoPacienteToDomain(DadosDetalhamentoPacienteDTO dto) {
        return new Paciente(
                dto.id() != null ? dto.id() : null,
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.cpf(),
                dto.endereco(),
                true
        );
    }

    public static Paciente dadosCadastroPacienteToDomain(DadosCadastroPacienteDTO dto) {
        return new Paciente(
                null,
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.cpf(),
                new Endereco(dto.endereco()),
                true
        );
    }


}
