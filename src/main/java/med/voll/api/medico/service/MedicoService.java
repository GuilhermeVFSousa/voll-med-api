package med.voll.api.medico.service;

import med.voll.api.exceptions.DadosVinculadosException;
import med.voll.api.medico.DTO.DadosCadastroMedicoDTO;
import org.springframework.transaction.annotation.Transactional;
import med.voll.api.consulta.repository.ConsultaRepository;
import med.voll.api.exceptions.CRMExistenteException;
import med.voll.api.exceptions.EmailExistenteException;
import med.voll.api.exceptions.MedicoNaoEncontradoException;
import med.voll.api.medico.DTO.DadosDetalhamentoMedicoDTO;
import med.voll.api.medico.domain.Medico;
import med.voll.api.medico.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository repository;

    @Autowired
    private ConsultaRepository consultaRepository;

    public Medico buscarMedicoPorId(@NonNull Long id) throws MedicoNaoEncontradoException {
        return repository.findMedicoById(id).orElseThrow(MedicoNaoEncontradoException::new);
    }

    @Transactional
    public Medico cadastrarMedico(@NonNull DadosCadastroMedicoDTO dto) throws EmailExistenteException, CRMExistenteException {
        validarEmail(dto.email());
        validarCRM(dto.crm());
        var novoMedico = Medico.dadosCadastroMedicoToDomain(dto);
        return repository.save(novoMedico);
    }

    @Transactional
    public Medico atualizarMedico(@NonNull Long id, @NonNull DadosDetalhamentoMedicoDTO dto)
            throws MedicoNaoEncontradoException, EmailExistenteException, CRMExistenteException {
        var medicoAtual = repository.findMedicoById(id).orElseThrow(MedicoNaoEncontradoException::new);

        if (!medicoAtual.getEmail().equals(dto.email()))
            validarEmail(dto.email());

        if (!medicoAtual.getCrm().equals(dto.crm()))
            validarCRM(dto.crm());

        medicoAtual.setNome(dto.nome());
        medicoAtual.setEmail(dto.email());
        medicoAtual.setCrm(dto.crm());
        medicoAtual.setTelefone(dto.telefone());
        medicoAtual.setEspecialidade(dto.especialidade());
        medicoAtual.setEndereco(dto.endereco());

        return repository.save(medicoAtual);
    }

    @Transactional
    public void excluirMedico(@NonNull Long id) throws MedicoNaoEncontradoException, DadosVinculadosException {
        var medico = repository.findMedicoById(id).orElseThrow(MedicoNaoEncontradoException::new);
        var checkDataIntegrity = consultaRepository.listAllConsultasByMedicoId(id);
        if (!checkDataIntegrity.isEmpty())
            throw new DadosVinculadosException();
        repository.delete(medico);
    }

    private void validarEmail(String email) throws EmailExistenteException {
        var checkEmail = repository.checkExistentEmail(email);
        if (checkEmail.isPresent())
            throw new EmailExistenteException();
    }

    private void validarCRM(String crm) throws CRMExistenteException {
        var checkCRM = repository.checkExistentCRM(crm);
        if (checkCRM.isPresent())
            throw new CRMExistenteException();
    }
}
