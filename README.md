# 📋 PLANO DE AÇÃO E ROTEIRO DE IMPLEMENTAÇÃO — MVP MANDABAA (SPRINT 5 & 5.1)

**Projeto:** Mandabaa — Plataforma de Gestão e Incentivo à Reciclagem de PET  
**Disciplina:** Engenharia de Software | DCX - UFPB  
**Metodologia:** TDD (Desenvolvimento Orientado a Testes) e V&V (Verificação e Validação) com IA Generativa e revisão humana  
**Prazo:** 5 dias | **Capacidade da Sprint:** 13 Story Points  

---

## 1. OBJETIVO DA ENTREGA E REGRAS DA PROFESSORA

Entregar uma primeira versão funcional e visualmente agradável do **Mandabaa** em 5 dias, unindo a **Sprint 5** (tabela atualizada do Backlog com Prioridade e Story Points, seleção de 13 SP priorizando as histórias mais importantes e implementação parcial) com o roteiro obrigatório da **Sprint 5.1** (Verificação e Validação com TDD assistido por IA):

1. **Verificação (*"Estamos construindo o produto corretamente?"*):** garantida por testes unitários automatizados em **Java com JUnit 5 e Mockito**, seguindo o ciclo estrito:
   * 🔴 **RED:** criar o teste a partir dos critérios *Dado que... Quando... Então...* e comprovar que ele **falha** antes de escrever a regra de negócio.
   * 🟢 **GREEN:** escrever apenas o código mínimo necessário na classe de serviço para fazer os testes passarem.
   * 🔵 **REFACTOR:** melhorar nomes, clareza e divisão de responsabilidades mantendo 100% dos testes verdes.
2. **Validação (*"Estamos construindo o produto certo?"*):** demonstrar na tela, com um **Roteiro de Teste de Aceitação** em linguagem simples, o caminho feliz e os cenários de erro, encerrando com o **Checklist de Aceite (Definition of Done)**.
3. **Revisão Humana Obrigatória:** nada gerado por IA entra no projeto sem revisão, execução na IDE e registro de prompts/respostas pela equipe.

---

## 2. BACKLOG ATUALIZADO E ESCOPO DO MVP (SPRINT 5)

A tabela oficial também está salva no repositório em `docs/backlog-sprint5.md`.

### 2.1. Backlog Consolidado (Total: 35 Story Points)

| ID | User Story | Prioridade (Cliente) | Story Points | Situação no Projeto |
| :---: | :--- | :---: | :---: | :--- |
| **US01** | **Registro e Histórico de Coletas** | **Importante** | **2** | **SELECIONADA — Sprint 5 / 5.1** |
| **US02** | Busca de Pontos de Coleta Próximos | Essencial | 5 | Próxima Sprint (mapa/geolocalização) |
| **US03** | **Resgate de Benefícios e Cashback** | **Essencial** | **8** | **SELECIONADA — Sprint 5 / 5.1** |
| **US04** | Cadastro e Gestão de Ofertas por Empresas Parceiras | Importante | 8 | Backlog futuro |
| **US05** | **Consulta de Saldo e Carteira** | **Essencial** | **3** | **SELECIONADA — Sprint 5 / 5.1** |
| **US06** | Ranking de Entregas | Desejável | 3 | Backlog futuro |
| **US07** | Notificações e Campanhas Temporárias de Recompensas | Desejável | 5 | Backlog futuro |
| **US08** | Consulta da Verificação dos Pontos de Coleta | Importante | 1 | Próxima Sprint (acompanha US02) |

### 2.2. Seleção da Sprint (Capacidade: 13 Story Points — 100% Prioritária!)
$$\text{US03 (8 SP — Essencial)} + \text{US05 (3 SP — Essencial)} + \text{US01 (2 SP — Importante)} = \mathbf{13\text{ Story Points}}$$

