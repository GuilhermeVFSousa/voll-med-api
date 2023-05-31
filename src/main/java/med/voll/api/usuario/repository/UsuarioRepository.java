package med.voll.api.usuario.repository;

import med.voll.api.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	UserDetails findByLogin(String login);

	@Query("FROM Usuario u " +
			"WHERE u.login = :email")
	Optional<Usuario> findByEmail(@NonNull String email);

	@Query("FROM Usuario u " +
			"ORDER BY u.id ASC")
	List<Usuario> findAllOrderById();

	@Query("SELECT u.imagem FROM Usuario u " +
			"WHERE u.login = :email")
	Optional<String> findUsuarioImagemByEmail(@NonNull String email);

	@Query("FROM Usuario u " +
			"WHERE u.id = :id")
	Optional<Usuario> findUsuarioById(@NonNull Long id);

}
