package time.domain;

import jogador.domain.Jogador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementação concreta do algoritmo Snake Draft (Serpente):
 * ordena os jogadores por nível de habilidade (maior para o menor) e
 * os distribui em zigue-zague entre os times, para equilibrar o nível
 * médio de estrelas de cada equipe.
 */
public class SorteioEquilibradoStrategy implements EstrategiaSorteio {

    @Override
    public List<Time> distribuirJogadores(List<Jogador> jogadores, RegraSorteio regra) {
        int quantidadeTimes = regra.calcularQuantidadeTimes(jogadores.size());

        List<Time> times = criarTimes(quantidadeTimes);

        List<Jogador> ordenados = new ArrayList<>(jogadores);
        ordenados.sort(Comparator.comparingInt(Jogador::getNivelHabilidade).reversed());

        boolean indoParaFrente = true;
        int indiceTime = 0;

        for (Jogador jogador : ordenados) {
            times.get(indiceTime).adicionarJogador(jogador);

            if (indoParaFrente) {
                if (indiceTime == quantidadeTimes - 1) {
                    indoParaFrente = false;
                } else {
                    indiceTime++;
                }
            } else {
                if (indiceTime == 0) {
                    indoParaFrente = true;
                } else {
                    indiceTime--;
                }
            }
        }

        return times;
    }

    private List<Time> criarTimes(int quantidadeTimes) {
        List<Time> times = new ArrayList<>();
        for (int i = 0; i < quantidadeTimes; i++) {
            String nome = "Time " + (char) ('A' + i);
            times.add(new Time((long) (i + 1), nome, null));
        }
        return times;
    }
}
