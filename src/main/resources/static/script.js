/*
 * SCRIPT FRONTEND — Mandabaa (Sprint 5 & 5.1)
 * Inclui:
 *   - Módulo de Autenticação (Entrada, Login, Cadastro e Logout)
 *   - US05: Consulta de Saldo e Carteira
 *   - US01: Histórico e Registro de Coletas de PET
 *   - US03: Benefícios, Resgates e Cashback
 */

const ENDERECO_API = window.location.protocol.startsWith('http')
    ? `${window.location.origin}/api`
    : 'http://localhost:8088/api';

const CHAVE_SESSAO = 'mandabaa_usuario_sessao';
let ofertaSelecionada = null;

/* ================================================================
   GERENCIAMENTO DE SESSÃO / USUÁRIO ATIVO
   ================================================================ */
function obterUsuarioLogado() {
    try {
        const dados = sessionStorage.getItem(CHAVE_SESSAO);
        return dados ? JSON.parse(dados) : null;
    } catch (_) {
        return null;
    }
}

function salvarSessao(usuario) {
    sessionStorage.setItem(CHAVE_SESSAO, JSON.stringify(usuario));
}

function limparSessao() {
    sessionStorage.removeItem(CHAVE_SESSAO);
}

function obterUsuarioId() {
    const usuario = obterUsuarioLogado();
    return usuario ? usuario.usuarioId : 1;
}

/* ================================================================
   CONTROLE DE TELAS (VISITANTE vs. LOGADO)
   ================================================================ */
function aplicarEstadoSessao() {
    const usuario = obterUsuarioLogado();

    const secaoVisitante = document.getElementById('secao-visitante');
    const secaoLogado = document.getElementById('secao-logado');
    const navLogado = document.getElementById('nav-logado');
    const acoesDeslogado = document.getElementById('acoes-deslogado');
    const acoesLogado = document.getElementById('acoes-logado');

    if (usuario) {
        // Usuário ESTÁ autenticado
        secaoVisitante.classList.add('oculto');
        secaoLogado.classList.remove('oculto');
        navLogado.classList.remove('oculto');
        acoesDeslogado.classList.add('oculto');
        acoesLogado.classList.remove('oculto');

        // Atualiza elementos visuais com dados do usuário
        document.getElementById('header-nome-usuario').textContent = usuario.nome || 'Reciclador';
        document.getElementById('carteira-usuario-chip').textContent = `👤 ${usuario.nome || 'Reciclador'}`;

        // Carrega dados da conta
        mostrarAba('carteira');
    } else {
        // Usuário NÃO está autenticado (mostrar tela inicial pública)
        secaoVisitante.classList.remove('oculto');
        secaoLogado.classList.add('oculto');
        navLogado.classList.add('oculto');
        acoesDeslogado.classList.remove('oculto');
        acoesLogado.classList.add('oculto');

        mostrarVisaoVisitante('inicio');
    }
}

function mostrarVisaoVisitante(visao) {
    document.querySelectorAll('.visao-visitante').forEach(v => v.classList.add('oculto'));
    const alvo = document.getElementById(`visao-${visao}`);
    if (alvo) alvo.classList.remove('oculto');
}

function irParaInicio() {
    const usuario = obterUsuarioLogado();
    if (usuario) {
        mostrarAba('carteira');
    } else {
        mostrarVisaoVisitante('inicio');
    }
}

/* ================================================================
   FLUXO DE AUTENTICAÇÃO (LOGIN, CADASTRO, LOGOUT)
   ================================================================ */
