# Sistema de Gerenciamento de Atendimentos
 
API REST para controle de atendimentos realizados por especialistas em uma clínica médica. Permite criar, consultar, listar (com paginação, ordenação e filtro), atualizar o status e cancelar atendimentos, sem remoção física dos registros.
 
Desafio técnico desenvolvido para a **Astro Soluções Digitais**.
 
---
 
## Stack
 
| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Persistência | Spring Data JPA / Hibernate |
| Banco de dados | MySQL 8 |
| Versionamento de schema | Flyway |
| Validação | Bean Validation (Jakarta) |
| Boilerplate | Lombok |
| Build | Maven |
| Containerização | Docker + Docker Compose |
 
---
 
## Como executar
 
### Opção 1 — Docker (recomendado)
 
O único pré-requisito é ter o **Docker** e o **Docker Compose** instalados. O banco de dados sobe junto com a aplicação, e as migrations são aplicadas automaticamente — não é preciso instalar Java, Maven ou MySQL na máquina.
 
```bash
git clone https://github.com/MarcoantonioCaldeira/TesteAstro.git
cd TesteAstro
docker compose up --build
```
 
A API ficará disponível em `http://localhost:5050`.
 
> O primeiro build baixa as dependências e a imagem do MySQL, podendo levar alguns minutos. A aplicação aguarda o healthcheck do banco antes de subir, garantindo que a conexão só seja tentada quando o MySQL estiver pronto.
 
### Opção 2 — Execução local
 
Requisitos: **JDK 21**, **Maven** e uma instância de **MySQL 8** rodando.
 
1. Crie o banco:
```sql
CREATE DATABASE pacientes_db;
```
 
2. Ajuste as credenciais em `src/main/resources/application.properties` se necessário (ou exporte as variáveis de ambiente `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS`).
3. Rode a aplicação:
```bash
./mvnw spring-boot:run
```
 
As migrations do Flyway criarão a tabela na primeira execução.
 
---
 
## Endpoints
 
Base URL: `http://localhost:5050`
 
### Criar atendimento
 
`POST /appointments`
 
O status inicial é definido automaticamente como `AGUARDANDO` e não deve ser informado na criação.
 
```json
{
  "nomeDoPaciente": "Maria Silva",
  "nomeDoEspecialista": "Dr. Carlos Souza",
  "dataDoAtendimento": "2026-07-10T14:30:00"
}
```
 
**201 Created** — retorna o atendimento criado, com `id` e status `AGUARDANDO`. O header `Location` aponta para o recurso.
 
### Buscar atendimento por ID
 
`GET /appointments/{id}`
 
**200 OK** — retorna o atendimento.
**404 Not Found** — quando o ID não existe.
 
### Listar atendimentos
 
`GET /appointments`
 
Suporta paginação, ordenação e filtro por status via query params:
 
| Parâmetro | Descrição | Exemplo |
|---|---|---|
| `page` | Página (base 0) | `?page=0` |
| `size` | Itens por página | `?size=10` |
| `sort` | Campo e direção | `?sort=dataDoAtendimento,desc` |
| `status` | Filtra por status (opcional) | `?status=AGUARDANDO` |
 
Exemplo combinado:
 
```
GET /appointments?status=AGUARDANDO&page=0&size=10&sort=dataDoAtendimento,desc
```
 
**200 OK** — retorna uma página de atendimentos com metadados (`totalElements`, `totalPages`, etc.).
 
### Atualizar status
 
`PATCH /appointments/{id}/status`
 
```json
{
  "status": "EM_ATENDIMENTO"
}
```
 
**200 OK** — retorna o atendimento com o status atualizado.
 
### Cancelar atendimento
 
`DELETE /appointments/{id}`
 
O cancelamento **não remove o registro do banco**: apenas altera o status para `CANCELADO` (soft delete). O atendimento continua consultável após o cancelamento.
 
**200 OK** — retorna o atendimento com status `CANCELADO`.
 
---
 
## Status possíveis
 
| Status | Significado |
|---|---|
| `AGUARDANDO` | Atendimento criado, aguardando início |
| `EM_ATENDIMENTO` | Atendimento em andamento |
| `FINALIZADO` | Atendimento concluído |
| `CANCELADO` | Atendimento cancelado (via soft delete) |
 
---
 
## Estrutura do projeto
 
```
br.com.astro.atendimentos
├── controller        → endpoints REST
├── dto
│   ├── request       → dados de entrada (criação e atualização de status)
│   └── response      → dados de saída
├── entity            → mapeamento JPA
├── enums             → enum de status
├── exceptions        → exceção de domínio + handler global
├── repository        → acesso a dados (Spring Data JPA)
└── service
    └── impl          → regras de negócio
```
 
