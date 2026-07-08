package br.com.astro.atendimentos.repository;

import br.com.astro.atendimentos.entity.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AtendimentoRepository extends JpaRepository<Atendimento, UUID> {
}