package br.com.az.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import br.com.az.dto.EventoDto;
import br.com.az.model.Evento;
import br.com.az.repository.EventoRepository;

@Service
public class EventoService {

	@Autowired
	private EventoRepository eventoRepository;
	@Autowired
	private EntityManager em;

	public List<Evento> listarTodos() {
		return eventoRepository.findAllByStatusTrue();
	}

	public void salvar(Evento evento) {
		eventoRepository.save(evento);
	}

	public Evento buscar(Long id) {
		return eventoRepository.getOne(id);
	}

	public void remover(Long id) {
		Evento evento = buscar(id);
		evento.setStatus(false);
		eventoRepository.save(evento);
	}

	public void validarDados(EventoDto eventoDto, BindingResult result) {
		LocalTime horaInicial = LocalTime.parse(eventoDto.getHoraInicial());
		LocalTime horaFinal = LocalTime.parse(eventoDto.getHoraFinal());

		if (horaFinal.isBefore(horaInicial)) {
			result.addError(new ObjectError("evento", "A hora final não pode ser anterior à hora inicial."));
		}

	}

	public Evento converterDtoParaEvento(EventoDto eventoDto) {
		Evento evento = new Evento();
		evento.setId(eventoDto.getId().get());
		evento.setNome(eventoDto.getNome());
		evento.setTelefone(eventoDto.getTelefone());
		evento.setValor(eventoDto.getValor());
		evento.setData(eventoDto.getData());
		evento.setHoraInicial(eventoDto.getHoraInicial());
		evento.setHoraFinal(eventoDto.getHoraFinal());
		return evento;
	}

	public EventoDto converterEventoParaDto(Evento evento) {
		EventoDto eventoDto = new EventoDto();
		eventoDto.setId(Optional.ofNullable(evento.getId()));
		eventoDto.setNome(evento.getNome());
		eventoDto.setTelefone(evento.getTelefone());
		eventoDto.setValor(evento.getValor());
		eventoDto.setData(evento.getData());
		eventoDto.setHoraInicial(evento.getHoraInicial());
		eventoDto.setHoraFinal(evento.getHoraFinal());

		if (evento.getUsuario() != null) {
			eventoDto.setEmailUsuario(evento.getUsuario().getEmail());
		}
		return eventoDto;
	}

	public List<Evento> buscarPorData(LocalDate dataInicial, LocalDate dataFinal) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Evento> cq = cb.createQuery(Evento.class);
		Root<Evento> filtro = cq.from(Evento.class);

		List<Predicate> predicados = new ArrayList<Predicate>();

		if (dataInicial != null && dataFinal != null) {
			Predicate periodo = cb.between(filtro.get("data"), dataInicial, dataFinal);
			predicados.add(periodo);
		}

		Predicate status = cb.isTrue(filtro.get("status"));
		predicados.add(status);

		cq.select(filtro).where(predicados.toArray(new Predicate[] {}));
		cq.orderBy(cb.desc(filtro.get("data")));
		TypedQuery<Evento> query = em.createQuery(cq);

		return query.getResultList();
	}
}
