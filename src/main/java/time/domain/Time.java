package time.domain;

import jogador.domain.Jogador;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa a equipe formada para jogar dentro de um racha.
 */
public class Time {

    private Long id;
    private String nome;
    private String corCamisa;
    private Set<Jogador> jogadores = new HashSet<>();

    public Time() {
    }

    public Time(Long id, String nome, String corCamisa) {
        this.id = id;
        this.nome = nome;
        this.corCamisa = corCamisa;
    }

    public void adicionarJogador(Jogador jogador) {
        jogadores.add(jogador);
    }

    public void removerJogador(Jogador jogador) {
        jogadores.remove(jogador);
    }

    public double getNivelMedio() {
        if (jogadores.isEmpty()) {
            return 0.0;
        }
        return jogadores.stream()
                .mapToInt(Jogador::getNivelHabilidade)
                .average()
                .orElse(0.0);
    }

    public int getTotalEstrelas() {
        return jogadores.stream()
                .mapToInt(Jogador::getNivelHabilidade)
                .sum();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCorCamisa() {
        return corCamisa;
    }

    public void setCorCamisa(String corCamisa) {
        this.corCamisa = corCamisa;
    }

    public Set<Jogador> getJogadores() {
        return jogadores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Time)) return false;
        Time time = (Time) o;
        return id != null && id.equals(time.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
