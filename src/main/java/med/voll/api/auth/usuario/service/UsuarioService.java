package med.voll.api.auth.usuario.service;

import med.voll.api.auth.usuario.DTO.DadosAutenticacaoDTO;
import med.voll.api.auth.usuario.DTO.DadosUsuarioDTO;
import med.voll.api.auth.usuario.domain.Usuario;
import med.voll.api.auth.usuario.repository.UsuarioRepository;
import med.voll.api.exceptions.EmailExistenteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<DadosUsuarioDTO> usuarios() {
        return repository.findAllOrderById().stream().map(this::domainToDto).collect(Collectors.toList());
    }

    public Usuario criarUsuario(DadosUsuarioDTO dto) throws EmailExistenteException {
        var usuario = dtoToDomain(dto);
        validarEmail(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setAtivo(true);
        return repository.save(usuario);
    }

    public Optional<Usuario> obterUsuarioPorEmail(@NonNull String email) throws EmailExistenteException {
        var usuario = repository.findByEmail(email).orElseThrow(EmailExistenteException::new);
        return Optional.of(usuario);
    }

    public String obterImagemPorUsuario(@NonNull String email) {
        var checkEmail = repository.findByEmail(email).orElseThrow(EmailExistenteException::new);
        return repository.findUsuarioImagemByEmail(checkEmail.getLogin()).orElse("");
    }

    private Usuario dtoToDomain(DadosUsuarioDTO dto) {
        return Usuario.converterDadosUsuarioDtoToDomain(dto);
    }

    private DadosUsuarioDTO domainToDto(Usuario usuario) {
        return Usuario.converterDomainToDadosUsuarioDTO(usuario);
    }

    private void validarEmail(Usuario usuario) throws EmailExistenteException{
        var email = repository.findByEmail(usuario.getLogin());
        if (email.isPresent())
            throw new EmailExistenteException();
    }
}