async function executarLogin(evento) {
    if (evento) evento.preventDefault();

    const login = document.getElementById('login-identificador').value.trim();
    const senha = document.getElementById('login-senha').value.trim();

    if (!login || !senha) {
        exibirAviso('Informe e-mail/CPF e senha.', 'erro');
        return;
    }

    try {
        const resposta = await fetch(`${ENDERECO_API}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ login, senha })
        });

        const texto = await resposta.text();
        const dados = texto ? JSON.parse(texto) : null;

        if (resposta.ok && dados && dados.usuarioId) {
            salvarSessao(dados);
            aplicarEstadoSessao();
            exibirAviso(`Bem-vindo(a) de volta, ${dados.nome}!`, 'sucesso');
        } else {
            const msg = (dados && dados.mensagem) || 'E-mail, CPF ou senha inválidos.';
            exibirAviso(msg, 'erro');
        }
    } catch (_) {
        exibirAviso('Não foi possível conectar ao servidor para efetuar login.', 'erro');
    }
}

async function executarCadastro(evento) {
    if (evento) evento.preventDefault();

    const nome = document.getElementById('cadastro-nome').value.trim();
    const email = document.getElementById('cadastro-email').value.trim();
    const cpf = document.getElementById('cadastro-cpf').value.trim();
    const senha = document.getElementById('cadastro-senha').value.trim();

    if (!nome || !email || !senha) {
        exibirAviso('Preencha todos os campos obrigatórios.', 'erro');
        return;
    }

    try {
        const resposta = await fetch(`${ENDERECO_API}/auth/cadastro`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, cpf, senha })
        });

        const texto = await resposta.text();
        const dados = texto ? JSON.parse(texto) : null;

        if (resposta.ok && dados && dados.usuarioId) {
            salvarSessao(dados);
            aplicarEstadoSessao();
            exibirAviso(`Conta criada com sucesso! Olá, ${dados.nome}!`, 'sucesso');
        } else {
            const msg = (dados && dados.mensagem) || 'Erro ao realizar cadastro.';
            exibirAviso(msg, 'erro');
        }
    } catch (_) {
        exibirAviso('Não foi possível conectar ao servidor para cadastrar.', 'erro');
    }
}

function fazerLogout() {
    limparSessao();
    aplicarEstadoSessao();
    exibirAviso('Você saiu da sua conta.', 'sucesso');
}

/* ================================================================
   NAVEGAÇÃO ENTRE ABAS DA ÁREA LOGADA
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
    const usuarioId = obterUsuarioId();
    let dados = {
        saldoPontos:      0,
        equivalenteReais: 0.00,
        regraCalculo:     '1 kg de PET = 50 pontos • 100 pontos = R$ 2,00',
        semSaldo:         true
    };

    try {
        const resposta = await fetch(`${ENDERECO_API}/carteira/${usuarioId}`);
        if (resposta.ok) {
            const texto = await resposta.text();
            if (texto) dados = JSON.parse(texto);
        }
    } catch (_) { /* fallback gracioso */ }

    document.getElementById('saldo-pontos').textContent =
        (dados.saldoPontos || 0).toLocaleString('pt-BR');
    document.getElementById('saldo-reais').textContent =
        `R$ ${Number(dados.equivalenteReais || 0).toFixed(2).replace('.', ',')}`;
    document.getElementById('saldo-area-beneficios').textContent =
        `${(dados.saldoPontos || 0).toLocaleString('pt-BR')} pontos`;
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
    const usuarioId = obterUsuarioId();
    let historico = {
        totalPetKg: 0.0,
        coletas: []
    };

    try {
        const resposta = await fetch(`${ENDERECO_API}/coletas/usuario/${usuarioId}`);
        if (resposta.ok) {
            const texto = await resposta.text();
            if (texto) historico = JSON.parse(texto);
        }
    } catch (_) { /* fallback gracioso */ }

    document.getElementById('total-pet-kg').textContent =
        `${Number(historico.totalPetKg || 0).toFixed(1).replace('.', ',')} kg`;

    const lista = document.getElementById('lista-coletas');
    lista.innerHTML = '';

    if (!historico.coletas || historico.coletas.length === 0) {
        lista.innerHTML = `
            <div class="estado-vazio">
                <span>🌿</span>
                <p>Nenhuma coleta registrada para este usuário ainda.</p>
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
    const usuarioId   = obterUsuarioId();
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
            body:    JSON.stringify({ usuarioId, pontoColeta, pesoKg, bonusRecebido: bonus })
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
    let ofertas = [];
    try {
        const resposta = await fetch(`${ENDERECO_API}/ofertas`);
        if (resposta.ok) {
            const texto = await resposta.text();
            if (texto) {
                const listaBanco = JSON.parse(texto);
                if (Array.isArray(listaBanco)) ofertas = listaBanco;
            }
        }
    } catch (_) { /* fallback */ }

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
    const usuarioId = obterUsuarioId();
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
                usuarioId,
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
    const usuarioId = obterUsuarioId();
    let resgates = [];

    try {
        const resposta = await fetch(`${ENDERECO_API}/resgates/usuario/${usuarioId}`);
        if (resposta.ok) {
            const texto = await resposta.text();
            if (texto) resgates = JSON.parse(texto);
        }
    } catch (_) { /* fallback */ }

    const lista = document.getElementById('lista-resgates');
    lista.innerHTML = '';

    if (!resgates.length) {
        lista.innerHTML = `
            <div class="estado-vazio">
                <span>🎟️</span>
                <p>Você ainda não resgatou nenhum benefício nesta conta.</p>
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
    aplicarEstadoSessao();
});
