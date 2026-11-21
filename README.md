# 📋 PLANO DE AÇÃO E ROTEIRO DE IMPLEMENTAÇÃO — MVP MANDABAA (SPRINT 5 & 5.1)

**Projeto:** Mandabaa — Plataforma de Gestão e Incentivo à Reciclagem de PET  
**Disciplina:** Engenharia de Software | DCX - UFPB  
**Metodologia:** TDD (Desenvolvimento Orientado a Testes) e V&V (Verificação e Validação) com IA Generativa e revisão humana  
**Prazo:** 5 dias | **Capacidade da Sprint:** 13 Story Points  

---

## 1. OBJETIVO DA ENTREGA E REGRAS DA PROFESSORA

Entregar uma primeira versão funcional e visualmente agradável do **Mandabaa** em 5 dias, unindo o que foi pedido na **Sprint 5** (tabela atualizada do Backlog com Prioridade e Story Points, seleção de 13 SP e implementação parcial) com o roteiro obrigatório da **Sprint 5.1** (Verificação e Validação com TDD assistido por IA):

1. **Verificação (*"Estamos construindo o produto corretamente?"*):** garantida por testes unitários automatizados em **Java com JUnit 5 e Mockito**, no ciclo estrito:
   * 🔴 **RED:** criar o teste a partir dos critérios *Dado que... Quando... Então...* e comprovar que ele **falha** antes de escrever a regra de negócio.
   * 🟢 **GREEN:** escrever apenas o código mínimo necessário na classe de serviço para fazer os testes passarem.
   * 🔵 **REFACTOR:** melhorar nomes, clareza e divisão de responsabilidades mantendo 100% dos testes verdes.
2. **Validação (*"Estamos construindo o produto certo?"*):** demonstrar na tela, com um **Roteiro de Teste de Aceitação** em linguagem simples, o caminho feliz e os cenários de erro, fechando com o **Checklist de Aceite (Definition of Done)**.
3. **Revisão Humana Obrigatória:** nada gerado por IA entra no projeto sem revisão, execução na IDE e registro de prompts/respostas pela equipe.

---

## 2. BACKLOG ATUALIZADO E ESCOPO DO MVP (SPRINT 5)

A tabela oficial também está salva no repositório em `docs/backlog-sprint5.md`.

### 2.1. Backlog Consolidado (Total: 35 Story Points)

| ID | User Story | Prioridade (Cliente) | Story Points | Situação no Projeto |
| :---: | :--- | :---: | :---: | :--- |
| **US01** | Registro e Histórico de Coletas | Importante | 2 | Modelada na Sprint 4 |
| **US02** | Busca de Pontos de Coleta Próximos | Essencial | 5 | Modelada na Sprint 4 |
| **US03** | **Resgate de Benefícios e Cashback** | **Essencial** | **8** | **SELECIONADA — Sprint 5 / 5.1** |
| **US04** | Cadastro e Gestão de Ofertas por Empresas Parceiras | Importante | 8 | Backlog futuro (passaria de 13 SP) |
| **US05** | Consulta de Saldo e Carteira | Essencial | 3 | Modelada na Sprint 4 + apoio visual na US03 |
| **US06** | Ranking de Entregas | Desejável | 3 | Backlog futuro |
| **US07** | **Notificações e Campanhas Temporárias de Recompensas** | **Desejável** | **5** | **SELECIONADA — Sprint 5 / 5.1** |
| **US08** | Consulta da Verificação dos Pontos de Coleta | Importante | 1 | Modelada na Sprint 4 |

### 2.2. Seleção da Sprint (Capacidade: 13 Story Points)
$$\text{US03 (8 SP)} + \text{US07 (5 SP)} = \mathbf{13\text{ Story Points}}$$

### 2.3. O que entra e o que fica de fora no MVP

