package time.domain;

import core.domain.Usuario;
import jogador.domain.Jogador;
import jogador.domain.PosicaoEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SorteioEquilibradoStrategyTest {

    @Test
    void deveDistribuirJogadoresRespeitandoAQuantidadeDeTimesDaRegra() {
        List<Jogador> jogadores = criarJogadores(new int[]{5, 4, 4, 3, 3, 2, 2, 1});
        RegraSorteio regra = RegraSorteio.porQuantidadeTimes(2);

        List<Time> times = new SorteioEquilibradoStrategy().distribuirJogadores(jogadores, regra);

        assertEquals(2, times.size());
        assertEquals(8, times.get(0).getJogadores().size() + times.get(1).getJogadores().size());
    }

    @Test
    void nivelMedioEntreTimesDeveFicarEquilibrado() {
        // 8 jogadores com estrelas bem variadas, 2 times -> snake draft deve
        // deixar o total de estrelas dos dois times bem próximo.
        List<Jogador> jogadores = criarJogadores(new int[]{5, 5, 4, 4, 2, 2, 1, 1});
        RegraSorteio regra = RegraSorteio.porQuantidadeTimes(2);

        List<Time> times = new SorteioEquilibradoStrategy().distribuirJogadores(jogadores, regra);

        int totalTimeA = times.get(0).getTotalEstrelas();
        int totalTimeB = times.get(1).getTotalEstrelas();

        assertTrue(Math.abs(totalTimeA - totalTimeB) <= 1,
                "Diferença de estrelas entre os times deveria ser no máximo 1, mas foi " + Math.abs(totalTimeA - totalTimeB));
    }

    @Test
    void nenhumJogadorDeveFicarDeForaDoSorteio() {
        List<Jogador> jogadores = criarJogadores(new int[]{3, 3, 3, 3, 3, 3, 3});
        RegraSorteio regra = RegraSorteio.porTamanhoTime(4);

        List<Time> times = new SorteioEquilibradoStrategy().distribuirJogadores(jogadores, regra);

        int totalDistribuido = times.stream().mapToInt(t -> t.getJogadores().size()).sum();
        assertEquals(jogadores.size(), totalDistribuido);
    }

    private List<Jogador> criarJogadores(int[] niveis) {
        List<Jogador> jogadores = new ArrayList<>();
        long id = 1;
        for (int nivel : niveis) {
            jogadores.add(new Jogador(new Usuario(id, "Jogador " + id, null, null),
                    PosicaoEnum.MEIO_CAMPO, nivel, true));
            id++;
        }
        return jogadores;
    }
}
