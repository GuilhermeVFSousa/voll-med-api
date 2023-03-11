package med.voll.api.domain.medico;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