* **Entra no MVP:**
  * **US03:** visualizar saldo na área de benefícios; listar ofertas com parceiro, descrição, custo em pontos e condições; confirmar resgate com resumo; debitar pontos e atualizar estoque; gerar comprovante/código; consultar histórico de benefícios resgatados com situação (`DISPONIVEL` / `UTILIZADO` / `EXPIRADO` para cupom e `PAGAMENTO_PENDENTE` / `PAGAMENTO_CONFIRMADO` para cashback); avisar exatamente **quantos pontos faltam** quando o saldo for insuficiente.
  * **US07:** listar campanhas temporárias com benefício, validade, condições e marcação clara de `ATIVA` ou `EXPIRADA`; permitir ligar/desligar a preferência de notificações do usuário e exibir avisos dentro do sistema respeitando essa preferência.
  * **Carga inicial no banco:** usuário de demonstração, ofertas e campanhas já cadastrados via `data.sql`.
* **Fica de fora neste recorte (simplificações planejadas):**
  * Transferência bancária real / API de PIX (cashback nasce como `PAGAMENTO_PENDENTE`).
  * Aplicativo mobile nativo e envio real de e-mail, SMS ou push externo.
  * Painel de cadastro de ofertas pelas empresas parceiras (US04).

---

## 3. CRITÉRIOS DE ACEITAÇÃO EM GIVEN-WHEN-THEN (FIÉIS AO BACKLOG v3)

Estes cenários servem de entrada direta para a **Fase RED** do TDD:

### 3.1. US03 — Resgate de Benefícios e Cashback (8 SP | Essencial)

* **CA01 (Caminho Feliz — Resgate de Cupom):**
  * **Dado que** o usuário possui 1.000 pontos e escolhe um cupom ativo com estoque 5 que custa 500 pontos,
  * **Quando** ele confirma o resgate,
  * **Então** o saldo cai para 500 pontos, o estoque cai para 4, o resgate fica salvo com situação `DISPONIVEL` e um código de comprovante é devolvido.
* **CA02 (Regra de Exceção — Saldo Insuficiente com Cálculo do que Falta):**
  * **Dado que** o usuário possui 200 pontos e tenta resgatar uma oferta de 500 pontos,
  * **Quando** ele solicita o resgate,
  * **Então** o sistema lança `SaldoInsuficienteExcecao` informando que **faltam 300 pontos**, não desconta nada do saldo e não altera o estoque.
* **CA03 (Regra de Exceção — Oferta Indisponível, Esgotada ou Vencida):**
  * **Dado que** uma oferta está com estoque 0, inativa ou com data de validade vencida,
  * **Quando** o usuário tenta resgatá-la,
  * **Então** o sistema lança `OfertaIndisponivelExcecao` e mantém o saldo do usuário inalterado.
* **CA04 (Caminho Feliz — Solicitação de Cashback):**
  * **Dado que** o usuário possui saldo suficiente e escolhe uma oferta de cashback informando a forma de recebimento (ex.: chave PIX),
  * **Quando** ele confirma o resgate,
  * **Então** os pontos são descontados e o resgate é registrado com situação `PAGAMENTO_PENDENTE`.

### 3.2. US07 — Notificações e Campanhas Temporárias (5 SP | Desejável)

* **CA01 (Caminho Feliz — Identificação de Campanhas Ativas e Expiradas):**
  * **Dado que** existem campanhas cadastradas com datas de término futuras e passadas,
  * **Quando** o sistema lista as campanhas,
  * **Então** as vigentes recebem situação `ATIVA` e as vencidas recebem situação `EXPIRADA`, trazendo benefício, período e condições.
* **CA02 (Caminho Feliz — Notificações Ativadas):**
  * **Dado que** o usuário está com `receberNotificacoes = true` e há campanhas ativas,
  * **Quando** o sistema consulta os avisos do usuário,
  * **Então** retorna apenas as campanhas ativas disponíveis.
* **CA03 (Regra de Negócio — Preferência de Notificações Desativada):**
  * **Dado que** o usuário desativou notificações (`receberNotificacoes = false`),
  * **Quando** o sistema consulta os avisos do usuário,
  * **Então** retorna lista vazia, respeitando a preferência configurada.

---

## 4. ARQUITETURA EM PORTUGUÊS E CONTRATOS DE API REST

