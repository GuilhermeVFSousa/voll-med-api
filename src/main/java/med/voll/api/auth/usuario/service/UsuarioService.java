package med.voll.api.auth.usuario.service;

import med.voll.api.auth.usuario.DTO.DadosAutenticacaoDTO;
import med.voll.api.auth.usuario.DTO.DadosUsuarioDTO;
import med.voll.api.auth.usuario.domain.Usuario;
import med.voll.api.auth.usuario.repository.UsuarioRepository;
import med.voll.api.exceptions.EmailExistenteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Set<DadosUsuarioDTO> usuarios() {
        return repository.findAll().stream().map(this::domainToDto).collect(Collectors.toSet());
    }

    public Usuario criarUsuario(DadosAutenticacaoDTO dto) throws EmailExistenteException {
        var usuario = dtoToDomain(dto);
        validarEmail(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    private Usuario dtoToDomain(DadosAutenticacaoDTO dto) {
        return new Usuario(null, dto.login(), dto.senha(), false, true);
    }

    private DadosUsuarioDTO domainToDto(Usuario usuario) {
        return new DadosUsuarioDTO(usuario.getId(), usuario.getLogin(), usuario.isSuperUser());
    }

    private void validarEmail(Usuario usuario) throws EmailExistenteException{
        var email = repository.findByEmail(usuario.getLogin());
        if (email.isPresent())
            throw new EmailExistenteException();
    }
}
