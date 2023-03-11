package med.voll.api.domain.medico;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import med.voll.api.domain.endereco.DadosEndereco;

public record DadosCadastroMedico(
		
		@NotBlank(message = "Nome é obrigatório")
		String nome,
		
		@NotBlank
		String telefone,
		
		@NotBlank
		@Email(message = "Formato de E-mail inválido")
		String email, 
		
		@NotBlank
		@Pattern(regexp = "\\d{4,6}", message = "Formato do CRM é inválido") // digitos (4 a 6 digitos)
		String crm, 
		
		@NotNull
		Especialidade especialidade, 
		
		@NotNull
		@Valid
		DadosEndereco endereco
		) {

}
