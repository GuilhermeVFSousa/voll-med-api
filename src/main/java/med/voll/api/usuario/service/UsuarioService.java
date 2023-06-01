package med.voll.api.usuario.service;

import med.voll.api.usuario.DTO.DadosUsuarioComSenhaDTO;
import med.voll.api.usuario.DTO.DadosUsuarioDTO;
import med.voll.api.usuario.domain.Usuario;
import med.voll.api.usuario.repository.UsuarioRepository;
import med.voll.api.exceptions.EmailExistenteException;
import med.voll.api.exceptions.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<DadosUsuarioDTO> usuarios() {
        return repository.findAllOrderById().stream()
                .map(Usuario::converterDomainToDadosUsuarioDTO)
                .collect(Collectors.toList());
    }

    public Usuario criarUsuario(DadosUsuarioComSenhaDTO dto) throws EmailExistenteException {
        var usuario = Usuario.converterDadosUsuarioComSenhaDtoToDomain(dto);
        validarEmail(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setAtivo(true);
        return repository.save(usuario);
    }

    public Usuario editarUsuario(@NonNull String email,
                                 @NonNull DadosUsuarioComSenhaDTO dto,
                                 @NonNull boolean atualizarSenha,
                                 @NonNull boolean imageRemove)
            throws UsuarioNaoEncontradoException, EmailExistenteException {
        var usuarioAtual = repository.findByEmail(email).orElseThrow(UsuarioNaoEncontradoException::new);
        var changes = false;

        if (!usuarioAtual.getLogin().equals(dto.login())){
            validarEmail(dto.login());
            usuarioAtual.setLogin(dto.login());
            changes = true;
        }

        if (!usuarioAtual.getNome().equals(dto.nome())){
            usuarioAtual.setNome(dto.nome());
            changes = true;
        }

        if (imageRemove){
            usuarioAtual.setImagem(null);
            changes = true;
        }

        if (dto.imagem() != null){
            usuarioAtual.setImagem(dto.imagem());
            changes = true;
        }

        if (atualizarSenha){
            usuarioAtual.setSenha(passwordEncoder.encode(dto.password()));
            changes = true;
        }

        return changes ? repository.save(usuarioAtual) : usuarioAtual;
    }

    public void inativarUsuario(@NonNull Long id) throws UsuarioNaoEncontradoException {
        var usuario = repository.findUsuarioById(id).orElseThrow(UsuarioNaoEncontradoException::new);
        usuario.setAtivo(false);
        repository.save(usuario);
    }

    public void ativarUsuario(@NonNull Long id) throws UsuarioNaoEncontradoException {
        var usuario = repository.findUsuarioById(id).orElseThrow(UsuarioNaoEncontradoException::new);
        usuario.setAtivo(true);
        repository.save(usuario);
    }

    public void deletarUsuario(@NonNull Long id) throws UsuarioNaoEncontradoException {
        var usuario = repository.findUsuarioById(id).orElseThrow(UsuarioNaoEncontradoException::new);
        repository.delete(usuario);
    }

    public Usuario obterUsuarioPorEmail(@NonNull String email) throws UsuarioNaoEncontradoException {
        return repository.findByEmail(email).orElseThrow(UsuarioNaoEncontradoException::new);
    }

    public Usuario obterUsuarioPorId(@NonNull Long id) throws UsuarioNaoEncontradoException {
        return repository.findUsuarioById(id).orElseThrow(UsuarioNaoEncontradoException::new);
    }

    public String obterImagemPorUsuario(@NonNull String email) throws UsuarioNaoEncontradoException {
        var checkEmail = repository.findByEmail(email).orElseThrow(UsuarioNaoEncontradoException::new);
        return repository.findUsuarioImagemByEmail(checkEmail.getLogin()).orElse("");
    }

    private void validarEmail(Usuario usuario) throws EmailExistenteException{
        var email = repository.findByEmail(usuario.getLogin());
        if (email.isPresent())
            throw new EmailExistenteException();
    }

    private void validarEmail(String email) throws EmailExistenteException{
        var checkEmail = repository.findByEmail(email);
        if (checkEmail.isPresent())
            throw new EmailExistenteException();
    }
}
