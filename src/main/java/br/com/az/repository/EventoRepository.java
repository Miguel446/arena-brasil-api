package br.com.az.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.az.model.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

	List<Evento> findAllByStatusTrue();
}
