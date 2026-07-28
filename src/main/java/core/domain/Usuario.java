package core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa a pessoa que usa o sistema. Um usuário pode ser organizador
 * de um racha e/ou jogador, e pode se conectar a outros usuários por meio de {@link Conexao}.
 */
public class Usuario {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private List<Conexao> conexoes = new ArrayList<>();

    public Usuario(Long id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    /**
     * Verifica se este usuário tem uma conexão ACEITA com outro usuário.
     * Útil, por exemplo, para decidir quem pode confirmar presença
     * em um racha organizado por este usuário.
     */
    public boolean estaConectadoCom(Usuario outro) {
        return conexoes.stream()
                .filter(Conexao::estaAceita)
                .anyMatch(c -> c.getSolicitante().equals(outro) || c.getSolicitado().equals(outro));
    }

    public void adicionarConexao(Conexao conexao) {
        conexoes.add(conexao);
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Conexao> getConexoes() {
        return conexoes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return id != null && id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
