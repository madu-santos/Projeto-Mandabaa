# 📋 PLANO DE AÇÃO E ROTEIRO DE IMPLEMENTAÇÃO — MVP MANDABAA (SPRINT 5 & 5.1)

**Projeto:** Mandabaa — Plataforma de Gestão e Incentivo à Reciclagem de PET  
**Disciplina:** Engenharia de Software | DCX - UFPB  
**Metodologia:** TDD (Test-Driven Development) & V&V (Verificação e Validação) com GenAI (*Human-in-the-Loop*)  
**Duração:** 5 dias úteis | **Capacidade:** 13 Story Points  

---

## 1. OBJETIVO DA ENTREGA E DIRETRIZES PEDAGÓGICAS

Desenvolver e homologar uma versão funcional demonstrável do **Mandabaa** em 5 dias, entregando um sistema arquiteturalmente sólido, com telas elegantes e visualmente organizadas, tecnicamente verificadas e validadas.

Seguindo as diretrizes da **Sprint 5.1**, o projeto aplica a engenharia de software com **TDD**:
1. **Verificação ("Construindo o produto corretamente?"):** A conformidade técnica e a integridade das regras são asseguradas por testes unitários automatizados em **JUnit 5 + Mockito**, executados no ciclo estrito:
   * 🔴 **RED:** Criação e execução dos testes antes de qualquer código de negócio, comprovando a falha inicial.
   * 🟢 **GREEN:** Implementação do código estritamente mínimo para fazer a suíte passar.
   * 🔵 **REFACTOR:** Melhoria da legibilidade, design e boas práticas (SRP), garantindo que os testes continuem verdes.
2. **Validação ("Construindo o produto certo?"):** A conformidade com o usuário e cliente é comprovada na interface gráfica através de testes de aceitação derivados dos critérios *Given-When-Then*.
3. **Conscientização do Custo do Defeito:** Detectar e sanar inconsistências na fase de requisitos e arquitetura (custo 1x) antes que se propaguem para a interface ou homologação (custo 20x a 100x).
4. **Supervisão Humana Obrigatória:** As saídas geradas por LLMs devem ser auditadas e corrigidas criticamente pela equipe antes de serem incorporadas ao projeto.

---

## 2. BACKLOG ATUALIZADO E ESCOPO DO MVP

Conforme a **Sprint 5**, o backlog do Mandabaa foi formalizado com a estimativa de esforço e complexidade (Planning Poker).

### 2.1. Backlog Geral Consolidado (35 Story Points)

| ID | User Story | Prioridade | Story Points | Situação no Projeto |
| :---: | :--- | :---: | :---: | :--- |
| **US01** | Registro e Histórico de Coletas | Importante | 2 | Arquitetura definida (Sprint 4) |
| **US02** | Busca de Pontos de Coleta Próximos | Essencial | 5 | Arquitetura definida (Sprint 4) |
| **US03** | **Resgate de Benefícios e Cashback** | **Essencial** | **8** | **SELECIONADA PARA O MVP (Sprint 5/5.1)** |
| **US04** | Gestão de Ofertas por Empresas Parceiras | Importante | 8 | Backlog futuro (Excede teto de 13 SP) |
| **US05** | Consulta de Saldo e Carteira | Essencial | 3 | Arquitetura definida (Sprint 4) |
| **US06** | Ranking de Entregas | Desejável | 3 | Backlog futuro |
| **US07** | **Notificações e Campanhas de Recompensas** | **Desejável** | **5** | **SELECIONADA PARA O MVP (Sprint 5/5.1)** |
| **US08** | Auditoria e Verificação de Pontos | Importante | 1 | Arquitetura definida (Sprint 4) |

### 2.2. Seleção da Sprint 5 & 5.1 (Capacidade: 13 Story Points)

Para cumprir rigorosamente o limite de 13 SP previsto para a Sprint:
$$\text{US03 (8 SP)} + \text{US07 (5 SP)} = \mathbf{13\text{ Story Points}}$$

### 2.3. Fronteiras do Escopo do MVP

