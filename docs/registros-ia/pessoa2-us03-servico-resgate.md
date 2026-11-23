# Registro de Uso de IA e Revisão Humana — Pessoa 2 (Regras de Negócio da US03)

| Campo | Registro |
| :--- | :--- |
| **Responsável** | Pessoa 2 — Regras de Negócio da US03 (Resgate de Benefícios e Cashback) |
| **Data** | 21/11/2026 |
| **Etapa** | Sprint 5 / 5.1 — Implementação e Refatoração do `ServicoResgate` (fases GREEN e REFACTOR) |
| **IA utilizada** | Claude (Anthropic) — versão utilizada no ambiente desktop do grupo |
| **Entradas fornecidas à IA** | `plano_acao_mvp_mandabaa.md` (critérios de aceitação da US03), assinaturas das classes `ServicoResgate`, `SolicitacaoResgateDTO`, `RespostaResgateDTO`, `Oferta`, `Usuario`, `Resgate`, e os arquivos de exceção do pacote `comum.excecao`. |
| **Observação sobre o ciclo TDD** | O arquivo de testes `ServicoResgateTest.java` (Fase RED, gerado pela Pessoa 4) foi recebido por e-mail antes da implementação; a Fase GREEN foi feita localmente e commitada em seguida. Os commits do dia 21/11 não foram separados por fase (GREEN e REFACTOR aparecem como um único commit `c17c8e4`), o que é reconhecido como um gap de rastreabilidade. O comportamento do código está 100% correto e comprovado pelos 23 testes da Pessoa 4. |
| **Revisão humana** | ✅ Revisado. O código gerado foi lido linha a linha, testado na IDE com JDK 21 e ajustado conforme descrito na seção 3. |

---

## 1. Contexto e Critérios de Aceitação Usados (US03)

A US03 cobre o **Resgate de Benefícios e Cashback** (8 SP — Essencial). Os critérios que guiaram a implementação estão no `plano_acao_mvp_mandabaa.md` (Seção 3.3):

| Cenário | Descrição resumida |
| :--- | :--- |
| **CA01** | Usuário com saldo ≥ custo + oferta ativa/com estoque → resgate salvo como `DISPONIVEL`, saldo decrementado, comprovante gerado |
| **CA02** | Saldo insuficiente → `SaldoInsuficienteExcecao` com mensagem "Saldo insuficiente. Faltam X pontos." — saldo e estoque inalterados |
| **CA03** | Oferta esgotada, inativa ou vencida → `OfertaIndisponivelExcecao` — saldo inalterado |
| **CA04** | Cashback com chave PIX → resgate salvo como `PAGAMENTO_PENDENTE` |

---

## 2. Prompts utilizados (resumo fiel)

### 🟢 Prompt A — Fase GREEN

> *"Estamos na Fase GREEN do TDD no projeto Mandabaa. Tenho o seguinte arquivo de testes unitários [ServicoResgateTest.java colado]. Com base nesses testes, escreva apenas o código MÍNIMO necessário na classe ServicoResgate para que todos os testes passem. Não se preocupe com elegância. Não altere os testes. Classes disponíveis: [assinaturas coladas]."*

**Resposta da IA:** Gerou a implementação inicial de `ServicoResgate.realizarResgate()` com um bloco único de validações em sequência (verificar usuário → verificar oferta → verificar saldo → salvar resgate). O código passou nos 4 testes do RED.

### 🔵 Prompt B — Fase REFACTOR

> *"Agora estamos na Fase REFACTOR. Observe o código mínimo que gerou na fase GREEN [código colado]. Refatore aplicando boas práticas: Princípio da Responsabilidade Única (SRP), nomes claros em português e eliminação de duplicação. Garanta que a assinatura dos métodos e o comportamento externo permaneçam iguais. Liste e explique as refatorações feitas."*

**Resposta da IA:** Propôs a extração das validações em métodos privados com nomes descritivos.

---

## 3. Revisão Humana e Ajustes Aplicados (Human-in-the-Loop)

A IA gerou um código correto na essência, mas foram necessários os seguintes ajustes antes do commit:

| # | Ajuste feito | Motivo |
| :-: | :--- | :--- |
| 1 | **Mensagem exata de saldo insuficiente** | A IA gerou `"Pontos insuficientes: faltam X"`. O critério CA02 exige `"Saldo insuficiente. Faltam X pontos."` Corrigido manualmente. |
| 2 | **Validação de oferta nula (ID inexistente)** | O esqueleto original usava `orElseThrow(RecursoNaoEncontradoExcecao)`, mas o plano e os testes da Pessoa 4 esperam `OfertaIndisponivelExcecao` para oferta inexistente. Ajustado para lançar `OfertaIndisponivelExcecao`. |
| 3 | **Validação de chave PIX em branco** | A IA não havia incluído validação para chave PIX nula, vazia ou só com espaços. Adicionado manualmente: `if (tipo CASHBACK e pix em branco) → IllegalArgumentException`. |
| 4 | **Validação de tipo divergente** | Adicionada verificação de que `solicitacao.getTipo()` corresponde a `oferta.getTipo()`, evitando resgatar como CUPOM uma oferta de CASHBACK e vice-versa. |
| 5 | **Validação de entrada nula** | Adicionada guarda inicial: `if (solicitacao == null || ids nulos ou tipo em branco) → IllegalArgumentException` antes de consultar o banco. |
| 6 | **Nomes dos métodos privados em português** | A IA nomeou `validateBalance`, `validateOffer`. Renomeados para `validarSaldo`, `validarDisponibilidadeDaOferta`, `gerarCodigoComprovante` para seguir o padrão do projeto. |
| 7 | **`@Transactional` verificado** | Confirmado que o método `realizarResgate` está anotado com `@Transactional` para garantir atomicidade (rollback em caso de erro após debitar pontos). |

---

## 4. Refatorações da Fase REFACTOR — Explicação Detalhada

A versão GREEN tinha tudo em um único método `realizarResgate`. A versão REFACTOR extrai:

| Método privado | Responsabilidade |
| :--- | :--- |
| `validarEntrada(solicitacao)` | Verifica nulos e campos obrigatórios antes de qualquer acesso ao banco |
| `validarDisponibilidadeDaOferta(oferta)` | Checa estoque > 0, `ativa = true` e validade ≥ hoje (lança `OfertaIndisponivelExcecao` com mensagem específica para cada causa) |
| `validarSaldo(usuario, oferta)` | Calcula pontos faltantes e lança `SaldoInsuficienteExcecao` com a mensagem exata do CA02 |
| `gerarCodigoComprovante()` | Gera UUID único para o campo `codigoComprovante` do resgate |
| `construirResgate(usuario, oferta, solicitacao, situacao)` | Monta o objeto `Resgate` com todos os campos, mantendo o método principal limpo |

**Benefícios:**
- Cada método tem uma única razão para mudar (SRP).
- O método `realizarResgate` ficou legível como um "roteiro" de alto nível.
- As mensagens de erro são geradas no ponto exato da validação, sem `if-else` aninhados.

---

## 5. Resultado Final

| Critério | Status |
| :--- | :---: |
| Todos os 4 testes originais (`ServicoResgateTest`) passam | ✅ |
| Todos os 19 testes complementares (`ServicoResgateComplementarTest`) passam | ✅ |
| Total do projeto: 51 testes, 0 falhas | ✅ |
| Código refatorado com SRP e nomes em português | ✅ |
| `@Transactional` garantindo atomicidade | ✅ |
| **Gap reconhecido:** RED não comprovado historicamente (commits do mesmo dia) | ⚠️ Documentado |
