package med.voll.api.consulta.repository;

import java.time.LocalDateTime;
import java.util.List;

import med.voll.api.consulta.domain.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
	
    boolean existsByPacienteIdAndDataBetween(Long idPaciente, LocalDateTime primeiroHorario, LocalDateTime ultimoHorario);

    boolean existsByMedicoIdAndDataAndMotivoCancelamentoIsNull(Long idMedico, LocalDateTime data);

    @Query("FROM Consulta c")
    List<Consulta> listAllConsultas();

    @Query("FROM Consulta c " +
            "WHERE c.medico.id = :id")
    List<Consulta> listAllConsultasByMedicoId(Long id);

}