* **O que ENTRA no MVP:**
  * **US03 (Fluxo Principal de Resgate):** Consulta de saldo de pontos; catálogo de ofertas ativas; validação de saldo; geração e exibição de voucher/código de desconto; solicitação de cashback com registro em estado inicial *"Pendente"*.
  * **US07 (Engajamento e Avisos):** Painel visual de campanhas promocionais ativas vs. expiradas; cards de avisos in-app informando sobre bônus de pontos temporários.
  * **Persistência e Carga Inicial:** Banco de dados PostgreSQL com carga prévia (*seed*) de 3 ofertas de parceiros e 2 campanhas vigentes para a demonstração.
* **O que FICA FORA deste MVP (Simplificações deliberadas):**
  * Integração com gateways bancários reais ou APIs de PIX (o cashback apenas gera um registro rastreável).
  * Envio de notificações externas (SMS, e-mail ou push nativo de celular).
  * Painel corporativo para empresas cadastrarem ofertas (US04).

---

## 3. ESPECIFICAÇÃO FORMAL DAS USER STORIES (GIVEN-WHEN-THEN)

Estes cenários constituem o insumo obrigatório para a criação dos testes automatizados na fase RED:

### 3.1. US03 — Resgate de Benefícios e Cashback (8 SP | Essencial)

* **Cenário 01 (Caminho Feliz - Resgate de Cupom):**
  * **Dado que** o usuário está autenticado com saldo de 1.000 pontos e seleciona uma oferta válida de 500 pontos com estoque disponível,
  * **Quando** ele solicita a confirmação do resgate,
  * **Então** o sistema debita 500 pontos do saldo, gera um voucher alfanumérico único e retorna status *"CONCLUIDO"*.
* **Cenário 02 (Regra de Exceção - Saldo Insuficiente):**
  * **Dado que** o usuário possui saldo de 200 pontos e tenta resgatar uma oferta de 500 pontos,
  * **Quando** ele solicita o resgate,
  * **Então** o sistema rejeita a operação, lança uma `SaldoInsuficienteException`, mantém o saldo inalterado em 200 pontos e exibe mensagem amigável informando a pontuação faltante.
* **Cenário 03 (Regra de Exceção - Oferta Esgotada ou Inativa):**
  * **Dado que** uma oferta cadastrada possui quantidade disponível igual a 0 ou está com status inativo,
  * **Quando** o usuário tentar confirmar o resgate,
  * **Então** o sistema impede a transação, lança `OfertaIndisponivelException` e não debita pontos da carteira.
* **Cenário 04 (Caminho Alternativo - Solicitação de Cashback):**
  * **Dado que** o usuário possui saldo suficiente e seleciona a opção de cashback informando uma chave PIX,
  * **Quando** ele submete a solicitação,
  * **Então** o sistema debita os pontos correspondentes e cria o registro transacional com a situação *"PENDENTE"*.

### 3.2. US07 — Notificações e Campanhas Temporárias (5 SP | Desejável)

* **Cenário 01 (Caminho Feliz - Listagem de Campanhas Ativas):**
  * **Dado que** existem campanhas com data de término posterior à data corrente cadastradas no sistema,
  * **Quando** o usuário acessa a seção de campanhas,
  * **Então** o sistema retorna exclusivamente as campanhas ativas exibindo o título, o multiplicador de bônus e a validade.
* **Cenário 02 (Regra de Exceção - Identificação de Campanha Expirada):**
  * **Dado que** uma campanha possui data de término anterior à data de consulta,
  * **Quando** o sistema processa os registros de campanhas,
  * **Então** ela deve ser classificada com status *"EXPIRADA"* e ter seu botão de participação desabilitado.

---

## 4. ARQUITETURA TÉCNICA E CONTRATOS DE API REST

A implementação respeita a arquitetura de **Monólito Modular em Camadas** definida na Sprint 4, com governança arquitetural centralizada:

