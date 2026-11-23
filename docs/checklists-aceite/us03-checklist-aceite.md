# Checklist de Aceite (Definition of Done) — US03: Resgate de Benefícios e Cashback

| Campo | Informação |
| :--- | :--- |
| **História** | US03 — Resgate de Benefícios e Cashback • 8 SP • Essencial |
| **Responsável pela verificação/validação** | Pessoa 4 (Cybelle) — QA |
| **Verificação técnica executada em** | 23/11/2026 (assistida por IA; **revisão humana pendente**: Cybelle deve reler e confirmar) |
| **Demonstração ao PO/professora** | ____/____/2026 — responsável: __________________ |
| **Roteiro** | `docs/roteiros-aceitacao/us03-roteiro-aceitacao.md` |

## Resumo no formato do Roteiro TDD (Etapa 5)

| Critério de Qualidade | Status | Evidência |
| :--- | :---: | :--- |
| **Verificação (Código)** | ☑ Aprovado quanto ao comportamento; ⚠️ RED não comprovado (seção 1) | Suíte JUnit 5 + Mockito: 51/51 verdes em 23/11/2026 (23 testes desta história) |
| **Validação (Negócio)** | ☐ Pendente | Pré-verificada pela QA; falta a demonstração ao cliente/PO |
| **Status da US03** | ☐ DONE (pendente do aceite do PO) | — |

## 1. Verificação (estamos construindo o produto corretamente?)

| Critério | Status | Evidência |
| :--- | :---: | :--- |
| Testes unitários cobrem CA01 a CA04 | ✅ Comprovado | `ServicoResgateTest` (4 originais) + `ServicoResgateComplementarTest` (19 novos) = **23 testes** |
| CA03 cobre **as três** causas (esgotada, inativa, vencida) | ✅ Comprovado | Os originais só cobriam "esgotada"; inativa e vencida foram acrescentadas |
| Erros não alteram saldo/estoque nem gravam resgate | ✅ Comprovado | `verificarNadaFoiGravado()` (nenhum `save` em usuário, oferta ou resgate) nos testes de erro |
| Mensagem exata "Saldo insuficiente. Faltam 300 pontos." | ✅ Comprovado | `deveInformarMensagemExataDePontosFaltantesESemGravarNada` |
| Cashback exige chave PIX e fica `PAGAMENTO_PENDENTE` | ✅ Comprovado | `deveRecusarCashbackSemChavePix` (null, vazio e só espaços) e `deveDebitarPontosEGuardarChavePixNoCashback` |
| Código de comprovante único | ✅ Comprovado | `deveGerarCodigosDeComprovanteDiferentesEmResgatesDiferentes` |
| Suíte com 100% de sucesso | ✅ Comprovado (23/11/2026) | `mvn test` → 51/51. `evidencias/pessoa4-mvn-test-final.txt` |
| Testes realmente detectam defeitos | ✅ Comprovado (simulação) | M1 a M7 detectados. **M2 (vencida), M3 (inativa) e M6 (sem PIX) só foram detectados pelos testes novos** — a suíte original não pegaria. `evidencias/pessoa4-simulacao-mutacoes.txt` |
| 🔴 RED | ❌ **Não comprovado** | No commit `3ea39d1` os 4 testes da US03 **passaram de primeira**: `ServicoResgate` já estava implementado desde `c17c8e4` (21/11), antes de os testes entrarem no Git. Não houve RED real |
| 🟢 GREEN | ⚠️ Pendente | Não há registro da Pessoa 2 em `docs/registros-ia/` (só `pessoa1` e `pessoa3`). Código existe e está verde hoje |
| 🔵 REFACTOR | ⚠️ Pendente | Código está organizado em métodos privados (SRP), mas sem registro/print próprio da Pessoa 2 |

## 2. Validação (estamos construindo o produto certo?)

| Critério | Status | Evidência |
| :--- | :---: | :--- |
| CA01 — Cupom: saldo, estoque, comprovante e selo DISPONIVEL | ✅ Pré-verificado pela QA (navegador) • ⬜ Demonstração pendente | Roteiro, cenário 1 |
| CA02 — "Faltam 300 pontos", saldo e estoque mantidos | ✅ Pré-verificado pela QA (navegador) • ⬜ Demonstração pendente | Roteiro, cenário 2 |
| CA03 — Esgotada / vencida / inativa recusadas | ✅ Pré-verificado pela QA (navegador + API) • ⬜ Demonstração pendente | Roteiro, cenário 3 |
| CA04 — Cashback com PIX → PAGAMENTO_PENDENTE; sem PIX → erro | ✅ Pré-verificado pela QA (navegador) • ⬜ Demonstração pendente | Roteiro, cenário 4 |
| Histórico "Meus Resgates" exibe código, pontos e selo | ✅ Pré-verificado pela QA (navegador) • ⬜ Demonstração pendente | Roteiro, cenários 1 e 4 |
| Resultados observados e prints preenchidos no roteiro | ⬜ Pendente | A preencher na demonstração |

## 3. Observações e pendências

- ⚠️ **Sem controle de concorrência:** dois resgates simultâneos da última unidade de estoque podem passar nos dois. Fora do escopo do MVP, mas vale registrar como risco.
- ⚠️ A situação `UTILIZADO`/`EXPIRADO` (cupom) e `PAGAMENTO_CONFIRMADO` (cashback) existem como valores previstos, mas **não há fluxo** para mudar para eles no MVP.
- ⚠️ "Esgotada" e "inativa" não são clicáveis na tela (botão desabilitado / oferta oculta); esses erros só aparecem ao chamar a API direto.
- Mensagens de erro na tela somem em ~3,5 s.

## 4. Situação da história

| Item | Situação |
| :--- | :--- |
| Verificação técnica | ⚠️ Aprovada para o comportamento (testes verdes e eficazes); **TDD RED não comprovado** |
| Validação na interface | ⬜ Pendente (demonstração oficial) |
| **Aceite do Cliente/PO** | ⬜ **PENDENTE** — nome: __________________ data: ____/____/2026 |
| **Status da User Story** | ⬜ **DONE** — só marcar após o aceite do PO |
