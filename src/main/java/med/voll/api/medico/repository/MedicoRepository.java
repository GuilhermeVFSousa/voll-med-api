package med.voll.api.medico.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.transaction.Transactional;
import med.voll.api.medico.Especialidade;
import med.voll.api.medico.domain.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface MedicoRepository extends JpaRepository<Medico, Long> {

	Page<Medico> findAllByAtivoTrue(Pageable pageable);

	Page<Medico> findAllByAtivoFalse(Pageable pageable);

	@Query("""
			SELECT m FROM Medico m
			WHERE
			m.ativo = 1
			AND
			m.especialidade = :especialidade
			AND
			m.id NOT IN(
				SELECT c.medico.id from Consulta c
				WHERE
				c.data = :data
			)
			ORDER BY rand()
			limit 1
			""")
	Medico escolherMedicoAleatorioLivreNaData(Especialidade especialidade, LocalDateTime data);
	
	 @Query("""
	            select m.ativo
	            from Medico m
	            where
	            m.id = :id
	            """)
	Boolean findAtivoById(Long id);

	@Query("""
			SELECT m FROM Medico m
			WHERE
			m.ativo = 1
			AND
			m.id = :idMedico
			AND
			m.id NOT IN(
				SELECT c.medico.id from Consulta c
				WHERE
				(c.data >= :data AND c.data < :dataTermino)
				OR
				(c.dataTermino > :data AND c.dataTermino <= :dataTermino)
				OR
				(c.data <= :data AND c.dataTermino >= :dataTermino)
			)
			""")
	Medico escolherMedicoLivreNaData(Long idMedico, LocalDateTime data, LocalDateTime dataTermino);

	@Query("FROM Medico m " +
			"WHERE m.id = :id")
	Optional<Medico> findMedicoById(@NonNull Long id);

	@Query("SELECT m.email FROM Medico m " +
			"WHERE m.email = :email")
	Optional<Medico> checkExistentEmail(@NonNull String email);

	@Query("SELECT m.crm FROM Medico m " +
			"WHERE m.crm = :crm")
	Optional<Medico> checkExistentCRM(@NonNull String crm);
}
