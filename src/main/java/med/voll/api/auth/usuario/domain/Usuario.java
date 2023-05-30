package med.voll.api.auth.usuario.domain;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;
import med.voll.api.auth.usuario.DTO.DadosUsuarioDTO;
import med.voll.api.auth.usuario.enums.Roles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {
	@Serial
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String login;

	private String senha;

	private String nome;

	@Column(columnDefinition = "longtext")
	private String imagem;

	private boolean superUser = false;

	private boolean ativo = true;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (superUser) {
			return List.of(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.name()));
		} else {
			return List.of(new SimpleGrantedAuthority(Roles.ROLE_NORMAL_USER.name()));
		}
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public static DadosUsuarioDTO converterDomainToDadosUsuarioDTO(Usuario usuario) {
		return new DadosUsuarioDTO(
				usuario.id != null ? usuario.id : null,
				usuario.getLogin(),
				usuario.getPassword(),
				usuario.getNome(),
				null,
				usuario.isSuperUser(),
				usuario.isAtivo()
		);
	}

	public static Usuario converterDadosUsuarioDtoToDomain(DadosUsuarioDTO dto) {
		return new Usuario(
				dto.id() != null ? dto.id() : null,
				dto.login(),
				dto.password(),
				dto.nome(),
				dto.imagem() != null ? dto.imagem() : null,
				dto.superUser(),
				dto.ativo()
		);
	}

}
