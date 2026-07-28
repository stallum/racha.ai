package racha.domain;

/**
 * Ciclo de vida do racha:
 * AGENDADO -> SORTEADO -> EM_ANDAMENTO -> FINALIZADO
 * (com CANCELADO possível a partir de qualquer estado, exceto FINALIZADO)
 */
public enum StatusRachaEnum {
    AGENDADO,
    SORTEADO,
    EM_ANDAMENTO,
    FINALIZADO,
    CANCELADO
}