O sistema segue o **Monólito Modular em Camadas** definido na Sprint 4, com todos os pacotes e classes em português para facilitar a leitura de toda a equipe:

```text
Projeto-Mandabaa/
├── docker-compose.yml              (PostgreSQL 15 no Docker)
├── pom.xml                         (Java 21, Spring Boot 3.3.5, JPA, JUnit 5, Mockito)
├── docs/
│   └── backlog-sprint5.md          (Tabela oficial do Backlog v4)
└── src/
    ├── main/
    │   ├── java/com/mandabaa/
    │   │   ├── AplicacaoMandabaa.java
    │   │   ├── configuracao/       -> ConfiguracaoCors
    │   │   ├── comum/excecao/      -> SaldoInsuficienteExcecao, OfertaIndisponivelExcecao,
    │   │   │                          ManipuladorGlobalDeExcecoes
    │   │   ├── usuarios/           -> Usuario, RepositorioUsuario
    │   │   ├── parceiros/          -> Oferta, RepositorioOferta
    │   │   ├── carteira/           -> Resgate, RepositorioResgate, ServicoResgate,
    │   │   │                          SolicitacaoResgateDTO, RespostaResgateDTO
    │   │   └── engajamento/        -> Campanha, RepositorioCampanha, ServicoCampanha,
    │   │                              CampanhaDTO
    │   └── resources/
    │       ├── application.properties
    │       └── static/             -> index.html, estilo.css, script.js (base da Pessoa 5)
    └── test/java/com/mandabaa/     -> ServicoResgateTest, ServicoCampanhaTest (Pessoa 4)
```

### Contratos de API REST (JSON)

| Funcionalidade | Rota | Exemplo de Retorno |
| :--- | :--- | :--- |
| Consultar carteira | `GET /api/carteira/{usuarioId}` | `{"usuarioId": 1, "saldoPontos": 1000, "equivalenteReais": 20.00, "receberNotificacoes": true}` |
| Listar ofertas | `GET /api/ofertas` | Lista de ofertas com `parceiro`, `titulo`, `condicoes`, `tipo`, `custoPontos`, `estoque` |
| Solicitar resgate | `POST /api/resgates` | `{"resgateId": 1, "situacao": "DISPONIVEL", "codigoComprovante": "MANDABAA-A1B2", "novoSaldo": 500}` |
| Histórico de resgates | `GET /api/resgates/usuario/{usuarioId}` | Lista dos cupons e cashbacks já resgatados com sua `situacao` |
| Listar campanhas | `GET /api/campanhas` | Lista com `titulo`, `condicoes`, `multiplicadorBonus`, `situacao` (`ATIVA`/`EXPIRADA`) |
| Listar avisos do usuário | `GET /api/campanhas/notificacoes/{usuarioId}` | Campanhas ativas se `receberNotificacoes = true`, ou `[]` se `false` |
| Alterar preferência | `PUT /api/usuarios/{usuarioId}/notificacoes` | `{"receberNotificacoes": false}` |

---

## 5. DIVISÃO EQUILIBRADA ENTRE AS 5 PESSOAS

* **Pessoa 1 — Arquitetura de Software, Controladores REST e Integração:**  
  Monta e mantém a estrutura do Monólito Modular em Spring Boot, configura CORS e `ManipuladorGlobalDeExcecoes`, padroniza os DTOs, cria os **Controladores REST** (`ControladorCarteira`, `ControladorOferta`, `ControladorResgate`, `ControladorCampanha`) e garante que tudo integre limpo sem virar *Big Ball of Mud*.
* **Pessoa 2 — Regra de Negócio da US03 (Backend — 8 SP):**  
  Implementa e refatora a lógica completa de `ServicoResgate` (validações de saldo com cálculo de pontos faltantes, estoque/validade, geração de código de cupom e fluxo de cashback pendente) nas fases **GREEN** e **REFACTOR** do TDD.
