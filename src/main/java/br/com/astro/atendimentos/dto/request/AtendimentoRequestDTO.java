package br.com.astro.atendimentos.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AtendimentoRequestDTO(

    @NotBlank(message = "O nome do paciente deve ser preenchido.")
    String nomeDoPaciente,

    @NotBlank(message = "O nome do especialista deve ser preenchido.")
    String nomeDoEspecialista,

    @NotNull(message = "A data de atendimento deve ser preenchida.")
    LocalDateTime dataDoAtendimento
) {
}
