# Roteiro de Teste de Aceitação — US03: Resgate de Benefícios e Cashback

| Campo | Informação |
| :--- | :--- |
| **História** | US03 — Resgate de Benefícios e Cashback (8 SP — Essencial) |
| **Elaborado por** | Pessoa 4 (Requisitos, TDD e QA) |
| **Telas** | `http://localhost:8080/index.html` → abas **Benefícios** e **Meus Resgates** |
| **APIs usadas pela tela** | `GET /api/ofertas` • `POST /api/resgates` • `GET /api/resgates/usuario/1` |
| **Regra** | 100 pontos = R$ 2,00. Cashback nasce como `PAGAMENTO_PENDENTE` (não há transferência real). |

## 1. Ofertas de demonstração (conferidas no `data.sql` e na tela)

| ID | Oferta | Tipo | Custo | Estoque inicial |
| :-: | :--- | :-: | :-: | :-: |
| 20 | Desconto de 10% em compras (Supermercado Verde) | CUPOM | 500 pts | 15 |
| 10 | Cashback de R$ 5,00 (Mandabaa Cashback) | CASHBACK | 800 pts | 10 |
| 30 | Desconto na Conta de Energia (EcoLuz Solar) | CUPOM | 1.500 pts | **0 (esgotada)** |

Na tela as ofertas aparecem nesta ordem (por custo): **Desconto 10% → Cashback → Energia**.

## 2. Antes de começar

1. Aplicação e banco no ar (`docs/guia-demonstracao-pessoa4.md`).
2. **Restaurar o banco** (saldo 1.250, estoques 15/10/0, zero resgates):

```powershell
Get-Content docs\roteiros-aceitacao\restaurar-demo.sql -Encoding UTF8 | docker exec -i postgres-mandabaa psql -U postgres -d mandabaa_db
```

3. Abrir `http://localhost:8080/index.html`, apertar **F5**.

> ⚠️ **Mensagens de erro somem em ~3,5 segundos** (aviso vermelho no canto inferior direito). Deixe o print pronto antes de clicar em **Confirmar**.
>
> ⚠️ Cada resgate **muda saldo e estoque**. Por isso **cada cenário tem a sua preparação**, e é só restaurar o banco para voltar ao início.

---

## Cenário 1 — CA01: Resgate de cupom (caminho feliz)

**Critério:** Dado um usuário com 1.000 pontos e um cupom ativo de 500 pontos com estoque, ao confirmar: saldo vai a 500, estoque cai 1, resgate salvo como `DISPONIVEL`, com código de comprovante.

**Preparação (saldo 1.000):**

```powershell
Get-Content docs\roteiros-aceitacao\restaurar-demo.sql -Encoding UTF8 | docker exec -i postgres-mandabaa psql -U postgres -d mandabaa_db -q
docker exec postgres-mandabaa psql -U postgres -d mandabaa_db -c "UPDATE usuario SET saldo_pontos = 1000 WHERE id = 1;"
```

F5 na página. *(Observação: no banco restaurado o estoque do cupom é 15, não 5 como no teste unitário. O que se verifica é a diminuição de 1: 15 → 14.)*

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | Clicar na aba **Benefícios** | — | "Seu saldo atual: **1.000 pontos**" |
| 2 | No card **Desconto de 10% em compras**, clicar **Resgatar** | — | Janela **Confirmar Resgate**: *Você vai trocar 500 pontos por "Desconto de 10% em compras". Válido em produtos selecionados nas lojas parceiras.* — **sem** campo PIX |
| 3 | Clicar **Confirmar** | — | Janela **Resgate Confirmado!** com *Cupom disponível! Apresente o código abaixo na loja parceira:* e um **código de 12 letras/números** |
| 4 | Clicar **Fechar e Atualizar Saldo** | — | "Seu saldo atual: **500 pontos**" |
| 5 | Abrir a aba **Meus Resgates** | — | **Desconto de 10% em compras** • Código: *(o mesmo do passo 3)* • 500 pontos • selo **DISPONIVEL** |
| 6 | Conferir o estoque (PowerShell) | `docker exec postgres-mandabaa psql -U postgres -d mandabaa_db -c "SELECT estoque FROM oferta WHERE id = 20;"` | **14** |

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

---

## Cenário 2 — CA02: Saldo insuficiente (erro)

**Critério:** Dado saldo de 200 pontos e oferta de 500, o sistema recusa informando que **faltam 300 pontos**, sem descontar saldo nem alterar estoque.

**Preparação (saldo 200):**

```powershell
Get-Content docs\roteiros-aceitacao\restaurar-demo.sql -Encoding UTF8 | docker exec -i postgres-mandabaa psql -U postgres -d mandabaa_db -q
docker exec postgres-mandabaa psql -U postgres -d mandabaa_db -c "UPDATE usuario SET saldo_pontos = 200 WHERE id = 1;"
```

F5 na página.

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | Aba **Benefícios** | — | "Seu saldo atual: **200 pontos**" |
| 2 | **Resgatar** no *Desconto de 10%* → **Confirmar** | — | Aviso vermelho: **Saldo insuficiente. Faltam 300 pontos.** |
| 3 | Olhar o saldo na tela | — | Continua **200 pontos**; **não** aparece o comprovante |
| 4 | Aba **Meus Resgates** | — | **Vazia** (nenhum resgate novo) |
| 5 | Conferir estoque (PowerShell) | mesmo comando do cenário 1, passo 6 | Continua **15** |

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

