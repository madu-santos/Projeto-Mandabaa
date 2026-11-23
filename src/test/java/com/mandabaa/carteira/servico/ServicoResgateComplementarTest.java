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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes COMPLEMENTARES da US03 (Pessoa 4 — QA, Sprint 5.1).
 *
 * Fecham as lacunas deixadas pelos 4 testes originais de {@link ServicoResgateTest}:
 *  - CA03 cobria só "esgotada" (faltavam "inativa" e "vencida");
 *  - os erros não provavam que NADA foi gravado (saldo, estoque e resgate);
 *  - não havia teste de cashback sem chave PIX, tipo divergente, histórico de resgates etc.
 *
 * Os testes originais foram mantidos intactos em ServicoResgateTest.
 */
@ExtendWith(MockitoExtension.class)
class ServicoResgateComplementarTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @Mock
    private RepositorioOferta repositorioOferta;

    @Mock
    private RepositorioResgate repositorioResgate;

    @InjectMocks
    private ServicoResgate servicoResgate;

    // ---------------------------------------------------------------- apoio (dados de teste)

    private Usuario usuarioComSaldo(int saldo) {
        return Usuario.builder().id(1L).nome("Alex").saldoPontos(saldo).build();
    }

    private Oferta ofertaCupom(long id, int custo, int estoque) {
        return Oferta.builder().id(id).titulo("Cupom 10%").tipo("CUPOM").custoPontos(custo)
                .estoque(estoque).ativa(true).validade(LocalDate.now().plusDays(30)).build();
    }

    private Oferta ofertaCashback(long id, int custo, int estoque) {
        return Oferta.builder().id(id).titulo("Cashback R$ 5,00").tipo("CASHBACK").custoPontos(custo)
                .estoque(estoque).ativa(true).valorCashback(new BigDecimal("5.00"))
                .validade(LocalDate.now().plusDays(30)).build();
    }

    private void prepararBusca(Usuario usuario, Oferta oferta) {
        when(repositorioUsuario.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(repositorioOferta.findById(oferta.getId())).thenReturn(Optional.of(oferta));
    }

    private void prepararSalvarResgate() {
        when(repositorioResgate.save(any(Resgate.class))).thenAnswer(i -> {
            Resgate r = i.getArgument(0);
            r.setId(100L);
            return r;
        });
    }

    private void verificarNadaFoiGravado() {
        verify(repositorioUsuario, never()).save(any());
        verify(repositorioOferta, never()).save(any());
        verify(repositorioResgate, never()).save(any());
    }

    // ---------------------------------------------------------------- CA01 — resgate de cupom

    @Test
    @DisplayName("US03 - CA01: Deve gravar o resgate de cupom com tipo, situação, pontos e comprovante corretos")
    void deveGravarResgateDeCupomComDadosCorretos() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        prepararBusca(usuario, oferta);
        prepararSalvarResgate();

        // WHEN
        RespostaResgateDTO resposta = servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, "CUPOM", null));

        // THEN
        ArgumentCaptor<Resgate> captor = ArgumentCaptor.forClass(Resgate.class);
        verify(repositorioResgate).save(captor.capture());
        Resgate gravado = captor.getValue();
        assertSame(usuario, gravado.getUsuario());
        assertSame(oferta, gravado.getOferta());
        assertEquals("CUPOM", gravado.getTipo());
        assertEquals("DISPONIVEL", gravado.getSituacao());
        assertEquals(500, gravado.getPontosDescontados());
        assertNull(gravado.getFormaRecebimento());
        assertNotNull(gravado.getCriadoEm());
        assertFalse(gravado.getCodigoComprovante().isBlank());
        assertEquals(gravado.getCodigoComprovante(), resposta.getCodigoComprovante());
        assertEquals(100L, resposta.getResgateId());
    }

    @Test
    @DisplayName("US03 - CA01: Dois resgates devem gerar códigos de comprovante diferentes (único)")
    void deveGerarCodigosDeComprovanteDiferentesEmResgatesDiferentes() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(2000);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        prepararBusca(usuario, oferta);
        prepararSalvarResgate();
        SolicitacaoResgateDTO solicitacao = new SolicitacaoResgateDTO(1L, 20L, "CUPOM", null);

        // WHEN
        String codigo1 = servicoResgate.realizarResgate(solicitacao).getCodigoComprovante();
        String codigo2 = servicoResgate.realizarResgate(solicitacao).getCodigoComprovante();

        // THEN
        assertNotEquals(codigo1, codigo2);
        assertEquals(1000, usuario.getSaldoPontos());
        assertEquals(3, oferta.getEstoque());
    }

    // ---------------------------------------------------------------- CA02 — saldo insuficiente

    @Test
    @DisplayName("US03 - CA02: Mensagem exata 'Saldo insuficiente. Faltam 300 pontos.' e nada é gravado")
    void deveInformarMensagemExataDePontosFaltantesESemGravarNada() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(200);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        prepararBusca(usuario, oferta);

        // WHEN
        SaldoInsuficienteExcecao ex = assertThrows(SaldoInsuficienteExcecao.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, "CUPOM", null)));

        // THEN
        assertEquals("Saldo insuficiente. Faltam 300 pontos.", ex.getMessage());
        assertEquals(200, usuario.getSaldoPontos());
        assertEquals(5, oferta.getEstoque());
        verificarNadaFoiGravado();
    }

    @Test
    @DisplayName("US03 - CA02 (limite): Saldo exatamente igual ao custo permite o resgate e zera o saldo")
    void devePermitirResgateQuandoSaldoForExatamenteOCusto() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(500);
        Oferta oferta = ofertaCupom(20L, 500, 1);
        prepararBusca(usuario, oferta);
        prepararSalvarResgate();

        // WHEN
        RespostaResgateDTO resposta = servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, "CUPOM", null));

        // THEN
        assertEquals(0, resposta.getNovoSaldo());
        assertEquals(0, usuario.getSaldoPontos());
        assertEquals(0, oferta.getEstoque());
    }

    // ---------------------------------------------------------------- CA03 — oferta indisponível

    @Test
    @DisplayName("US03 - CA03: Oferta esgotada não altera saldo, estoque nem grava nada")
    void deveManterTudoIntactoQuandoOfertaEsgotada() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCupom(30L, 500, 0);
        prepararBusca(usuario, oferta);

        // WHEN
        OfertaIndisponivelExcecao ex = assertThrows(OfertaIndisponivelExcecao.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 30L, "CUPOM", null)));

        // THEN
        assertTrue(ex.getMessage().contains("esgotada"));
        assertEquals(1000, usuario.getSaldoPontos());
        assertEquals(0, oferta.getEstoque());
        verificarNadaFoiGravado();
    }

    @Test
    @DisplayName("US03 - CA03: Deve lançar OfertaIndisponivelExcecao quando oferta estiver inativa")
    void deveLancarExcecaoQuandoOfertaInativa() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        oferta.setAtiva(false);
        prepararBusca(usuario, oferta);

        // WHEN
        OfertaIndisponivelExcecao ex = assertThrows(OfertaIndisponivelExcecao.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, "CUPOM", null)));

        // THEN
        assertTrue(ex.getMessage().contains("inativa"));
        assertEquals(1000, usuario.getSaldoPontos());
        assertEquals(5, oferta.getEstoque());
        verificarNadaFoiGravado();
    }

    @Test
    @DisplayName("US03 - CA03: Deve lançar OfertaIndisponivelExcecao quando oferta estiver vencida")
    void deveLancarExcecaoQuandoOfertaVencida() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        oferta.setValidade(LocalDate.now().minusDays(1));
        prepararBusca(usuario, oferta);

        // WHEN
        OfertaIndisponivelExcecao ex = assertThrows(OfertaIndisponivelExcecao.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, "CUPOM", null)));

        // THEN
        assertTrue(ex.getMessage().contains("vencida"));
        assertEquals(1000, usuario.getSaldoPontos());
        assertEquals(5, oferta.getEstoque());
        verificarNadaFoiGravado();
    }

    @Test
    @DisplayName("US03 - CA03 (limite): Oferta que vence hoje ainda pode ser resgatada")
    void devePermitirResgateQuandoOfertaVenceHoje() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        oferta.setValidade(LocalDate.now());
        prepararBusca(usuario, oferta);
        prepararSalvarResgate();

        // WHEN
        RespostaResgateDTO resposta = servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, "CUPOM", null));

        // THEN
        assertEquals("DISPONIVEL", resposta.getSituacao());
        assertEquals(500, resposta.getNovoSaldo());
    }

    @Test
    @DisplayName("US03 - CA03: Oferta inexistente deve lançar OfertaIndisponivelExcecao sem alterar o saldo")
    void deveLancarExcecaoQuandoOfertaNaoExistir() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        when(repositorioUsuario.findById(1L)).thenReturn(Optional.of(usuario));
        when(repositorioOferta.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(OfertaIndisponivelExcecao.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 99L, "CUPOM", null)));
        assertEquals(1000, usuario.getSaldoPontos());
        verificarNadaFoiGravado();
    }

    // ---------------------------------------------------------------- CA04 — cashback

    @Test
    @DisplayName("US03 - CA04: Cashback deve debitar pontos, reduzir estoque e guardar a chave PIX sem espaços")
    void deveDebitarPontosEGuardarChavePixNoCashback() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCashback(10L, 800, 10);
        prepararBusca(usuario, oferta);
        prepararSalvarResgate();

        // WHEN
        RespostaResgateDTO resposta = servicoResgate.realizarResgate(
                new SolicitacaoResgateDTO(1L, 10L, "CASHBACK", "  alex@email.com  "));

        // THEN
        ArgumentCaptor<Resgate> captor = ArgumentCaptor.forClass(Resgate.class);
        verify(repositorioResgate).save(captor.capture());
        Resgate gravado = captor.getValue();
        assertEquals("CASHBACK", gravado.getTipo());
        assertEquals("PAGAMENTO_PENDENTE", gravado.getSituacao());
        assertEquals("alex@email.com", gravado.getFormaRecebimento());
        assertEquals(800, gravado.getPontosDescontados());
        assertEquals(200, usuario.getSaldoPontos());
        assertEquals(9, oferta.getEstoque());
        assertEquals(200, resposta.getNovoSaldo());
        assertTrue(resposta.getMensagem().toLowerCase().contains("pendente"));
    }

    @ParameterizedTest(name = "chave PIX = [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("US03 - CA04: Cashback sem chave PIX deve ser recusado sem debitar pontos")
    void deveRecusarCashbackSemChavePix(String chavePix) {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCashback(10L, 800, 10);
        prepararBusca(usuario, oferta);

        // WHEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 10L, "CASHBACK", chavePix)));

        // THEN
        assertTrue(ex.getMessage().contains("PIX"));
        assertEquals(1000, usuario.getSaldoPontos());
        assertEquals(10, oferta.getEstoque());
        verificarNadaFoiGravado();
    }

    // ---------------------------------------------------------------- validações de entrada

    @Test
    @DisplayName("US03 (regra do plano): Tipo da solicitação diferente do tipo da oferta deve ser recusado")
    void deveRecusarQuandoTipoDaSolicitacaoNaoCorrespondeAOferta() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        prepararBusca(usuario, oferta);

        // WHEN / THEN
        assertThrows(IllegalArgumentException.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, "CASHBACK", "alex@email.com")));
        assertEquals(1000, usuario.getSaldoPontos());
        assertEquals(5, oferta.getEstoque());
        verificarNadaFoiGravado();
    }

    @Test
    @DisplayName("US03 (regra do plano): Tipo de resgate desconhecido deve ser recusado")
    void deveRecusarTipoDeResgateDesconhecido() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        prepararBusca(usuario, oferta);

        // WHEN / THEN
        assertThrows(IllegalArgumentException.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, "DOACAO", null)));
        verificarNadaFoiGravado();
    }

    @Test
    @DisplayName("US03: Usuário inexistente deve lançar RecursoNaoEncontradoExcecao sem gravar nada")
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        // GIVEN
        when(repositorioUsuario.findById(999L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThrows(RecursoNaoEncontradoExcecao.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(999L, 20L, "CUPOM", null)));
        verificarNadaFoiGravado();
    }

    @Test
    @DisplayName("US03: Solicitação nula, sem ids ou sem tipo deve ser recusada sem consultar o banco")
    void deveRecusarSolicitacaoIncompleta() {
        // WHEN / THEN
        assertThrows(IllegalArgumentException.class, () -> servicoResgate.realizarResgate(null));
        assertThrows(IllegalArgumentException.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(null, 20L, "CUPOM", null)));
        assertThrows(IllegalArgumentException.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, null, "CUPOM", null)));
        assertThrows(IllegalArgumentException.class,
                () -> servicoResgate.realizarResgate(new SolicitacaoResgateDTO(1L, 20L, " ", null)));
        verifyNoInteractions(repositorioUsuario, repositorioOferta, repositorioResgate);
    }

    // ---------------------------------------------------------------- histórico de resgates

    @Test
    @DisplayName("US03 (histórico): Deve listar os resgates do usuário convertidos para DTO")
    void deveListarResgatesDoUsuario() {
        // GIVEN
        Usuario usuario = usuarioComSaldo(1000);
        Oferta oferta = ofertaCupom(20L, 500, 5);
        oferta.setParceiro("Supermercado Verde");
        LocalDateTime agora = LocalDateTime.now();
        Resgate resgate = Resgate.builder().id(7L).usuario(usuario).oferta(oferta).tipo("CUPOM")
                .situacao("DISPONIVEL").codigoComprovante("ABC123").pontosDescontados(500).criadoEm(agora).build();
        when(repositorioUsuario.existsById(1L)).thenReturn(true);
        when(repositorioResgate.findByUsuarioIdOrderByCriadoEmDesc(1L)).thenReturn(List.of(resgate));

        // WHEN
        List<ItemResgateDTO> itens = servicoResgate.listarResgatesDoUsuario(1L);

        // THEN
        assertEquals(1, itens.size());
        ItemResgateDTO item = itens.get(0);
        assertEquals(7L, item.getResgateId());
        assertEquals("Cupom 10%", item.getTituloOferta());
        assertEquals("Supermercado Verde", item.getParceiro());
        assertEquals("DISPONIVEL", item.getSituacao());
        assertEquals("ABC123", item.getCodigoComprovante());
        assertEquals(500, item.getPontosDescontados());
        assertEquals(agora, item.getCriadoEm());
    }

    @Test
    @DisplayName("US03 (histórico): Listar resgates de usuário inexistente deve lançar RecursoNaoEncontradoExcecao")
    void deveLancarExcecaoAoListarResgatesDeUsuarioInexistente() {
        // GIVEN
        when(repositorioUsuario.existsById(999L)).thenReturn(false);

        // WHEN / THEN
        assertThrows(RecursoNaoEncontradoExcecao.class, () -> servicoResgate.listarResgatesDoUsuario(999L));
        verify(repositorioResgate, never()).findByUsuarioIdOrderByCriadoEmDesc(any());
    }
}
