package racha.domain;

import core.domain.Usuario;
import jogador.domain.Jogador;
import jogador.domain.PosicaoEnum;
import org.junit.jupiter.api.Test;
import time.domain.RegraSorteio;
import time.domain.SorteioEquilibradoStrategy;
import time.domain.Time;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RachaTest {

    @Test
    void deveFormarTresTimesComDozeJogadoresConfirmados() {
        Usuario organizador = new Usuario(1L, "Organizador", null, null);
        Racha racha = new Racha(1L, LocalDateTime.now(), "Quadra", organizador,
                RegraSorteio.porTamanhoTime(4));

        for (long i = 2; i <= 13; i++) {
            Jogador jogador = new Jogador(new Usuario(i, "Jogador " + i, null, null),
                    PosicaoEnum.MEIO_CAMPO, (int) (i % 5) + 1, true);
            racha.confirmarPresenca(jogador);
        }

        racha.realizarSorteio(new SorteioEquilibradoStrategy());

        assertEquals(3, racha.getTimesFormados().size());
        for (Time time : racha.getTimesFormados()) {
            assertEquals(4, time.getJogadores().size());
        }
    }

    @Test
    void naoDeveIniciarRachaSemSortear() {
        Usuario organizador = new Usuario(1L, "Organizador", null, null);
        Racha racha = new Racha(1L, LocalDateTime.now(), "Quadra", organizador,
                RegraSorteio.porQuantidadeTimes(2));

        assertThrows(IllegalStateException.class, racha::iniciarRacha);
    }

    @Test
    void naoDeveConfirmarPresencaAposSorteio() {
        Usuario organizador = new Usuario(1L, "Organizador", null, null);
        Racha racha = new Racha(1L, LocalDateTime.now(), "Quadra", organizador,
                RegraSorteio.porQuantidadeTimes(2));

        Jogador jogador1 = new Jogador(new Usuario(2L, "J1", null, null), PosicaoEnum.ATACANTE, 3, true);
        Jogador jogador2 = new Jogador(new Usuario(3L, "J2", null, null), PosicaoEnum.DEFENSOR, 3, true);
        racha.confirmarPresenca(jogador1);
        racha.confirmarPresenca(jogador2);
        racha.realizarSorteio(new SorteioEquilibradoStrategy());

        Jogador jogadorTardio = new Jogador(new Usuario(4L, "J3", null, null), PosicaoEnum.MEIO_CAMPO, 3, true);
        assertThrows(IllegalStateException.class, () -> racha.confirmarPresenca(jogadorTardio));
    }

    @Test
    void naoDeveConfirmarOMesmoJogadorDuasVezes() {
        Usuario organizador = new Usuario(1L, "Organizador", null, null);
        Racha racha = new Racha(1L, LocalDateTime.now(), "Quadra", organizador,
                RegraSorteio.porQuantidadeTimes(2));

        Jogador jogador = new Jogador(new Usuario(2L, "J1", null, null), PosicaoEnum.ATACANTE, 3, true);
        racha.confirmarPresenca(jogador);
        racha.confirmarPresenca(jogador);

        assertEquals(1, racha.getJogadoresConfirmados().size());
    }

    @Test
    void naoDeveSortearSemJogadoresConfirmados() {
        Usuario organizador = new Usuario(1L, "Organizador", null, null);
        Racha racha = new Racha(1L, LocalDateTime.now(), "Quadra", organizador,
                RegraSorteio.porQuantidadeTimes(2));

        assertThrows(IllegalStateException.class, () -> racha.realizarSorteio(new SorteioEquilibradoStrategy()));
    }

    @Test
    void naoDeveCancelarRachaJaFinalizado() {
        Usuario organizador = new Usuario(1L, "Organizador", null, null);
        Racha racha = new Racha(1L, LocalDateTime.now(), "Quadra", organizador,
                RegraSorteio.porQuantidadeTimes(1));

        Jogador jogador = new Jogador(new Usuario(2L, "J1", null, null), PosicaoEnum.ATACANTE, 3, true);
        racha.confirmarPresenca(jogador);
        racha.realizarSorteio(new SorteioEquilibradoStrategy());
        racha.iniciarRacha();
        racha.finalizarRacha();

        assertThrows(IllegalStateException.class, racha::cancelarRacha);
    }

    @Test
    void devePermitirCancelarRachaAindaAgendado() {
        Usuario organizador = new Usuario(1L, "Organizador", null, null);
        Racha racha = new Racha(1L, LocalDateTime.now(), "Quadra", organizador,
                RegraSorteio.porQuantidadeTimes(1));

        racha.cancelarRacha();

        assertEquals(StatusRachaEnum.CANCELADO, racha.getStatus());
    }
}