* **Pessoa 3 — Banco de Dados, Persistência e Regra de Negócio da US07 (5 SP):**  
  Sobe o PostgreSQL no Docker, revisa entidades/repositórios, cria o `data.sql` com dados de demonstração e implementa/refatora a lógica da US07 em `ServicoCampanha` (campanhas ativas vs. expiradas e respeito às preferências de notificação) nas fases **GREEN** e **REFACTOR**.
* **Pessoa 4 — Requisitos, TDD (Fase RED) e Qualidade (QA):**  
  Conduz a metodologia da **Sprint 5.1**: gera e revisa os testes unitários em JUnit 5 + Mockito (`ServicoResgateTest` e `ServicoCampanhaTest`), registra as evidências **RED**, valida **GREEN** e **REFACTOR**, gera os **Roteiros de Teste de Aceitação** e preenche os **Checklists de Aceite (Definition of Done)**.
* **Pessoa 5 — UI/UX, Aparência/Layout das Telas e Frontend Web:**  
  Cuida de todo o visual e experiência de uso em `index.html`, `estilo.css` e `script.js` (paleta verde Mandabaa, telas de carteira, catálogo com condições, confirmação, comprovante, histórico de resgates, campanhas e chave de ativar/desativar notificações) e conduz a demonstração visual na homologação.

---

## 6. ROTEIRO DETALHADO DIA A DIA (INSTRUÇÕES PASSO A PASSO)

### 📅 DIA 1 — Arquitetura Base, Banco no Docker, Visual Inicial e Fase RED (US03)

* **Pessoa 1 (Arquitetura):**
  1. Estruturar o projeto Spring Boot com os pacotes em português (`configuracao`, `comum.excecao`, `usuarios`, `parceiros`, `carteira`, `engajamento`). *(Já concluído e compilando!)*
  2. Alinhar os contratos JSON (Item 4) com todas as pessoas.
  3. Criar a branch `develop` (ou orientar cada pessoa a trabalhar em sua branch) e revisar os primeiros pull requests.
* **Pessoa 2 (Regra de Negócio US03):**
  1. Clonar o repositório, abrir no IntelliJ e revisar `ServicoResgate`, `SolicitacaoResgateDTO` e `RespostaResgateDTO`.
  2. Alinhar com a Pessoa 4 exatamente quais exceções e mensagens (`SaldoInsuficienteExcecao` com pontos faltantes e `OfertaIndisponivelExcecao`) serão esperadas nos testes.
* **Pessoa 3 (Banco de Dados + US07):**
  1. Executar `docker-compose up -d` para subir o PostgreSQL local e testar a conexão.
  2. Revisar as entidades iniciais (`Usuario`, `Oferta`, `Resgate`, `Campanha`) e ajustar algum campo se precisar.
  3. Começar a rascunhar o arquivo `src/main/resources/data.sql` com 1 usuário (1.000 pontos), 3 ofertas (cupom disponível, cashback e cupom esgotado) e 2 campanhas (1 ativa e 1 expirada).
* **Pessoa 4 (TDD / QA):**
  1. Usar o **Prompt 1 (Fase RED)** com os critérios da **US03** (Item 3.1) para gerar `ServicoResgateTest.java`.
  2. Revisar o código gerado (checar `@ExtendWith(MockitoExtension.class)`, mocks dos 3 repositórios e comentários `GIVEN / WHEN / THEN`).
  3. Rodar o teste na IDE contra o esqueleto atual de `ServicoResgate` e **tirar print comprovando a falha vermelha (RED 🔴)**.
* **Pessoa 5 (Layout e Frontend):**
  1. Abrir `src/main/resources/static/index.html`, `estilo.css` e `script.js` no navegador para conhecer a base inicial.
  2. Desenhar os ajustes visuais desejados (tipografia, cores verdes alinhadas ao mockup, ícones, espaçamentos) e planejar onde vão ficar o **histórico de resgates** e o **botão de ligar/desligar notificações**.

---

### 📅 DIA 2 — Fase GREEN (US03), Carga do Banco, Fase RED (US07) e Telas Principais