```
[ Frontend: HTML5 / CSS3 Responsivo / JavaScript Vanilla (Pessoa 5) ]
                             │
                             ▼  (HTTP / JSON REST)
┌────────────────────────────────────────────────────────────────────────┐
│  Arquitetura Backend Mandabaa (Governança da Pessoa 1)                 │
│   ├── Configuração Global: CORS, Exception Handler (@ControllerAdvice) │
│   ├── Módulo Carteira: /api/carteira  (Consulta Saldo/Extrato)         │
│   ├── Módulo Ofertas:  /api/ofertas   (Catálogo de Benefícios)         │
│   ├── Módulo Resgate:  /api/resgates  (Lógica de Validação - US03)     │
│   └── Módulo Engajamento: /api/campanhas (Status de Campanhas - US07)  │
└────────────────────────────────────────────────────────────────────────┘
                             │
                             ▼  (Spring Data JPA / Hibernate)
┌────────────────────────────────────────────────────────────────────────┐
│  PostgreSQL em Docker (Gestão da Pessoa 3)                             │
│   ├── usuario  (id, nome, saldo_pontos)                                │
│   ├── oferta   (id, parceiro, titulo, custo_pontos, estoque)           │
│   ├── resgate  (id, usuario_id, oferta_id, codigo, status)             │
│   └── campanha (id, titulo, multiplicador, data_fim, ativa)            │
└────────────────────────────────────────────────────────────────────────┘
```

### Contratos de Endpoints (JSON)

1. **`GET /api/carteira/{usuarioId}`**
   * *Response (200 OK):* `{"usuarioId": 1, "saldoPontos": 1250, "equivalenteReais": 25.00}`
2. **`GET /api/ofertas`**
   * *Response (200 OK):* `[{"id": 10, "parceiro": "Supermercado Verde", "titulo": "Desconto de R$ 10", "custoPontos": 500, "estoque": 15}]`
3. **`POST /api/resgates`**
   * *Request:* `{"usuarioId": 1, "ofertaId": 10, "tipo": "CUPOM"}`
   * *Response Sucesso (201 Created):* `{"resgateId": 101, "status": "CONCLUIDO", "codigoVoucher": "MANDABAA-500-XYZ", "novoSaldo": 750}`
   * *Response Erro (400 Bad Request):* `{"status": "ERRO", "mensagem": "Saldo insuficiente para realizar este resgate."}`
4. **`GET /api/campanhas`**
   * *Response (200 OK):* `[{"id": 1, "titulo": "Semana do PET em Dobro", "multiplicador": 2, "status": "ATIVA", "dataFim": "2026-11-30"}]`

---

## 5. DIVISÃO GERAL DE PAPÉIS REBALANCEADA (PESSOA 1 A PESSOA 5)

* **Pessoa 1 — Arquitetura de Software & Orquestração do Sistema:**  
  Responsável pela estruturação do Monólito Modular em Java/Spring Boot, definição de pacotes e camadas, padronização de DTOs, governança de APIs, tratamento global de erros, segurança/CORS e garantia de que o sistema não degenere para uma *Big Ball of Mud*.
* **Pessoa 2 — Backend & Lógica de Negócio da US03:**  
  Responsável pelo desenvolvimento da lógica de resgate de pontos, validações de integridade de saldo/estoque, geração de vouchers e endpoints da US03, atuando no ciclo TDD em parceria com a Pessoa 4.
* **Pessoa 3 — Banco de Dados & Persistência:**  
  Responsável pela modelagem relacional, container Docker do PostgreSQL, scripts DDL/DML, entidades Spring Data JPA e carga inicial dos dados de demonstração.
* **Pessoa 4 — Engenharia de Requisitos, TDD & Qualidade (QA):**  
  Responsável pela condução da metodologia V&V (Sprint 5.1), criação dos testes unitários automatizados em JUnit 5/Mockito na fase RED, auditoria das fases GREEN/REFACTOR, elaboração dos roteiros de aceitação e checklist de *Definition of Done*.
* **Pessoa 5 — UI/UX, Layout das Telas & Frontend Web:**  
  Responsável por toda a identidade visual, design de interface e layout das telas em HTML5/CSS3 responsivo (paleta verde Mandabaa), componentes gráficos (carteira, catálogo, modal de voucher, avisos) e integração JavaScript com a API REST da Pessoa 1.

---

## 6. ROTEIRO OPERACIONAL DIA A DIA — INSTRUÇÕES DETALHADAS POR PESSOA

### 📅 DIA 1 — Setup Arquitetural, Banco no Docker, Design Base e Fase RED

