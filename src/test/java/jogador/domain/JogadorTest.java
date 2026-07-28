package jogador.domain;

import core.domain.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JogadorTest {

    @Test
    void deveCriarJogadorComNivelValido() {
        Jogador jogador = new Jogador(new Usuario(1L, "A", null, null), PosicaoEnum.ATACANTE, 5, true);
        assertEquals(5, jogador.getNivelHabilidade());
    }

    @Test
    void naoDeveAceitarNivelAbaixoDoMinimo() {
        Usuario usuario = new Usuario(1L, "A", null, null);
        assertThrows(IllegalArgumentException.class,
                () -> new Jogador(usuario, PosicaoEnum.ATACANTE, 0, true));
    }

    @Test
    void naoDeveAceitarNivelAcimaDoMaximo() {
        Usuario usuario = new Usuario(1L, "A", null, null);
        assertThrows(IllegalArgumentException.class,
                () -> new Jogador(usuario, PosicaoEnum.ATACANTE, 6, true));
    }

    @Test
    void atualizarNivelDeveRefletirNoGetter() {
        Jogador jogador = new Jogador(new Usuario(1L, "A", null, null), PosicaoEnum.MEIO_CAMPO, 2, false);
        jogador.atualizarNivel(4);
        assertEquals(4, jogador.getNivelHabilidade());
    }

    @Test
    void doisJogadoresComMesmoUsuarioDevemSerIguais() {
        Usuario usuario = new Usuario(1L, "A", null, null);
        Jogador j1 = new Jogador(usuario, PosicaoEnum.GOLEIRO, 3, true);
        Jogador j2 = new Jogador(usuario, PosicaoEnum.ATACANTE, 5, false);
        assertEquals(j1, j2);
    }
}