**Por que essa seleção é mais fácil e mais forte?**
* Cumpre à risca a orientação da professora de implementar **primeiro as funcionalidades prioritárias** (sem puxar história *Desejável* na frente de *Essencial*).
* Entrega exatamente o **fluxo das telas do mockup verde** que o grupo já apresentou: **Histórico de Coletas (US01) $\rightarrow$ Minha Carteira (US05) $\rightarrow$ Benefícios e Resgates (US03)**.
* Evita módulos extras sem relação direta (mapa/GPS, notificações push ou cadastro de empresas) e facilita os testes e a demonstração.

### 2.3. O que entra e o que fica de fora no MVP

* **Entra no MVP:**
  * **US01 (Coletas):** listar entregas validadas de PET (ponto, data, peso em kg, pontos e bônus), somar o total de PET entregue e permitir registrar uma nova entrega validada atualizando saldo e histórico.
  * **US05 (Carteira):** exibir saldo de pontos, equivalente oficial em reais (100 pontos = R$ 2,00), regra transparente de cálculo (50 pontos por kg), atalho para benefícios e aviso quando o usuário estiver sem saldo.
  * **US03 (Benefícios e Cashback):** listar ofertas com parceiro, descrição, condições e custo; validar saldo (avisando **quantos pontos faltam** se não tiver); confirmar resgate com resumo; atualizar saldo e estoque; gerar comprovante (`DISPONIVEL`/`UTILIZADO`/`EXPIRADO` para cupom e `PAGAMENTO_PENDENTE`/`PAGAMENTO_CONFIRMADO` para cashback); consultar histórico de resgates.
  * **Carga inicial no banco:** usuário de demonstração, entregas iniciais e ofertas já cadastrados via `data.sql`.
* **Fica de fora neste recorte (simplificações planejadas):**
  * Transferência bancária real / integração PIX (cashback nasce como `PAGAMENTO_PENDENTE`).
  * Mapa interativo com GPS (US02/US08), cadastro de ofertas por empresas (US04), ranking (US06) e notificações/campanhas (US07).

---

## 3. CRITÉRIOS DE ACEITAÇÃO EM GIVEN-WHEN-THEN (FIÉIS AO BACKLOG v3)

Estes cenários alimentam diretamente a **Fase RED** do TDD:

### 3.1. US01 — Registro e Histórico de Coletas (2 SP | Importante)

* **CA01 (Caminho Feliz — Consultar Histórico e Total de PET):**
  * **Dado que** o usuário possui duas entregas validadas (2,5 kg com 125 pontos + 25 bônus; e 1,8 kg com 90 pontos + 0 bônus),
  * **Quando** ele consulta o histórico de coletas,
  * **Então** o sistema retorna as duas entregas com data, peso, pontos e bônus e calcula o total entregue de **4,3 kg**.
* **CA02 (Caminho Feliz — Registrar Nova Entrega Validada):**
  * **Dado que** o usuário possui saldo de 1.000 pontos e entrega 2,0 kg de PET (50 pontos/kg = 100 pontos) sem bônus,
  * **Quando** a entrega validada é registrada,
  * **Então** a coleta é salva no histórico e o saldo do usuário passa para **1.100 pontos**.
* **CA03 (Regra de Exceção — Peso Inválido):**
  * **Dado que** é informada uma entrega com peso menor ou igual a zero,
  * **Quando** o sistema tenta registrar a coleta,
  * **Então** lança exceção de argumento inválido (`IllegalArgumentException`) e não altera o saldo.

### 3.2. US05 — Consulta de Saldo e Carteira (3 SP | Essencial)

* **CA01 (Caminho Feliz — Carteira com Saldo):**
  * **Dado que** o usuário possui 1.250 pontos acumulados e a regra oficial é 100 pontos = R$ 2,00,
  * **Quando** ele consulta a carteira,
  * **Então** o sistema retorna `saldoPontos = 1250`, `equivalenteReais = 25.00`, `semSaldo = false` e o texto claro da regra de cálculo.