---
 
## Tratamento de erros
 
O tratamento de exceções é centralizado em um `@RestControllerAdvice`, mantendo os controllers limpos e as respostas de erro padronizadas. As respostas seguem o formato **RFC 7807 (Problem Details)**, nativo do Spring.
 
| Situação | Status | Exceção tratada |
|---|---|---|
| Atendimento não encontrado | 404 | `AtendimentoNaoEncontradoException` |
| Campo inválido no corpo | 400 | `MethodArgumentNotValidException` |
| Valor inválido em parâmetro (ex.: status inexistente) | 400 | `MethodArgumentTypeMismatchException` |
 
---
 
## Respostas do desafio
 
### 1. Como você validou que sua implementação estava pronta para ser entregue?
 
A validação foi feita em três frentes.
 
Testei manualmente todos os endpoints com o Insomnia, cobrindo tanto os caminhos de sucesso quanto os de erro. Além dos casos felizes (criar, listar, buscar, atualizar status, cancelar), validei explicitamente os cenários de falha: busca por um ID inexistente retornando **404**, criação sem campo obrigatório retornando **400** com a indicação do campo, e filtro por um status inválido retornando **400**. Validar o comportamento de erro é tão importante quanto o de sucesso, porque é onde APIs costumam falhar silenciosamente.
Também validei o requisito crítico do cancelamento: após cancelar um atendimento, fiz uma nova consulta pelo mesmo ID e confirmei que o registro continua no banco, apenas com o status alterado para `CANCELADO`. Isso garante que a regra "não remover do banco" foi de fato respeitada, e não apenas assumida. E por ultimo executei a aplicação containerizada com `docker compose up` a partir de um ambiente limpo. Isso valida que o projeto não depende de nenhuma configuração local da minha máquina: o banco sobe do zero, as migrations do Flyway criam o schema automaticamente, e a API responde igual ao ambiente de desenvolvimento. Se roda no container, roda na máquina do avaliador.
 
### 2. Quais melhorias você faria caso tivesse mais tempo?
 
- **Testes automatizados.** Testes unitários da camada de serviço (com Mockito). Foi a ausência que mais senti; num cenário real seria prioridade antes mesmo de features.
- **Validação de transição de status.** Hoje qualquer status pode ir para qualquer outro. O ideal seria impedir transições inválidas (por exemplo, reabrir um atendimento `CANCELADO` ou `FINALIZADO`), retornando um erro semântico (409/422). É uma regra de negócio que adiciona maior robustez.
- **Documentação interativa com OpenAPI/Swagger.** Exporia os endpoints numa UI navegável, facilitando o consumo e servindo como documentação viva.
- **Serialização de paginação com `PagedModel`.** O Spring emite um aviso ao serializar `Page` diretamente, por não haver garantia de estabilidade do JSON. A forma recomendada é usar `PagedModel` via `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)`, estabilizando o contrato da resposta.
### 3. Durante o desenvolvimento, quais decisões de arquitetura ou implementação você tomou e por quê?
 
- **Linguagem ubíqua no domínio, convenção REST nas rotas.** As entidades e regras de negócio usam português (`Atendimento`, `nomeDoPaciente`), refletindo a linguagem do domínio da clínica, enquanto as rotas seguem a convenção de mercado em inglês (`/appointments`). Isso mantém o código alinhado ao negócio sem violar as expectativas de quem consome a API.
- **DTOs segregados por operação.** Em vez de um DTO único, separei entrada e saída (`request`/`response`) e, dentro da entrada, separei o DTO de criação do de atualização de status. Cada endpoint aceita apenas os campos que fazem sentido para ele: o de criação não permite que o cliente defina o status (que nasce sempre `AGUARDANDO` por regra de negócio), e o de atualização mexe exclusivamente no status. Isso previne que campos sejam manipulados indevidamente e deixa o contrato de cada operação explícito.
- **Flyway para versionamento do schema.** Optei por `ddl-auto=none` e Flyway em vez de deixar o Hibernate gerar as tabelas. Migrations versionadas são reproduzíveis, auditáveis e seguras para evoluir o banco ao longo do tempo — prática essencial em qualquer projeto que vá além de um protótipo.
- **Soft delete no cancelamento.** O requisito "não remover do banco" foi atendido tratando o cancelamento como uma mudança de estado, não como exclusão. O registro permanece íntegro e consultável, preservando o histórico — algo importante num contexto clínico, onde rastreabilidade importa.
- **Camada de serviço com interface + implementação.** Separei o contrato (`AtendimentoService`) da implementação, favorecendo baixo acoplamento e testabilidade. O controller depende da abstração, não da classe concreta.
- **Tratamento de erros centralizado e padronizado.** Um `@RestControllerAdvice` concentra o tratamento de exceções, retornando respostas no padrão RFC 7807. Os controllers ficam livres de `try/catch`, e o cliente recebe erros consistentes.
### 4. Se essa API precisasse suportar milhões de registros, quais alterações você faria para manter um bom desempenho?
 
