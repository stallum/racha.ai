package racha.domain;

import core.domain.Usuario;
import jogador.domain.Jogador;
import time.domain.EstrategiaSorteio;
import time.domain.RegraSorteio;
import time.domain.Time;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Entidade raiz que gerencia o evento de racha: confirmação de presença,
 * sorteio dos times e o ciclo de vida do evento (agendado -> sorteado ->
 * em andamento -> finalizado, ou cancelado).
 */
public class Racha {

    private Long id;
    private LocalDateTime dataHora;
    private String local;
    private StatusRachaEnum status;
    private Usuario organizador;
    private List<Jogador> jogadoresConfirmados = new ArrayList<>();
    private Set<Time> timesFormados = new HashSet<>();
    private RegraSorteio regraSorteio;

    public Racha() {
    }

    public Racha(Long id, LocalDateTime dataHora, String local, Usuario organizador, RegraSorteio regraSorteio) {
        this.id = id;
        this.dataHora = dataHora;
        this.local = local;
        this.organizador = organizador;
        this.regraSorteio = regraSorteio;
        this.status = StatusRachaEnum.AGENDADO;
    }

    /**
     * Confirma a presença de um jogador. Só pode ser feito enquanto o
     * racha está AGENDADO. Em uma versão futura, aqui é o lugar natural
     * para checar {@code organizador.estaConectadoCom(jogador.getDadosUsuario())}
     * caso a confirmação deva ficar restrita a conexões do organizador.
     */
    public void confirmarPresenca(Jogador jogador) {
        exigirStatus(StatusRachaEnum.AGENDADO, "confirmar presença");
        if (!jogadoresConfirmados.contains(jogador)) {
            jogadoresConfirmados.add(jogador);
        }
    }

    public void removerPresenca(Jogador jogador) {
        exigirStatus(StatusRachaEnum.AGENDADO, "remover presença");
        jogadoresConfirmados.remove(jogador);
    }

    /**
     * Executa o sorteio dos times usando a estratégia informada,
     * respeitando a {@link RegraSorteio} configurada para este racha
     * (ex: tamanho fixo de time ou quantidade fixa de times).
     */
    public void realizarSorteio(EstrategiaSorteio estrategia) {
        exigirStatus(StatusRachaEnum.AGENDADO, "realizar sorteio");
        if (jogadoresConfirmados.isEmpty()) {
            throw new IllegalStateException("Não há jogadores confirmados para sortear.");
        }
        List<Time> times = estrategia.distribuirJogadores(jogadoresConfirmados, regraSorteio);
        this.timesFormados = new HashSet<>(times);
        this.status = StatusRachaEnum.SORTEADO;
    }

    public void iniciarRacha() {
        exigirStatus(StatusRachaEnum.SORTEADO, "iniciar o racha");
        this.status = StatusRachaEnum.EM_ANDAMENTO;
    }

    public void finalizarRacha() {
        exigirStatus(StatusRachaEnum.EM_ANDAMENTO, "finalizar o racha");
        this.status = StatusRachaEnum.FINALIZADO;
    }

    public void cancelarRacha() {
        if (status == StatusRachaEnum.FINALIZADO) {
            throw new IllegalStateException("Não é possível cancelar um racha já finalizado.");
        }
        this.status = StatusRachaEnum.CANCELADO;
    }

    private void exigirStatus(StatusRachaEnum esperado, String acao) {
        if (status != esperado) {
            throw new IllegalStateException(
                    "Não é possível " + acao + " com o racha no status " + status + ".");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public StatusRachaEnum getStatus() {
        return status;
    }

    public Usuario getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Usuario organizador) {
        this.organizador = organizador;
    }

    public List<Jogador> getJogadoresConfirmados() {
        return jogadoresConfirmados;
    }

    public Set<Time> getTimesFormados() {
        return timesFormados;
    }

    public RegraSorteio getRegraSorteio() {
        return regraSorteio;
    }

    public void setRegraSorteio(RegraSorteio regraSorteio) {
        this.regraSorteio = regraSorteio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Racha)) return false;
        Racha racha = (Racha) o;
        return id != null && id.equals(racha.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
