package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AgendaExiste;
import br.ufal.ic.p2.wepayu.Exception.AgendaInvalida;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representa uma agenda de pagamento e gerencia o registro das agendas
 * disponíveis no sistema.
 *
 * <p>As agendas definem a frequência e o dia em que um empregado recebe
 * seu pagamento, permitindo calcular o período correspondente a cada
 * pagamento e o valor fixo do salário devido.</p>
 *
 * <p>Formatos de agenda aceitos:</p>
 * <ul>
 *     <li>{@code semanal D}: pagamento semanal no dia D, em que
 *     1 representa segunda-feira e 7 representa domingo.</li>
 *     <li>{@code semanal K D}: pagamento a cada K semanas, no dia D,
 *     em que K varia de 1 a 52 e D varia de 1 a 7.</li>
 *     <li>{@code mensal N}: pagamento mensal no dia N, de 1 a 28.</li>
 *     <li>{@code mensal $}: pagamento mensal no último dia do mês.</li>
 * </ul>
 *
 * <p>O período de pagamento é representado pelo intervalo
 * {@code [dataAnterior, dataAtual)}, incluindo o início e excluindo
 * a data do pagamento atual.</p>
 */
public final class AgendaPagamento {

    /**
     * Conjunto de descrições das agendas cadastradas no sistema.
     *
     * <p>A ordem de inserção é preservada para facilitar a listagem
     * e a persistência das agendas.</p>
     */
    private static final Set<String> DISPONIVEIS = new LinkedHashSet<>();

    static {
        reiniciar();
    }

    /**
     * Restaura o conjunto inicial de agendas disponíveis.
     *
     * <p>Este método deve ser chamado durante a reinicialização
     * do sistema.</p>
     */
    public static void reiniciar() {
        DISPONIVEIS.clear();
        DISPONIVEIS.addAll(Arrays.asList(
                "semanal 5",
                "semanal 2 5",
                "mensal $"
        ));
    }

    /**
     * Cadastra uma nova agenda de pagamento.
     *
     * @param descricao descrição da agenda a ser cadastrada
     * @throws AgendaInvalida se a descrição for nula ou possuir
     *                        uma sintaxe inválida
     * @throws AgendaExiste se a agenda já estiver cadastrada
     * @throws Exception se ocorrer uma exceção durante o cadastro
     */
    public static void criar(String descricao) throws Exception {
        if (descricao == null || !sintaxeValida(descricao)) {
            throw new AgendaInvalida();
        }

        String agenda = normaliza(descricao);

        if (DISPONIVEIS.contains(agenda)) {
            throw new AgendaExiste();
        }

        DISPONIVEIS.add(agenda);
    }

    /**
     * Verifica se uma descrição corresponde a uma agenda cadastrada.
     *
     * <p>A comparação é realizada após a normalização dos espaços
     * em branco da descrição.</p>
     *
     * @param texto descrição da agenda a ser consultada
     * @return {@code true} se a agenda estiver cadastrada;
     *         {@code false} caso contrário
     */
    public static boolean valida(String texto) {
        return texto != null && DISPONIVEIS.contains(normaliza(texto));
    }

    /**
     * Retorna uma cópia do conjunto de agendas cadastradas.
     *
     * <p>A cópia impede que alterações no conjunto retornado modifiquem
     * diretamente o registro interno da classe.</p>
     *
     * @return conjunto contendo as agendas disponíveis
     */
    public static Set<String> getDisponiveis() {
        return new LinkedHashSet<>(DISPONIVEIS);
    }

    /**
     * Substitui o conjunto atual de agendas pelo conjunto informado.
     *
     * <p>Este método pode ser utilizado durante a restauração dos dados
     * persistidos do sistema.</p>
     *
     * @param agendas conjunto de agendas que será armazenado
     */
    public static void setDisponiveis(Set<String> agendas) {
        DISPONIVEIS.clear();
        DISPONIVEIS.addAll(agendas);
    }

    /**
     * Normaliza uma descrição, removendo espaços em branco
     * das extremidades e substituindo sequências internas
     * de espaços em branco por um único espaço.
     *
     * @param texto texto que será normalizado
     * @return texto normalizado
     */
    public static String normaliza(String texto) {
        return texto.trim().replaceAll("\\s+", " ");
    }