* **Pessoa 1 (Arquitetura & Setup do Sistema):**
  1. Inicializar o projeto Spring Boot com as dependências essenciais (`Spring Web`, `Spring Data JPA`, `PostgreSQL Driver`, `Lombok`, `Validation`).
  2. Estruturar os pacotes do **Monólito Modular**:
     * `com.mandabaa.config` (CORS, Beans de Infraestrutura);
     * `com.mandabaa.common` (Tratamento de Exceções, DTOs compartilhados);
     * `com.mandabaa.rewards` (Módulo da US03);
     * `com.mandabaa.engagement` (Módulo da US07).
  3. Formalizar os contratos de DTOs de entrada e saída (Item 4) com a Pessoa 2, Pessoa 3 e Pessoa 5.
  4. Configurar a branch principal do Git e definir o padrão de commits da equipe.
* **Pessoa 2 (Backend US03):**
  1. Criar o esqueleto vazio da classe de serviço `ResgateService.java` no pacote `com.mandabaa.rewards` apenas com as assinaturas dos métodos públicos.
  2. Alinhar com a Pessoa 4 os cenários de teste de saldo insuficiente e oferta esgotada.
* **Pessoa 3 (Banco de Dados):**
  1. Criar o arquivo `docker-compose.yml` para rodar o PostgreSQL 15 localmente na porta `5432`.
  2. Subir o container (`docker-compose up -d`) e validar a conexão.
  3. Criar o script `schema.sql` com as tabelas `usuario`, `oferta`, `resgate` e `campanha`.
* **Pessoa 4 (QA / TDD):**
  1. Pegar os cenários Given-When-Then da US03 (Item 3 deste documento).
  2. Submeter o **Prompt 1 (Fase RED)** à GenAI para gerar a classe `ResgateServiceTest.java`.
  3. Inserir a classe em `src/test/java/` no projeto, rodar a suíte na IDE e **comprovar e registrar em captura de tela que os testes falharam (RED 🔴)**.
* **Pessoa 5 (UI/UX, Layout & Frontend):**
  1. Criar a pasta de assets frontend (`src/main/resources/static` ou pasta dedicada `frontend/`).
  2. Criar os arquivos `index.html` e `style.css`.
  3. Definir a identidade visual do Mandabaa no CSS: tipografia moderna, espaçamentos consistentes e paleta verde (`#2d6a4f`, `#40916c`, `#d8f3dc`, `#1b4332`).
  4. Construir o layout estrutural: cabeçalho com o logo da reciclagem, menu de navegação responsivo (*Carteira*, *Benefícios*, *Campanhas*) e rodapé institucional.

---

### 📅 DIA 2 — Fase GREEN, Persistência JPA, Governança de APIs e Telas Principais

* **Pessoa 1 (Arquitetura & Governança):**
  1. Configurar o tratamento global de exceções (`@RestControllerAdvice`) capturando `SaldoInsuficienteException` e retornando JSON padronizado com HTTP 400.
  2. Configurar a classe `CorsConfig.java` liberando requisições do frontend local (`localhost:8080` ou `Live Server`).
  3. Auditar a estrutura criada pela Pessoa 3 para garantir que as entidades JPA não quebram o encapsulamento dos módulos.
* **Pessoa 2 (Backend US03):**
  1. Receber o teste unitário vermelho da Pessoa 4.
  2. Aplicar o **Prompt 2 (Fase GREEN)** na GenAI e implementar o código estritamente mínimo no `ResgateService.java` para fazer o teste passar.
  3. Executar os testes unitários na IDE e **comprovar que todos ficaram verdes (GREEN 🟢)**.
* **Pessoa 3 (Banco de Dados):**
  1. Mapear as classes `@Entity` no Spring: `Usuario.java`, `Oferta.java`, `Resgate.java` e `Campanha.java`.
  2. Criar as interfaces `UsuarioRepository`, `OfertaRepository` e `ResgateRepository` estendendo `JpaRepository`.
  3. Criar o script `data.sql` populando o banco com dados de teste: 1 usuário com 1.000 pontos e 3 ofertas atraentes de parceiros.
* **Pessoa 4 (QA / TDD):**
  1. Auditar o resultado da fase GREEN da Pessoa 2, verificando que os mocks simularam o isolamento correto.
  2. Submeter o **Prompt 1 (Fase RED)** da US07 à GenAI para gerar o `CampanhaServiceTest.java`.
  3. Executar o teste na IDE e **registrar a falha vermelha (RED 🔴) da US07**.
