package med.voll.api.usuario.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import med.voll.api.usuario.DTO.DadosUsuarioComSenhaDTO;
import med.voll.api.usuario.DTO.DadosUsuarioDTO;
import med.voll.api.usuario.domain.Usuario;
import med.voll.api.usuario.enums.Roles;
import med.voll.api.usuario.service.UsuarioService;
import med.voll.api.exceptions.*;
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
import java.util.Map;

@RestController
@RequestMapping(value = "/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public ResponseEntity<List<DadosUsuarioDTO>> getUsers(@NonNull @CurrentSecurityContext SecurityContext context) {
        try {
            UsuarioController.isSuperUser(context);
            var users = service.usuarios();
            return ResponseEntity.ok().body(users);
        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }

    }

    @GetMapping({"/id/{id}", "/id/{id}/"})
    public ResponseEntity<DadosUsuarioDTO> getUserById(@NonNull @Valid @PathVariable Long id,
                                                       @NonNull @CurrentSecurityContext SecurityContext context) {
        try {
            UsuarioController.isSuperUser(context);
            var user = service.obterUsuarioPorId(id);
            var dto = Usuario.converterDomainToDadosUsuarioDTOComImagem(user);
            return ResponseEntity.ok(dto);
        } catch (UsuarioNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "Usuário não encontrado");
        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@NotNull @Valid @RequestBody DadosUsuarioComSenhaDTO dto,
                                    @NonNull @CurrentSecurityContext SecurityContext context) {
        try {
            UsuarioController.isSuperUser(context);
            if (dto.password() == null)
                throw new SenhaNulaExceptionException();
            var user = service.criarUsuario(dto);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
            return ResponseEntity.created(uri).build();
        } catch (EmailExistenteException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
        } catch (SenhaNulaExceptionException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "A senha não pode ser nula");
        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }
    }

    @GetMapping({"/email/{email}", "/email/{email}/"})
    ResponseEntity<DadosUsuarioDTO> getUserByEmail(@NonNull @Valid @PathVariable String email,
                                                   @NonNull @Valid @CurrentSecurityContext SecurityContext context) {
        try {
            validateUserEmailAndTokenEmail(email, context);
            var user = service.obterUsuarioPorEmail(email);
            var dto = Usuario.converterDomainToDadosUsuarioDTO(user);

            return ResponseEntity.ok(dto);
        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        } catch (UsuarioNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "Usuário não encontrado");
        }
    }

    @PutMapping({"/{email}", "/{email}/"})
    ResponseEntity<DadosUsuarioDTO> editUserByEmail(@NonNull @Valid @PathVariable String email,
                                                                @NonNull @Valid @RequestParam(defaultValue = "false") boolean updatePassword,
                                                                @NonNull @Valid @RequestParam(defaultValue = "false") boolean imageRemove,
                                                                @NonNull @Valid @RequestBody DadosUsuarioComSenhaDTO dto,
                                                                @NonNull @Valid @CurrentSecurityContext SecurityContext context) {
        try {
            if (UsuarioController.checkIsSuperUser(context)) {
                var updatedUser = service.editarUsuario(email, dto, updatePassword, imageRemove);
                var updatedUserDto = Usuario.converterDomainToDadosUsuarioDTO(updatedUser);

                return ResponseEntity.ok(updatedUserDto);
            } else {
                UsuarioController.validateUserEmailAndTokenEmail(email, context);
                var updatedUser = service.editarUsuario(email, dto, updatePassword, imageRemove);
                var updatedUserDto = Usuario.converterDomainToDadosUsuarioDTO(updatedUser);

                return ResponseEntity.ok(updatedUserDto);
            }

        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        } catch (EmailExistenteException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "E-mail em uso");
        } catch (UsuarioNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "Usuário não encontrado");
        }
    }

    @DeleteMapping({"/inativar/{id}", "/inativar/{id}/"})
    ResponseEntity<?> inactiveUser(@NonNull @Valid @PathVariable Long id,
                                   @NonNull @Valid @CurrentSecurityContext SecurityContext context) {
        try {
            UsuarioController.isSuperUser(context);
            service.inativarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (UsuarioNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }
    }

    @PutMapping({"/ativar/{id}", "/ativar/{id}/"})
    ResponseEntity<?> activeUser(@NonNull @Valid @PathVariable Long id,
                                   @NonNull @Valid @CurrentSecurityContext SecurityContext context) {
        try {
            UsuarioController.isSuperUser(context);
            service.ativarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (UsuarioNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }
    }

    @DeleteMapping({"/{id}", "/{id}/"})
    ResponseEntity<?> deleteUser(@NonNull @Valid @PathVariable Long id,
                                 @NonNull @Valid @CurrentSecurityContext SecurityContext context) {
        try {
            UsuarioController.isSuperUser(context);
            service.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (UsuarioNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }
    }

    @GetMapping({"/{email}/imagem", "/{email}/imagem/"})
    ResponseEntity<Map<String, String>> getUserImage(@NonNull @PathVariable String email,
                                                     @NonNull @CurrentSecurityContext SecurityContext context) {
        try {
            UsuarioController.validateUserEmailAndTokenEmail(email, context);

            var image = service.obterImagemPorUsuario(email);
            var response = Map.of("imagem", image);

            return ResponseEntity.ok().body(response);
        } catch (UsuarioNaoEncontradoException e) {
            throw new HttpErrorResponseException(HttpStatus.BAD_REQUEST, "Usuário não encontrado");
        } catch (NaoAutorizadoException e) {
            throw new HttpErrorResponseException(HttpStatus.UNAUTHORIZED, "Não autorizado");
        }
    }

    private static void validateUserEmailAndTokenEmail(String email, SecurityContext context) throws NaoAutorizadoException {
        var principal = context.getAuthentication().getPrincipal();
        var user = (Usuario) principal;

        if (!user.getLogin().equals(email))
            throw new NaoAutorizadoException();
    }

    private static void isSuperUser(SecurityContext context) throws NaoAutorizadoException {
        var authentication = context.getAuthentication();
        var superUser = authentication.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.name()));
        if (!superUser)
            throw new NaoAutorizadoException();
    }

    private static boolean checkIsSuperUser(SecurityContext context) {
        var authentication = context.getAuthentication();
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ROLE_ADMIN.name()));
    }


}
