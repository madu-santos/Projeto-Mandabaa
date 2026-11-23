/*
 * SCRIPT FRONTEND — Mandabaa (Pessoa 5 — UI/UX, Sprint 5.1)
 * Conecta as telas às rotas REST das 3 histórias do MVP:
 *   US05 — Consulta de Saldo e Carteira
 *   US01 — Histórico e Registro de Coletas de PET
 *   US03 — Benefícios, Resgates e Cashback
 */

const ENDERECO_API = window.location.protocol.startsWith('http')
    ? `${window.location.origin}/api`
    : 'http://localhost:8088/api';
const USUARIO_ID   = 1;
let ofertaSelecionada = null;

/* ================================================================
   NAVEGAÇÃO ENTRE ABAS
   ================================================================ */
function mostrarAba(nomeAba) {
    document.querySelectorAll('.aba-conteudo').forEach(a => a.classList.remove('ativa'));
    document.querySelectorAll('.botao-aba').forEach(b => b.classList.remove('ativa'));

    const abaAlvo = document.getElementById(`aba-${nomeAba}`);
    if (abaAlvo) abaAlvo.classList.add('ativa');

    const mapaRotulo = { carteira: 'carteira', beneficios: 'benefícios', resgates: 'resgates' };
    const botaoAlvo = Array.from(document.querySelectorAll('.botao-aba'))
        .find(b => b.textContent.trim().toLowerCase().includes(mapaRotulo[nomeAba]));
    if (botaoAlvo) botaoAlvo.classList.add('ativa');

    if (nomeAba === 'carteira')  { carregarCarteira(); carregarHistoricoColetas(); }
    if (nomeAba === 'beneficios') { carregarCarteira(); carregarOfertas(); }
    if (nomeAba === 'resgates')  { carregarMeusResgates(); }
}

/* ================================================================
   US05 — CONSULTA DE SALDO E CARTEIRA
   ================================================================ */
async function carregarCarteira() {
    // Dados de exemplo (usados se o backend não responder)
    let dados = {
        saldoPontos:      1250,
        equivalenteReais: 25.00,
        regraCalculo:     '1 kg de PET = 50 pontos • 100 pontos = R$ 2,00',
        semSaldo:         false
    };
    try {
        const resposta = await fetch(`${ENDERECO_API}/carteira/${USUARIO_ID}`);
        if (resposta.ok) {
            const texto = await resposta.text();
            if (texto) dados = JSON.parse(texto);
        }
    } catch (_) { /* usa dados de exemplo */ }

    document.getElementById('saldo-pontos').textContent =
        dados.saldoPontos.toLocaleString('pt-BR');
    document.getElementById('saldo-reais').textContent =
        `R$ ${Number(dados.equivalenteReais).toFixed(2).replace('.', ',')}`;
    document.getElementById('saldo-area-beneficios').textContent =
        `${dados.saldoPontos.toLocaleString('pt-BR')} pontos`;
    if (dados.regraCalculo) {
        document.getElementById('regra-calculo').textContent = dados.regraCalculo;
    }

    const avisoSemSaldo = document.getElementById('aviso-sem-saldo');
    if (dados.semSaldo || dados.saldoPontos === 0) {
        avisoSemSaldo.classList.remove('oculto');
    } else {
        avisoSemSaldo.classList.add('oculto');
    }
}

/* ================================================================
   US01 — HISTÓRICO DE COLETAS
   ================================================================ */