* **CA02 (Caminho Alternativo — Carteira Sem Saldo):**
  * **Dado que** o usuário possui 0 pontos,
  * **Quando** ele consulta a carteira,
  * **Então** o sistema retorna `saldoPontos = 0`, `equivalenteReais = 0.00`, `semSaldo = true` e uma mensagem convidando o usuário a entregar PET em um ponto de coleta.
* **CA03 (Regra de Exceção — Usuário Inexistente):**
  * **Dado que** é consultado um ID de usuário que não existe no banco,
  * **Quando** o serviço busca a carteira,
  * **Então** lança `RecursoNaoEncontradoExcecao`.

### 3.3. US03 — Resgate de Benefícios e Cashback (8 SP | Essencial)

* **CA01 (Caminho Feliz — Resgate de Cupom):**
  * **Dado que** o usuário possui 1.000 pontos e escolhe um cupom ativo com estoque 5 que custa 500 pontos,
  * **Quando** ele confirma o resgate,
  * **Então** o saldo cai para 500 pontos, o estoque cai para 4, o resgate fica salvo com situação `DISPONIVEL` e um código de comprovante único é devolvido.
* **CA02 (Regra de Exceção — Saldo Insuficiente Informando Pontos Faltantes):**
  * **Dado que** o usuário possui 200 pontos e tenta resgatar uma oferta de 500 pontos,
  * **Quando** ele solicita o resgate,
  * **Então** o sistema lança `SaldoInsuficienteExcecao` informando que **faltam 300 pontos**, não desconta nada do saldo e não altera o estoque.
* **CA03 (Regra de Exceção — Oferta Esgotada, Inativa ou Vencida):**
  * **Dado que** uma oferta está com estoque 0, inativa ou com validade expirada,
  * **Quando** o usuário tenta resgatá-la,
  * **Então** o sistema lança `OfertaIndisponivelExcecao` e mantém o saldo do usuário inalterado.
* **CA04 (Caminho Feliz — Solicitação de Cashback):**
  * **Dado que** o usuário possui saldo suficiente e escolhe uma oferta de cashback informando a chave PIX,
  * **Quando** ele confirma o resgate,
  * **Então** os pontos são descontados e o resgate é salvo com situação `PAGAMENTO_PENDENTE`.

---

## 4. ARQUITETURA EM PORTUGUÊS E CONTRATOS DE API REST

Monólito Modular em Camadas (Java 21 + Spring Boot 3.3.5 + PostgreSQL + HTML/CSS/JS), 100% em português:

```text
Projeto-Mandabaa/
├── docker-compose.yml              (PostgreSQL 15 no Docker)
├── pom.xml                         (Java 21, Spring Boot 3.3.5, JPA, JUnit 5, Mockito)
├── docs/
│   └── backlog-sprint5.md          (Tabela oficial do Backlog — 13 SP)
└── src/
    ├── main/
    │   ├── java/com/mandabaa/
    │   │   ├── AplicacaoMandabaa.java
    │   │   ├── configuracao/       -> ConfiguracaoCors
    │   │   ├── comum/excecao/      -> SaldoInsuficienteExcecao, OfertaIndisponivelExcecao,
    │   │   │                          RecursoNaoEncontradoExcecao, ManipuladorGlobalDeExcecoes
    │   │   ├── usuarios/           -> Usuario, RepositorioUsuario
    │   │   ├── coletas/            -> Coleta, RepositorioColeta, ColetaDTO,
    │   │   │                          HistoricoColetasDTO, ServicoColeta (US01)
    │   │   ├── parceiros/          -> Oferta, RepositorioOferta
    │   │   └── carteira/           -> Resgate, RepositorioResgate, CarteiraDTO,
    │   │                              SolicitacaoResgateDTO, RespostaResgateDTO,
    │   │                              ServicoCarteira (US05), ServicoResgate (US03)
    │   └── resources/
    │       ├── application.properties
    │       └── static/             -> index.html, estilo.css, script.js (base da Pessoa 5)
    └── test/java/com/mandabaa/     -> ServicoResgateTest, ServicoCarteiraTest,
                                       ServicoColetaTest (Pessoa 4)
```

