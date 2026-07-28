package time.domain;

import core.domain.Usuario;
import jogador.domain.Jogador;
import jogador.domain.PosicaoEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeTest {

    @Test
    void nivelMedioDeveSerZeroSemJogadores() {
        Time time = new Time(1L, "Time A", "Azul");
        assertEquals(0.0, time.getNivelMedio());
    }

    @Test
    void deveCalcularNivelMedioETotalEstrelas() {
        Time time = new Time(1L, "Time A", "Azul");
        time.adicionarJogador(new Jogador(new Usuario(1L, "A", null, null), PosicaoEnum.ATACANTE, 5, true));
        time.adicionarJogador(new Jogador(new Usuario(2L, "B", null, null), PosicaoEnum.DEFENSOR, 3, true));

        assertEquals(4.0, time.getNivelMedio());
        assertEquals(8, time.getTotalEstrelas());
    }

    @Test
    void removerJogadorDeveTirarDoTime() {
        Time time = new Time(1L, "Time A", "Azul");
        Jogador jogador = new Jogador(new Usuario(1L, "A", null, null), PosicaoEnum.ATACANTE, 5, true);
        time.adicionarJogador(jogador);
        time.removerJogador(jogador);

        assertEquals(0, time.getJogadores().size());
    }
}

class RegraSorteioTest {

    @Test
    void porTamanhoTimeDeveArredondarQuantidadeDeTimesParaCima() {
        RegraSorteio regra = RegraSorteio.porTamanhoTime(4);
        assertEquals(3, regra.calcularQuantidadeTimes(12));
        assertEquals(3, regra.calcularQuantidadeTimes(10)); // 10/4 = 2.5 -> arredonda pra 3
    }

    @Test
    void porQuantidadeTimesDeveArredondarTamanhoDoTimeParaCima() {
        RegraSorteio regra = RegraSorteio.porQuantidadeTimes(3);
        assertEquals(4, regra.calcularTamanhoTime(12));
        assertEquals(4, regra.calcularTamanhoTime(10)); // 10/3 = 3.33 -> arredonda pra 4
    }

    @Test
    void naoDeveAceitarTamanhoDeTimeInvalido() {
        assertThrows(IllegalArgumentException.class, () -> RegraSorteio.porTamanhoTime(0));
    }

    @Test
    void naoDeveAceitarQuantidadeDeTimesInvalida() {
        assertThrows(IllegalArgumentException.class, () -> RegraSorteio.porQuantidadeTimes(-1));
    }
}
