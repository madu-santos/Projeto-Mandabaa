/*
 * SCRIPT INICIAL DO FRONTEND (Dia 1):
 * Conecta as telas às rotas da API REST das 3 histórias do MVP:
 * - US05: Carteira e Saldo
 * - US01: Histórico de Coletas de PET
 * - US03: Benefícios, Resgates e Cashback
 */
const ENDERECO_API = 'http://localhost:8080/api';
let ofertaSelecionada = null;

function mostrarAba(nomeAba) {
    document.querySelectorAll('.aba-conteudo').forEach(aba => aba.classList.remove('ativa'));
    document.querySelectorAll('.botao-aba').forEach(botao => botao.classList.remove('ativa'));

    const abaAlvo = document.getElementById(`aba-${nomeAba}`);
    if (abaAlvo) abaAlvo.classList.add('ativa');

    const mapaRotulo = { carteira: 'carteira', beneficios: 'benefícios', resgates: 'resgates' };
    const botaoAlvo = Array.from(document.querySelectorAll('.botao-aba'))
        .find(b => b.textContent.toLowerCase().includes(mapaRotulo[nomeAba]));
    if (botaoAlvo) botaoAlvo.classList.add('ativa');

    if (nomeAba === 'carteira') {
        carregarCarteira();
        carregarHistoricoColetas();
    }
    if (nomeAba === 'beneficios') {
        carregarCarteira();
        carregarOfertas();
    }
    if (nomeAba === 'resgates') {
        carregarMeusResgates();
    }
}

// 1. Consulta de Carteira e Saldo (US05)
async function carregarCarteira() {
    let dados = {
        saldoPontos: 1250,
        equivalenteReais: 25.00,
        regraCalculo: 'Cada 1 kg de PET validado vale 50 pontos • 100 pontos = R$ 2,00',
        semSaldo: false
    };
    try {
        const resposta = await fetch(`${ENDERECO_API}/carteira/1`);
        if (resposta.ok) {
            dados = await resposta.json();
        }
    } catch (erro) {
        // Usa dados de exemplo enquanto o backend não está rodando
    }

    document.getElementById('saldo-pontos').textContent = dados.saldoPontos.toLocaleString('pt-BR');
    document.getElementById('saldo-reais').textContent = `R$ ${Number(dados.equivalenteReais).toFixed(2).replace('.', ',')}`;
    document.getElementById('saldo-area-beneficios').textContent = `${dados.saldoPontos.toLocaleString('pt-BR')} pontos`;
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

// 2. Histórico de Coletas de PET (US01)
async function carregarHistoricoColetas() {
    let historico = {
        totalPetKg: 4.3,
        coletas: [
            { id: 1, pontoColeta: 'Cooperativa Verde', dataEntrega: '2026-11-14', pesoKg: 2.5, pontosRecebidos: 125, bonusRecebido: 25 },
            { id: 2, pontoColeta: 'Ecoponto Central', dataEntrega: '2026-11-09', pesoKg: 1.8, pontosRecebidos: 90, bonusRecebido: 0 }
        ]
    };
    try {
        const resposta = await fetch(`${ENDERECO_API}/coletas/usuario/1`);
        if (resposta.ok) {
            historico = await resposta.json();
        }
    } catch (erro) {
        // Usa dados de exemplo enquanto o backend não está rodando
    }

    document.getElementById('total-pet-kg').textContent = `${Number(historico.totalPetKg).toFixed(1).replace('.', ',')} kg`;
    const lista = document.getElementById('lista-coletas');
    lista.innerHTML = '';

    historico.coletas.forEach(coleta => {
        const linha = document.createElement('div');
        linha.className = 'linha-registro';
        const textoBonus = coleta.bonusRecebido > 0 ? ` (+${coleta.bonusRecebido} bônus)` : '';
        linha.innerHTML = `
            <div>
                <strong>♻️ PET entregue — ${coleta.pontoColeta}</strong>
                <div class="detalhe">${Number(coleta.pesoKg).toFixed(1).replace('.', ',')} kg • ${coleta.dataEntrega}</div>
            </div>
            <div class="valor-positivo">+${coleta.pontosRecebidos} pts${textoBonus}</div>
        `;
        lista.appendChild(linha);
    });
}

// 3. Catálogo de Benefícios e Cashback (US03)
async function carregarOfertas() {
    let ofertas = [
        { id: 10, parceiro: 'Mandabaa Cashback', titulo: 'Cashback de R$ 5,00', tipo: 'CASHBACK', custoPontos: 800, estoque: 10, condicoes: 'Recebimento via PIX após confirmação (até 2 dias úteis).' },
        { id: 20, parceiro: 'Supermercado Verde', titulo: 'Desconto de 10% em compras', tipo: 'CUPOM', custoPontos: 500, estoque: 15, condicoes: 'Válido em produtos selecionados nas lojas parceiras.' },
        { id: 30, parceiro: 'EcoLuz Solar', titulo: 'Desconto na Conta de Energia', tipo: 'CUPOM', custoPontos: 1500, estoque: 0, condicoes: 'Oferta temporariamente esgotada.' }
    ];
    try {
        const resposta = await fetch(`${ENDERECO_API}/ofertas`);
        if (resposta.ok) {
            ofertas = await resposta.json();
        }
    } catch (erro) {
        // Usa dados de exemplo enquanto o backend não está rodando
    }

    const grade = document.getElementById('grade-ofertas');
    grade.innerHTML = '';
    ofertas.forEach(oferta => {
        const cartao = document.createElement('div');
        cartao.className = 'cartao-item';
        const esgotada = oferta.estoque === 0;
        cartao.innerHTML = `
            <div>
                <span class="selo ${oferta.tipo === 'CASHBACK' ? 'selo-pendente' : 'selo-disponivel'}">${oferta.tipo}</span>
                <h3 style="margin-top:0.5rem">${oferta.titulo}</h3>
                <p class="parceiro">${oferta.parceiro}</p>
                <p class="parceiro">${oferta.condicoes || ''}</p>
                <div class="etiqueta-pontos">${oferta.custoPontos} pontos</div>
            </div>
            <button class="botao-primario" ${esgotada ? 'disabled style="opacity:0.5; cursor:not-allowed;"' : ''}>
                ${esgotada ? 'Esgotado' : 'Resgatar'}
            </button>
        `;
        if (!esgotada) {
            cartao.querySelector('button').onclick = () => abrirModalResgate(oferta);
        }
        grade.appendChild(cartao);
    });
}

function abrirModalResgate(oferta) {
    ofertaSelecionada = oferta;
    document.getElementById('texto-modal-resgate').textContent =
        `Você vai trocar ${oferta.custoPontos} pontos por "${oferta.titulo}". ${oferta.condicoes || ''}`;

    const campoPix = document.getElementById('campo-pix-container');
    const inputPix = document.getElementById('input-chave-pix');
    inputPix.value = '';
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
    fecharModalResgate();

    try {
        const resposta = await fetch(`${ENDERECO_API}/resgates`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                usuarioId: 1,
                ofertaId: ofertaAtual.id,
                tipo: ofertaAtual.tipo || 'CUPOM',
                chavePix: ofertaAtual.tipo === 'CASHBACK' ? chavePix : null
            })
        });

        const dados = await resposta.json();

        if (resposta.ok) {
            document.getElementById('descricao-comprovante').textContent =
                dados.situacao === 'PAGAMENTO_PENDENTE'
                    ? 'Solicitação de cashback registrada! Situação: Pagamento Pendente.'
                    : 'Cupom disponível! Apresente o código abaixo na loja parceira:';
            document.getElementById('codigo-comprovante').textContent = dados.codigoComprovante || dados.situacao;
            document.getElementById('modal-comprovante').classList.remove('oculto');
            exibirAviso('Resgate realizado com sucesso!', 'sucesso');
        } else {
            exibirAviso(dados.mensagem || 'Não foi possível concluir o resgate.', 'erro');
        }
    } catch (erro) {
        exibirAviso('Não foi possível conectar ao servidor do Mandabaa.', 'erro');
    }
}

