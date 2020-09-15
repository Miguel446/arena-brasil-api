package br.com.az.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.az.dto.EventoDto;
import br.com.az.model.Evento;
import br.com.az.response.Response;
import br.com.az.service.EventoService;

@RestController
@RequestMapping("/relatorio")
public class RelatorioController {

	private static final Logger log = LoggerFactory.getLogger(RelatorioController.class);

	@Autowired
	private EventoService eventoService;

	/**
	 * Lista todos os eventos de até 1 mês atrás
	 *
	 * @return ResponseEntity<Response<List<EventoDto>>>
	 */
	@GetMapping("/listar")
	public ResponseEntity<Response<List<EventoDto>>> listar() {
		log.info("Listando todos os eventos do mês no relatório");
		Response<List<EventoDto>> response = new Response<List<EventoDto>>();
		Evento evento = new Evento();
		evento.setDataInicial(LocalDate.now().minusMonths(1));
		evento.setDataFinal(LocalDate.now());

		List<Evento> lista = eventoService.buscarPorData(evento.getDataInicial(), evento.getDataFinal());
		response.setData(lista.stream().map(e -> eventoService.converterEventoParaDto(e)).collect(Collectors.toList()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Busca pelos eventos cadastrados entre a data inicial e a data final
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param result
	 * @return ResponseEntity<Response<List<EventoDto>>>
	 */
	@GetMapping("/consultar")
	public ResponseEntity<Response<List<EventoDto>>> listar(@RequestParam LocalDate dataInicial,
			@RequestParam LocalDate dataFinal, BindingResult result) {
		log.info("Consultando relatório por datas entre {} e {}", dataInicial.toString(), dataFinal.toString());
		Response<List<EventoDto>> response = new Response<List<EventoDto>>();

		try {
			List<Evento> lista = eventoService.buscarPorData(dataInicial, dataFinal);
			response.setData(
					lista.stream().map(e -> eventoService.converterEventoParaDto(e)).collect(Collectors.toList()));
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Erro ao consultar relatório com datas entre {} e {}", dataInicial.toString(),
					dataFinal.toString());
			result.getAllErrors().add(new ObjectError("relatorio", "Erro ao buscar relatorio"));
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

	}
}
