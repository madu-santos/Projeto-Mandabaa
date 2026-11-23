# Registro de Uso de IA e Revisão Humana — Pessoa 4 (Requisitos, TDD e QA)

| Campo | Registro |
| :--- | :--- |
| **Responsável** | Pessoa 4 — Cybelle |
| **Data** | 23/11/2026 |
| **Etapa** | Sprint 5 / 5.1 — Auditoria dos testes, complemento de cobertura, roteiros de aceitação, checklists e evidências |
| **IA utilizada** | Claude Code (Anthropic), no aplicativo desktop. Modelos informados pelo ambiente durante a sessão: **Claude Opus 5.5** (início) e **Claude Sonnet 5.5** (parte final). O ambiente trocou de modelo no meio da sessão; não foi possível separar exatamente quais respostas vieram de cada um |
| **Entradas fornecidas à IA** | `plano_acao_mvp_mandabaa.md`, `relatorio_analise_mandabaa.md`, ZIP/repositório do projeto e descrição do Roteiro TDD |
| **Observação** | O arquivo `Roteiro TDD.pdf` **não estava** no computador (procurado em Downloads, Documentos e Desktop). A IA usou o resumo do roteiro escrito no pedido. **Pendente:** Cybelle conferir os artefatos contra o PDF original |
| **Revisão humana** | ⬜ **Pendente.** Tudo abaixo foi produzido com apoio de IA e precisa ser lido, entendido e confirmado por Cybelle antes de ser entregue |

---

## 1. Prompt (resumo)

Pedido da Cybelle (Pessoa 4): ler plano, relatório, roteiro e projeto; conferir os testes existentes; completar a cobertura dos critérios de US01, US05 e US03; checar as evidências de RED/GREEN/REFACTOR; criar roteiros de aceitação e checklists; criar este registro; preparar a demonstração. Regras: respostas honestas sobre o que foi ou não comprovado, preservar contratos da API, corrigir código de produção só se necessário e explicar.

## 2. O que já existia quando a Pessoa 4 recebeu o projeto

| Item | Situação encontrada |
| :--- | :--- |
| Testes | `ServicoResgateTest` (4), `ServicoCarteiraTest` (3), `ServicoColetaTest` (3) = **10 testes**, adicionados ao Git no commit `3ea39d1` |
| Serviços | `ServicoResgate` (commit `c17c8e4`, 21/11), `ServicoCarteira` e `ServicoColeta` (commit `6a97545`) implementados |
| Evidências | `fase-green-sucesso.png` e `fase-refactor-sucesso.png`: 10 testes, 0 falhas. **Não existe print de RED** |
| Registros de IA | `pessoa1-arquitetura.md` e `pessoa3-banco-servicos.md`. **Não existe registro da Pessoa 2** |
| Roteiros/checklists | Só os `README.md` de orientação |
| Alegação da Pessoa 3 (“10 testes passando”) | ✅ **Verificada hoje**: 10/10 com JDK 21 |

## 3. Comandos executados e resultados reais (23/11/2026)

| # | Comando / ação | Resultado |
| :-: | :--- | :--- |
| 1 | `mvn test` no terminal padrão (JAVA_HOME = JDK 25) | ❌ **Erro de compilação** (Lombok não gerou getters/construtores). **Causa: ambiente**, não o código: o projeto pede Java 21 |
| 2 | `mvn test` com `JAVA_HOME` apontando para o JDK 21 | ✅ **10 testes, 0 falhas** (3 + 4 + 3) |
| 3 | Criação de 3 classes de teste complementares | — |
| 4 | `mvn test` após os testes novos | ✅ **51 testes, 0 falhas** (log em `evidencias/pessoa4-mvn-test-final.txt`) |
| 5 | Simulação de defeitos em **cópia isolada** (12 mutações) | ✅ Todas detectadas. Log: `evidencias/pessoa4-simulacao-mutacoes.txt` |
| 6 | Reexecução do estado do commit `3ea39d1` em cópia temporária (`git worktree`, removida depois) | US01 e US05: **6 falhas** (RED real). US03: **4 testes passaram** (sem RED). Log: `evidencias/pessoa4-red-reproduzido-commit-3ea39d1.txt` |
| 7 | Banco PostgreSQL 16 em Docker (porta 5499) + `mvn spring-boot:run` | ✅ Aplicação subiu; `data.sql` carregou |
| 8 | Fluxos no navegador e na API (ver roteiros) | ✅ Conforme descrito em "Pré-verificação" de cada roteiro |

> Ambiente: o PostgreSQL local do computador já usava a porta 5432, então o banco de teste foi iniciado em `127.0.0.1:5499` e a aplicação apontada para ele por variável de ambiente (`SPRING_DATASOURCE_URL`). **Nenhum arquivo do projeto foi alterado para isso.** O container de teste foi removido ao final.

