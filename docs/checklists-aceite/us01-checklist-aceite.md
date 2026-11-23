# Checklist de Aceite (Definition of Done) — US01: Registro e Histórico de Coletas

| Campo | Informação |
| :--- | :--- |
| **História** | US01 — Registro e Histórico de Coletas • 2 SP • Importante |
| **Responsável pela verificação/validação** | Pessoa 4 (Cybelle) — QA |
| **Verificação técnica executada em** | 23/11/2026 (assistida por IA; **revisão humana pendente**: Cybelle deve reler e confirmar) |
| **Demonstração ao PO/professora** | ____/____/2026 — responsável: __________________ |
| **Roteiro** | `docs/roteiros-aceitacao/us01-roteiro-aceitacao.md` |

## Resumo no formato do Roteiro TDD (Etapa 5)

| Critério de Qualidade | Status | Evidência |
| :--- | :---: | :--- |
| **Verificação (Código)** | ☑ Aprovado pela QA (com ressalvas de GREEN/REFACTOR, seção 1) | Suíte JUnit 5 + Mockito: 51/51 verdes em 23/11/2026 (14 testes desta história) |
| **Validação (Negócio)** | ☐ Pendente | Pré-verificada pela QA; falta a demonstração ao cliente/PO |
| **Status da US01** | ☐ DONE (pendente do aceite do PO) | — |

## 1. Verificação (estamos construindo o produto corretamente?)

| Critério | Status | Evidência |
| :--- | :---: | :--- |
| Testes unitários cobrem CA01, CA02 e CA03 | ✅ Comprovado | `ServicoColetaTest` (3 testes originais) + `ServicoColetaComplementarTest` (11 testes novos) = **14 testes** |
| Suíte executa com 100% de sucesso | ✅ Comprovado (23/11/2026) | `mvn test` → 51/51 no projeto todo, 0 falhas. Log: `docs/registros-ia/evidencias/pessoa4-mvn-test-final.txt` |
| Repositórios isolados com Mockito (sem banco) | ✅ Comprovado | `@ExtendWith(MockitoExtension.class)` + `@Mock` nos dois repositórios |
| Erros não alteram saldo nem gravam coleta | ✅ Comprovado | Testes `deveManterSaldoIgualQuandoPesoInvalido`, `deveLancarExcecaoAoRegistrarEntregaParaUsuarioInexistente` |
| Testes realmente detectam defeitos | ✅ Comprovado (simulação) | 12 defeitos plantados numa cópia isolada, todos detectados: `evidencias/pessoa4-simulacao-mutacoes.txt` (M8, M9 e M10 são da US01) |
| 🔴 RED | ✅ Reproduzido | Commit `3ea39d1` (testes + esqueleto) executado em 23/11/2026: 3 falhas em `ServicoColetaTest` (retorno nulo / exceção não lançada). `evidencias/pessoa4-red-reproduzido-commit-3ea39d1.txt`. *Não é print da época:* é reexecução do estado do Git |
| 🟢 GREEN | ⚠️ Parcial | Commit `6a97545` e print do grupo `evidencias/fase-green-sucesso.png` (10 testes). O mesmo commit traz GREEN **e** REFACTOR juntos |
| 🔵 REFACTOR | ⚠️ Parcial | Descrito em `docs/registros-ia/pessoa3-banco-servicos.md`, mas **não há commit separado** do GREEN; print do refactor é praticamente idêntico ao do green |

## 2. Validação (estamos construindo o produto certo?)

| Critério | Status | Evidência |
| :--- | :---: | :--- |
| CA01 — Histórico com 2 entregas e total de 4,3 kg na tela | ✅ Pré-verificado pela QA (23/11) • ⬜ Demonstração oficial pendente | Roteiro, cenário 1 |
| CA02 — Nova entrega de 2 kg: saldo 1.000 → 1.100 e histórico atualizado | ✅ Pré-verificado pela QA (via PowerShell + conferência na API) • ⬜ Demonstração pendente | Roteiro, cenário 2 |
| CA03 — Peso ≤ 0 recusado (HTTP 400) e saldo inalterado | ✅ Pré-verificado pela QA • ⬜ Demonstração pendente | Roteiro, cenário 3 |
| Resultados observados e prints preenchidos no roteiro | ⬜ Pendente | A preencher na demonstração |

## 3. Observações e pendências

- ⚠️ **A interface não tem formulário para registrar coleta** (CA02/CA03 só via API/PowerShell). Se o PO exigir demonstração 100% pela tela, é preciso uma tela de registro (responsabilidade da Pessoa 5).
- ⚠️ Pontos de bônus não são validados contra limite máximo (apenas negativos viram zero).

## 4. Situação da história

| Item | Situação |
| :--- | :--- |
| Verificação técnica | ✅ Aprovada pela QA (com ressalvas de GREEN/REFACTOR acima) |
| Validação na interface | ⬜ Pendente (demonstração oficial) |
| **Aceite do Cliente/PO** | ⬜ **PENDENTE** — nome: __________________ data: ____/____/2026 |
| **Status da User Story** | ⬜ **DONE** — só marcar após o aceite do PO |