---

## Cenário 3 — CA03: Oferta indisponível (esgotada, inativa e vencida)

**Critério:** Oferta sem estoque, inativa ou vencida é recusada e o saldo permanece.

Fazer **os três** (3A, 3B, 3C). Antes de cada um, **restaurar o banco** (saldo 1.250).

### 3A — Esgotada

**Preparação:** restaurar o banco. A oferta 30 já nasce esgotada.

| # | Passo | Resultado esperado |
| :-: | :--- | :--- |
| 1 | Aba **Benefícios** → card **Desconto na Conta de Energia** | O botão aparece **desabilitado**, escrito **Esgotado**; a tela não deixa clicar |
| 2 | Tentar o resgate direto na API (PowerShell, abaixo) | `HTTP 400` e `{"mensagem":"Esta oferta está esgotada.","status":"ERRO"}` |
| 3 | Conferir saldo na tela (F5) | Continua **1.250 pontos** |

```powershell
$p = @{ usuarioId = 1; ofertaId = 30; tipo = "CUPOM" } | ConvertTo-Json
try { Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/resgates -ContentType "application/json" -Body $p } catch { "HTTP " + [int]$_.Exception.Response.StatusCode; (New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())).ReadToEnd() }
```

### 3B — Vencida

**Preparação:** restaurar o banco e deixar o cupom vencido:

```powershell
docker exec postgres-mandabaa psql -U postgres -d mandabaa_db -c "UPDATE oferta SET validade = '2026-11-01' WHERE id = 20;"
```

| # | Passo | Resultado esperado |
| :-: | :--- | :--- |
| 1 | F5 → aba **Benefícios** → **Resgatar** no *Desconto de 10%* → **Confirmar** | Aviso vermelho: **Esta oferta está vencida.** |
| 2 | Conferir saldo | Continua **1.250 pontos**; sem comprovante; **Meus Resgates** vazio |

### 3C — Inativa

**Preparação:** restaurar o banco e desativar a oferta:

```powershell
docker exec postgres-mandabaa psql -U postgres -d mandabaa_db -c "UPDATE oferta SET ativa = false WHERE id = 20;"
```

| # | Passo | Resultado esperado |
| :-: | :--- | :--- |
| 1 | F5 → aba **Benefícios** | O *Desconto de 10%* **não aparece mais** na lista (a tela só mostra ofertas ativas); ficam só Cashback e Energia |
| 2 | Tentar o resgate direto na API (mesmo comando de 3A, trocando `ofertaId = 20`) | `HTTP 400` e `{"mensagem":"Esta oferta está inativa.","status":"ERRO"}` |
| 3 | Conferir saldo | Continua **1.250 pontos** |

| Resultado observado (3A / 3B / 3C) | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

---

## Cenário 4 — CA04: Cashback com chave PIX (caminho feliz)

**Critério:** Com saldo suficiente e chave PIX informada, os pontos são descontados e o resgate fica **`PAGAMENTO_PENDENTE`**.

**Preparação:** restaurar o banco (saldo 1.250, cashback custa 800).

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | Aba **Benefícios** → card **Cashback de R$ 5,00** → **Resgatar** | — | Janela de confirmação **com o campo "Forma de recebimento (Chave PIX)"** |
| 2 | Preencher a chave PIX | `alex@email.com` | — |
| 3 | Clicar **Confirmar** | — | Janela **Resgate Confirmado!** com *Solicitação de cashback registrada! Situação: Pagamento Pendente.* e um código |
| 4 | **Fechar e Atualizar Saldo** | — | Saldo **450 pontos** (1.250 − 800) |
| 5 | Aba **Meus Resgates** | — | **Cashback de R$ 5,00** • 800 pontos • selo **PAGAMENTO_PENDENTE** |

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

### 4B — Cashback SEM chave PIX (erro; regra adicional do plano)

**Preparação:** restaurar o banco.

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | **Resgatar** no cashback → **Confirmar** sem preencher o PIX | campo vazio | Aviso vermelho: **Informe a chave PIX para solicitar cashback.** |
| 2 | Conferir saldo e **Meus Resgates** | — | Saldo **1.250**; nenhum resgate novo |

| Situação | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| :--- | :--- |

---

## Pré-verificação feita pela QA (antes da demonstração)

Em 23/11/2026, no navegador contra a aplicação real (PostgreSQL 16 em Docker, porta 5499):

- Cenário 1: cupom resgatado, saldo 1.250 → 750 (neste teste o saldo inicial era 1.250), estoque 15 → 14, comprovante exibido, resgate em **Meus Resgates** com selo DISPONIVEL.
- Cenário 2: saldo 200 → aviso **"Saldo insuficiente. Faltam 300 pontos."**, saldo 200, estoque 15 e zero resgates gravados.
- Cenário 3: 3A (botão "Esgotado" desabilitado + API HTTP 400), 3B (aviso "Esta oferta está vencida.") e 3C (oferta some da lista + API "Esta oferta está inativa.").
- Cenário 4: cashback com PIX → "Pagamento Pendente", saldo 1.250 → 450; 4B → "Informe a chave PIX para solicitar cashback.".

> Isto **não substitui** a execução na demonstração. Os campos de resultado, situação e evidência ficam em branco.
