package br.com.astro.atendimentos.dto.response;
import br.com.astro.atendimentos.enums.Status;
import java.time.LocalDateTime;

public record AtendimentoResponseDTO(
    String id,
    String nomeDoPaciente,
    String nomeDoEspecialista,
    LocalDateTime dataDoAtendimento,
    Status statusAtendimento
) {
}
