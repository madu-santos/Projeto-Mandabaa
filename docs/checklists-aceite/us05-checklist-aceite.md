# Checklist de Aceite (Definition of Done) — US05: Consulta de Saldo e Carteira

| Campo | Informação |
| :--- | :--- |
| **História** | US05 — Consulta de Saldo e Carteira • 3 SP • Essencial |
| **Responsável pela verificação/validação** | Pessoa 4 (Cybelle) — QA |
| **Verificação técnica executada em** | 23/11/2026 (assistida por IA; **revisão humana pendente**: Cybelle deve reler e confirmar) |
| **Demonstração ao PO/professora** | ____/____/2026 — responsável: __________________ |
| **Roteiro** | `docs/roteiros-aceitacao/us05-roteiro-aceitacao.md` |

## Resumo no formato do Roteiro TDD (Etapa 5)

| Critério de Qualidade | Status | Evidência |
| :--- | :---: | :--- |
| **Verificação (Código)** | ☑ Aprovado pela QA (com ressalvas de GREEN/REFACTOR, seção 1) | Suíte JUnit 5 + Mockito: 51/51 verdes em 23/11/2026 (14 testes desta história) |
| **Validação (Negócio)** | ☐ Pendente | Pré-verificada pela QA; falta a demonstração ao cliente/PO |
| **Status da US05** | ☐ DONE (pendente do aceite do PO) | — |

## 1. Verificação (estamos construindo o produto corretamente?)

| Critério | Status | Evidência |
| :--- | :---: | :--- |
| Testes unitários cobrem CA01, CA02 e CA03 | ✅ Comprovado | `ServicoCarteiraTest` (3 originais) + `ServicoCarteiraComplementarTest` (11 novos) = **14 testes** |
| Suíte executa com 100% de sucesso | ✅ Comprovado (23/11/2026) | `mvn test` → 51/51, 0 falhas. `evidencias/pessoa4-mvn-test-final.txt` |
| Repositório isolado com Mockito | ✅ Comprovado | `@Mock RepositorioUsuario` |
| Regra 100 pontos = R$ 2,00 verificada em vários valores | ✅ Comprovado | Teste parametrizado: 1, 50, 99, 100, 1250 e 99.999 pontos |
| Usuário inexistente → `RecursoNaoEncontradoExcecao` | ✅ Comprovado | `ServicoCarteiraTest` CA03 |
| Consulta não altera dados | ✅ Comprovado | `naoDeveAlterarOUsuarioNaConsulta` |
| Testes realmente detectam defeitos | ✅ Comprovado (simulação) | M11 (R$ 0,20/ponto) e M12 (`semSaldo` sempre falso) detectados: `evidencias/pessoa4-simulacao-mutacoes.txt` |
| 🔴 RED | ✅ Reproduzido | Commit `3ea39d1`: 3 falhas em `ServicoCarteiraTest` (retorno nulo / exceção não lançada). `evidencias/pessoa4-red-reproduzido-commit-3ea39d1.txt` (reexecução do Git, não print da época) |
| 🟢 GREEN | ⚠️ Parcial | Commit `6a97545` + print do grupo `evidencias/fase-green-sucesso.png` |
| 🔵 REFACTOR | ⚠️ Parcial | Descrito em `pessoa3-banco-servicos.md`; sem commit separado do GREEN |

> Ponto de atenção: o teste **original** do CA01 usa `||` no texto da regra (aceita se aparecer só um dos dois trechos). O teste complementar `deveExibirAsDuasRegrasDeCalculoNoTexto` exige **os dois**.

## 2. Validação (estamos construindo o produto certo?)

| Critério | Status | Evidência |
| :--- | :---: | :--- |
| CA01 — 1.250 pontos, R$ 25,00, sem aviso, texto da regra | ✅ Pré-verificado pela QA (23/11, navegador) • ⬜ Demonstração pendente | Roteiro, cenário 1 |
| CA02 — 0 pontos, R$ 0,00, aviso para entregar PET | ✅ Pré-verificado pela QA (navegador) • ⬜ Demonstração pendente | Roteiro, cenário 2 |
| CA03 — usuário inexistente → "Usuário não encontrado" (HTTP 404) | ✅ Pré-verificado pela QA (via API) • ⬜ Demonstração pendente | Roteiro, cenário 3 |
| Resultados observados e prints preenchidos no roteiro | ⬜ Pendente | A preencher na demonstração |

## 3. Observações e pendências

- ⚠️ O aviso de "sem saldo" na tela é um texto fixo do `index.html` (não vem da mensagem do backend). Mesmo conteúdo, mas vale saber ao explicar.
- ⚠️ Com o servidor desligado, a tela mostra dados de exemplo (1.250 pontos) em vez de erro — confirmar sempre que o backend está no ar.
- A tela consulta sempre o usuário 1 (fixo no `script.js`).

## 4. Situação da história

| Item | Situação |
| :--- | :--- |
| Verificação técnica | ✅ Aprovada pela QA (com ressalvas de GREEN/REFACTOR) |
| Validação na interface | ⬜ Pendente (demonstração oficial) |
| **Aceite do Cliente/PO** | ⬜ **PENDENTE** — nome: __________________ data: ____/____/2026 |
| **Status da User Story** | ⬜ **DONE** — só marcar após o aceite do PO |
