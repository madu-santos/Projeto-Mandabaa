# Guia de Demonstração — Pessoa 4 (QA)

Passo a passo para **abrir, testar, demonstrar e entregar**. Comandos em PowerShell, na raiz do projeto
(`C:\Users\cybel\IdeaProjects\Projeto-Mandabaa`).

## 0. Atenção antes de tudo (2 armadilhas deste computador)

1. **Java:** o terminal usa o **JDK 25**, e o projeto exige o **JDK 21** (o Lombok quebra no 25). No IntelliJ, em *File → Project Structure → SDK*, escolha o **21**. No terminal, use antes de qualquer `mvn`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

2. **Porta 5432 ocupada:** existe outro PostgreSQL rodando neste computador. Se `docker compose up -d` reclamar da porta 5432, pare o serviço local ou use o banco em outra porta (item 1B).

O `mvn` não está no PATH deste computador. No IntelliJ use o painel **Maven** (ou `Run`), ou instale o Maven.

## 1. Abrir e executar o projeto

**1A. Com o docker compose do projeto (porta 5432 livre):**

```powershell
docker compose up -d
```

**1B. Se a 5432 estiver ocupada** (banco na porta 5499, sem mexer em arquivos do projeto):

```powershell
docker run -d --name postgres-mandabaa -e POSTGRES_DB=mandabaa_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgrespassword -p 127.0.0.1:5499:5432 postgres:16-alpine
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5499/mandabaa_db"
```

Depois, subir a aplicação (no IntelliJ: executar `AplicacaoMandabaa`, ou no terminal):

```powershell
mvn spring-boot:run
```

Abrir `http://localhost:8080/index.html`. Teste rápido: `http://localhost:8080/api/carteira/1` deve mostrar um JSON (Alex, 1250).

## 2. Rodar as 3 classes de teste

```powershell
mvn test
```

Resultado esperado: `Tests run: 51, Failures: 0, Errors: 0` e `BUILD SUCCESS`. No IntelliJ: botão direito na pasta `src/test/java` → *Run All Tests*.

Rodar uma classe só:

```powershell
mvn test -Dtest=ServicoResgateTest,ServicoResgateComplementarTest
```

## 3. O que explicar sobre os testes (roteiro de fala)

- **Mockito:** os testes não usam banco. O repositório é “falso” (`@Mock`), então só a **regra de negócio** é testada, de forma rápida.
- **GIVEN / WHEN / THEN:** *Dado* (prepara os dados), *Quando* (chama o serviço), *Então* (confere o resultado).
- **O que cada classe cobre:**
  - `ServicoColetaTest`: histórico + total 4,3 kg; nova coleta 2 kg → saldo 1.100; peso ≤ 0 → erro.
  - `ServicoCarteiraTest`: 1.250 pts → R$ 25,00; 0 pts → aviso; usuário inexistente → erro.
  - `ServicoResgateTest`: cupom (saldo 500, estoque −1, DISPONIVEL); faltam 300 pontos; oferta esgotada; cashback PAGAMENTO_PENDENTE.
  - As classes `...ComplementarTest` fecham as lacunas: oferta **inativa/vencida**, cashback **sem PIX**, “nada foi gravado nos erros”, regra de R$ 0,02 por ponto etc.
- **TDD em uma frase:** RED = escrever o teste antes e vê-lo falhar; GREEN = código mínimo para passar; REFACTOR = organizar sem quebrar.
- **Seja transparente (a professora pode perguntar):** US01 e US05 têm RED comprovado pelo Git; **US03 não** (o serviço foi feito antes dos testes). Os testes novos foram validados por uma simulação: quebrando a regra de propósito, eles falham (`evidencias/pessoa4-simulacao-mutacoes.txt`).

## 4. Executar os roteiros na interface

Sempre comece por **restaurar o banco** (cada roteiro também traz a sua preparação):

```powershell
Get-Content docs\roteiros-aceitacao\restaurar-demo.sql -Encoding UTF8 | docker exec -i postgres-mandabaa psql -U postgres -d mandabaa_db
```

Ordem sugerida (≈ 10 min): `us05` → `us01` → `us03`, em `docs/roteiros-aceitacao/`.

## 5. Mostrar sucesso e erro (resumo para a banca)

| História | Sucesso | Erro |
| :--- | :--- | :--- |
| US05 | 1.250 pts = R$ 25,00 | Saldo 0 → aviso amarelo • usuário 999 → 404 |
| US01 | Histórico 4,3 kg • nova coleta 2 kg → 1.100 | Peso 0 → HTTP 400, saldo igual |
| US03 | Cupom: 1.000 → 500, comprovante, DISPONIVEL • Cashback + PIX → PAGAMENTO_PENDENTE | Saldo 200 → “Faltam 300 pontos” • vencida • sem PIX |

⚠️ A tela **não tem formulário de nova coleta**: no US01 use o comando PowerShell do roteiro. Avise a banca com naturalidade (“o registro é feito pelo ponto de coleta; a tela do usuário só exibe”).
⚠️ Mensagens vermelhas somem em ~3,5 s: deixe o print pronto.
⚠️ Se o servidor estiver desligado, a tela mostra **dados de exemplo** em vez de erro.

## 6. Conferir os checklists com o PO

1. Abra `docs/checklists-aceite/` e passe os 3 checklists com o PO.
2. Preencha: data da demonstração, “Resultado observado” e prints nos roteiros.
3. Só marque **DONE** depois que o PO disser que aceita (campo “Aceite do Cliente/PO”).

## 7. Levar as alterações ao repositório e avisar a próxima pessoa

Hoje tudo está **somente no seu computador**, sem commit. Sugestão (branch própria, como o grupo combinou):

```powershell
git checkout -b feature/pessoa4-qa
git add src/test docs
git status
git commit -m "test/docs: testes complementares, roteiros, checklists e registros da Pessoa 4 (QA)"
git push -u origin feature/pessoa4-qa
```

Depois abra um Pull Request no GitHub para a `main`. Antes de entregar, **leia e confirme** os arquivos (a revisão humana é obrigatória) e adicione seus prints.

**Mensagem sugerida ao grupo / Pessoa 5:**

> Concluí a parte de QA: 51 testes (10 originais + 41 complementares), todos verdes com JDK 21; roteiros e checklists das 3 histórias em `docs/`; registro em `docs/registros-ia/pessoa4-qa-tdd.md`. **Pendências:** (1) a tela não tem formulário de nova coleta (US01 CA02/CA03 só por API); (2) falta registro de GREEN/REFACTOR da Pessoa 2 e não há RED da US03; (3) aceite do PO e marcação de DONE; (4) prints da demonstração. Para rodar: usar JDK 21 e a 5432 livre (ou porta alternativa).

## 8. Se algo der errado

| Sintoma | Causa provável | O que fazer |
| :--- | :--- | :--- |
| `cannot find symbol ... getX()` ao compilar | JDK 25 | `$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"` |
| `port is already allocated` (5432) | Outro PostgreSQL | Use o item 1B |
| Tela mostra 1.250 mas o banco foi zerado | Backend fora do ar → dados de exemplo | Conferir `http://localhost:8080/api/carteira/1` |
| Saldo/estoque “estranhos” | Roteiro anterior alterou dados | Rodar `restaurar-demo.sql` e F5 |