async function carregarHistoricoColetas() {
    // Dados de exemplo
    let historico = {
        totalPetKg: 4.3,
        coletas: [
            { id: 1, pontoColeta: 'Cooperativa Verde',  dataEntrega: '2026-11-14', pesoKg: 2.5, pontosRecebidos: 125, bonusRecebido: 25 },
            { id: 2, pontoColeta: 'Ecoponto Central',   dataEntrega: '2026-11-09', pesoKg: 1.8, pontosRecebidos: 90,  bonusRecebido: 0  }
        ]
    };
    try {
        const resposta = await fetch(`${ENDERECO_API}/coletas/usuario/${USUARIO_ID}`);
        if (resposta.ok) {
            const texto = await resposta.text();
            if (texto) historico = JSON.parse(texto);
        }
    } catch (_) { /* usa dados de exemplo */ }

    document.getElementById('total-pet-kg').textContent =
        `${Number(historico.totalPetKg).toFixed(1).replace('.', ',')} kg`;

    const lista = document.getElementById('lista-coletas');
    lista.innerHTML = '';

    if (!historico.coletas || historico.coletas.length === 0) {
        lista.innerHTML = `
            <div class="estado-vazio">
                <span>🌿</span>
                <p>Nenhuma coleta registrada ainda.</p>
            </div>`;
        return;
    }

    historico.coletas.forEach(coleta => {
        const dataFormatada = coleta.dataEntrega
            ? new Date(coleta.dataEntrega + 'T00:00:00').toLocaleDateString('pt-BR')
            : '—';

        const linha = document.createElement('div');
        linha.className = 'linha-registro';
        linha.innerHTML = `
            <div class="info">
                <strong>♻️ PET entregue — ${coleta.pontoColeta}</strong>
                <div class="detalhe">
                    ${Number(coleta.pesoKg).toFixed(1).replace('.', ',')} kg &nbsp;•&nbsp; ${dataFormatada}
                </div>
            </div>
            <div class="valor-positivo">
                +${coleta.pontosRecebidos} pts
                ${coleta.bonusRecebido > 0 ? `<span class="bonus-tag">+${coleta.bonusRecebido} bônus</span>` : ''}
            </div>`;
        lista.appendChild(linha);
    });
}

/* ================================================================
   US01 — REGISTRAR NOVA ENTREGA (modal)
   ================================================================ */
function abrirModalColeta() {
    document.getElementById('input-ponto-coleta').value = '';
    document.getElementById('input-peso-kg').value = '';
    document.getElementById('input-bonus').value = '';
    document.getElementById('modal-coleta').classList.remove('oculto');
    document.getElementById('input-ponto-coleta').focus();
}

function fecharModalColeta() {
    document.getElementById('modal-coleta').classList.add('oculto');
}

async function confirmarColeta() {
    const pontoColeta = document.getElementById('input-ponto-coleta').value.trim();
    const pesoKg      = parseFloat(document.getElementById('input-peso-kg').value);
    const bonus       = parseInt(document.getElementById('input-bonus').value) || 0;

    if (!pontoColeta) {
        exibirAviso('Informe o ponto de coleta.', 'erro');
        return;
    }
    if (!pesoKg || pesoKg <= 0) {
        exibirAviso('O peso deve ser maior que zero.', 'erro');
        return;
    }

    fecharModalColeta();

    try {
        const resposta = await fetch(`${ENDERECO_API}/coletas`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({ usuarioId: USUARIO_ID, pontoColeta, pesoKg, bonusRecebido: bonus })
        });

        const texto = await resposta.text();
        const dados = texto ? JSON.parse(texto) : null;

        if (resposta.ok && dados) {
            exibirAviso(`✅ Coleta registrada! +${dados.pontosRecebidos} pontos adicionados.`, 'sucesso');
            carregarCarteira();
            carregarHistoricoColetas();
        } else {
            const msg = (dados && dados.mensagem) || 'Não foi possível registrar a coleta.';
            exibirAviso(msg, 'erro');
        }
    } catch (_) {
        exibirAviso('Não foi possível conectar ao servidor.', 'erro');
    }
}

/* ================================================================
   US03 — CATÁLOGO DE BENEFÍCIOS
   ================================================================ */
