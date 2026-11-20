const API_BASE_URL = 'http://localhost:8080/api';
let ofertaSelecionadaId = null;

// Navegacao por abas
function mostrarAba(nomeAba) {
    document.querySelectorAll('.aba-conteudo').forEach(aba => aba.classList.remove('ativa'));
    document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));

    const abaAlvo = document.getElementById(`aba-${nomeAba}`);
    if (abaAlvo) abaAlvo.classList.add('ativa');

    // Destacar botao
    const btnAlvo = Array.from(document.querySelectorAll('.nav-btn')).find(b => b.textContent.toLowerCase().includes(nomeAba));
    if (btnAlvo) btnAlvo.classList.add('active');

    if (nomeAba === 'carteira') carregarCarteira();
    if (nomeAba === 'beneficios') carregarOfertas();
    if (nomeAba === 'campanhas') carregarCampanhas();
}

// 1. Carregar Carteira (US05)
async function carregarCarteira() {
    try {
        const response = await fetch(`${API_BASE_URL}/carteira/1`);
        if (response.ok) {
            const data = await response.json();
            document.getElementById('saldo-pontos').textContent = data.saldoPontos.toLocaleString('pt-BR');
            document.getElementById('saldo-reais').textContent = `R$ ${data.equivalenteReais.toFixed(2).replace('.', ',')}`;
        }
    } catch (e) {
        // Fallback visual de demonstracao inicial
        document.getElementById('saldo-pontos').textContent = '1.000';
        document.getElementById('saldo-reais').textContent = 'R$ 20,00';
    }
}

// 2. Carregar Ofertas (US03)
async function carregarOfertas() {
    const grid = document.getElementById('grid-ofertas');
    try {
        const response = await fetch(`${API_BASE_URL}/ofertas`);
        if (response.ok) {
            const ofertas = await response.json();
            renderizarOfertas(ofertas);
            return;
        }
    } catch (e) {
        // Fallback demonstrativo inicial
        renderizarOfertas([
            { id: 10, parceiro: 'Supermercado Verde', titulo: 'Desconto de R$ 10,00', custoPontos: 500, estoque: 10 },
            { id: 20, parceiro: 'Farmácia Vida', titulo: 'Vale-Compras de R$ 15,00', custoPontos: 750, estoque: 5 },
            { id: 30, parceiro: 'EcoLuz Solar', titulo: 'Desconto na Conta de Energia', custoPontos: 1200, estoque: 0 }
        ]);
    }
}

function renderizarOfertas(ofertas) {
    const grid = document.getElementById('grid-ofertas');
    grid.innerHTML = '';
    ofertas.forEach(oferta => {
        const card = document.createElement('div');
        card.className = 'card-oferta';
        const esgotada = oferta.estoque === 0;
        card.innerHTML = `
            <div>
                <h3>${oferta.titulo}</h3>
                <p class="parceiro">${oferta.parceiro}</p>
                <div class="pontos-tag">${oferta.custoPontos} pontos</div>
            </div>
            <button class="btn-primary" ${esgotada ? 'disabled style="opacity:0.5; cursor:not-allowed;"' : ''} onclick="abrirModalResgate(${oferta.id}, '${oferta.titulo}', ${oferta.custoPontos})">
                ${esgotada ? 'Esgotado' : 'Resgatar Benefício'}
            </button>
        `;
        grid.appendChild(card);
    });
}

// 3. Modal de Resgate e Confirmacao
function abrirModalResgate(id, titulo, pontos) {
    ofertaSelecionadaId = id;
    document.getElementById('modal-resgate-texto').textContent = `Deseja resgatar "${titulo}" por ${pontos} pontos?`;
    document.getElementById('modal-resgate').classList.remove('oculto');
    document.getElementById('btn-confirmar-resgate').onclick = executarResgate;
}

function fecharModalResgate() {
    document.getElementById('modal-resgate').classList.add('oculto');
    ofertaSelecionadaId = null;
}

async function executarResgate() {
    if (!ofertaSelecionadaId) return;
    fecharModalResgate();

    try {
        const response = await fetch(`${API_BASE_URL}/resgates`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usuarioId: 1, ofertaId: ofertaSelecionadaId, tipo: 'CUPOM' })
        });

        const data = await response.json();

        if (response.ok && data.status === 'CONCLUIDO') {
            document.getElementById('codigo-voucher').textContent = data.codigoVoucher;
            document.getElementById('modal-voucher').classList.remove('oculto');
            exibirToast('Resgate realizado com sucesso!', 'sucesso');
        } else {
            exibirToast(data.mensagem || 'Erro ao realizar resgate.', 'erro');
        }
    } catch (e) {
        exibirToast('Não foi possível conectar ao backend Mandabaa.', 'erro');
    }
}

function fecharModalVoucher() {
    document.getElementById('modal-voucher').classList.add('oculto');
    carregarCarteira();
}

// 4. Carregar Campanhas (US07)
async function carregarCampanhas() {
    const grid = document.getElementById('grid-campanhas');
    try {
        const response = await fetch(`${API_BASE_URL}/campanhas`);
        if (response.ok) {
            const campanhas = await response.json();
            renderizarCampanhas(campanhas);
            return;
        }
    } catch (e) {
        // Fallback demonstrativo
        renderizarCampanhas([
            { id: 1, titulo: 'Semana do PET em Dobro', descricao: 'Entregue garrafas PET e ganhe o dobro de pontos!', multiplicadorBonus: 2, status: 'ATIVA' },
            { id: 2, titulo: 'Bônus de Início de Semestre', descricao: 'Campanha de incentivo aos calouros da universidade.', multiplicadorBonus: 1.5, status: 'EXPIRADA' }
        ]);
    }
}

function renderizarCampanhas(campanhas) {
    const grid = document.getElementById('grid-campanhas');
    grid.innerHTML = '';
    campanhas.forEach(camp => {
        const card = document.createElement('div');
        card.className = 'card-campanha';
        const ativa = camp.status === 'ATIVA';
        card.innerHTML = `
            <div>
                <span class="badge ${ativa ? 'badge-ativa' : 'badge-expirada'}">${camp.status}</span>
                <h3 style="margin-top:0.5rem">${camp.titulo}</h3>
                <p style="color:var(--text-muted); font-size:0.9rem; margin-top:0.25rem">${camp.descricao || ''}</p>
            </div>
            <div style="margin-top:1rem; font-weight:600; color:var(--primary)">
                Bônus: ${camp.multiplicadorBonus}x pontos
            </div>
        `;
        grid.appendChild(card);
    });
}

function exibirToast(mensagem, tipo) {
    const toast = document.getElementById('toast');
    toast.textContent = mensagem;
    toast.className = `toast ${tipo}`;
    setTimeout(() => {
        toast.className = 'toast oculto';
    }, 3500);
}

// Inicializar na carteira
document.addEventListener('DOMContentLoaded', () => {
    carregarCarteira();
});
