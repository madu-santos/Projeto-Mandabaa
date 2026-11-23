# Registro de Uso de IA e Revisão Humana — Pessoa 5 (UI/UX e Frontend Web)

| Campo | Registro |
| :--- | :--- |
| **Responsável** | Pessoa 5 — UI/UX, Aparência Visual e Frontend Web |
| **Data** | 23/11/2026 |
| **Etapa** | Sprint 5 / 5.1 — Implementação do Design Visual Fiel ao Mockup Verde, Modais e Registro de Coletas |
| **IA utilizada** | Claude e Gemini (Antigravity) |
| **Entradas fornecidas à IA** | `plano_acao_mvp_mandabaa.md` (itens 2.3, 3.1 a 3.3 e 5), layout do mockup verde original apresentado na disciplina, contratos REST dos controladores, e arquivos base `index.html`, `estilo.css` e `script.js`. |
| **Revisão humana** | ✅ Revisado. O layout foi inspecionado no navegador com resolução desktop e mobile, testado com a aplicação conectada ao PostgreSQL e validado contra todos os fluxos da interface. |

---

## 1. O que foi gerado com apoio de IA

1. **Reestruturação e Refinamento do HTML (`index.html`):**
   - Header moderno com marca e slogan oficial (*"Recicle. Acumule. Resgate."*) e navegação em abas (*pill design*).
   - Card de Saldo e Carteira com design de destaque em gradiente verde, chip do usuário (*Alex Silva*), pill da regra de cálculo e botão de ação para Benefícios.
   - Inclusão do botão e modal interativo **"+ Registrar Entrega"** para resolver a lacuna da US01 (CA02 e CA03).
   - Grade responsiva de benefícios com selos de categoria (*Cupom* vs. *Cashback*) e estoque visual.
   - Modais interativos com backdrop blur para confirmação de resgate, entrada de chave PIX dinâmica e comprovante com código estilizado.

2. **Folha de Estilos Completa (`estilo.css`):**
   - Paleta de cores inspirada na identidade visual sustentável do Mandabaa (tons de verde floresta, verde pastel, branco e cinzas suaves).
   - Animações suaves de transição entre abas (`fadeIn`) e avisos flutuantes (`slideIn`).
   - Adaptação responsiva para telas móveis (breakpoints de até 680px).
   - Estados vazios (*empty states*) amigáveis para coletas e resgates.

3. **Lógica de Conexão Frontend (`script.js`):**
   - Detecção dinâmica de URL base da API (`window.location.origin/api`), permitindo que a aplicação rode em qualquer porta (como 8080 ou 8088).
   - Integração completa com as 6 rotas REST do backend:
     - `GET /api/carteira/1`
     - `GET /api/coletas/usuario/1`
     - `POST /api/coletas` (nova entrega validada)
     - `GET /api/ofertas`
     - `POST /api/resgates` (cupom e cashback com PIX)
     - `GET /api/resgates/usuario/1`
   - Validações preventivas de entrada no navegador (evitando erros de peso negativo e chave PIX vazia).

---

## 2. Revisão Humana e Ajustes Aplicados (Human-in-the-Loop)

| # | Ajuste Realizado | Justificativa |
| :-: | :--- | :--- |
| 1 | **Criação do Modal de Nova Coleta** | A versão original do frontend não permitia registrar entregas pela tela (apenas exibia o histórico). Para demonstrar o CA02 e CA03 da US01 sem depender de curl/PowerShell na Sprint Review, foi implementado o modal de cadastro. |
| 2 | **Endereço dinâmico da API** | O script apontava de forma fixa para `localhost:8080`. Com portas ocupadas no ambiente de execução, o script foi ajustado para pegar dinamicamente a porta em que a página foi carregada. |
| 3 | **Tratamento de fallback gracioso** | Caso o backend esteja inicializando, a tela exibe valores coerentes de exemplo sem quebrar a execução do JavaScript. |
| 4 | **Validação de chave PIX no cliente** | No resgate de cashback, o modal só permite submissão se a chave PIX estiver preenchida, exibindo feedback imediato na tela. |
| 5 | **Mapeamento de situações em português** | Rótulos claros para `DISPONIVEL` (✅ Disponível), `PAGAMENTO_PENDENTE` (⏳ Pagamento Pendente) e `PAGAMENTO_CONFIRMADO` (✅ Pago). |

---

## 3. Verificação Visual e Validação

- Testado no navegador em `http://localhost:8088/index.html`.
- Todas as 3 abas navegáveis e integradas ao banco PostgreSQL via Docker.
- Fluxo ponta a ponta validado:
  - Visualização de saldo inicial (1.250 pts = R$ 25,00).
  - Histórico de coletas com 4,3 kg total.
  - Registro de nova coleta de 2 kg atualizando o saldo para 1.100 pts.
  - Resgate de cupom e cashback com comprovante e novo saldo refletido.