* **Pessoa 5 (UI/UX, Layout & Frontend):**
  1. Desenvolver o layout visual do card da **Carteira**: destaque limpo para o saldo de pontos (`1.250 pts`) e conversão em reais (`R$ 25,00`).
  2. Desenvolver a grade (grid responsivo) do **Catálogo de Ofertas**: cards estilizados com imagem do parceiro, título do desconto, custo em pontos e botão de resgate com efeitos visuais (*hover/active*).
  3. Implementar classes de status visual no CSS para indicar ofertas ativas e ofertas desabilitadas.

---

### 📅 DIA 3 — Fase REFACTOR, Integração dos Controllers e Modais de Voucher

* **Pessoa 1 (Arquitetura & Orquestração):**
  1. Revisar o design dos controladores REST para garantir adesão aos verbos HTTP corretos (GET, POST) e códigos de status adequados (200, 201, 400).
  2. Configurar a comunicação síncrona entre o módulo de carteira e o módulo de ofertas.
  3. Validar se o Spring Boot inicia perfeitamente conectado ao PostgreSQL no Docker sem erros de injeção de dependência.
* **Pessoa 2 (Backend US03):**
  1. Aplicar o **Prompt 3 (Fase REFACTOR)** na GenAI sobre o `ResgateService.java`.
  2. Refatorar o código isolando validações de regras de negócio em métodos privados com nomes autoexplicativos (Single Responsibility Principle).
  3. Reexecutar os testes unitários e **garantir que permanecem 100% verdes (REFACTOR 🔵)**.
  4. Implementar a classe `ResgateController.java` com o endpoint `POST /api/resgates`.
* **Pessoa 3 (Banco de Dados):**
  1. Mapear a interface `CampanhaRepository.java`.
  2. Inserir no `data.sql` as campanhas da US07: uma ativa (com bônus em dobro) e uma expirada para teste visual.
  3. Testar a transacionalidade (`@Transactional`) garantindo que falhas de resgate realizam rollback sem debitar pontos incorretamente.
* **Pessoa 4 (QA / TDD):**
  1. Acompanhar a refatoração do código da Pessoa 2 e da Pessoa 2 (Campanhas), garantindo que não houve regressão.
  2. Implementar a fase GREEN e REFACTOR da US07 em conjunto com a Pessoa 2/Pessoa 1.
  3. Montar a planilha de rastreabilidade registrando as evidências das três fases do TDD.
* **Pessoa 5 (UI/UX, Layout & Frontend):**
  1. Criar o layout visual do **Modal de Confirmação de Resgate**: popup elegante com overlay escuro, exibindo o resumo dos pontos que serão debitados e botões *"Confirmar"* e *"Cancelar"*.
  2. Criar o layout do **Modal de Comprovante de Sucesso**: caixa em verde com ícone de sucesso e código alfanumérico em destaque (ex: `MANDABAA-2026-PET`) pronto para uso.
  3. Criar a seção de **Campanhas e Avisos** no layout, com cards estilizados e badges coloridos (*"Ativa"* em verde e *"Expirada"* em cinza).

---

### 📅 DIA 4 — Integração Frontend-Backend com Fetch API e Roteiros de Homologação

* **Pessoa 1 (Arquitetura & Integração Geral):**
  1. Conduzir os testes de integração entre o frontend e a API REST.
  2. Analisar o tráfego HTTP no console da aplicação (Network tab) garantindo payloads limpos e sem dados sensíveis expostos.
  3. Garantir que todas as rotas tratam exceções de forma resiliente, sem expor *stack traces* na resposta do cliente.
* **Pessoa 2 (Backend US03):**
  1. Criar o endpoint de consulta de carteira `GET /api/carteira/{usuarioId}` e de listagem `GET /api/ofertas`.
  2. Auxiliar a Pessoa 5 na validação das chamadas assíncronas do frontend.
* **Pessoa 3 (Banco de Dados):**
  1. Inspecionar o banco PostgreSQL durante os testes integrados: certificar-se de que quando um resgate ocorre na interface, a linha é inserida na tabela `resgate` e o saldo na tabela `usuario` é atualizado na mesma transação.
  2. Exportar o dump/script final limpo para inicialização automática na apresentação.
