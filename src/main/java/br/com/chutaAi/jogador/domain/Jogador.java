package br.com.chutaAi.jogador.domain;

import br.com.chutaAi.core.domain.PosicaoEnum;
import br.com.chutaAi.core.domain.Usuario;

public class Jogador {
    private Usuario usuario;
    private PosicaoEnum posicao;
    private int Estrelas;
    private boolean mensalista;

    public Jogador(Usuario usuario, PosicaoEnum posicao, int Estrelas) {
        this.usuario = usuario;
        this.posicao = posicao;
        this.Estrelas = Estrelas;
    }

    public void setMensalista(boolean mensalista) {
        this.mensalista = mensalista;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public PosicaoEnum getPosicao() {
        return this.posicao;
    }

    public int getEstrelas() {
        return this.Estrelas;
    }

    public boolean isMensalista() {
        return this.mensalista;
    }
}
