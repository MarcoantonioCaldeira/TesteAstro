package br.com.astro.atendimentos.entity;

import br.com.astro.atendimentos.enums.Status;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name="atendimentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atendimento {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(nullable = false)
    private String nomeDoPaciente;

    @Column(nullable = false)
    private String nomeDoEspecialista;

    @Column(nullable = false)
    private LocalDateTime dataDoAtendimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status statusAtendimento;
}