* **Pessoa 1 (Arquitetura e Controladores):**
  1. Criar `ControladorCarteira` (`GET /api/carteira/{usuarioId}`) e `ControladorOferta` (`GET /api/ofertas`) conectados aos repositórios.
  2. Garantir que o sistema sobe conectado ao Docker PostgreSQL sem erros.
* **Pessoa 2 (US03 — Fase GREEN):**
  1. Pegar a classe `ServicoResgateTest.java` aprovada pela Pessoa 4.
  2. Rodar o **Prompt 2 (Fase GREEN)** para implementar o código mínimo em `ServicoResgate.java`.
  3. Executar os testes unitários na IDE até todos ficarem **verdes (GREEN 🟢)** e salvar o print + prompt usado.
* **Pessoa 3 (Banco de Dados + US07):**
  1. Finalizar `src/main/resources/data.sql` com os dados de demonstração e mudar `spring.sql.init.mode=always` no `application.properties`.
  2. Testar a inicialização do Spring Boot conferindo no banco se o usuário, ofertas e campanhas foram gravados corretamente.
  3. Alinhar com a Pessoa 4 a assinatura de `ServicoCampanha` para os testes da US07.
* **Pessoa 4 (TDD / QA):**
  1. Conferir se a Pessoa 2 passou nos testes da US03 **sem alterar os testes**.
  2. Usar o **Prompt 1 (Fase RED)** com os critérios da **US07** (Item 3.2) para gerar `ServicoCampanhaTest.java`.
  3. Rodar o teste na IDE contra o esqueleto de `ServicoCampanha` e **registrar o print da falha vermelha (RED 🔴)**.
* **Pessoa 5 (Layout e Frontend):**
  1. Refinar o visual da aba **Minha Carteira** e da aba **Benefícios** (mostrando parceiro, descrição, condições de uso, custo em pontos e distinção visual entre Cupom e Cashback).
  2. Criar a área visual de **Meus Benefícios Resgatados** (exibindo código/comprovante e selos de situação: `DISPONIVEL`, `PAGAMENTO_PENDENTE`, etc.).

---

### 📅 DIA 3 — Fase REFACTOR (US03), Fase GREEN (US07) e Controladores Finais

* **Pessoa 1 (Arquitetura e Controladores):**
  1. Criar `ControladorResgate` (`POST /api/resgates` e `GET /api/resgates/usuario/{usuarioId}`).
  2. Criar `ControladorCampanha` (`GET /api/campanhas` e `GET /api/campanhas/notificacoes/{usuarioId}`) e o endpoint de preferência de notificação (`PUT /api/usuarios/{usuarioId}/notificacoes`).
  3. Testar todas as rotas via navegador ou ferramenta REST (Postman/Insomnia) garantindo respostas JSON limpas.
* **Pessoa 2 (US03 — Fase REFACTOR):**
  1. Rodar o **Prompt 3 (Fase REFACTOR)** sobre `ServicoResgate.java` para extrair métodos privados (ex.: `validarOfertaDisponivel`, `validarSaldoSuficiente`, `gerarCodigoComprovante`).
  2. Reexecutar `ServicoResgateTest` e comprovar que tudo continua **verde (REFACTOR 🔵)**.
  3. Salvar a explicação das refatorações para o registro de IA.
* **Pessoa 3 (US07 — Fase GREEN):**
  1. Receber `ServicoCampanhaTest.java` da Pessoa 4.
  2. Rodar o **Prompt 2 (Fase GREEN)** para implementar o mínimo em `ServicoCampanha.java` (classificação `ATIVA`/`EXPIRADA` por data e filtro de notificações conforme preferência do usuário).
  3. Executar os testes e registrar o **GREEN 🟢**.
* **Pessoa 4 (TDD / QA):**
  1. Auditar a refatoração da US03 (confirmando zero regressão) e o GREEN da US07.
  2. Organizar na pasta `docs/registros-ia/` os prompts, respostas e validações humanas coletados até aqui.
* **Pessoa 5 (Layout e Frontend):**
  1. Refinar os modais de **Confirmação de Resgate** (mostrando pontos a descontar, regras e campo de chave PIX quando for cashback) e de **Comprovante**.
  2. Criar na aba **Campanhas** o controle de ligar/desligar notificações e o painel de avisos do usuário.