async function carregarOfertas() {
    let ofertas = [
        { id: 10, parceiro: 'Mandabaa Cashback',  titulo: 'Cashback de R$ 5,00',          tipo: 'CASHBACK', custoPontos: 800,  estoque: 10, condicoes: 'Recebimento via PIX após confirmação (até 2 dias úteis).' },
        { id: 20, parceiro: 'Supermercado Verde',  titulo: 'Desconto de 10% em compras',  tipo: 'CUPOM',    custoPontos: 500,  estoque: 15, condicoes: 'Válido em produtos selecionados nas lojas parceiras.' },
        { id: 30, parceiro: 'EcoLuz Solar',         titulo: 'Desconto na Conta de Energia', tipo: 'CUPOM',    custoPontos: 1500, estoque: 0,  condicoes: 'Oferta temporariamente esgotada.' }
    ];
    try {
        const resposta = await fetch(`${ENDERECO_API}/ofertas`);
        if (resposta.ok) {
            const texto = await resposta.text();
            if (texto) {
                const listaBanco = JSON.parse(texto);
                if (Array.isArray(listaBanco) && listaBanco.length > 0) ofertas = listaBanco;
            }
        }
    } catch (_) { /* usa dados de exemplo */ }

    const grade = document.getElementById('grade-ofertas');
    grade.innerHTML = '';

    if (!ofertas.length) {
        grade.innerHTML = `<div class="estado-vazio"><span>🎁</span><p>Nenhuma oferta disponível no momento.</p></div>`;
        return;
    }

    ofertas.forEach(oferta => {
        const esgotada = oferta.estoque === 0;
        const isCashback = oferta.tipo === 'CASHBACK';

        const cartao = document.createElement('div');
        cartao.className = `cartao-item${esgotada ? ' esgotada' : ''}`;

        const seloTipo = isCashback
            ? '<span class="selo selo-cashback">💰 Cashback</span>'
            : '<span class="selo selo-cupom">🏷️ Cupom</span>';

        const infoEstoque = esgotada
            ? '<span class="selo selo-esgotada">Esgotado</span>'
            : (oferta.estoque <= 5
                ? `<div class="estoque-info estoque-baixo">⚠️ Apenas ${oferta.estoque} disponíveis</div>`
                : `<div class="estoque-info">${oferta.estoque} disponíveis</div>`);

        cartao.innerHTML = `
            <div>
                <div class="cartao-item-topo">
                    ${seloTipo}
                    ${esgotada ? '<span class="selo selo-esgotada">Esgotado</span>' : ''}
                </div>
                <h3>${oferta.titulo}</h3>
                <p class="parceiro">🏢 ${oferta.parceiro}</p>
                <div class="etiqueta-pontos">${oferta.custoPontos.toLocaleString('pt-BR')}<small> pontos</small></div>
                ${infoEstoque}
                <p class="condicoes">📋 ${oferta.condicoes || ''}</p>
            </div>
            <button class="botao-primario" ${esgotada ? 'disabled' : ''}>
                ${esgotada ? '🚫 Esgotado' : '🎁 Resgatar'}
            </button>`;

        if (!esgotada) {
            cartao.querySelector('button').onclick = () => abrirModalResgate(oferta);
        }
        grade.appendChild(cartao);
    });
}

/* ================================================================
   US03 — MODAL DE RESGATE
   ================================================================ */
function abrirModalResgate(oferta) {
    ofertaSelecionada = oferta;

    const textoCusto = `Você vai trocar ${oferta.custoPontos} pontos por "${oferta.titulo}".`;
    const textoCondicoes = oferta.condicoes ? ` ${oferta.condicoes}` : '';
    document.getElementById('texto-modal-resgate').textContent = textoCusto + textoCondicoes;

    const campoPix = document.getElementById('campo-pix-container');
    document.getElementById('input-chave-pix').value = '';
    if (oferta.tipo === 'CASHBACK') {
        campoPix.classList.remove('oculto');
    } else {
        campoPix.classList.add('oculto');
    }

    document.getElementById('modal-resgate').classList.remove('oculto');
    document.getElementById('botao-confirmar-resgate').onclick = confirmarResgate;
}

function fecharModalResgate() {
    document.getElementById('modal-resgate').classList.add('oculto');
    ofertaSelecionada = null;
}

async function confirmarResgate() {
    if (!ofertaSelecionada) return;
    const ofertaAtual = ofertaSelecionada;
    const chavePix = document.getElementById('input-chave-pix').value.trim();

    if (ofertaAtual.tipo === 'CASHBACK' && !chavePix) {
        exibirAviso('Informe a chave PIX para receber o cashback.', 'erro');
        return;
    }

    fecharModalResgate();

    try {
        const resposta = await fetch(`${ENDERECO_API}/resgates`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify({
                usuarioId: USUARIO_ID,
                ofertaId:  ofertaAtual.id,
                tipo:      ofertaAtual.tipo || 'CUPOM',
                chavePix:  ofertaAtual.tipo === 'CASHBACK' ? chavePix : null
            })
        });

        const texto = await resposta.text();
        const dados = texto ? JSON.parse(texto) : null;

        if (resposta.ok && dados) {
            const isPix = dados.situacao === 'PAGAMENTO_PENDENTE';
            document.getElementById('descricao-comprovante').textContent = isPix
                ? '✅ Solicitação de cashback registrada! Aguardando confirmação de pagamento:'
                : '✅ Cupom disponível! Apresente o código abaixo na loja parceira:';

            document.getElementById('codigo-comprovante').textContent =
                dados.codigoComprovante || dados.situacao;

            document.getElementById('pontos-descontados-comprovante').textContent =
                `${ofertaAtual.custoPontos} pontos descontados • Novo saldo: ${dados.novoSaldo?.toLocaleString('pt-BR') ?? '—'} pts`;

            document.getElementById('modal-comprovante').classList.remove('oculto');
            exibirAviso('🎉 Resgate realizado com sucesso!', 'sucesso');
        } else {
            const msg = (dados && dados.mensagem) || 'Não foi possível concluir o resgate.';
            exibirAviso(msg, 'erro');
        }
    } catch (_) {
        exibirAviso('Não foi possível conectar ao servidor do Mandabaa.', 'erro');
    }
}