function fecharModalComprovante() {
    document.getElementById('modal-comprovante').classList.add('oculto');
    carregarCarteira();
    carregarOfertas();
}

// 4. Histórico de Benefícios Resgatados (US03 - CA07, CA08 e CA09)
async function carregarMeusResgates() {
    let resgates = [
        { resgateId: 1, tituloOferta: 'Desconto de 10% em compras', tipo: 'CUPOM', situacao: 'DISPONIVEL', codigoComprovante: 'MANDABAA-500-A1B2', pontosDescontados: 500 },
        { resgateId: 2, tituloOferta: 'Cashback de R$ 5,00', tipo: 'CASHBACK', situacao: 'PAGAMENTO_PENDENTE', codigoComprovante: 'PIX-PENDENTE-002', pontosDescontados: 800 }
    ];
    try {
        const resposta = await fetch(`${ENDERECO_API}/resgates/usuario/1`);
        if (resposta.ok) {
            resgates = await resposta.json();
        }
    } catch (erro) {
        // Usa dados de exemplo enquanto o backend não está rodando
    }

    const lista = document.getElementById('lista-resgates');
    lista.innerHTML = '';
    resgates.forEach(item => {
        const linha = document.createElement('div');
        linha.className = 'linha-registro';
        const classeSelo = item.situacao === 'DISPONIVEL' || item.situacao === 'PAGAMENTO_CONFIRMADO'
            ? 'selo-disponivel'
            : (item.situacao === 'PAGAMENTO_PENDENTE' ? 'selo-pendente' : 'selo-expirado');
        linha.innerHTML = `
            <div>
                <strong>${item.tituloOferta || 'Benefício resgatado'}</strong>
                <div class="detalhe">Código: <code>${item.codigoComprovante || '-'}</code> • ${item.pontosDescontados} pontos</div>
            </div>
            <span class="selo ${classeSelo}">${item.situacao}</span>
        `;
        lista.appendChild(linha);
    });
}

function exibirAviso(mensagem, tipo) {
    const aviso = document.getElementById('aviso-flutuante');
    aviso.textContent = mensagem;
    aviso.className = `aviso-flutuante ${tipo}`;
    setTimeout(() => {
        aviso.className = 'aviso-flutuante oculto';
    }, 3500);
}

document.addEventListener('DOMContentLoaded', () => {
    carregarCarteira();
    carregarHistoricoColetas();
});
