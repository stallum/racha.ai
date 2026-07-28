package time.domain;

/**
 * Regra de formação de times de um racha, independente do algoritmo
 * usado para distribuir os jogadores (isso é responsabilidade de
 * {@link EstrategiaSorteio}).
 *
 * Cobre os dois cenários mais comuns:
 *  - tamanho de time fixo (ex: sempre times de 4) -> a quantidade de
 *    times é calculada a partir do total de jogadores confirmados;
 *  - quantidade de times fixa (ex: sempre 3 times) -> o tamanho de
 *    cada time é calculado a partir do total de jogadores confirmados.
 */
public class RegraSorteio {

    private final int quantidadeTimes;
    private final int tamanhoTime;

    private RegraSorteio(int quantidadeTimes, int tamanhoTime) {
        this.quantidadeTimes = quantidadeTimes;
        this.tamanhoTime = tamanhoTime;
    }

    public static RegraSorteio porTamanhoTime(int tamanhoTime) {
        if (tamanhoTime <= 0) {
            throw new IllegalArgumentException("Tamanho de time deve ser maior que zero.");
        }
        return new RegraSorteio(0, tamanhoTime);
    }

    public static RegraSorteio porQuantidadeTimes(int quantidadeTimes) {
        if (quantidadeTimes <= 0) {
            throw new IllegalArgumentException("Quantidade de times deve ser maior que zero.");
        }
        return new RegraSorteio(quantidadeTimes, 0);
    }

    public int calcularQuantidadeTimes(int totalJogadores) {
        if (quantidadeTimes > 0) {
            return quantidadeTimes;
        }
        return (int) Math.ceil((double) totalJogadores / tamanhoTime);
    }

    public int calcularTamanhoTime(int totalJogadores) {
        if (tamanhoTime > 0) {
            return tamanhoTime;
        }
        return (int) Math.ceil((double) totalJogadores / quantidadeTimes);
    }
}