---

### 📅 DIA 4 — Fase REFACTOR (US07), Integração Completa e Roteiros de Aceitação

* **Pessoa 1 (Integração Geral):**
  1. Testar junto com a Pessoa 5 todas as chamadas do `script.js` contra o backend real rodando na porta 8080.
  2. Conferir tratamento de erros (ex.: saldo insuficiente retornando HTTP 400 com a mensagem exata de quantos pontos faltam) e corrigir qualquer divergência de campo JSON.
* **Pessoa 2 (Revisão US03):**
  1. Testar o fluxo de ponta a ponta da US03 na tela (cupom, cashback, erro de saldo insuficiente, oferta esgotada) e ajustar qualquer detalhe de transação (`@Transactional`).
* **Pessoa 3 (US07 — Fase REFACTOR + Banco):**
  1. Rodar o **Prompt 3 (Fase REFACTOR)** sobre `ServicoCampanha.java`, reexecutar os testes e confirmar o **REFACTOR 🔵**.
  2. Conferir diretamente no PostgreSQL se os resgates feitos pela tela estão sendo persistidos e se o saldo e o estoque atualizam corretamente.
* **Pessoa 4 (Roteiros de Aceitação e Checklists):**
  1. Rodar o **Prompt 4** para gerar os **Roteiros de Teste de Aceitação** (em linguagem simples para o cliente/professora) da US03 e da US07 e salvar em `docs/roteiros-aceitacao/`.
  2. Preparar os **Checklists de Aceite (Definition of Done)** em `docs/checklists-aceite/`.
* **Pessoa 5 (Conexão Frontend e Acabamento):**
  1. Conectar 100% do `script.js` às rotas reais (carteira, ofertas, resgate, histórico de resgates, campanhas, avisos e preferência de notificação).
  2. Polir responsividade (celular e computador), contraste de cores e mensagens amigáveis de aviso.

---

### 📅 DIA 5 — Homologação (Sprint Review), Validação e Entrega Final

* **Pessoa 1 (Build e Revisão Arquitetural):**
  1. Executar `mvn clean test` e `mvn clean package` garantindo compilação limpa e 100% dos testes passando.
  2. Verificar se o repositório está limpo, organizado e com todos os commits sincronizados na branch principal.
* **Pessoa 2 e Pessoa 3 (Estabilidade e Banco Limpo):**
  1. Reiniciar o container Docker com carga limpa (`data.sql`) pronta para a apresentação.
  2. Apoiar correções pontuais caso algum cenário de aceitação encontre erro.
* **Pessoa 4 (Sessão de Homologação — Sprint 5.1):**
  1. Conduzir a demonstração em duas partes:
     * **A. Verificação Técnica:** rodar `ServicoResgateTest` e `ServicoCampanhaTest` na IDE mostrando todos verdes.
     * **B. Validação com o Cliente/PO:** executar passo a passo os Roteiros de Aceitação na interface (caminho feliz e erros).
  2. Preencher os **Checklists de Aceite (Definition of Done)** marcando as histórias como `DONE`.
* **Pessoa 5 (Apresentação Visual):**
  1. Navegar pela interface demonstrando os fluxos completos (saldo, ofertas, confirmação, voucher, cashback pendente, histórico, campanhas ativas/expiradas e preferências de aviso).
  2. Garantir o acabamento visual final e tirar prints das telas para o Notion.

---

## 7. PROMPTS OFICIAIS DO CICLO TDD (SPRINT 5.1)

