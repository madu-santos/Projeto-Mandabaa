# Registro de Uso de IA e Revisão Humana — Pessoa 1 (Arquitetura)

| Campo | Registro |
| :--- | :--- |
| **Responsável** | Pessoa 1 (Arquitetura de Software, Controladores REST e Integração) |
| **Data** | 20–21/11/2026 |
| **Etapa** | Setup do Monólito Modular, Contratos de API e Esqueletos para TDD (Sprint 5 & 5.1) |
| **Entradas fornecidas à IA** | Documento de Visão do Mandabaa, Backlog v3 (Sprint 4), Arquitetura Modular da Sprint 4, instruções da Sprint 5 e roteiro de V&V/TDD da Sprint 5.1. |

## O que foi gerado com apoio da IA
1. Configuração Maven (`pom.xml`) com Java 21, Spring Boot 3.3.5, Spring Web, Spring Data JPA, PostgreSQL, Lombok, JUnit 5 e Mockito.
2. Estrutura de pacotes do Monólito Modular em português (`configuracao`, `comum.excecao`, `usuarios`, `coletas`, `parceiros`, `carteira`).
3. Configuração de CORS (`ConfiguracaoCors`) e tratamento global de erros em JSON (`ManipuladorGlobalDeExcecoes`).
4. Entidades JPA iniciais, Repositórios, DTOs, **4 Controladores REST** (`ControladorCarteira`, `ControladorColeta`, `ControladorOferta`, `ControladorResgate`) e os **3 esqueletos vazios de serviço** (`ServicoColeta`, `ServicoCarteira`, `ServicoResgate`) preservando o início estrito do TDD (Fase RED).
5. Arquivo `docker-compose.yml`, base inicial do frontend em português (`index.html`, `estilo.css`, `script.js`) e `docs/backlog-sprint5.md`.

## Revisão Humana e Ajustes Aplicados (Human-in-the-Loop)
1. **Padronização de idioma:** A primeira versão gerada tinha nomes de pacotes e classes em inglês (`rewards`, `engagement`, `GlobalExceptionHandler`, `style.css`, `app.js`). A equipe solicitou e aplicou a tradução completa para português para facilitar o entendimento de todos os integrantes.
2. **Alinhamento de escopo à Sprint 5:** A proposta inicial de outra IA sugeria US03 + US07 (incluindo uma história *Desejável* antes de *Essenciais*). Na revisão contra o PDF da Sprint 5, o escopo foi reajustado para **US03 (8 SP — Essencial) + US05 (3 SP — Essencial) + US01 (2 SP — Importante) = 13 SP**, respeitando a prioridade definida pelo cliente e o fluxo real do mockup.
3. **Alinhamento aos critérios reais do Backlog v3:** Foram corrigidos os campos de entidades e DTOs para refletir critérios que tinham ficado de fora (ex.: cálculo de pontos faltantes no saldo insuficiente, situações `DISPONIVEL/UTILIZADO/EXPIRADO` e `PAGAMENTO_PENDENTE/PAGAMENTO_CONFIRMADO`, além da rota de consulta dos benefícios resgatados).
4. **Prevenção de erro de inicialização:** Ajustado `spring.sql.init.mode=never` em `application.properties` até que a Pessoa 3 crie o `data.sql`, evitando falha ao subir a aplicação.
5. **Verificação técnica:** Compilação validada com Maven (`mvn clean compile` $\rightarrow$ `BUILD SUCCESS`, 0 erros).