### Contratos de API REST (JSON)

| História | Funcionalidade | Rota |
| :---: | :--- | :--- |
| **US05** | Consultar carteira e saldo | `GET /api/carteira/{usuarioId}` |
| **US01** | Consultar histórico de coletas | `GET /api/coletas/usuario/{usuarioId}` |
| **US01** | Registrar nova entrega validada | `POST /api/coletas` |
| **US03** | Listar ofertas disponíveis | `GET /api/ofertas` |
| **US03** | Solicitar resgate (cupom ou cashback) | `POST /api/resgates` |
| **US03** | Consultar benefícios já resgatados | `GET /api/resgates/usuario/{usuarioId}` |

---

## 5. DIVISÃO EQUILIBRADA ENTRE AS 5 PESSOAS

* **Pessoa 1 — Arquitetura de Software, Controladores REST e Integração:**  
  Mantém a estrutura do Monólito Modular em Spring Boot, configura CORS e `ManipuladorGlobalDeExcecoes`, padroniza DTOs, implementa os **Controladores REST** (`ControladorCarteira`, `ControladorColeta`, `ControladorOferta`, `ControladorResgate`) e garante a integração limpa entre frontend, backend e banco.
* **Pessoa 2 — Regra de Negócio da US03 (8 SP — Backend em TDD):**  
  Implementa e refatora a lógica completa de `ServicoResgate` (validações de saldo com cálculo de pontos faltantes, estoque/validade, geração de comprovante e fluxo de cashback pendente) nas fases **GREEN** e **REFACTOR**.
* **Pessoa 3 — Banco de Dados + Regras de Negócio da US01 e US05 (2 + 3 = 5 SP):**  
  Sobe o PostgreSQL no Docker, revisa entidades/repositórios, cria o `data.sql` de demonstração e implementa/refatora `ServicoColeta` (US01) e `ServicoCarteira` (US05) nas fases **GREEN** e **REFACTOR**.
* **Pessoa 4 — Requisitos, TDD (Fase RED) e Qualidade (QA):**  
  Conduz o roteiro da **Sprint 5.1**: gera e roda os testes unitários **RED** (`ServicoResgateTest`, `ServicoCarteiraTest`, `ServicoColetaTest`) com JUnit 5 + Mockito, audita **GREEN** e **REFACTOR**, gera os **Roteiros de Teste de Aceitação** e preenche os **Checklists de Aceite (Definition of Done)**.
* **Pessoa 5 — UI/UX, Aparência/Layout das Telas e Frontend Web:**  
  Personaliza toda a aparência visual fiel ao mockup verde do Mandabaa (`index.html`, `estilo.css`, `script.js`): **Minha Carteira + Histórico de Coletas** (US05 + US01), **Benefícios** com modais de confirmação/comprovante e **Meus Resgates** (US03), além de conduzir a apresentação visual na homologação.

---

## 6. ROTEIRO DETALHADO DIA A DIA (INSTRUÇÕES PASSO A PASSO)

### 📅 DIA 1 — Arquitetura Base, Banco no Docker, Visual Inicial e Fase RED (US03)

* **Pessoa 1 (Arquitetura):**
  1. Estruturar o projeto Spring Boot com todos os pacotes e esqueletos em português (`configuracao`, `comum.excecao`, `usuarios`, `coletas`, `parceiros`, `carteira`). *(Já pronto e compilando!)*
  2. Alinhar os contratos das 6 rotas REST (Item 4) com a equipe.
  3. Orientar cada integrante a trabalhar em sua branch no Git para evitar conflitos.
