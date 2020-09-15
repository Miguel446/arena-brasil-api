package br.com.az.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.az.dto.UsuarioDto;
import br.com.az.model.Usuario;
import br.com.az.response.Response;
import br.com.az.service.UsuarioService;

@RestController
@RequestMapping("/cadastrar")
public class CadastroController {

	private static final Logger log = LoggerFactory.getLogger(CadastroController.class);

	@Autowired
	private UsuarioService usuarioService;

	/**
	 * Cadastra um novo usuário no sistema
	 * 
	 * @param usuarioDto
	 * @param result
	 * @return ResponseEntity<Response<UsuarioDto>>
	 */
	@PostMapping
	public ResponseEntity<Response<UsuarioDto>> cadastrar(@Valid @RequestBody UsuarioDto usuarioDto,
			BindingResult result) {
		log.info("Cadastrando novo usuário para email={}", usuarioDto.getEmail());
		Response<UsuarioDto> response = new Response<UsuarioDto>();
		usuarioService.validarDados(usuarioDto, result);

		if (result.hasErrors()) {
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			log.error("Erro ao cadastrar usuário {}", response.getErrors().toString());
			return ResponseEntity.badRequest().body(response);
		}

		Usuario usuario = usuarioService.converterDtoParaUsuario(usuarioDto);
		usuarioService.salvar(usuario);
		response.setData(usuarioService.converterUsuarioParaDto(usuario));
		return ResponseEntity.ok(response);
	}

}
