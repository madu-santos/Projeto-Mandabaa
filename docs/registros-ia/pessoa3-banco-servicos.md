# Relatório Técnico de Implementação — Pessoa 3 (Banco e Serviços)

| Campo | Detalhe |
| :--- | :--- |
| **Responsável** | Pessoa 3 (Banco de Dados + Regras de Negócio da US01 e US05) |
| **Data** | 21–23/11/2026 |
| **Etapa** | Subida do PostgreSQL no Docker, Carga Inicial (`data.sql`), Configuração do `application.properties` e Ciclo TDD (Fases GREEN e REFACTOR) de `ServicoCarteira` (US05) e `ServicoColeta` (US01) |
| **Escopo e Referências** | Documento de Visão do Mandabaa, Backlog v3 (Sprint 4), instruções da Sprint 5 e 5.1 (TDD), critérios de aceitação Given-When-Then da US01 e US05, entidades JPA (`Usuario`, `Coleta`, `Oferta`, `Resgate`) e testes unitários em JUnit 5 + Mockito da Pessoa 4. |

---

## O que foi desenvolvido e implementado

1. **Subida e Validação do Banco no Docker:**
   - Execução do contêiner PostgreSQL 15 (`postgres-mandabaa`) na porta 5432 a partir do `docker-compose.yml`, conferindo conectividade e isolamento do ambiente de dados.

2. **Carga Inicial de Demonstração (`src/main/resources/data.sql`):**
   - Criação do script SQL compatível com o PostgreSQL e fiel ao mockup/Backlog:
     - 1 usuário de demonstração: Alex, com 1.250 pontos iniciais.
     - 2 coletas validadas: 2,5 kg (125 pontos + 25 bônus) e 1,8 kg (90 pontos + 0 bônus), somando 4,3 kg entregues.
     - 3 ofertas de parceiros: Cashback de R$ 5,00 via PIX (id 10), Cupom de 10% de desconto (id 20) e Oferta Esgotada com estoque 0 (id 30).
     - Sincronização automática de sequences do PostgreSQL via `setval(pg_get_serial_sequence(...))`.

3. **Configuração de Inicialização (`src/main/resources/application.properties`):**
   - Ativação de `spring.sql.init.mode=always` para execução automática do `data.sql` em bancos externos.
   - Manutenção de `spring.jpa.defer-datasource-initialization=true` para postergar o script de dados até que o Hibernate crie as tabelas via `ddl-auto=update`.

4. **Implementação da Fase GREEN (Código Mínimo):**
   - Implementação direta em `ServicoCarteira.java` atendendo os 3 cenários da US05 (saldo em pontos, equivalente em reais de R$ 0,02/ponto, aviso de carteira sem saldo e `RecursoNaoEncontradoExcecao`).
   - Implementação direta em `ServicoColeta.java` atendendo os 3 cenários da US01 (cálculo de 50 pontos/kg + bônus, atualização do saldo do usuário, somatório de 4,3 kg no histórico e validação de peso <= 0 com `IllegalArgumentException`).
   - Todos os testes passaram sem alteração nos arquivos de teste da Pessoa 4.

5. **Refatoração Explicada da Fase REFACTOR:**
   - Aplicação do Princípio da Responsabilidade Única (SRP): métodos principais atuando como orquestradores declarativos e extração de métodos privados coesos para busca, validação, cálculo monetário e conversão de DTOs.
   - Eliminação de valores mágicos com constantes nomeadas (`VALOR_POR_PONTO`, `REGRA_CALCULO_OFICIAL`, `PONTOS_POR_KG`).
   - Adição de `@Transactional` para garantir atomicidade nas operações de escrita (coleta + saldo) e `@Transactional(readOnly = true)` para otimização de leitura.

6. **Verificação Técnica Automatizada:**
   - Execução de `mvn test` comprovando 100% de sucesso nas três classes de serviço (`BUILD SUCCESS`, 10/10 testes passando).

---

## Revisão Técnica e Ajustes Aplicados

1. **Prevenção de Erros de Duplicidade no PostgreSQL:**
   - Foram aplicadas cláusulas `ON CONFLICT (id) DO NOTHING;` e sincronização de sequences (`setval`) para garantir que reinicializações da aplicação não disparem `duplicate key value violates unique constraint`.
2. **Correção da Ordem de Inicialização (Spring Boot 3.3.5):**
   - Confirmada a exigência técnica de `spring.jpa.defer-datasource-initialization=true`, prevenindo o erro `relation "usuario" does not exist` ao subir o backend.
3. **Respeito Estrito ao Ciclo TDD:**
   - Foi garantido que nenhuma linha dos testes unitários criados pela Pessoa 4 (`ServicoCarteiraTest.java` e `ServicoColetaTest.java`) fosse modificada. O código de produção foi ajustado para satisfazer rigorosamente as asserções existentes.
4. **Precisão Monetária e Validação Defensiva:**
   - Ajustado o cálculo do equivalente em reais com `BigDecimal` e arredondamento `RoundingMode.HALF_UP` (duas casas decimais).
   - Inclusão de validação preventiva contra parâmetros nulos e normalização de bônus negativos/nulos para zero.
5. **Limpeza e Profissionalização do Código:**
   - Remoção de comentários supérfluos sobre etapas de desenvolvimento, mantendo apenas documentação Javadoc concisa e indicação das regras oficiais de negócio.
6. **Evidências Arquivadas:**
   - Prints dos testes verdes salvos em `docs/registros-ia/evidencias/fase-green-sucesso.png` e `docs/registros-ia/evidencias/fase-refactor-sucesso.png`.
