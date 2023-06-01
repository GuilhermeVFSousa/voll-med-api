package med.voll.api.paciente.repository;

import med.voll.api.paciente.domain.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Page<Paciente> findAllByAtivoTrue(Pageable paginacao);
    
    Page<Paciente> findAllByAtivoFalse(Pageable paginacao);
    
    @Query("""
            select p.ativo
            from Paciente p
            where
            p.id = :id
            """)
    Boolean findAtivoById(Long id);

    @Query("FROM Paciente p " +
            "WHERE p.id = :id")
    Optional<Paciente> findUserById(@NonNull Long id);

    @Query("SELECT p.email FROM Paciente p " +
            "WHERE p.email = :email")
    Optional<String> checkExistentEmail(@NonNull String email);

    @Query("SELECT p.cpf FROM Paciente p " +
            "WHERE p.cpf = :cpf")
    Optional<String> checkExistentCPF(@NonNull String cpf);
}
