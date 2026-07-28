package core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

    @Test
    void doisUsuariosComMesmoIdDevemSerIguais() {
        Usuario u1 = new Usuario(1L, "Artur", null, null);
        Usuario u2 = new Usuario(1L, "Artur (duplicado)", null, null);
        assertEquals(u1, u2);
    }

    @Test
    void naoDeveEstarConectadoSemConexaoAceita() {
        Usuario a = new Usuario(1L, "A", null, null);
        Usuario b = new Usuario(2L, "B", null, null);

        Conexao conexao = new Conexao(1L, a, b);
        a.adicionarConexao(conexao);

        assertFalse(a.estaConectadoCom(b));
    }

    @Test
    void deveEstarConectadoAposAceitarConexao() {
        Usuario a = new Usuario(1L, "A", null, null);
        Usuario b = new Usuario(2L, "B", null, null);

        Conexao conexao = new Conexao(1L, a, b);
        conexao.aceitar();
        a.adicionarConexao(conexao);

        assertTrue(a.estaConectadoCom(b));
    }

    @Test
    void naoDevePermitirConexaoComSiMesmo() {
        Usuario a = new Usuario(1L, "A", null, null);
        assertThrows(IllegalArgumentException.class, () -> new Conexao(1L, a, a));
    }

    @Test
    void conexaoRecusadaNaoContaComoConectado() {
        Usuario a = new Usuario(1L, "A", null, null);
        Usuario b = new Usuario(2L, "B", null, null);

        Conexao conexao = new Conexao(1L, a, b);
        conexao.recusar();
        a.adicionarConexao(conexao);

        assertFalse(a.estaConectadoCom(b));
    }
}
