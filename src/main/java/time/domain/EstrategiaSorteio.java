package time.domain;

import jogador.domain.Jogador;

import java.util.List;

/**
 * Define o contrato para diferentes algoritmos de distribuição de
 * jogadores em equipes (padrão Strategy). Cada implementação decide
 * *como* distribuir (por habilidade, por posição, aleatório, etc.),
 * enquanto a {@link RegraSorteio} decide *quantos* times / de que
 * tamanho — assim novas estratégias se encaixam em qualquer cenário
 * de racha sem precisar mudar a regra de formação de times.
 */
public interface EstrategiaSorteio {
    List<Time> distribuirJogadores(List<Jogador> jogadores, RegraSorteio regra);
}
