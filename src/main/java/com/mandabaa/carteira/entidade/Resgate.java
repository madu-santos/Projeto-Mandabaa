package com.mandabaa.carteira.entidade;

import com.mandabaa.parceiros.entidade.Oferta;
import com.mandabaa.usuarios.entidade.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registro de uma troca de pontos por benefício (módulo "Carteira e Recompensas").
 *
 * Situações previstas na US03 do Backlog v3:
 * - Para CUPOM: "DISPONIVEL", "UTILIZADO" ou "EXPIRADO" (CA08)
 * - Para CASHBACK: "PAGAMENTO_PENDENTE" ou "PAGAMENTO_CONFIRMADO" (CA09)
 */
@Entity
@Table(name = "resgate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resgate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferta_id")
    private Oferta oferta;

    /** "CUPOM" ou "CASHBACK". */
    @Column(nullable = false)
    private String tipo;

    /** Cupom: DISPONIVEL / UTILIZADO / EXPIRADO. Cashback: PAGAMENTO_PENDENTE / PAGAMENTO_CONFIRMADO. */
    @Column(nullable = false)
    private String situacao;

    @Column(name = "codigo_comprovante")
    private String codigoComprovante;

    /** Chave ou forma de recebimento informada no resgate de cashback. */
    @Column(name = "forma_recebimento")
    private String formaRecebimento;

    @Column(name = "pontos_descontados", nullable = false)
    private Integer pontosDescontados;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
}
