CREATE TABLE atendimentos
(
    id                   CHAR(36)     NOT NULL,
    nome_do_paciente     VARCHAR(255) NOT NULL,
    nome_do_especialista VARCHAR(255) NOT NULL,
    data_do_atendimento  DATETIME     NOT NULL,
    status_atendimento   VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_atendimentos PRIMARY KEY (id)
);

CREATE INDEX idx_atendimentos_status ON atendimentos (status_atendimento);