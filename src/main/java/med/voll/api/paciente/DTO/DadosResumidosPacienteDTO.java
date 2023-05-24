package med.voll.api.paciente.DTO;

public record DadosResumidosPacienteDTO(String nome, String cpf) {

    public DadosResumidosPacienteDTO(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }
}