* **Pessoa 4 (QA / Roteiros de Aceitação):**
  1. Submeter o **Prompt 4 (Roteiro de Aceitação)** à GenAI para gerar o roteiro não-técnico da US03 e US07.
  2. Preparar o documento do roteiro com os passos exatos de clique, campos a preencher e resultados visuais esperados.
  3. Preparar a folha de *Checklist de Aceite — Definition of Done (DoD)*.
* **Pessoa 5 (UI/UX, Layout & Frontend):**
  1. No arquivo `app.js`, programar as funções assíncronas com `fetch()`:
     * `carregarCarteira()` $\rightarrow$ preenche o card de saldo na tela;
     * `carregarOfertas()` $\rightarrow$ renderiza os cards de benefícios dinamicamente;
     * `solicitarResgate(ofertaId)` $\rightarrow$ dispara o POST e abre o modal de voucher;
     * `carregarCampanhas()` $\rightarrow$ popula os cards de campanhas ativas e expiradas.
  2. Criar os alertas visuais (toasts flutuantes): verde para sucesso e vermelho com mensagem amigável quando o backend retornar erro (ex: saldo insuficiente).

---

### 📅 DIA 5 — Homologação com Cliente/PO, Validação de Aceitação e Entrega

* **Pessoa 1 (Arquitetura & Fechamento Técnico):**
  1. Realizar a revisão arquitetural final do sistema, garantindo a conformidade com o Monólito Modular.
  2. Executar o build completo do projeto (`mvn clean package`) comprovando integridade de empacotamento.
  3. Apoiar a demonstração técnica respondendo pelas decisões de arquitetura e isolamento de módulos.
* **Pessoa 2 (Backend US03):**
  1. Garantir que a suíte completa de testes unitários execute com 100% de sucesso na IDE antes da apresentação.
* **Pessoa 3 (Banco de Dados):**
  1. Reiniciar o container Docker e executar o script de carga inicial para que o sistema inicie a demonstração em estado limpo e confiável.
* **Pessoa 4 (QA / Condução da Homologação):**
  1. Conduzir a sessão de **Sprint Review / Homologação** dividida nas duas etapas da Sprint 5.1:
     * **A. Demonstração de Verificação:** Exibir a IDE e rodar a suíte JUnit 5 comprovando que 100% dos testes unitários estão verdes.
     * **B. Demonstração de Validação:** Guiar a navegação na interface gráfica seguindo o Roteiro de Aceitação (resgatar cupom com sucesso e forçar cenário de erro sem saldo).
  2. Coletar o aceite do cliente/PO e assinar formalmente o **Termo de Aceite (Definition of Done)**.
* **Pessoa 5 (UI/UX & Demonstração Visual):**
  1. Conduzir a apresentação visual da interface gráfica durante a homologação, demonstrando a elegância do layout, responsividade e usabilidade das telas.
  2. Organizar os arquivos de frontend e os prints das telas para compor a documentação de entrega.

---

## 7. ROTEIRO OFICIAL DE PROMPTS PARA O CICLO TDD (SPRINT 5.1)

A equipe utilizará a GenAI seguindo estes 4 modelos de prompt padronizados:

### 🔴 Prompt 1: Fase RED (Testes Unitários Automatizados)
> *"Atue como um especialista em QA e instrutor de Java com JUnit 5 e Mockito. Tenho a seguinte User Story e Critérios de Aceitação em Given-When-Then para a plataforma Mandabaa:*  
> *User Story: [Inserir US03 ou US07]*  
> *Critérios de Aceitação: [Inserir cenários do Item 3]*  
> *Solicitação:*  
> *1. Crie APENAS a classe de Teste Unitário automatizado cobrindo o cenário principal e cenários de erro/exceção.*  
> *2. Estruture o teste organizando e comentando explicitamente os blocos: // GIVEN (Dado / Arrange), // WHEN (Quando / Act) e // THEN (Então / Assert).*  
> *3. Caso haja dependências externas (como Repositórios JPA), crie Mocks/Stubs apropriados com Mockito.*  
> *4. NÃO escreva a classe de código de negócio ainda (Abordagem TDD estrita).*  
> *5. Utilize @ExtendWith(MockitoExtension.class) e asserções do org.junit.jupiter.api.Assertions."*