## 4. Auditoria dos testes originais (resumo)

| Classe | Critérios cobertos | Qualidade (A-A-A, mocks) | Lacunas encontradas |
| :--- | :--- | :--- | :--- |
| `ServicoCarteiraTest` | CA01, CA02, CA03 | Boa: blocos GIVEN/WHEN/THEN, repositório mockado | CA01 aceita o texto da regra com `||` (basta um dos dois trechos); sem saldo nulo, ID nulo, conversão com outros valores |
| `ServicoColetaTest` | CA01, CA02, CA03 | Boa | CA03 não provava que o **saldo ficou igual**; sem bônus, usuário inexistente, histórico vazio, conteúdo gravado |
| `ServicoResgateTest` | CA01, CA02, CA03 (**só esgotada**), CA04 | Boa, mas blocos só “GIVEN/WHEN/THEN” sem detalhe | **Faltavam inativa e vencida**; erros não provavam que nada foi gravado; sem cashback sem PIX, tipo divergente, histórico de resgates, unicidade do comprovante |

## 5. O que foi acrescentado

Três classes novas (os arquivos originais **não foram alterados**):

| Arquivo | Testes |
| :--- | :-: |
| `src/test/java/com/mandabaa/carteira/servico/ServicoResgateComplementarTest.java` | 19 |
| `src/test/java/com/mandabaa/carteira/servico/ServicoCarteiraComplementarTest.java` | 11 |
| `src/test/java/com/mandabaa/coletas/servico/ServicoColetaComplementarTest.java` | 11 |

Outros arquivos criados: `docs/roteiros-aceitacao/us01|us03|us05-roteiro-aceitacao.md`, `docs/roteiros-aceitacao/restaurar-demo.sql`, `docs/checklists-aceite/us01|us03|us05-checklist-aceite.md`, `docs/guia-demonstracao-pessoa4.md`, as evidências em `docs/registros-ia/evidencias/pessoa4-*.txt` e este registro.

## 6. Defeitos de produção encontrados

**Nenhum defeito de regra de negócio foi encontrado.** Os testes novos passaram de primeira contra o código existente, ou seja, o código já respeitava os critérios. **Nenhum código de produção foi alterado.**

Por isso os testes novos **não passaram por um ciclo RED real** (o comportamento já existia). Para provar que não são testes “vazios”, foi feita a simulação do item 3.5: ao quebrar a regra de propósito, os testes falham. Isto é uma **simulação didática**, não um RED histórico.

## 7. Evidência TDD por história (posição honesta)

| História | 🔴 RED | 🟢 GREEN | 🔵 REFACTOR |
| :--- | :--- | :--- | :--- |
| **US01** | ✅ Reproduzido em 23/11 a partir do commit `3ea39d1` (3 falhas) | ⚠️ Commit `6a97545` + print do grupo | ⚠️ Sem commit separado; print quase idêntico ao do green |
| **US05** | ✅ Reproduzido em 23/11 (3 falhas) | ⚠️ idem | ⚠️ idem |
| **US03** | ❌ **Não comprovado** — testes passaram de primeira porque o serviço já existia | ⚠️ Sem registro da Pessoa 2 | ⚠️ Sem registro da Pessoa 2 |

Observações:
- Os commits `3ea39d1`, `6a97545` e `20cb9f5` têm o **mesmo horário** (09:16:07 de 23/11). A ordem existe, mas o horário não prova quando cada etapa foi feita.
- Os dois prints do grupo mostram caminho `C:\Users\ACER\...` e a mesma saída. Não foi possível validar a autenticidade; foram tratados como **evidência do grupo, não verificada**.

## 8. Limitações e pendências

1. ⬜ Revisão humana (Cybelle) de todos os artefatos.
2. ⬜ Execução dos roteiros na demonstração, com prints e preenchimento dos campos “Resultado observado”.
3. ⬜ Aceite do PO e marcação de DONE nos checklists.
4. ⬜ Registro da Pessoa 2 (GREEN/REFACTOR da US03).
5. ⬜ Conferir os entregáveis com o `Roteiro TDD.pdf` original.
6. ⚠️ A interface **não tem formulário de nova coleta** (US01 CA02/CA03 só por API).
7. ⚠️ Sem controle de concorrência em resgates simultâneos (risco, fora do MVP).
8. ⚠️ `script.js` mostra dados de exemplo se o backend estiver fora do ar.
9. ⚠️ Porta 5432 do computador já em uso por outro PostgreSQL: `docker compose up` pode falhar (ver guia).
