package app;

import core.domain.Conexao;
import core.domain.Usuario;
import jogador.domain.Jogador;
import jogador.domain.PosicaoEnum;
import racha.domain.Racha;
import racha.domain.StatusRachaEnum;
import time.domain.RegraSorteio;
import time.domain.SorteioEquilibradoStrategy;
import time.domain.Time;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Ponto de entrada simples para exercitar o fluxo do domínio pelo
 * terminal: conexões entre usuários, cadastro de jogadores, confirmação
 * e remoção de presença, atualização de nível, sorteio de times e o
 * ciclo de vida completo do racha (iniciar, finalizar, cancelar).
 *
 * Não é um teste automatizado (isso já está em src/test/java) — é só
 * um "passeio guiado" pelo código, com prints em cada etapa.
 */
public class Main {

    public static void main(String[] args) {
        secao("1. Cadastro de usuários");
        Usuario organizador = new Usuario(1L, "Artur", "83 99999-0001", "artur@email.com");
        List<Usuario> usuariosJogadores = criarUsuariosDeTeste();
        System.out.println("Organizador: " + organizador.getNome());
        usuariosJogadores.forEach(u -> System.out.println("Usuário cadastrado: " + u.getNome()));

        secao("2. Conexões (estilo rede social)");
        conectarComOrganizador(organizador, usuariosJogadores);

        secao("3. Perfis de jogador (posição + nível de habilidade)");
        List<Jogador> jogadores = criarJogadoresAPartirDosUsuarios(usuariosJogadores);
        jogadores.forEach(j -> System.out.printf("%s - %s - nível %d - mensalista: %b%n",
                j.getDadosUsuario().getNome(), j.getPosicao(), j.getNivelHabilidade(), j.isMensalista()));

        System.out.println("\nAtualizando nível do jogador Gabriel (1 -> 3)...");
        Jogador gabriel = jogadores.get(6);
        gabriel.atualizarNivel(3);
        System.out.println("Novo nível de " + gabriel.getDadosUsuario().getNome() + ": " + gabriel.getNivelHabilidade());

        secao("4. Criação do racha");
        Racha racha = new Racha(
                1L,
                LocalDateTime.now().plusDays(2),
                "Quadra do Bairro",
                organizador,
                RegraSorteio.porTamanhoTime(4) // 4 jogadores por time
        );
        System.out.println("Racha criado com status: " + racha.getStatus());

        secao("5. Confirmação (e remoção) de presença");
        jogadores.forEach(racha::confirmarPresenca);
        System.out.println("Confirmados: " + racha.getJogadoresConfirmados().size());

        Jogador jogadorDesistente = jogadores.get(7); // Hugo
        racha.removerPresenca(jogadorDesistente);
        System.out.println(jogadorDesistente.getDadosUsuario().getNome() + " desistiu. Confirmados agora: "
                + racha.getJogadoresConfirmados().size());

        secao("6. Sorteio dos times");
        racha.realizarSorteio(new SorteioEquilibradoStrategy());
        System.out.println("Status após o sorteio: " + racha.getStatus());
        for (Time time : racha.getTimesFormados()) {
            System.out.printf("%s - nível médio: %.2f - jogadores: %d%n",
                    time.getNome(), time.getNivelMedio(), time.getJogadores().size());
        }

        secao("7. Ciclo de vida do racha (iniciar / finalizar)");
        racha.iniciarRacha();
        System.out.println("Status após iniciar: " + racha.getStatus());
        racha.finalizarRacha();
        System.out.println("Status após finalizar: " + racha.getStatus());

        secao("8. Validações de negócio (erros esperados)");
        tentarErro("Cancelar um racha já finalizado", racha::cancelarRacha);
        tentarErro("Confirmar presença após o racha ter sido sorteado/finalizado",
                () -> racha.confirmarPresenca(jogadorDesistente));

        secao("9. Um segundo racha, cancelado ainda AGENDADO");
        Racha rachaCancelavel = new Racha(2L, LocalDateTime.now().plusDays(5), "Campo do Zé",
                organizador, RegraSorteio.porQuantidadeTimes(2));
        rachaCancelavel.cancelarRacha();
        System.out.println("Status do segundo racha: " + rachaCancelavel.getStatus()
                + " (esperado: " + StatusRachaEnum.CANCELADO + ")");
    }

    private static void conectarComOrganizador(Usuario organizador, List<Usuario> usuarios) {
        long idConexao = 1;
        for (Usuario usuario : usuarios) {
            Conexao conexao = new Conexao(idConexao++, organizador, usuario);
            conexao.aceitar();
            organizador.adicionarConexao(conexao);
            usuario.adicionarConexao(conexao);
        }
        System.out.println(organizador.getNome() + " está conectado com "
                + usuarios.stream().filter(organizador::estaConectadoCom).count() + " jogadores.");
    }

    private static void tentarErro(String descricao, Runnable acao) {
        try {
            acao.run();
            System.out.println("[inesperado] " + descricao + " não lançou erro.");
        } catch (IllegalStateException e) {
            System.out.println("OK - erro esperado ao tentar \"" + descricao + "\": " + e.getMessage());
        }
    }

    private static void secao(String titulo) {
        System.out.println("\n=== " + titulo + " ===");
    }

    private static List<Usuario> criarUsuariosDeTeste() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario(2L, "Felipe", null, null));
        usuarios.add(new Usuario(3L, "José", null, null));
        usuarios.add(new Usuario(4L, "Bruno", null, null));
        usuarios.add(new Usuario(5L, "Caio", null, null));
        usuarios.add(new Usuario(6L, "Diego", null, null));
        usuarios.add(new Usuario(7L, "Erick", null, null));
        usuarios.add(new Usuario(8L, "Gabriel", null, null));
        usuarios.add(new Usuario(9L, "Hugo", null, null));
        return usuarios;
    }

    private static List<Jogador> criarJogadoresAPartirDosUsuarios(List<Usuario> usuarios) {
        PosicaoEnum[] posicoes = {
                PosicaoEnum.ATACANTE, PosicaoEnum.MEIO_CAMPO, PosicaoEnum.DEFENSOR, PosicaoEnum.GOLEIRO,
                PosicaoEnum.ATACANTE, PosicaoEnum.MEIO_CAMPO, PosicaoEnum.DEFENSOR, PosicaoEnum.ATACANTE
        };
        int[] niveis = {5, 4, 3, 3, 2, 2, 1, 4};
        boolean[] mensalistas = {true, true, false, true, false, true, false, true};

        List<Jogador> jogadores = new ArrayList<>();
        for (int i = 0; i < usuarios.size(); i++) {
            jogadores.add(new Jogador(usuarios.get(i), posicoes[i], niveis[i], mensalistas[i]));
        }
        return jogadores;
    }
}