* **Pessoa 2 (US03 — 8 SP):**
  1. Clonar o repositório, abrir no IntelliJ e revisar `ServicoResgate`, `SolicitacaoResgateDTO` e `RespostaResgateDTO`.
  2. Alinhar com a Pessoa 4 os 4 cenários de teste da US03 (incluindo o texto exato de quantos pontos faltam no erro de saldo insuficiente).
* **Pessoa 3 (Banco + US01/US05):**
  1. Rodar `docker-compose up -d` para subir o PostgreSQL local e conferir a conexão.
  2. Revisar as entidades `Usuario`, `Coleta`, `Oferta` e `Resgate`.
  3. Preparar o rascunho de `src/main/resources/data.sql` com: 1 usuário (Alex, 1.250 pontos), 2 coletas (2,5 kg e 1,8 kg) e 3 ofertas (cashback R$ 5, desconto 10% e oferta esgotada).
* **Pessoa 4 (TDD / QA):**
  1. Usar o **Prompt 1 (Fase RED)** com os critérios da **US03** (Item 3.3) para gerar `ServicoResgateTest.java`.
  2. Revisar o teste (checar `@ExtendWith(MockitoExtension.class)`, mocks e blocos `GIVEN / WHEN / THEN`).
  3. Executar o teste contra o esqueleto atual de `ServicoResgate` e **salvar print comprovando a falha vermelha (RED 🔴)**.
* **Pessoa 5 (Layout e Frontend):**
  1. Abrir `src/main/resources/static/index.html` no navegador para ver o esqueleto inicial.
  2. Comparar com o mockup verde do grupo e planejar melhorias de visual (cards, ícones, cores, responsividade e visualização de saldo/coletas/benefícios).

---

### 📅 DIA 2 — Fase GREEN (US03), Carga do Banco, Fase RED (US05 e US01) e Telas Principais

* **Pessoa 1 (Arquitetura e Controladores — *já entregue!*):**
  1. `ControladorCarteira` (`GET /api/carteira/{usuarioId}`) e `ControladorOferta` (`GET /api/ofertas`) já criados e compilando no repositório.
  2. Apoiar a Pessoa 3 para garantir que a aplicação suba conectada ao PostgreSQL no Docker sem erros.
* **Pessoa 2 (US03 — Fase GREEN):**
  1. Receber `ServicoResgateTest.java` da Pessoa 4.
  2. Rodar o **Prompt 2 (Fase GREEN)** para implementar o código mínimo em `ServicoResgate.java`.
  3. Executar os testes unitários até ficarem **100% verdes (GREEN 🟢)** e salvar print + prompt.
* **Pessoa 3 (Banco + US01/US05):**
  1. Finalizar `src/main/resources/data.sql` e ativar `spring.sql.init.mode=always` no `application.properties`.
  2. Subir o Spring Boot e confirmar no banco que os dados de demonstração foram carregados.
  3. Alinhar com a Pessoa 4 as assinaturas de `ServicoCarteira` e `ServicoColeta`.
* **Pessoa 4 (TDD / QA):**
  1. Auditar se a Pessoa 2 passou nos testes da US03 **sem alterar o arquivo de teste**.
  2. Usar o **Prompt 1 (Fase RED)** para gerar `ServicoCarteiraTest.java` (US05) e `ServicoColetaTest.java` (US01).
  3. Rodar ambos na IDE contra os esqueletos e **registrar o print das falhas vermelhas (RED 🔴)**.
* **Pessoa 5 (Layout e Frontend):**
  1. Personalizar o visual da aba **Minha Carteira** (card de saldo, valor em R$, texto da regra de cálculo, alerta de saldo zero) e do **Histórico de Coletas** logo abaixo (igual ao mockup!).
  2. Refinar os cards da aba **Benefícios** (mostrando parceiro, descrição, condições, custo e selo de Cupom vs. Cashback).

