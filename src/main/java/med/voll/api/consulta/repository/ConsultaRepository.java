package med.voll.api.consulta.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import med.voll.api.consulta.domain.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
	
    boolean existsByPacienteIdAndDataBetween(Long idPaciente, LocalDateTime primeiroHorario, LocalDateTime ultimoHorario);

    boolean existsByMedicoIdAndDataAndMotivoCancelamentoIsNull(Long idMedico, LocalDateTime data);

    @Query("FROM Consulta c")
    List<Consulta> listAllConsultas();

    @Query("FROM Consulta c " +
            "WHERE c.medico.id = :id " +
            "AND (c.data BETWEEN :initialDate AND :finalDate)")
    List<Consulta> listAllConsultasByDate(@NonNull Long id,
                                          @NonNull LocalDateTime initialDate,
                                          @NonNull LocalDateTime finalDate);

    @Query("FROM Consulta c " +
            "WHERE c.medico.id = :id")
    List<Consulta> listAllConsultasByMedicoId(@NonNull Long id);

    @Query("FROM Consulta c " +
            "WHERE c.paciente.id = :id")
    List<Consulta> findConsultaByPacienteId(@NonNull Long id);

}
