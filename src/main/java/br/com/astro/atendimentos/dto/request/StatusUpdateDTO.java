package br.com.astro.atendimentos.dto.request;

import br.com.astro.atendimentos.enums.Status;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateDTO(@NotNull Status status) {}