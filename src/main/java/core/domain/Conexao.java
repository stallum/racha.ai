package core.domain;

import java.util.Objects;

/**
 * Representa o vínculo entre dois usuários.
 * Um usuário só pode organizar rachas para/com quem está ACEITA.
 */
public class Conexao {

    private Long id;
    private Usuario solicitante;
    private Usuario solicitado;
    private StatusConexaoEnum status;

    public Conexao() {
    }

    public Conexao(Long id, Usuario solicitante, Usuario solicitado) {
        if (solicitante.equals(solicitado)) {
            throw new IllegalArgumentException("Um usuário não pode se conectar a si mesmo.");
        }
        this.id = id;
        this.solicitante = solicitante;
        this.solicitado = solicitado;
        this.status = StatusConexaoEnum.PENDENTE;
    }

    public void aceitar() {
        this.status = StatusConexaoEnum.ACEITA;
    }

    public void recusar() {
        this.status = StatusConexaoEnum.RECUSADA;
    }

    public boolean estaAceita() {
        return status == StatusConexaoEnum.ACEITA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getSolicitado() {
        return solicitado;
    }

    public void setSolicitado(Usuario solicitado) {
        this.solicitado = solicitado;
    }

    public StatusConexaoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusConexaoEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conexao)) return false;
        Conexao conexao = (Conexao) o;
        return id != null && id.equals(conexao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
