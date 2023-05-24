package med.voll.api.medico;

import lombok.Getter;

public enum Especialidade {
    ORTOPEDIA("ortopedia"),
    CARDIOLOGIA("cardiologia"),
    GINECOLOGIA("ginecologia"),
    DERMATOLOGIA("dermatologia"),
    PEDIATRIA("pediatria");

    private String nome;
    private Especialidade(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
