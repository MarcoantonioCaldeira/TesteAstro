package br.com.astro.atendimentos.controller;

import br.com.astro.atendimentos.dto.request.AtendimentoRequestDTO;
import br.com.astro.atendimentos.dto.request.StatusUpdateDTO;
import br.com.astro.atendimentos.dto.response.AtendimentoResponseDTO;
import br.com.astro.atendimentos.enums.Status;
import br.com.astro.atendimentos.service.AtendimentoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/appointments")
public class AtendimentoController {

    private final AtendimentoService service;

    public AtendimentoController(AtendimentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> criar(@RequestBody @Valid AtendimentoRequestDTO dto) {
        AtendimentoResponseDTO response = service.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<AtendimentoResponseDTO>> listar(
            @RequestParam(required = false) Status status,
            @PageableDefault(size = 10, sort = "dataDoAtendimento") Pageable pageable) {
        return ResponseEntity.ok(service.listar(status, pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AtendimentoResponseDTO> atualizarStatus(
            @PathVariable String id,
            @RequestBody @Valid StatusUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizarStatus(id, dto.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AtendimentoResponseDTO> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}