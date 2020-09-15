package br.com.az.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.az.dto.EventoDto;
import br.com.az.model.Evento;
import br.com.az.model.Usuario;
import br.com.az.response.Response;
import br.com.az.security.JwtTokenUtil;
import br.com.az.service.EventoService;
import br.com.az.service.UsuarioService;

@RestController
@RequestMapping("/calendario")
public class CalendarioController {

	private static final Logger log = LoggerFactory.getLogger(CalendarioController.class);

	private static final String TOKEN_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	@Autowired
	private EventoService eventoService;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	 * Valida e cadastra um novo evento
	 * 
	 * @param eventoDto
	 * @param result
	 * @param request
	 * @return ResponseEntity<Response<EventoDto>>
	 */
	@PostMapping
	public ResponseEntity<Response<EventoDto>> cadastrar(@Valid @RequestBody EventoDto eventoDto, BindingResult result,
			HttpServletRequest request) {
		log.info("Cadastrando novo evento");
		Response<EventoDto> response = new Response<EventoDto>();
		eventoService.validarDados(eventoDto, result);

		if (result.hasErrors()) {
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			log.error("Erro ao cadastrar evento {}", response.getErrors().toString());
			return ResponseEntity.badRequest().body(response);
		}

		Optional<Usuario> usuario = usuarioService
				.buscarPorEmail(jwtTokenUtil.getUsernameFromToken(obterToken(request)));

		Evento evento = eventoService.converterDtoParaEvento(eventoDto);
		usuario.ifPresent(u -> evento.setUsuario(u));
		eventoService.salvar(evento);
		response.setData(eventoDto);
		return ResponseEntity.ok().body(response);
	}

	/**
	 * Lista todos os eventos cadastrados
	 * 
	 * @return ResponseEntity<Response<List<EventoDto>>>
	 */
	@GetMapping
	public ResponseEntity<Response<List<EventoDto>>> listar() {
		log.info("Listando todos os eventos");
		Response<List<EventoDto>> response = new Response<List<EventoDto>>();
		List<EventoDto> listaEventoDto = eventoService.listarTodos().stream()
				.map(e -> eventoService.converterEventoParaDto(e)).collect(Collectors.toList());
		response.setData(listaEventoDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * Busca pelo Id de um evento cadastrado
	 * 
	 * @param id
	 * @param result
	 * @return ResponseEntity<Response<EventoDto>>
	 */
	@GetMapping("/evento")
	public ResponseEntity<Response<EventoDto>> buscar(@RequestParam Long id, BindingResult result) {
		log.info("Buscando Evento id={}", id);
		Response<EventoDto> response = new Response<EventoDto>();
		Optional<Evento> evento = Optional.ofNullable(eventoService.buscar(id));

		if (!evento.isPresent()) {
			log.error("Evento não encontrado id={}", id);
			result.getAllErrors().add(new ObjectError("evento", "Evento não encontrado"));
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(eventoService.converterEventoParaDto(evento.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Retorna o token a partir do header da requisiçao
	 * 
	 * @param request
	 * @return String
	 */
	private String obterToken(HttpServletRequest request) {
		Optional<String> token = Optional.ofNullable(request.getHeader(TOKEN_HEADER));
		if (token.isPresent() && token.get().startsWith(BEARER_PREFIX)) {
			token = Optional.of(token.get().substring(7));
		}

		if (token.isPresent()) {
			return token.get();
		}
		return "";
	}

}
