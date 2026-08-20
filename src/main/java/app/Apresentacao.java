package app;

import conexao.domain.Database;
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
import java.util.Scanner;

/**
 * Ponto de entrada interativo para exercitar o fluxo do domínio pelo
 * terminal com inputs dinâmicos do usuário (cadastro com parada em '0').
 */
public class Apresentacao {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        secao("1. Cadastro do Organizador e Usuários");

        System.out.print("Digite o nome do Organizador: ");
        String nomeOrganizador = scanner.nextLine();
        System.out.print("Digite o telefone do Organizador: ");
        String telOrganizador = scanner.nextLine();
        System.out.print("Digite o e-mail do Organizador: ");
        String emailOrganizador = scanner.nextLine();

        Usuario organizador = new Usuario(1L, nomeOrganizador, telOrganizador, emailOrganizador);
        List<Usuario> usuariosJogadores = criarUsuariosDinamicos();

        System.out.println("\nOrganizador: " + organizador.getNome());
        usuariosJogadores.forEach(u -> System.out.println("Usuário cadastrado: " + u.getNome()));

        secao("2. Conexões (estilo rede social)");
        conectarComOrganizador(organizador, usuariosJogadores);

        secao("3. Perfis de jogador (posição + nível de habilidade)");
        List<Jogador> jogadores = criarJogadoresAPartirDosUsuarios(usuariosJogadores);
        jogadores.forEach(j -> System.out.printf("%s - %s - nível %d - mensalista: %b%n",
                j.getDadosUsuario().getNome(), j.getPosicao(), j.getNivelHabilidade(), j.isMensalista()));

        System.out.println("\nEscolha o índice de um jogador para atualizar o nível (0 a " + (jogadores.size() - 1) + "): ");
        int indiceJogador = lerInteiroSeguro(0, jogadores.size() - 1);
        Jogador jogadorEscolhido = jogadores.get(indiceJogador);

        System.out.print("Digite o novo nível para " + jogadorEscolhido.getDadosUsuario().getNome() + " (1 a 5): ");
        int novoNivel = lerInteiroSeguro(1, 5);

        jogadorEscolhido.atualizarNivel(novoNivel);
        System.out.println("Novo nível de " + jogadorEscolhido.getDadosUsuario().getNome() + ": " + jogadorEscolhido.getNivelHabilidade());

        secao("4. Criação do racha");
        System.out.print("Digite o local do racha (ex: Quadra do Bairro): ");
        String localRacha = scanner.nextLine();

        System.out.print("Digite a quantidade de jogadores por time (ex: 4): ");
        int tamanhoTime = lerInteiroSeguro(1, 11);

        Racha racha = new Racha(
                1L,
                LocalDateTime.now().plusDays(2),
                localRacha,
                organizador,
                RegraSorteio.porTamanhoTime(tamanhoTime)
        );
        System.out.println("Racha criado com status: " + racha.getStatus());

        secao("5. Confirmação (e remoção) de presença");
        jogadores.forEach(racha::confirmarPresenca);
        System.out.println("Confirmados: " + racha.getJogadoresConfirmados().size());

        Jogador jogadorDesistente = jogadores.get(jogadores.size() - 1);
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
        LocalDateTime horarioInicio = LocalDateTime.now();
        Database.definirComeco(horarioInicio);
        System.out.println("Status após iniciar: " + racha.getStatus());
        System.out.println("Horário de início: " + horarioInicio);

        racha.finalizarRacha();
        LocalDateTime horarioFim = LocalDateTime.now();
        Database.definirFim(horarioFim);
        System.out.println("Status após finalizar: " + racha.getStatus());
        System.out.println("Horário de fim: " + horarioFim);

        secao("8. Registro da partida em CSV");
        Database.registrarPartida(racha);
        System.out.println("Partida gravada em relatorio_rachas.csv");
        System.out.println("Linha registrada: " + Database.escreveRelatorio(racha));
        System.out.println("Jogadores confirmados: " + racha.getJogadoresConfirmados().size());

        secao("9. Validações de negócio (erros esperados)");
        tentarErro("Cancelar um racha já finalizado", racha::cancelarRacha);
        tentarErro("Confirmar presença após o racha ter sido sorteado/finalizado",
                () -> racha.confirmarPresenca(jogadorDesistente));

        secao("10. Um segundo racha, cancelado ainda AGENDADO");
        System.out.print("Digite o local para um segundo racha (cancelável): ");
        String localSegundoRacha = scanner.nextLine();

        Racha rachaCancelavel = new Racha(2L, LocalDateTime.now().plusDays(5), localSegundoRacha,
                organizador, RegraSorteio.porQuantidadeTimes(2));
        rachaCancelavel.cancelarRacha();
        System.out.println("Status do segundo racha: " + rachaCancelavel.getStatus()
                + " (esperado: " + StatusRachaEnum.CANCELADO + ")");

        System.out.println("\nFim da execução interativa!");
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

    private static List<Usuario> criarUsuariosDinamicos() {
        List<Usuario> usuarios = new ArrayList<>();
        System.out.println("\n--- Cadastro de Jogadores (Digite '0' para parar) ---");

        long idContador = 2L;
        while (true) {
            System.out.print("Nome do jogador (ou '0' para finalizar): ");
            String nome = scanner.nextLine().trim();

            if (nome.equals("0")) {
                break;
            }

            if (!nome.isEmpty()) {
                usuarios.add(new Usuario(idContador++, nome, null, null));
            } else {
                System.out.println("O nome não pode estar vazio. Tente novamente.");
            }
        }
        return usuarios;
    }

    private static List<Jogador> criarJogadoresAPartirDosUsuarios(List<Usuario> usuarios) {
        PosicaoEnum[] posicoesDisponiveis = {
                PosicaoEnum.ATACANTE, PosicaoEnum.MEIO_CAMPO, PosicaoEnum.DEFENSOR, PosicaoEnum.GOLEIRO
        };

        List<Jogador> jogadores = new ArrayList<>();
        for (int i = 0; i < usuarios.size(); i++) {
            PosicaoEnum posicao = posicoesDisponiveis[i % posicoesDisponiveis.length];
            int nivel = (i % 5) + 1;
            boolean mensalista = (i % 2 == 0);

            jogadores.add(new Jogador(usuarios.get(i), posicao, nivel, mensalista));
        }
        return jogadores;
    }

    private static int lerInteiroSeguro(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine();
                int valor = Integer.parseInt(input);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf("Por favor, digite um valor entre %d e %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número inteiro: ");
            }
        }
    }
}