function fecharModalComprovante() {
    document.getElementById('modal-comprovante').classList.add('oculto');
    carregarCarteira();
    carregarOfertas();
}

/* ================================================================
   US03 — HISTÓRICO DE RESGATES
   ================================================================ */
async function carregarMeusResgates() {
    let resgates = [
        { resgateId: 1, tituloOferta: 'Desconto de 10% em compras', parceiro: 'Supermercado Verde', tipo: 'CUPOM',    situacao: 'DISPONIVEL',       codigoComprovante: 'MANDABAA-500-A1B2', pontosDescontados: 500 },
        { resgateId: 2, tituloOferta: 'Cashback de R$ 5,00',         parceiro: 'Mandabaa Cashback',  tipo: 'CASHBACK', situacao: 'PAGAMENTO_PENDENTE', codigoComprovante: 'PIX-PENDENTE-002',  pontosDescontados: 800 }
    ];
    try {
        const resposta = await fetch(`${ENDERECO_API}/resgates/usuario/${USUARIO_ID}`);
        if (resposta.ok) {
            const texto = await resposta.text();
            if (texto) resgates = JSON.parse(texto);
        }
    } catch (_) { /* usa dados de exemplo */ }

    const lista = document.getElementById('lista-resgates');
    lista.innerHTML = '';

    if (!resgates.length) {
        lista.innerHTML = `
            <div class="estado-vazio">
                <span>🎟️</span>
                <p>Você ainda não resgatou nenhum benefício.</p>
            </div>`;
        return;
    }

    resgates.forEach(item => {
        const { classeSelo, rotuloSelo, icone } = mapearSituacao(item.situacao);

        const linha = document.createElement('div');
        linha.className = 'linha-registro';
        linha.innerHTML = `
            <div class="info">
                <strong>${icone} ${item.tituloOferta || 'Benefício resgatado'}</strong>
                <div class="detalhe">
                    ${item.parceiro ? `🏢 ${item.parceiro} &nbsp;•&nbsp; ` : ''}
                    Código: <code>${item.codigoComprovante || '—'}</code>
                    &nbsp;•&nbsp; ${item.pontosDescontados} pts descontados
                </div>
            </div>
            <span class="selo ${classeSelo}">${rotuloSelo}</span>`;
        lista.appendChild(linha);
    });
}

function mapearSituacao(situacao) {
    const mapa = {
        'DISPONIVEL':          { classeSelo: 'selo-disponivel', rotuloSelo: '✅ Disponível',          icone: '🏷️' },
        'UTILIZADO':           { classeSelo: 'selo-expirado',   rotuloSelo: '☑️ Utilizado',           icone: '🏷️' },
        'EXPIRADO':            { classeSelo: 'selo-expirado',   rotuloSelo: '⌛ Expirado',            icone: '🏷️' },
        'PAGAMENTO_PENDENTE':  { classeSelo: 'selo-pendente',   rotuloSelo: '⏳ Pagamento Pendente',  icone: '💰' },
        'PAGAMENTO_CONFIRMADO':{ classeSelo: 'selo-disponivel', rotuloSelo: '✅ Pago',                icone: '💰' }
    };
    return mapa[situacao] || { classeSelo: 'selo-expirado', rotuloSelo: situacao, icone: '📋' };
}

/* ================================================================
   AVISO FLUTUANTE
   ================================================================ */
let avisoTimer = null;
function exibirAviso(mensagem, tipo) {
    const aviso = document.getElementById('aviso-flutuante');
    aviso.textContent = mensagem;
    aviso.className = `aviso-flutuante ${tipo}`;
    if (avisoTimer) clearTimeout(avisoTimer);
    avisoTimer = setTimeout(() => {
        aviso.className = 'aviso-flutuante oculto';
    }, 4000);
}

/* ================================================================
   INICIALIZAÇÃO
   ================================================================ */
document.addEventListener('DOMContentLoaded', () => {
    carregarCarteira();
    carregarHistoricoColetas();
});
