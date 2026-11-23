-- ============================================================================
-- RESTAURAR DEMO — devolve o banco ao estado inicial do data.sql
-- Usado pela Pessoa 4 (QA) para que CADA roteiro de aceitação possa ser
-- executado de forma independente, em qualquer ordem.
--
-- Como executar (PowerShell, na raiz do projeto, com o container no ar):
--   Get-Content docs\roteiros-aceitacao\restaurar-demo.sql | docker exec -i postgres-mandabaa psql -U postgres -d mandabaa_db
--
-- Depois de rodar, recarregue a página (F5) no navegador.
-- Não precisa reiniciar a aplicação.
-- ============================================================================

-- 1) Apaga todos os resgates feitos durante os testes
DELETE FROM resgate;

-- 2) Apaga as coletas criadas nos testes (mantém as 2 coletas iniciais: ids 1 e 2)
DELETE FROM coleta WHERE id > 2;

-- 3) Saldo inicial do usuário de demonstração (Alex)
UPDATE usuario SET saldo_pontos = 1250 WHERE id = 1;

-- 4) Ofertas: estoque, validade e situação iniciais
UPDATE oferta SET estoque = 10, validade = '2026-12-31', ativa = true WHERE id = 10;  -- Cashback R$ 5,00 (800 pts)
UPDATE oferta SET estoque = 15, validade = '2026-12-31', ativa = true WHERE id = 20;  -- Desconto 10% (500 pts)
UPDATE oferta SET estoque = 0,  validade = '2026-12-31', ativa = true WHERE id = 30;  -- Energia (1500 pts) ESGOTADA

-- 5) Confere o resultado (deve mostrar: Alex 1250 | 2 coletas | 0 resgates | estoques 10/15/0)
SELECT 'usuario' AS tabela, id::text AS chave, saldo_pontos::text AS valor FROM usuario
UNION ALL SELECT 'coletas', 'total', COUNT(*)::text FROM coleta
UNION ALL SELECT 'resgates', 'total', COUNT(*)::text FROM resgate
UNION ALL SELECT 'oferta', id::text, 'estoque=' || estoque || ' ativa=' || ativa || ' validade=' || validade FROM oferta
ORDER BY 1, 2;