    /**
     * Define a segunda-feira utilizada como referência para calcular
     * a periodicidade das agendas com intervalos de várias semanas.
     *
     * <p>Para uma agenda {@code semanal K D}, são consideradas
     * semanas cujo índice, contado a partir desta data, seja
     * múltiplo de K. A data de referência corresponde ao início
     * da semana de 27 de dezembro de 2004.</p>
     */
    private static final LocalDate INICIO_DAS_SEMANAS =
            LocalDate.of(2004, 12, 27);

    /**
     * Descrição normalizada da agenda.
     */
    private final String texto;

    /**
     * Indica se a agenda possui periodicidade mensal.
     */
    private final boolean mensal;

    /**
     * Intervalo, em semanas, entre os pagamentos.
     *
     * <p>Utilizado somente em agendas semanais.</p>
     */
    private final int semanas;

    /**
     * Dia da semana em que o pagamento ocorre.
     *
     * <p>Utiliza valores de 1 a 7, correspondentes a segunda-feira
     * e domingo, respectivamente.</p>
     */
    private final int diaSemana;

    /**
     * Dia do mês em que o pagamento ocorre.
     *
     * <p>O valor zero representa o último dia do mês.</p>
     */
    private final int diaMes;

    /**
     * Cria uma instância de agenda de pagamento.
     *
     * @param texto descrição normalizada da agenda
     * @param mensal indica se a periodicidade é mensal
     * @param semanas intervalo entre pagamentos, em semanas
     * @param diaSemana dia da semana do pagamento
     * @param diaMes dia do mês do pagamento, sendo zero o último dia
     */
    private AgendaPagamento(
            String texto,
            boolean mensal,
            int semanas,
            int diaSemana,
            int diaMes
    ) {
        this.texto = texto;
        this.mensal = mensal;
        this.semanas = semanas;
        this.diaSemana = diaSemana;
        this.diaMes = diaMes;
    }

