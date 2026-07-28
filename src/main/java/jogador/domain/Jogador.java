package jogador.domain;

import core.domain.Usuario;

import java.util.Objects;

/**
 * Representa o perfil de jogo de um usuário. Usa COMPOSIÇÃO para vincular
 * as estatísticas de jogo a uma pessoa real (Usuario).
 */
public class Jogador {

    private static final int NIVEL_MINIMO = 1;
    private static final int NIVEL_MAXIMO = 5;

    private Usuario usuario;
    private PosicaoEnum posicao;
    private int nivelHabilidade;
    private boolean mensalista;

    public Jogador() {
    }

    public Jogador(Usuario usuario, PosicaoEnum posicao, int nivelHabilidade, boolean mensalista) {
        this.usuario = usuario;
        this.posicao = posicao;
        atualizarNivel(nivelHabilidade);
        this.mensalista = mensalista;
    }

    public void atualizarNivel(int novoNivel) {
        if (novoNivel < NIVEL_MINIMO || novoNivel > NIVEL_MAXIMO) {
            throw new IllegalArgumentException(
                    "Nível de habilidade deve estar entre " + NIVEL_MINIMO + " e " + NIVEL_MAXIMO + ".");
        }
        this.nivelHabilidade = novoNivel;
    }

    public Usuario getDadosUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public PosicaoEnum getPosicao() {
        return posicao;
    }

    public void setPosicao(PosicaoEnum posicao) {
        this.posicao = posicao;
    }

    public int getNivelHabilidade() {
        return nivelHabilidade;
    }

    public boolean isMensalista() {
        return mensalista;
    }

    public void setMensalista(boolean mensalista) {
        this.mensalista = mensalista;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jogador)) return false;
        Jogador jogador = (Jogador) o;
        return Objects.equals(usuario, jogador.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(usuario);
    }
}
