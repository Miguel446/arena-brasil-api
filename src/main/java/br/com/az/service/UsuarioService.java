package br.com.az.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import br.com.az.dto.UsuarioDto;
import br.com.az.model.Usuario;
import br.com.az.repository.UsuarioRepository;
import br.com.az.util.PerfilEnum;
import br.com.az.util.SenhaUtils;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public Optional<Usuario> buscarPorEmail(String email) {
		return Optional.ofNullable(this.usuarioRepository.findFirstByStatusTrueAndEmail(email));
	}

	public void salvar(Usuario usuario) {
		usuarioRepository.save(usuario);
	}

	public void validarDados(UsuarioDto usuarioDto, BindingResult result) {
		Optional<Usuario> usuario = buscarPorEmail(usuarioDto.getEmail());

		if (usuario.isPresent()) {
			result.addError(new ObjectError("usuario", "Email já cadastrado"));
		}
	}

	public Usuario converterDtoParaUsuario(UsuarioDto usuarioDto) {
		Usuario usuario = new Usuario();
		usuario.setNome(usuarioDto.getNome());
		usuario.setEmail(usuarioDto.getEmail());
		usuario.setSenha(SenhaUtils.gerarBCrypt(usuarioDto.getSenha()));
		usuario.setPerfil(PerfilEnum.ROLE_ADMIN);
		return usuario;
	}

	public UsuarioDto converterUsuarioParaDto(Usuario usuario) {
		UsuarioDto usuarioDto = new UsuarioDto();
		usuarioDto.setEmail(usuario.getEmail());
		usuarioDto.setNome(usuario.getNome());
		return usuarioDto;
	}

}
