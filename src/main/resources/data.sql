-- Carga inicial de demonstração

-- Usuário de demonstração
INSERT INTO usuario (id, nome, saldo_pontos)
VALUES (1, 'Alex', 1250)
ON CONFLICT (id) DO NOTHING;

-- Coletas validadas (US01)
INSERT INTO coleta (id, usuario_id, ponto_coleta, data_entrega, peso_kg, pontos_recebidos, bonus_recebido, validada)
VALUES (1, 1, 'Cooperativa Verde', '2026-11-14', 2.5, 125, 25, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO coleta (id, usuario_id, ponto_coleta, data_entrega, peso_kg, pontos_recebidos, bonus_recebido, validada)
VALUES (2, 1, 'Ecoponto Central', '2026-11-09', 1.8, 90, 0, true)
ON CONFLICT (id) DO NOTHING;

-- Catálogo de ofertas (US03)
INSERT INTO oferta (id, parceiro, titulo, descricao, condicoes, tipo, valor_cashback, custo_pontos, estoque, validade, ativa)
VALUES (10, 'Mandabaa Cashback', 'Cashback de R$ 5,00', 'Receba R$ 5,00 diretamente na sua conta bancária via chave PIX.', 'Recebimento via PIX após confirmação (até 2 dias úteis).', 'CASHBACK', 5.00, 800, 10, '2026-12-31', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO oferta (id, parceiro, titulo, descricao, condicoes, tipo, valor_cashback, custo_pontos, estoque, validade, ativa)
VALUES (20, 'Supermercado Verde', 'Desconto de 10% em compras', '10% de desconto em produtos sustentáveis nas lojas parceiras.', 'Válido em produtos selecionados nas lojas parceiras.', 'CUPOM', NULL, 500, 15, '2026-12-31', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO oferta (id, parceiro, titulo, descricao, condicoes, tipo, valor_cashback, custo_pontos, estoque, validade, ativa)
VALUES (30, 'EcoLuz Solar', 'Desconto na Conta de Energia', 'Desconto especial na tarifa de energia sustentável.', 'Oferta temporariamente esgotada.', 'CUPOM', NULL, 1500, 0, '2026-12-31', true)
ON CONFLICT (id) DO NOTHING;

-- Sincronização de sequências para novos cadastros via JPA
SELECT setval(pg_get_serial_sequence('usuario', 'id'), COALESCE((SELECT MAX(id) FROM usuario), 1));
SELECT setval(pg_get_serial_sequence('coleta', 'id'), COALESCE((SELECT MAX(id) FROM coleta), 1));
SELECT setval(pg_get_serial_sequence('oferta', 'id'), COALESCE((SELECT MAX(id) FROM oferta), 1));