### 🔴 Prompt 1 — Fase RED (Testes Unitários)
> *"Atue como um especialista em QA e instrutor de Java com JUnit 5 e Mockito. Tenho a seguinte User Story e Critérios de Aceitação em Given-When-Then para a plataforma Mandabaa:*  
> *User Story: [Colar US03 ou US07]*  
> *Critérios de Aceitação: [Colar os cenários do Item 3]*  
> *Classes existentes no projeto: [Colar a assinatura do Servico e das Entidades]*  
> *Solicitação:*  
> *1. Crie APENAS a classe de Teste Unitário automatizado cobrindo o cenário principal e cenários de erro/exceção.*  
> *2. Estruture o teste organizando e comentando explicitamente os blocos: // GIVEN (Dado / Arrange), // WHEN (Quando / Act) e // THEN (Então / Assert).*  
> *3. Crie Mocks/Stubs com Mockito para isolar os repositórios.*  
> *4. NÃO escreva a classe de código de negócio ainda (Abordagem TDD)."*

### 🟢 Prompt 2 — Fase GREEN (Código Mínimo)
> *"Agora estamos na fase GREEN do TDD no projeto Mandabaa. Com base no código de teste que geramos na fase anterior:*  
> *Código de Teste: [Colar teste da Fase RED]*  
> *Solicitação:*  
> *1. Escreva APENAS o código MÍNIMO de negócio na classe de serviço necessário para fazer todos esses testes passarem.*  
> *2. Não se preocupe com elegância ou arquitetura complexa agora. Escreva uma solução direta e simples.*  
> *3. Não altere os testes."*

### 🔵 Prompt 3 — Fase REFACTOR (Refatoração Explicada)
> *"Agora estamos na fase REFACTOR do TDD no projeto Mandabaa. Observe o código de negócio mínimo gerado na fase GREEN:*  
> *Código de Negócio Atual: [Colar código da Fase GREEN]*  
> *Solicitação:*  
> *1. Refatore este código aplicando boas práticas de Engenharia de Software (Princípio da Responsabilidade Única - SRP, nomes claros em português e eliminação de duplicação).*  
> *2. Garanta que a assinatura dos métodos e o comportamento externo permaneçam iguais para não quebrar a suíte de testes.*  
> *3. Liste e explique detalhadamente quais refatorações foram realizadas e por que elas melhoram o código."*

### 📋 Prompt 4 — Roteiro de Aceitação para o Cliente
> *"Atue como um Analista de Negócios e QA. Com base nos Critérios de Aceitação (Given-When-Then) da [US03 ou US07]:*  
> *Solicitação: Gere um Roteiro de Teste de Aceitação simplificado, em linguagem não técnica, para demonstrarmos ao cliente na interface do Mandabaa. Inclua passos amigáveis de navegação, dados a informar e o resultado visual esperado para cada cenário de sucesso e erro."*

---

## 8. CHECKLIST DE ACEITE — DEFINITION OF DONE (DoD)

Preencher uma tabela desta para **cada User Story testada** (US03 e US07):

| Critério de Qualidade | Status | Evidência |
| :--- | :---: | :--- |
| **Verificação (Código / TDD)** | [ ] Aprovado | Suíte de testes unitários em JUnit 5 + Mockito executando com 100% de sucesso (fases RED, GREEN e REFACTOR registradas). |
| **Validação (Negócio / Tela)** | [ ] Aprovado | Requisitos *Given-When-Then* demonstrados e homologados visualmente na interface web (sucesso e erro). |
| **Status da User Story** | **[ ] DONE** | **User Story aceita e movida para Concluído no Kanban da Sprint.** |

---

## 9. ARTEFATOS OBRIGATÓRIOS NO REPOSITÓRIO (SPRINT 5 & 5.1)

1. 📂 **Código do Teste de Unidade:** `src/test/java/com/mandabaa/...` (`ServicoResgateTest.java` e `ServicoCampanhaTest.java`).
2. 📂 **Código da Aplicação:** `src/main/java/com/mandabaa/...` e `src/main/resources/static/...`.
3. 📄 **Roteiro do Teste de Aceitação:** `docs/roteiros-aceitacao/` (um por User Story).
4. 📄 **Checklist de Aceite (Definition of Done):** `docs/checklists-aceite/` (um por User Story).
5. 📊 **Nova versão do Backlog (Sprint 5):** `docs/backlog-sprint5.md`.
6. 🤖 **Registros de IA (Supervisão Humana):** `docs/registros-ia/` (prompts, saídas e ajustes da equipe).