### 🟢 Prompt 2: Fase GREEN (Implementação Mínima de Negócio)
> *"Agora estamos na fase GREEN do TDD no projeto Mandabaa. Com base no código de teste que geramos na fase anterior:*  
> *Código de Teste: [Colar código de teste da fase RED]*  
> *Solicitação:*  
> *1. Escreva APENAS o código MÍNIMO de negócio na classe de serviço necessário para fazer todos esses testes passarem.*  
> *2. Não se preocupe com elegância ou arquitetura complexa agora; escreva uma solução direta e simples.*  
> *3. Não altere os testes para fazê-los passar."*

### 🔵 Prompt 3: Fase REFACTOR (Refatoração Assistida e Explicada)
> *"Agora estamos na fase REFACTOR do TDD no projeto Mandabaa. Observe o código de negócio mínimo gerado na fase GREEN e a suíte de testes existente:*  
> *Código de Negócio Atual: [Colar código da fase GREEN]*  
> *Solicitação:*  
> *1. Refatore este código aplicando boas práticas de Engenharia de Software (Single Responsibility Principle - SRP, nomes claros de variáveis e eliminação de código duplicado).*  
> *2. Garanta que a assinatura dos métodos e a funcionalidade externa permaneçam exatamente iguais para não quebrar a suíte de testes.*  
> *3. Liste e explique detalhadamente quais refatorações foram realizadas e por que elas melhoram o código."*

### 📋 Prompt 4: Roteiro de Aceitação para o Cliente (Sprint Review)
> *"Atue como um Analista de Negócios e QA. Com base na User Story e nos critérios de aceitação Given-When-Then da [US03 ou US07]:*  
> *Solicitação: Gere um Roteiro de Teste de Aceitação simplificado para ser executado e demonstrado visualmente ao cliente final/Product Owner na interface do Mandabaa. O roteiro deve conter: passos amigáveis de navegação na tela, dados a serem inseridos ou selecionados e o resultado visual esperado para o caminho feliz e para os cenários de erro."*

---

## 8. CHECKLIST DE ACEITE — DEFINITION OF DONE (DoD)

Ao final do Dia 5, este termo será preenchido para atestar a conclusão das histórias:

| Critério de Qualidade | Status | Evidência Comprobatória Exigida |
| :--- | :---: | :--- |
| **Verificação Técnica (TDD)** | [ ] Aprovado | 100% dos testes unitários em JUnit 5 e Mockito executando com sucesso (Verde) na IDE, cobrindo cenários felizes e exceções. |
| **Validação de Negócio (Aceitação)** | [ ] Aprovado | Cenários *Given-When-Then* demonstrados e validados visualmente na interface web sem falhas de execução. |
| **Rastreabilidade com GenAI** | [ ] Aprovado | Registro documentado contendo os prompts de RED, GREEN e REFACTOR e os pareceres de supervisão humana da equipe. |
| **Integridade de Persistência** | [ ] Aprovado | Transações registradas corretamente no banco de dados PostgreSQL executado via container Docker. |
| **Status Final da User Story** | **[ ] DONE** | **História formalmente aceita e movida para a coluna Concluído no Kanban.** |

---

## 9. PACOTE DE ARTEFATOS PARA ENTREGA FINAL

Deverão ser commitados e organizados no repositório do projeto:
1. 📂 **Testes de Unidade:** Classes `ResgateServiceTest.java` e `CampanhaServiceTest.java`.
2. 📂 **Código da Aplicação:** Classes de serviço refatoradas, controllers REST, entidades JPA e arquivos da interface (`index.html`, `style.css`, `app.js`).
3. 📄 **Roteiro de Teste de Aceitação:** Script não técnico executado na demonstração com o cliente.
4. 📄 **Termo de Aceite Preenchido:** Checklist de *Definition of Done* validado.
5. 📄 **Histórico de Prompts e Respostas:** Registros organizados das fases RED, GREEN e REFACTOR com os ajustes humanos.
6. 📊 **Tabela do Backlog Atualizada:** Tabela de Prioridades e Story Points da Sprint 5.
