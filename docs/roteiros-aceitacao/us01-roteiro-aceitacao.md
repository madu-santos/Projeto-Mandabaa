# Roteiro de Teste de Aceitação — US01: Registro e Histórico de Coletas

| Campo | Informação |
| :--- | :--- |
| **História** | US01 — Registro e Histórico de Coletas (2 SP — Importante) |
| **Elaborado por** | Pessoa 4 (Requisitos, TDD e QA) |
| **Tela** | `http://localhost:8080/index.html` → aba **Minha Carteira** (bloco *Histórico de Coletas*) |
| **APIs** | `GET /api/coletas/usuario/1` (tela) • `POST /api/coletas` (**sem botão na tela**) |
| **Regra** | 50 pontos por kg de PET (+ bônus, quando houver) |

## 1. Antes de começar

1. Aplicação e banco no ar (`docs/guia-demonstracao-pessoa4.md`).
2. **Restaurar o banco**:

```powershell
Get-Content docs\roteiros-aceitacao\restaurar-demo.sql -Encoding UTF8 | docker exec -i postgres-mandabaa psql -U postgres -d mandabaa_db
```

3. Abrir `http://localhost:8080/index.html` e apertar **F5**.

> ⚠️ **Limitação conhecida da interface:** a tela **apenas exibe** o histórico. **Não existe formulário/botão para registrar uma nova coleta** (no mundo real, quem registra é o responsável pelo ponto). Por isso, nos cenários 2 e 3 a coleta é enviada pelo PowerShell e **o resultado é conferido na tela**. Esse ponto foi repassado ao grupo (Pessoa 5) como pendência, caso queiram uma tela de registro.

---

## Cenário 1 — CA01: Histórico e total de PET (caminho feliz)

**Critério:** Dadas duas entregas validadas (2,5 kg → 125 pts + 25 de bônus; 1,8 kg → 90 pts, sem bônus), ao consultar o histórico aparecem as duas entregas e o total de **4,3 kg**.

**Preparação:** restaurar o banco (item 1).

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | Abrir a aba **Minha Carteira** e descer até **Histórico de Coletas** | — | Bloco aparece |
| 2 | Ler "Total de PET entregue" | — | **4,3 kg** |
| 3 | Conferir a 1ª linha | — | **PET entregue — Cooperativa Verde** • **2,5 kg • 2026-11-14** • **+125 pts (+25 bônus)** |
| 4 | Conferir a 2ª linha | — | **PET entregue — Ecoponto Central** • **1,8 kg • 2026-11-09** • **+90 pts** (sem "bônus") |
| 5 | Conferir a ordem | — | Mais recente primeiro (14/11 antes de 09/11) |

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

---

## Cenário 2 — CA02: Registrar nova entrega validada (caminho feliz)

**Critério:** Dado um usuário com **1.000 pontos**, ao registrar uma entrega validada de **2 kg** (100 pontos, sem bônus), a coleta entra no histórico e o saldo passa a **1.100 pontos**.

**Preparação (deixa o saldo em 1.000):**

```powershell
docker exec postgres-mandabaa psql -U postgres -d mandabaa_db -c "UPDATE usuario SET saldo_pontos = 1000 WHERE id = 1;"
```

Apertar **F5** e anotar: saldo **1.000** e total **4,3 kg**.

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | No PowerShell, registrar a coleta (comando abaixo) | usuário 1, "Ponto Central", **2,0 kg**, bônus 0 | Resposta com `"pesoKg":2.0`, `"pontosRecebidos":100`, `"bonusRecebido":0` |
| 2 | Na tela, apertar **F5** na aba **Minha Carteira** | — | Saldo **1.100 pontos** • Equivalente **R$ 22,00** |
| 3 | Ver o Histórico de Coletas | — | Nova linha no topo: **PET entregue — Ponto Central • 2,0 kg • (data de hoje) • +100 pts** |
| 4 | Ler o total | — | **6,3 kg** (4,3 + 2,0) |

```powershell
$corpo = @{ usuarioId = 1; pontoColeta = "Ponto Central"; pesoKg = 2.0; bonus = 0 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/coletas -ContentType "application/json" -Body $corpo
```

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

---

## Cenário 3 — CA03: Peso inválido (erro)

**Critério:** Ao tentar registrar peso **menor ou igual a zero**, o sistema recusa e o **saldo não muda**.

**Preparação:** saldo em 1.000 (mesmo comando do cenário 2) e **F5**. Anotar saldo e total de kg.

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | No PowerShell, enviar peso **0** (comando abaixo) | pesoKg = 0 | `HTTP 400` e `{"mensagem":"O peso da entrega deve ser maior que zero.","status":"ERRO"}` |
| 2 | Repetir com peso **-1,5** | pesoKg = -1.5 | Mesma resposta (HTTP 400) |
| 3 | Na tela, apertar **F5** | — | Saldo continua **1.000 pontos** |
| 4 | Ver o Histórico de Coletas | — | **Nenhuma linha nova**; total continua **4,3 kg** |

```powershell
$ruim = @{ usuarioId = 1; pontoColeta = "Ponto Central"; pesoKg = 0; bonus = 0 } | ConvertTo-Json
try { Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/coletas -ContentType "application/json" -Body $ruim } catch { "HTTP " + [int]$_.Exception.Response.StatusCode; (New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())).ReadToEnd() }
```

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

---

## Cenário 4 (extra, fora dos 3 critérios do plano) — Usuário inexistente

Abrir `http://localhost:8080/api/coletas/usuario/999` → esperado `{"mensagem":"Usuário não encontrado: 999","status":"ERRO"}` (HTTP 404).

| Situação | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| :--- | :--- |

---

## Pré-verificação feita pela QA (antes da demonstração)

Em 23/11/2026: o cenário 1 foi conferido no navegador; os cenários 2 e 3 foram executados com os comandos acima (saldo 1.000 → **1.100**, total **6,3 kg**; peso 0 e −1,5 → **HTTP 400** e saldo/histórico inalterados) e o resultado foi conferido pela API. O cenário 4 foi conferido pela API (404).

> Isto **não substitui** a execução na demonstração. Os campos de resultado, situação e evidência ficam em branco.
