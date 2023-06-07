package med.voll.api.paciente.service;

import med.voll.api.consulta.repository.ConsultaRepository;
import med.voll.api.exceptions.CPFExistenteException;
import med.voll.api.exceptions.DadosVinculadosException;
import med.voll.api.exceptions.EmailExistenteException;
import med.voll.api.exceptions.PacienteNaoEncontradoException;
import med.voll.api.paciente.DTO.DadosCadastroPacienteDTO;
import med.voll.api.paciente.DTO.DadosDetalhamentoPacienteDTO;
import med.voll.api.paciente.domain.Paciente;
import med.voll.api.paciente.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    @Autowired
    private ConsultaRepository consultaRepository;

    public Paciente buscarPacientePorId(@NonNull Long id) throws PacienteNaoEncontradoException {
        return repository.findPacienteById(id).orElseThrow(PacienteNaoEncontradoException::new);
    }

    @Transactional
    public Paciente cadastrarPaciente(@NonNull DadosCadastroPacienteDTO dto) throws EmailExistenteException, CPFExistenteException {
        validarEmail(dto.email());
        validarCPF(dto.cpf());
        var novoPaciente = Paciente.dadosCadastroPacienteToDomain(dto);
        return repository.save(novoPaciente);
    }

    @Transactional
    public Paciente atualizarPaciente(@NonNull Long id, @NonNull DadosDetalhamentoPacienteDTO dto)
            throws PacienteNaoEncontradoException, EmailExistenteException, CPFExistenteException {
       var pacienteAtual = repository.findPacienteById(id).orElseThrow(PacienteNaoEncontradoException::new);

       if (!pacienteAtual.getEmail().equals(dto.email()))
           validarEmail(dto.email());

       if (!pacienteAtual.getCpf().equals(dto.cpf()))
           validarCPF(dto.cpf());

       pacienteAtual.setNome(dto.nome());
       pacienteAtual.setEmail(dto.email());
       pacienteAtual.setCpf(dto.cpf());
       pacienteAtual.setTelefone(dto.telefone());
       pacienteAtual.setEndereco(dto.endereco());
       return repository.save(pacienteAtual);
    }

    @Transactional
    public void excluirPaciente(@NonNull Long id) throws PacienteNaoEncontradoException, DadosVinculadosException {
        var paciente = repository.findPacienteById(id).orElseThrow(PacienteNaoEncontradoException::new);
        var checkDataIntegrity = consultaRepository.findConsultaByPacienteId(id);
        if (!checkDataIntegrity.isEmpty())
            throw new DadosVinculadosException();
        repository.delete(paciente);
    }

    private void validarEmail(String email) throws EmailExistenteException {
        var checkEmail = repository.checkExistentEmail(email);
        if (checkEmail.isPresent())
            throw new EmailExistenteException();
    }

    private void validarCPF(String cpf) throws CPFExistenteException {
        var checkEmail = repository.checkExistentCPF(cpf);
        if (checkEmail.isPresent())
            throw new CPFExistenteException();
    }
}
