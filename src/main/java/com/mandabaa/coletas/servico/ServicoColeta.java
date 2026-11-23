package com.mandabaa.coletas.servico;

import com.mandabaa.coletas.dto.ColetaDTO;
import com.mandabaa.coletas.dto.HistoricoColetasDTO;
import com.mandabaa.coletas.entidade.Coleta;
import com.mandabaa.coletas.repositorio.RepositorioColeta;
import com.mandabaa.comum.excecao.RecursoNaoEncontradoExcecao;
import com.mandabaa.usuarios.entidade.Usuario;
import com.mandabaa.usuarios.repositorio.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço responsável pelo registro e histórico de coletas de PET (US01).
 */
@Service
@RequiredArgsConstructor
public class ServicoColeta {

    public static final int PONTOS_POR_KG = 50;

    private final RepositorioColeta repositorioColeta;
    private final RepositorioUsuario repositorioUsuario;

    @Transactional
    public ColetaDTO registrarEntregaValidada(Long usuarioId, String pontoColeta, Double pesoKg, Integer bonus) {
        validarParametrosEntrega(usuarioId, pesoKg);

        Usuario usuario = buscarUsuarioOuFalhar(usuarioId);

        int bonusFinal = normalizarBonus(bonus);
        int pontosBase = calcularPontosBase(pesoKg);
        int totalPontosRecebidos = pontosBase + bonusFinal;

        creditarPontosAoUsuario(usuario, totalPontosRecebidos);

        Coleta coleta = criarNovaColetaValidada(usuario, pontoColeta, pesoKg, pontosBase, bonusFinal);
        Coleta coletaSalva = repositorioColeta.save(coleta);

        return converterParaDTO(coletaSalva);
    }

    @Transactional(readOnly = true)
    public HistoricoColetasDTO consultarHistorico(Long usuarioId) {
        validarExistenciaUsuario(usuarioId);

        List<Coleta> coletasValidadas = repositorioColeta
                .findByUsuarioIdAndValidadaTrueOrderByDataEntregaDesc(usuarioId);

        double totalPetKg = calcularTotalPetKg(coletasValidadas);
        int totalPontosRecebidos = calcularTotalPontos(coletasValidadas);
        int totalBonusRecebidos = calcularTotalBonus(coletasValidadas);
        List<ColetaDTO> listaDTO = coletasValidadas.stream()
                .map(this::converterParaDTO)
                .toList();

        return HistoricoColetasDTO.builder()
                .usuarioId(usuarioId)
                .totalPetKg(totalPetKg)
                .totalPontosRecebidos(totalPontosRecebidos)
                .totalBonusRecebidos(totalBonusRecebidos)
                .coletas(listaDTO)
                .build();
    }

    private void validarParametrosEntrega(Long usuarioId, Double pesoKg) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("O ID do usuário é obrigatório para registrar a entrega.");
        }
        if (pesoKg == null || pesoKg <= 0.0) {
            throw new IllegalArgumentException("O peso da entrega deve ser maior que zero.");
        }
    }

    private void validarExistenciaUsuario(Long usuarioId) {
        if (usuarioId == null || !repositorioUsuario.existsById(usuarioId)) {
            throw new RecursoNaoEncontradoExcecao("Usuário não encontrado: " + usuarioId);
        }
    }

    private Usuario buscarUsuarioOuFalhar(Long usuarioId) {
        return repositorioUsuario.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoExcecao("Usuário não encontrado: " + usuarioId));
    }

    private int normalizarBonus(Integer bonus) {
        return bonus != null ? Math.max(bonus, 0) : 0;
    }

    private int calcularPontosBase(Double pesoKg) {
        return (int) Math.round(pesoKg * PONTOS_POR_KG);
    }

    private void creditarPontosAoUsuario(Usuario usuario, int pontosGanhos) {
        int saldoAtual = usuario.getSaldoPontos() != null ? usuario.getSaldoPontos() : 0;
        usuario.setSaldoPontos(saldoAtual + pontosGanhos);
        repositorioUsuario.save(usuario);
    }

    private Coleta criarNovaColetaValidada(Usuario usuario, String pontoColeta, Double pesoKg, int pontosBase, int bonus) {
        return Coleta.builder()
                .usuario(usuario)
                .pontoColeta(pontoColeta != null && !pontoColeta.isBlank() ? pontoColeta : "Ponto Central")
                .dataEntrega(LocalDate.now())
                .pesoKg(pesoKg)
                .pontosRecebidos(pontosBase)
                .bonusRecebido(bonus)
                .validada(true)
                .build();
    }

    private ColetaDTO converterParaDTO(Coleta coleta) {
        return ColetaDTO.builder()
                .id(coleta.getId())
                .pontoColeta(coleta.getPontoColeta())
                .dataEntrega(coleta.getDataEntrega())
                .pesoKg(coleta.getPesoKg())
                .pontosRecebidos(coleta.getPontosRecebidos())
                .bonusRecebido(coleta.getBonusRecebido())
                .build();
    }

    private double calcularTotalPetKg(List<Coleta> coletas) {
        double total = coletas.stream()
                .mapToDouble(Coleta::getPesoKg)
                .sum();
        return Math.round(total * 100.0) / 100.0;
    }

    private int calcularTotalPontos(List<Coleta> coletas) {
        return coletas.stream()
                .mapToInt(Coleta::getPontosRecebidos)
                .sum();
    }

    private int calcularTotalBonus(List<Coleta> coletas) {
        return coletas.stream()
                .mapToInt(c -> c.getBonusRecebido() != null ? c.getBonusRecebido() : 0)
                .sum();
    }
}
