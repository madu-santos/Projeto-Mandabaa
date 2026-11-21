/*
 * SCRIPT INICIAL DO FRONTEND (Dia 1):
 * Conecta as telas às rotas da API REST do backend.
 * Enquanto o backend ainda não tem os controladores prontos, mostra dados de exemplo na tela.
 */
const ENDERECO_API = 'http://localhost:8080/api';
let ofertaSelecionada = null;

function mostrarAba(nomeAba) {
    document.querySelectorAll('.aba-conteudo').forEach(aba => aba.classList.remove('ativa'));
    document.querySelectorAll('.botao-aba').forEach(botao => botao.classList.remove('ativa'));

    const abaAlvo = document.getElementById(`aba-${nomeAba}`);
    if (abaAlvo) abaAlvo.classList.add('ativa');

    const botaoAlvo = Array.from(document.querySelectorAll('.botao-aba'))
        .find(b => b.textContent.toLowerCase().includes(nomeAba));
    if (botaoAlvo) botaoAlvo.classList.add('ativa');

    if (nomeAba === 'carteira') carregarCarteira();
    if (nomeAba === 'beneficios') carregarOfertas();
    if (nomeAba === 'campanhas') carregarCampanhas();
}

// 1. Consulta de Carteira e Saldo
async function carregarCarteira() {
    try {
        const resposta = await fetch(`${ENDERECO_API}/carteira/1`);
        if (resposta.ok) {
            const dados = await resposta.json();
            document.getElementById('saldo-pontos').textContent = dados.saldoPontos.toLocaleString('pt-BR');
            document.getElementById('saldo-reais').textContent = `R$ ${dados.equivalenteReais.toFixed(2).replace('.', ',')}`;
            return;
        }
    } catch (erro) {
        // Dados de exemplo enquanto o backend não está rodando
    }
    document.getElementById('saldo-pontos').textContent = '1.000';
    document.getElementById('saldo-reais').textContent = 'R$ 20,00';
}

// 2. Listagem de Ofertas (US03)
async function carregarOfertas() {
    try {
        const resposta = await fetch(`${ENDERECO_API}/ofertas`);
        if (resposta.ok) {
            const ofertas = await resposta.json();
            desenharOfertas(ofertas);
            return;
        }
    } catch (erro) {
        // Dados de exemplo enquanto o backend não está rodando
    }
    desenharOfertas([
        { id: 10, parceiro: 'Supermercado Verde', titulo: 'Desconto de R$ 10,00', tipo: 'CUPOM', custoPontos: 500, estoque: 10, condicoes: 'Válido em compras acima de R$ 50,00.' },
        { id: 20, parceiro: 'Mandabaa Cashback', titulo: 'Cashback de R$ 15,00', tipo: 'CASHBACK', custoPontos: 750, estoque: 5, condicoes: 'Crédito via PIX após confirmação (até 2 dias úteis).' },
        { id: 30, parceiro: 'EcoLuz Solar', titulo: 'Desconto na Conta de Energia', tipo: 'CUPOM', custoPontos: 1200, estoque: 0, condicoes: 'Oferta temporariamente esgotada.' }
    ]);
}

function desenharOfertas(ofertas) {
    const grade = document.getElementById('grade-ofertas');
    grade.innerHTML = '';
    ofertas.forEach(oferta => {
        const cartao = document.createElement('div');
        cartao.className = 'cartao-item';
        const esgotada = oferta.estoque === 0;
        cartao.innerHTML = `
            <div>
                <h3>${oferta.titulo}</h3>
                <p class="parceiro">${oferta.parceiro}</p>
                <p class="parceiro">${oferta.condicoes || ''}</p>
                <div class="etiqueta-pontos">${oferta.custoPontos} pontos</div>
            </div>
            <button class="botao-primario" ${esgotada ? 'disabled style="opacity:0.5; cursor:not-allowed;"' : ''}>
                ${esgotada ? 'Esgotado' : 'Resgatar Benefício'}
            </button>
        `;
        if (!esgotada) {
            cartao.querySelector('button').onclick = () => abrirModalResgate(oferta);
        }
        grade.appendChild(cartao);
    });
}

// 3. Janela de Confirmação e Resgate (US03)
function abrirModalResgate(oferta) {
    ofertaSelecionada = oferta;
    document.getElementById('texto-modal-resgate').textContent =
        `Deseja resgatar "${oferta.titulo}" por ${oferta.custoPontos} pontos? ${oferta.condicoes || ''}`;
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
    fecharModalResgate();

    try {
        const resposta = await fetch(`${ENDERECO_API}/resgates`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                usuarioId: 1,
                ofertaId: ofertaAtual.id,
                tipo: ofertaAtual.tipo || 'CUPOM'
            })
        });

        const dados = await resposta.json();

        if (resposta.ok) {
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
}

// 4. Campanhas Temporárias e Avisos (US07)
async function carregarCampanhas() {
    try {
        const resposta = await fetch(`${ENDERECO_API}/campanhas`);
        if (resposta.ok) {
            const campanhas = await resposta.json();
            desenharCampanhas(campanhas);
            return;
        }
    } catch (erro) {
        // Dados de exemplo enquanto o backend não está rodando
    }
    desenharCampanhas([
        { id: 1, titulo: 'Semana do PET em Dobro', descricao: 'Entregue garrafas PET e ganhe o dobro de pontos!', condicoes: 'Válido até 30/11/2026.', multiplicadorBonus: 2, situacao: 'ATIVA' },
        { id: 2, titulo: 'Bônus de Início de Semestre', descricao: 'Campanha de incentivo aos estudantes.', condicoes: 'Encerrada em 10/11/2026.', multiplicadorBonus: 1.5, situacao: 'EXPIRADA' }
    ]);
}

function desenharCampanhas(campanhas) {
    const grade = document.getElementById('grade-campanhas');
    grade.innerHTML = '';
    campanhas.forEach(campanha => {
        const cartao = document.createElement('div');
        cartao.className = 'cartao-item';
        const ativa = campanha.situacao === 'ATIVA';
        cartao.innerHTML = `
            <div>
                <span class="selo ${ativa ? 'selo-ativa' : 'selo-expirada'}">${campanha.situacao}</span>
                <h3 style="margin-top:0.5rem">${campanha.titulo}</h3>
                <p class="parceiro" style="margin-top:0.25rem">${campanha.descricao || ''}</p>
                <p class="parceiro">${campanha.condicoes || ''}</p>
            </div>
            <div style="margin-top:1rem; font-weight:600; color:var(--cor-primaria)">
                Bônus: ${campanha.multiplicadorBonus}x pontos
            </div>
        `;
        grade.appendChild(cartao);
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
});
