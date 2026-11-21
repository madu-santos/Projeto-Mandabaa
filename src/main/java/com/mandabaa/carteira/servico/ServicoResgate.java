package com.mandabaa.carteira.servico;

import com.mandabaa.carteira.dto.ItemResgateDTO;
import com.mandabaa.carteira.dto.RespostaResgateDTO;
import com.mandabaa.carteira.dto.SolicitacaoResgateDTO;
import com.mandabaa.carteira.entidade.Resgate;
import com.mandabaa.carteira.repositorio.RepositorioResgate;
import com.mandabaa.comum.excecao.OfertaIndisponivelExcecao;
import com.mandabaa.comum.excecao.RecursoNaoEncontradoExcecao;
import com.mandabaa.comum.excecao.SaldoInsuficienteExcecao;
import com.mandabaa.parceiros.entidade.Oferta;
import com.mandabaa.parceiros.repositorio.RepositorioOferta;
import com.mandabaa.usuarios.entidade.Usuario;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ServicoResgate {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioOferta repositorioOferta;
    private final RepositorioResgate repositorioResgate;

    /** Valida, registra o resgate e atualiza saldo e estoque de forma atômica. */
    @Transactional
    public RespostaResgateDTO realizarResgate(SolicitacaoResgateDTO solicitacao) {
        validarSolicitacao(solicitacao);

        Usuario usuario = buscarUsuario(solicitacao.getUsuarioId());
        Oferta oferta = buscarOferta(solicitacao.getOfertaId());
        validarDisponibilidade(oferta);
        validarTipoSolicitacao(solicitacao.getTipo(), oferta.getTipo());
        validarChavePix(solicitacao.getTipo(), solicitacao.getChavePix());
        validarSaldo(usuario, oferta);

        int novoSaldo = usuario.getSaldoPontos() - oferta.getCustoPontos();
        String situacao = definirSituacaoInicial(solicitacao.getTipo());
        String codigoComprovante = gerarCodigoComprovante();

        Resgate resgate = criarResgate(usuario, oferta, solicitacao, situacao, codigoComprovante);

        usuario.setSaldoPontos(novoSaldo);
        oferta.setEstoque(oferta.getEstoque() - 1);

        repositorioUsuario.save(usuario);
        repositorioOferta.save(oferta);
        Resgate resgateSalvo = repositorioResgate.save(resgate);

        return RespostaResgateDTO.builder()
                .resgateId(resgateSalvo.getId())
                .situacao(situacao)
                .codigoComprovante(codigoComprovante)
                .novoSaldo(novoSaldo)
                .mensagem(criarMensagem(solicitacao.getTipo(), oferta, novoSaldo))
                .build();
    }



    @Transactional(readOnly = true)
    public List<ItemResgateDTO> listarResgatesDoUsuario(Long usuarioId) {
        if (!repositorioUsuario.existsById(usuarioId)) {
            throw new RecursoNaoEncontradoExcecao("Usuário não encontrado: " + usuarioId);
        }

        return repositorioResgate.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)
                .stream()
                .map(this::converterParaDTO)
                .toList();
    }




    private void validarSolicitacao(SolicitacaoResgateDTO solicitacao) {
        if (solicitacao == null) {
            throw new IllegalArgumentException("A solicitação de resgate não pode ser nula.");
        }
        if (solicitacao.getUsuarioId() == null || solicitacao.getOfertaId() == null) {
            throw new IllegalArgumentException("Informe o usuário e a oferta para realizar o resgate.");
        }
        if (solicitacao.getTipo() == null || solicitacao.getTipo().isBlank()) {
            throw new IllegalArgumentException("Informe o tipo de resgate (CUPOM ou CASHBACK).");
        }
    }


    private Usuario buscarUsuario(Long usuarioId) {
        return repositorioUsuario.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Usuário não encontrado: " + usuarioId));
    }


    private Oferta buscarOferta(Long ofertaId) {
        return repositorioOferta.findById(ofertaId)
                .orElseThrow(() -> new OfertaIndisponivelExcecao("Oferta não encontrada ou indisponível."));
    }


    private void validarDisponibilidade(Oferta oferta) {
        if (oferta.getAtiva() == null || !oferta.getAtiva()) {
            throw new OfertaIndisponivelExcecao("Esta oferta está inativa.");
        }
        if (oferta.getEstoque() == null || oferta.getEstoque() <= 0) {
            throw new OfertaIndisponivelExcecao("Esta oferta está esgotada.");
        }
        if (oferta.getValidade() != null && oferta.getValidade().isBefore(LocalDate.now())) {
            throw new OfertaIndisponivelExcecao("Esta oferta está vencida.");
        }
        if (oferta.getCustoPontos() == null || oferta.getCustoPontos() <= 0) {
            throw new OfertaIndisponivelExcecao("O custo da oferta está inválido.");
        }
    }


    private void validarTipoSolicitacao(String tipoSolicitacao, String tipoOferta) {
        String tipoPedido = normalizarTipo(tipoSolicitacao);
        String tipoCadastrado = normalizarTipo(tipoOferta);
        if (!tipoPedido.equals("CUPOM") && !tipoPedido.equals("CASHBACK")) {
            throw new IllegalArgumentException("Tipo de resgate inválido. Use CUPOM ou CASHBACK.");
        }
        if (!tipoPedido.equals(tipoCadastrado)) {
            throw new IllegalArgumentException("O tipo de resgate não corresponde ao tipo da oferta.");
        }
    }


    private void validarChavePix(String tipo, String chavePix) {
        if (normalizarTipo(tipo).equals("CASHBACK") && (chavePix == null || chavePix.isBlank())) {
            throw new IllegalArgumentException("Informe a chave PIX para solicitar cashback.");
        }
    }


    private void validarSaldo(Usuario usuario, Oferta oferta) {
        if (usuario.getSaldoPontos() == null || usuario.getSaldoPontos() < oferta.getCustoPontos()) {
            int saldoAtual = usuario.getSaldoPontos() == null ? 0 : usuario.getSaldoPontos();
            int faltantes = Math.max(0, oferta.getCustoPontos() - saldoAtual);
            throw new SaldoInsuficienteExcecao("Saldo insuficiente. Faltam " + faltantes + " pontos.");
        }
    }



    private String definirSituacaoInicial(String tipo) {
        return normalizarTipo(tipo).equals("CUPOM") ? "DISPONIVEL" : "PAGAMENTO_PENDENTE";
    }


    private String gerarCodigoComprovante() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }


    private Resgate criarResgate(Usuario usuario, Oferta oferta, SolicitacaoResgateDTO solicitacao,
                                 String situacao, String codigoComprovante) {
        String formaRecebimento = normalizarTipo(solicitacao.getTipo()).equals("CASHBACK")
                ? solicitacao.getChavePix().trim() : null;

        return Resgate.builder()
                .usuario(usuario)
                .oferta(oferta)
                .tipo(normalizarTipo(solicitacao.getTipo()))
                .situacao(situacao)
                .codigoComprovante(codigoComprovante)
                .formaRecebimento(formaRecebimento)
                .pontosDescontados(oferta.getCustoPontos())
                .criadoEm(LocalDateTime.now())
                .build();
    }


    private String criarMensagem(String tipo, Oferta oferta, int novoSaldo) {
        if (normalizarTipo(tipo).equals("CUPOM")) {
            return "Resgate realizado com sucesso! Seu cupom está disponível. Saldo: " + novoSaldo + " pontos.";
        }
        return "Cashback solicitado! O pagamento está pendente de confirmação. Saldo: " + novoSaldo + " pontos.";
    }


    private ItemResgateDTO converterParaDTO(Resgate resgate) {
        return ItemResgateDTO.builder()
                .resgateId(resgate.getId())
                .tituloOferta(resgate.getOferta().getTitulo())
                .parceiro(resgate.getOferta().getParceiro())
                .tipo(resgate.getTipo())
                .situacao(resgate.getSituacao())
                .codigoComprovante(resgate.getCodigoComprovante())
                .formaRecebimento(resgate.getFormaRecebimento())
                .pontosDescontados(resgate.getPontosDescontados())
                .criadoEm(resgate.getCriadoEm())
                .build();
    }


    private String normalizarTipo(String tipo) {
        return tipo == null ? "" : tipo.trim().toUpperCase();
    }
}