    /**
     * Verifica se uma descrição possui uma sintaxe válida de agenda.
     *
     * <p>A validação é realizada por meio da interpretação da descrição.
     * Exceções de argumento inválido indicam que a sintaxe não é aceita.</p>
     *
     * @param texto descrição que será validada
     * @return {@code true} se a descrição possuir uma sintaxe válida;
     *         {@code false} caso contrário
     */
    private static boolean sintaxeValida(String texto) {
        try {
            de(texto);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Converte uma descrição em uma instância de agenda de pagamento.
     *
     * <p>Este método verifica o formato da descrição e os intervalos
     * permitidos para seus parâmetros, mas não exige que a agenda
     * esteja cadastrada no sistema.</p>
     *
     * @param texto descrição da agenda a ser interpretada
     * @return instância correspondente à agenda informada
     * @throws IllegalArgumentException se a descrição for nula, possuir formato inválido ou apresentar parâmetros fora
     *                                  dos intervalos permitidos
     */
    public static AgendaPagamento de(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("Agenda nula");
        }

        String normal = normaliza(texto);
        String[] p = normal.split(" ");

        if (p[0].equals("semanal") && p.length == 2) {
            return new AgendaPagamento(
                    normal,
                    false,
                    1,
                    inteiro(p[1], 1, 7),
                    0
            );
        }

        if (p[0].equals("semanal") && p.length == 3) {
            return new AgendaPagamento(
                    normal,
                    false,
                    inteiro(p[1], 1, 52),
                    inteiro(p[2], 1, 7),
                    0
            );
        }

        if (p[0].equals("mensal") && p.length == 2) {
            int dia = p[1].equals("$")
                    ? 0
                    : inteiro(p[1], 1, 28);

            return new AgendaPagamento(
                    normal,
                    true,
                    0,
                    0,
                    dia
            );
        }

        throw new IllegalArgumentException(
                "Agenda invalida: " + texto
        );
    }

    /**
     * Converte uma representação textual em um número inteiro
     * e verifica se o resultado está dentro do intervalo permitido.
     *
     * @param s texto que representa o número
     * @param min limite mínimo permitido, inclusive
     * @param max limite máximo permitido, inclusive
     * @return número inteiro convertido
     * @throws IllegalArgumentException se o texto não representar
     *                                  um inteiro válido ou se o
     *                                  número estiver fora do intervalo
     */
    private static int inteiro(String s, int min, int max) {
        int v = Integer.parseInt(s);

        if (v < min || v > max) {
            throw new IllegalArgumentException(
                    "Fora do intervalo: " + s
            );
        }

        return v;
    }

    /**
     * Verifica se uma data corresponde a um dia de pagamento
     * definido pela agenda.
     *
     * <p>Nas agendas mensais, o pagamento ocorre no dia configurado
     * ou no último dia do mês, quando a descrição utiliza {@code $}.</p>
     *
     * <p>Nas agendas semanais, o dia da semana deve corresponder
     * ao configurado. Quando o intervalo é superior a uma semana,
     * também é verificado se a semana pertence à periodicidade
     * definida a partir da data de referência.</p>
     *
     * @param data data que será verificada
     * @return {@code true} se a data for um dia de pagamento;
     *         {@code false} caso contrário
     */
    public boolean ehDiaDePagamento(LocalDate data) {
        if (mensal) {
            int alvo = (diaMes == 0)
                    ? data.lengthOfMonth()
                    : diaMes;

            return data.getDayOfMonth() == alvo;
        }

        if (data.getDayOfWeek().getValue() != diaSemana) {
            return false;
        }

        if (semanas == 1) {
            return true;
        }

        long semana = Math.floorDiv(
                ChronoUnit.DAYS.between(INICIO_DAS_SEMANAS, data),
                7L
        );

        return Math.floorMod(semana, (long) semanas) == 0;
    }

    /**
     * Calcula a data inicial do período correspondente ao pagamento
     * realizado na data informada.
     *
     * <p>O período inclui a data inicial e exclui a data do pagamento
     * atual. Nas agendas semanais, a data inicial corresponde a
     * um número de semanas antes do pagamento. Nas agendas mensais
     * com dia fixo, corresponde à mesma data do mês anterior.
     * Para pagamentos no último dia do mês, corresponde ao primeiro
     * dia do mês do pagamento atual.</p>
     *
     * @param data data do pagamento atual
     * @return data inicial, inclusiva, do período de pagamento
     */
    public LocalDate inicioDoPeriodo(LocalDate data) {
        if (!mensal) {
            return data.minusDays(7L * semanas);
        }

        return (diaMes == 0)
                ? data.withDayOfMonth(1)
                : data.minusMonths(1);
    }

    /**
     * Calcula a quantidade de dias do período correspondente
     * ao pagamento informado.
     *
     * <p>Nas agendas semanais, a quantidade é determinada pelo
     * número de semanas da periodicidade. Nas agendas mensais,
     * o cálculo considera a duração do período entre as datas
     * de pagamento ou a quantidade de dias do mês, conforme
     * a modalidade configurada.</p>
     *
     * @param data data do pagamento atual
     * @return quantidade de dias do período de pagamento
     */
    public int diasDoPeriodo(LocalDate data) {
        if (!mensal) {
            return 7 * semanas;
        }

        if (diaMes == 0) {
            return data.lengthOfMonth();
        }

        return (int) ChronoUnit.DAYS.between(
                inicioDoPeriodo(data),
                data
        );
    }

    /**
     * Calcula a parcela fixa do salário mensal devida no período
     * correspondente à agenda.
     *
     * <p>Nas agendas mensais, o valor corresponde ao salário mensal
     * integral. Nas agendas semanais, o salário é convertido para
     * uma base semanal utilizando doze meses por ano e cinquenta
     * e duas semanas por ano, sendo multiplicado pelo intervalo
     * de semanas da agenda.</p>
     *
     * @param salarioMensal salário mensal utilizado no cálculo
     * @return valor fixo do salário devido no período
     */
    public double valorFixo(double salarioMensal) {
        if (mensal) {
            return salarioMensal;
        }

        return salarioMensal * 12.0 / 52.0 * semanas;
    }

    /**
     * Informa se a agenda possui periodicidade mensal.
     *
     * @return {@code true} se a agenda for mensal;
     *         {@code false} se for semanal
     */
    public boolean isMensal() {
        return mensal;
    }

    /**
     * Retorna a descrição normalizada da agenda.
     *
     * @return descrição da agenda no formato utilizado pelo sistema
     */
    @Override
    public String toString() {
        return texto;
    }
}