---

### 📅 DIA 3 — Fase REFACTOR (US03), Fase GREEN (US05 e US01) e Controladores Finais

* **Pessoa 1 (Arquitetura e Controladores — *já entregue!*):**
  1. `ControladorResgate` (`POST /api/resgates` e `GET /api/resgates/usuario/{usuarioId}`) e `ControladorColeta` (`GET /api/coletas/usuario/{usuarioId}` e `POST /api/coletas`) já criados e compilando no repositório.
  2. Assim que as Pessoas 2 e 3 concluírem a fase GREEN, testar todas as 6 rotas retornando JSON real do banco.
* **Pessoa 2 (US03 — Fase REFACTOR):**
  1. Rodar o **Prompt 3 (Fase REFACTOR)** sobre `ServicoResgate.java` para extrair validações em métodos privados claros (SRP).
  2. Reexecutar `ServicoResgateTest` comprovando que continua **verde (REFACTOR 🔵)** e registrar a explicação das melhorias.
* **Pessoa 3 (US05 e US01 — Fase GREEN):**
  1. Receber `ServicoCarteiraTest` e `ServicoColetaTest` da Pessoa 4.
  2. Rodar o **Prompt 2 (Fase GREEN)** para implementar o código mínimo em `ServicoCarteira.java` e `ServicoColeta.java`.
  3. Executar os testes e comprovar o **GREEN 🟢**.
* **Pessoa 4 (TDD / QA):**
  1. Auditar a refatoração da US03 e o GREEN de US05 e US01.
  2. Guardar os prompts, respostas da IA e prints em `docs/registros-ia/`.
* **Pessoa 5 (Layout e Frontend):**
  1. Refinar os modais de **Confirmação de Resgate** (com campo de chave PIX só para cashback) e de **Comprovante**.
  2. Refinar a aba **Meus Resgates** exibindo código e selos coloridos (`DISPONIVEL`, `PAGAMENTO_PENDENTE`, `EXPIRADO`).

---

### 📅 DIA 4 — Fase REFACTOR (US05/US01), Integração Completa e Roteiros de Aceitação

* **Pessoa 1 (Integração Geral):**
  1. Testar junto com a Pessoa 5 todas as chamadas do `script.js` contra o backend real na porta 8080.
  2. Validar o fluxo completo de erro (ex.: tentar resgatar sem pontos e ver na tela a mensagem exata *"Saldo insuficiente. Faltam X pontos"*).
* **Pessoa 2 (Revisão US03):**
  1. Testar resgate de cupom, resgate de cashback, saldo insuficiente e oferta esgotada ponta a ponta, conferindo `@Transactional`.
* **Pessoa 3 (US05/US01 — Fase REFACTOR + Banco):**
  1. Rodar o **Prompt 3 (Fase REFACTOR)** em `ServicoCarteira.java` e `ServicoColeta.java`, reexecutar os testes e confirmar **REFACTOR 🔵**.
  2. Conferir no PostgreSQL se saldo, estoque, coletas e resgates ficam salvos de forma consistente após testar na tela.
* **Pessoa 4 (Roteiros de Aceitação e Checklists):**
  1. Rodar o **Prompt 4** para gerar os **Roteiros de Teste de Aceitação** (em linguagem simples) da US03, US05 e US01 e salvar em `docs/roteiros-aceitacao/`.
  2. Preparar os **Checklists de Aceite (Definition of Done)** em `docs/checklists-aceite/`.
* **Pessoa 5 (Acabamento Visual):**
  1. Verificar se todas as telas consomem dados reais do backend (sem depender dos exemplos de fallback).
  2. Polir responsividade, alinhamento e mensagens amigáveis.

---

### 📅 DIA 5 — Homologação (Sprint Review), Validação e Entrega Final

