# Roteiro de Teste de Aceitação — US05: Consulta de Saldo e Carteira

| Campo | Informação |
| :--- | :--- |
| **História** | US05 — Consulta de Saldo e Carteira (3 SP — Essencial) |
| **Elaborado por** | Pessoa 4 (Requisitos, TDD e QA) |
| **Tela** | `http://localhost:8080/index.html` → aba **Minha Carteira** |
| **API usada pela tela** | `GET /api/carteira/1` |
| **Usuário de demonstração** | Alex (ID 1) — a tela sempre consulta o usuário 1 |

## 1. Antes de começar (vale para todos os roteiros)

1. Docker com o banco no ar e a aplicação rodando (veja `docs/guia-demonstracao-pessoa4.md`).
2. **Restaurar o banco** para o estado inicial (na raiz do projeto, PowerShell):

```powershell
Get-Content docs\roteiros-aceitacao\restaurar-demo.sql -Encoding UTF8 | docker exec -i postgres-mandabaa psql -U postgres -d mandabaa_db
```

3. Abrir `http://localhost:8080/index.html` e apertar **F5**.

> ⚠️ **Atenção:** se o servidor estiver desligado, o `script.js` mostra *dados de exemplo* (1.250 pontos) em vez de dar erro. Para ter certeza de que a tela está falando com o backend, abra `http://localhost:8080/api/carteira/1` em outra aba: deve aparecer um JSON.

Cada cenário abaixo traz sua **própria preparação**, então pode ser executado em qualquer ordem.

---

## Cenário 1 — CA01: Carteira com saldo (caminho feliz)

**Critério (Dado/Quando/Então):** Dado que o usuário possui 1.250 pontos, quando consulta a carteira, então vê saldo 1.250, equivalente R$ 25,00, sem aviso de saldo zero e o texto com a regra de cálculo.

**Preparação:** restaurar o banco (item 1 acima). Nada mais.

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | Abrir a tela e clicar na aba **Minha Carteira** | — | Card **Saldo Disponível** aparece |
| 2 | Olhar o número grande | — | **1.250 pontos** |
| 3 | Olhar a linha abaixo do saldo | — | **Equivalente a R$ 25,00** |
| 4 | Ler o texto pequeno | — | **Cada 1 kg de PET validado vale 50 pontos • 100 pontos = R$ 2,00** |
| 5 | Procurar a faixa amarela de aviso | — | **Não aparece** aviso de "sem pontos" |
| 6 | Clicar em **🎁 Ver Benefícios** | — | Abre a aba **Benefícios** mostrando "Seu saldo atual: 1.250 pontos" |

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência (print/arquivo)** | |

---

## Cenário 2 — CA02: Carteira sem saldo (caminho alternativo)

**Critério:** Dado que o usuário possui 0 pontos, quando consulta a carteira, então vê saldo 0, R$ 0,00, aviso de carteira sem saldo e orientação para entregar PET.

**Preparação (zera o saldo do Alex):**

```powershell
docker exec postgres-mandabaa psql -U postgres -d mandabaa_db -c "UPDATE usuario SET saldo_pontos = 0 WHERE id = 1;"
```

Depois, **F5** na página.

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | Clicar na aba **Minha Carteira** | — | Card de saldo aparece |
| 2 | Olhar o número grande | — | **0 pontos** |
| 3 | Olhar a linha do equivalente | — | **Equivalente a R$ 0,00** |
| 4 | Olhar a faixa amarela | — | **Você ainda não tem pontos. Entregue garrafas PET em um ponto de coleta para começar!** |
| 5 | Texto da regra | — | Continua exibindo *50 pontos por kg • 100 pontos = R$ 2,00* |

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

> Ao terminar, **restaure o banco** (item 1) antes do próximo roteiro.

---

## Cenário 3 — CA03: Usuário inexistente (erro)

**Critério:** Dado um ID que não existe, quando a carteira é consultada, então o sistema recusa com "usuário não encontrado" (no código: `RecursoNaoEncontradoExcecao`).

> A tela só consulta o usuário 1, então este erro **não pode ser provocado pelos botões**. Faça pelo navegador.

**Preparação:** nenhuma.

| # | Passo | Dados a informar | Resultado esperado |
| :-: | :--- | :--- | :--- |
| 1 | Em uma nova aba, abrir o endereço ao lado | `http://localhost:8080/api/carteira/999` | A página mostra `{"mensagem":"Usuário não encontrado: 999","status":"ERRO"}` |
| 2 | Conferir o código HTTP (F12 → aba Rede/Network) | — | **404** |
| 3 | Voltar à tela e apertar F5 | — | Saldo do Alex segue normal (nada foi alterado) |

| Resultado observado | |
| :--- | :--- |
| **Situação** | ☐ Aprovado  ☐ Reprovado  ☐ Pendente |
| **Evidência** | |

---

## Pré-verificação feita pela QA (antes da demonstração)

Em 23/11/2026 os cenários 1 e 2 foram executados no navegador contra a aplicação real (PostgreSQL 16 em Docker, porta 5499) e o cenário 3 pela API. Os textos e números acima são os que apareceram.

> Isto **não substitui** a execução na demonstração: os campos "Resultado observado", "Situação" e "Evidência" ficam em branco para serem preenchidos por quem demonstra.
