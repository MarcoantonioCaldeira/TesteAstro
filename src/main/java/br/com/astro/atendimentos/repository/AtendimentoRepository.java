package br.com.astro.atendimentos.repository;

import br.com.astro.atendimentos.entity.Atendimento;
import br.com.astro.atendimentos.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, String> {

    Page<Atendimento> findByStatusAtendimento(Status status, Pageable pageable);

}