Parte da fundação já está no projeto, e a partir dela a evolução seria natural.
 
**No que já foi implementado:** a listagem é paginada por padrão (nunca retorna a tabela inteira), e há um **índice na coluna `status_atendimento`**, que é justamente o campo usado no filtro — evitando full table scan nas consultas mais comuns.
 
**Evoluções no banco:**
 
- **Índices compostos** alinhados aos padrões de consulta reais (por exemplo, `status + data_do_atendimento`, se a listagem filtrada e ordenada por data for frequente).
- **Paginação por keyset (cursor)** em vez de offset. A paginação por `OFFSET` degrada em páginas altas, porque o banco precisa varrer e descartar todas as linhas anteriores; a paginação por cursor (usando a última chave vista) mantém desempenho constante independentemente da profundidade.
- **Particionamento da tabela** por data, caso o volume histórico justifique — mantendo as consultas recentes rápidas ao restringir a varredura às partições relevantes.
**Evoluções de infraestrutura:**
 
- **Read replicas** para separar leitura de escrita. Como um sistema de atendimentos tende a ter muito mais consultas do que gravações, direcionar as leituras para réplicas alivia o banco primário.
- **Cache** (por exemplo, Redis) para os dados mais consultados, reduzindo a pressão sobre o banco em endpoints de leitura de alta frequência.
- **Connection pool** dimensionado conforme a carga (o HikariCP já é o pool padrão; o ajuste seria de tuning).
A regra geral seria medir antes de otimizar: instrumentar as queries, identificar os gargalos reais com base em métricas, e aplicar cada uma dessas alavancas onde os dados apontarem necessidade — evitando complexidade prematura.
 
### 5. Imagine que, ao finalizar um atendimento, seja necessário comunicar uma API externa que pode levar até 3 minutos para responder. Como você implementaria essa funcionalidade sem comprometer a experiência do usuário?
 
A premissa central é: **a requisição do usuário nunca pode ficar bloqueada esperando os 3 minutos.** Manter uma conexão HTTP aberta por esse tempo esgota threads do servidor, estoura timeouts de clientes e proxies, e trava a experiência. A solução é **desacoplar** a finalização do atendimento da comunicação externa através de processamento assíncrono.
 
O fluxo seria:
 
1. O usuário finaliza o atendimento. A API atualiza o status e **responde imediatamente** (200), liberando o usuário. Do ponto de vista dele, a ação foi instantânea.
2. A comunicação com a API externa é **enfileirada** para processamento em background. Numa arquitetura robusta, isso significa publicar uma mensagem em um message broker (**RabbitMQ**, **Kafka**, ou **AWS SQS**); numa versão mais simples, um `@Async` do próprio Spring com um pool de threads dedicado já desacopla a chamada.
3. Um **worker/consumer** processa a fila e faz a chamada à API externa, agora sem pressa e sem impactar o tempo de resposta ao usuário.
Pontos essenciais para tornar isso confiável:
 
- **Retry com backoff.** Se a API externa falhar ou estourar timeout, a mensagem é reprocessada com tentativas espaçadas, em vez de se perder.
- **Dead-letter queue.** Mensagens que falham repetidamente são isoladas para análise, sem travar a fila.
- **Idempotência.** Garantir que reprocessar a mesma mensagem não gere efeito duplicado na API externa (por exemplo, com uma chave de idempotência), já que retries podem reenviar a mesma operação.
- **Rastreabilidade do resultado.** Se o desfecho da comunicação importar para o usuário, o resultado pode ser refletido em um campo de status da integração, ou comunicado depois via notificação/webhook — sem nunca bloquear a ação original.
Essa abordagem mantém a API responsiva e resiliente: a lentidão ou indisponibilidade do serviço externo deixa de ser um problema do usuário e passa a ser tratada de forma controlada pela infraestrutura.
 
---
 
## Autor
 
**Marco Antônio Caldeira**
[GitHub](https://github.com/MarcoantonioCaldeira)