* **Pessoa 1 (Build e Revisão Final):**
  1. Rodar `mvn clean test` e `mvn clean package` confirmando 100% de compilação e testes verdes.
  2. Conferir se todos os arquivos e documentos estão no GitHub na branch `main`.
* **Pessoa 2 e Pessoa 3 (Banco Limpo e Apoio):**
  1. Reiniciar o container Docker com o `data.sql` limpo pronto para a demonstração.
  2. Apoiar qualquer ajuste rápido durante o ensaio.
* **Pessoa 4 (Condução da Homologação — Sprint 5.1):**
  1. Conduzir a demonstração nas duas fases da Sprint 5.1:
     * **A. Verificação Técnica:** rodar `ServicoResgateTest`, `ServicoCarteiraTest` e `ServicoColetaTest` na IDE mostrando 100% verdes.
     * **B. Validação com Cliente/PO:** executar os Roteiros de Aceitação na tela (caminho feliz e exceções).
  2. Preencher os **Checklists de Aceite (Definition of Done)** marcando US01, US05 e US03 como `DONE`.
* **Pessoa 5 (Apresentação Visual):**
  1. Navegar pela interface mostrando o fluxo real completo (coletas $\rightarrow$ saldo $\rightarrow$ resgate $\rightarrow$ comprovante/histórico) e tirar prints para o Notion.

---

## 7. PROMPTS OFICIAIS DO CICLO TDD (SPRINT 5.1)

### 🔴 Prompt 1 — Fase RED (Testes Unitários)
> *"Atue como um especialista em QA e instrutor de Java com JUnit 5 e Mockito. Tenho a seguinte User Story e Critérios de Aceitação em Given-When-Then para a plataforma Mandabaa:*  
> *User Story: [Colar US01, US05 ou US03]*  
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
> *"Atue como um Analista de Negócios e QA. Com base nos Critérios de Aceitação (Given-When-Then) da [US01, US05 ou US03]:*  
> *Solicitação: Gere um Roteiro de Teste de Aceitação simplificado, em linguagem não técnica, para demonstrarmos ao cliente na interface do Mandabaa. Inclua passos amigáveis de navegação, dados a informar e o resultado visual esperado para cada cenário de sucesso e erro."*

---

## 8. CHECKLIST DE ACEITE — DEFINITION OF DONE (DoD)

Preencher uma tabela desta para **cada User Story testada** (US01, US05 e US03):

| Critério de Qualidade | Status | Evidência |
| :--- | :---: | :--- |
| **Verificação (Código / TDD)** | [ ] Aprovado | Suíte de testes unitários em JUnit 5 + Mockito executando com 100% de sucesso (fases RED, GREEN e REFACTOR registradas). |
| **Validação (Negócio / Tela)** | [ ] Aprovado | Requisitos *Given-When-Then* demonstrados e homologados visualmente na interface web (sucesso e erro). |
| **Status da User Story** | **[ ] DONE** | **User Story aceita e movida para Concluído no Kanban da Sprint.** |

---

## 9. ARTEFATOS OBRIGATÓRIOS NO REPOSITÓRIO (SPRINT 5 & 5.1)

1. 📂 **Código do Teste de Unidade:** `src/test/java/com/mandabaa/...` (`ServicoResgateTest`, `ServicoCarteiraTest`, `ServicoColetaTest`).
2. 📂 **Código da Aplicação:** `src/main/java/com/mandabaa/...` e `src/main/resources/static/...`.
3. 📄 **Roteiro do Teste de Aceitação:** `docs/roteiros-aceitacao/` (um por User Story).
4. 📄 **Checklist de Aceite (Definition of Done):** `docs/checklists-aceite/` (um por User Story).
5. 📊 **Nova versão do Backlog (Sprint 5):** `docs/backlog-sprint5.md`.
6. 🤖 **Registros de IA (Supervisão Humana):** `docs/registros-ia/` (prompts, saídas e ajustes da equipe).
