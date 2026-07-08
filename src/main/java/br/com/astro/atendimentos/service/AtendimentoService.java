package br.com.astro.atendimentos.service;

import br.com.astro.atendimentos.dto.request.AtendimentoRequestDTO;
import br.com.astro.atendimentos.dto.response.AtendimentoResponseDTO;
import br.com.astro.atendimentos.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AtendimentoService {

    AtendimentoResponseDTO criar(AtendimentoRequestDTO dto);

    AtendimentoResponseDTO buscarPorId(String id);

    Page<AtendimentoResponseDTO> listar(Status status, Pageable pageable);

    AtendimentoResponseDTO atualizarStatus(String id, Status novoStatus);

    AtendimentoResponseDTO cancelar(String id);
}
