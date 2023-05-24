package med.voll.api.endereco.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosEnderecoDTO(
		
		@NotBlank
		String logradouro, 
		
		@NotBlank
		String bairro, 
		
		@NotBlank
		@Pattern(regexp = "\\d{8}") // 8 digitos
		String cep, 
		
		@NotBlank
		String cidade, 
		
		@NotBlank
		String uf, 
		
		
		String complemento, 
		
		
		String numero
		) {
}
