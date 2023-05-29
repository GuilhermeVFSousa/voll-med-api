package med.voll.api.auth.usuario.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import med.voll.api.auth.usuario.DTO.DadosUsuarioDTO;
import med.voll.api.auth.usuario.enums.Roles;
import med.voll.api.auth.usuario.service.UsuarioService;
import med.voll.api.exceptions.EmailExistenteException;
import med.voll.api.exceptions.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public ResponseEntity<List<DadosUsuarioDTO>> usuarios(@NonNull @CurrentSecurityContext SecurityContext context) {
        if (UsuarioController.isSuperUser(context)){
            var users = service.usuarios();
            return ResponseEntity.ok().body(users);
        } else {
            throw new RecursoNaoEncontradoException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }
    }


    @PostMapping
    public ResponseEntity<?> create(@NotNull @Valid @RequestBody DadosUsuarioDTO dto,
                                    @NonNull @CurrentSecurityContext SecurityContext context) {
        if (UsuarioController.isSuperUser(context)){
            try {
                var user = service.criarUsuario(dto);
                URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
                return ResponseEntity.created(uri).build();
            } catch (EmailExistenteException e) {
                throw new RecursoNaoEncontradoException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
            }
        } else {
            throw new RecursoNaoEncontradoException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }
    }

    private static boolean isSuperUser(SecurityContext context) {
        var authentication = context.getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.name()));
    }
}
