-- ============================================================
-- Habitat Zero — Consultas SQL de Análise Operacional
-- ============================================================

-- ─────────────────────────────────────────────────────────────
-- CONSULTA 1
-- Listar todos os colonos e a estufa à qual estão atribuídos
-- Caso de uso: relatório de alocação de pessoal da colônia
-- ─────────────────────────────────────────────────────────────
SELECT
    c.nome              AS colono,
    c.cargo,
    COALESCE(e.nome, '— Sem estufa —') AS estufa,
    COALESCE(e.status, '')              AS status_estufa
FROM colono c
LEFT JOIN estufa e ON c.estufa_id = e.id
ORDER BY e.nome, c.cargo;


-- ─────────────────────────────────────────────────────────────
-- CONSULTA 2
-- Alertas não resolvidos, ordenados do mais crítico ao mais recente
-- Caso de uso: painel de triagem de emergências da estação
-- ─────────────────────────────────────────────────────────────
SELECT
    a.id,
    e.nome              AS estufa,
    a.severidade,
    a.tipo_sensor,
    a.valor_registrado,
    a.mensagem,
    a.criado_em
FROM alerta a
JOIN estufa e ON a.estufa_id = e.id
WHERE a.resolvido = 0
ORDER BY
    FIELD(a.severidade, 'EMERGENCIA', 'CRITICO', 'ATENCAO'),
    a.criado_em DESC;


-- ─────────────────────────────────────────────────────────────
-- CONSULTA 3
-- Média, mínimo e máximo de cada sensor por estufa nas últimas 24 horas
-- Caso de uso: relatório diário de condições ambientais por estufa
-- ─────────────────────────────────────────────────────────────
SELECT
    e.nome                          AS estufa,
    s.tipo_sensor,
    s.unidade,
    ROUND(AVG(s.valor_leitura), 2)  AS media,
    ROUND(MIN(s.valor_leitura), 2)  AS minimo,
    ROUND(MAX(s.valor_leitura), 2)  AS maximo,
    COUNT(*)                        AS total_leituras
FROM sensor_ambiente s
JOIN estufa e ON s.estufa_id = e.id
WHERE s.timestamp >= NOW() - INTERVAL 24 HOUR
GROUP BY e.id, s.tipo_sensor, s.unidade
ORDER BY e.nome, s.tipo_sensor;


-- ─────────────────────────────────────────────────────────────
-- CONSULTA 4
-- Distribuição das plantas por fase de crescimento em cada estufa ativa
-- Caso de uso: planejamento de colheita e reposição de cultivos
-- ─────────────────────────────────────────────────────────────
SELECT
    e.nome                                          AS estufa,
    SUM(p.fase_crescimento = 'SEMENTE')             AS semente,
    SUM(p.fase_crescimento = 'GERMINACAO')          AS germinacao,
    SUM(p.fase_crescimento = 'CRESCIMENTO')         AS crescimento,
    SUM(p.fase_crescimento = 'MATURACAO')           AS maturacao,
    SUM(p.fase_crescimento = 'COLHEITA')            AS pronta_colheita,
    COUNT(p.id)                                     AS total_plantas
FROM estufa e
LEFT JOIN planta p ON p.estufa_id = e.id
WHERE e.status = 'ATIVA'
GROUP BY e.id, e.nome
ORDER BY pronta_colheita DESC, e.nome;