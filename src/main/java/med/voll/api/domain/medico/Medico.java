package med.voll.api.domain.medico;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import med.voll.api.domain.endereco.Endereco;

@Table(name = "medicos")
@Entity(name = "Medico")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Medico {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String crm;
    
    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;
    
    @Embedded
    private Endereco endereco;
    
    private Boolean ativo;
    
    public Medico(DadosCadastroMedico dados) {
		this.id = null;
		this.nome = dados.nome();
		this.telefone = dados.telefone();
		this.email = dados.email();
		this.crm = dados.crm();
		this.especialidade = dados.especialidade();
		this.endereco = new Endereco(dados.endereco());
		this.ativo = true;
	}

	public void atualizarInformacoes(DadosAtualizacaoMedico dados) {
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

	public void inativar() {
		this.ativo = false;
		
	}
	
}
