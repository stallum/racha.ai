package conexao.domain;

/*
 * ESSA CLASSE É UTILIZADA PARA SIMULAR UM BANCO DE DADOS;
 * ASSIMILARÁ O CONTEUDO DO RACHA, JOGADORES QUE PARTICIPARAM (CONFIRMADOS) HORÁRIO DE COMEÇO E HORÁRIO DE FIM.
 * TODO anotações de gols, faltas, cartões e outras métricas de jogo.
 */

import jogador.domain.Jogador;
import racha.domain.Racha;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Persiste o histórico de partidas em um arquivo CSV, uma linha por racha.
 * Cada registro guarda a lista de jogadores, a quantidade de participantes,
 * o horário de início e o horário de término.
 */
public class Database {

    private static final String ARQUIVO_CSV = "relatorio_rachas.csv";
    private static final String CABECALHO = "jogadores,quantidade_jogadores,horario_inicio,horario_fim";
    private static final DateTimeFormatter FORMATADOR_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static LocalDateTime comeco;
    private static LocalDateTime fim;

    public static void definirComeco(LocalDateTime horarioComeco) {
        comeco = horarioComeco;
    }

    public static void definirFim(LocalDateTime horarioFim) {
        fim = horarioFim;
    }

    public static LocalDateTime getComeco() {
        return comeco;
    }

    public static LocalDateTime getFim() {
        return fim;
    }

    /**
     * Monta a linha CSV da partida usando os horários já definidos em
     * {@link #definirComeco(LocalDateTime)} e {@link #definirFim(LocalDateTime)}.
     */
    public static String escreveRelatorio(Racha racha) {
        return escreveRelatorio(racha, comeco, fim);
    }

    /**
     * Monta a linha CSV com a lista de jogadores, a quantidade,
     * o horário de início e o horário de fim.
     */
    public static String escreveRelatorio(Racha racha, LocalDateTime horarioInicio, LocalDateTime horarioFim) {
        validarRacha(racha);
        validarHorarios(horarioInicio, horarioFim);

        List<Jogador> jogadores = racha.getJogadoresConfirmados();
        String listaDeJogadores = montarListaDeJogadores(jogadores);
        int quantidadeDeJogadores = jogadores.size();

        return String.join("|",
                escaparCampoCsv(listaDeJogadores),
                String.valueOf(quantidadeDeJogadores),
                escaparCampoCsv(formatarHorario(horarioInicio)),
                escaparCampoCsv(formatarHorario(horarioFim)));
    }

    /**
     * Registra a partida no arquivo CSV, acrescentando uma nova linha a cada racha.
     * Usa os horários definidos em {@link #definirComeco(LocalDateTime)} e
     * {@link #definirFim(LocalDateTime)}.
     */
    public static void registrarPartida(Racha racha) {
        registrarPartida(racha, comeco, fim);
    }

    /**
     * Grava no CSV a lista de jogadores, a quantidade, o início e o fim da partida.
     * Se o arquivo ainda não existir, o cabeçalho é escrito primeiro.
     */
    public static void registrarPartida(Racha racha, LocalDateTime horarioInicio, LocalDateTime horarioFim) {
        comeco = horarioInicio;
        fim = horarioFim;

        String linha = escreveRelatorio(racha, horarioInicio, horarioFim);
        Path arquivo = Path.of(ARQUIVO_CSV);

        try {
            escreverCabecalhoSeNecessario(arquivo);
            Files.writeString(
                    arquivo,
                    linha + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível registrar a partida no arquivo CSV.", e);
        }
    }

    private static void escreverCabecalhoSeNecessario(Path arquivo) throws IOException {
        if (Files.notExists(arquivo) || Files.size(arquivo) == 0) {
            Files.writeString(
                    arquivo,
                    CABECALHO + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private static String montarListaDeJogadores(List<Jogador> jogadores) {
        return jogadores.stream()
                .map(Database::obterNomeDoJogador)
                .collect(Collectors.joining("; "));
    }

    private static String obterNomeDoJogador(Jogador jogador) {
        if (jogador == null || jogador.getDadosUsuario() == null || jogador.getDadosUsuario().getNome() == null) {
            return "Jogador desconhecido";
        }
        return jogador.getDadosUsuario().getNome();
    }

    private static String formatarHorario(LocalDateTime horario) {
        return horario.format(FORMATADOR_DATA_HORA);
    }

    private static String escaparCampoCsv(String valor) {
        String texto = valor == null ? "" : valor; // operador ternário se a condição for verdadeira retorna o valor e se falsa retorna uma string vazia.
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }

    private static void validarRacha(Racha racha) {
        if (racha == null) {
            throw new IllegalArgumentException("O racha não pode ser nulo.");
        }
    }

    private static void validarHorarios(LocalDateTime horarioInicio, LocalDateTime horarioFim) {
        if (horarioInicio == null || horarioFim == null) {
            throw new IllegalStateException(
                    "Horário de começo e horário de fim devem ser definidos antes de registrar a partida.");
        }
        if (horarioFim.isBefore(horarioInicio)) {
            throw new IllegalArgumentException("O horário de fim não pode ser anterior ao horário de início.");
        }
    }
}
