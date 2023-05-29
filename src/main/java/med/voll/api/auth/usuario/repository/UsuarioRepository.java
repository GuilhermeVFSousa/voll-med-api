package med.voll.api.auth.usuario.repository;

import med.voll.api.auth.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	UserDetails findByLogin(String login);

	@Query("FROM Usuario u " +
			"WHERE u.login = :email")
	Optional<Usuario> findByEmail(String email);

	@Query("FROM Usuario u " +
			"ORDER BY u.id ASC")
	List<Usuario> findAllOrderById();

}
