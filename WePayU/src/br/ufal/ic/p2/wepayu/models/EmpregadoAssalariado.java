package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

/**
 * Representa um empregado assalariado no sistema WePayU.
 *
 * <p>Este tipo de empregado recebe um salário mensal fixo,
 * cujo valor é utilizado para calcular a remuneração bruta
 * de acordo com a agenda de pagamento configurada.</p>
 *
 * <p>A classe estende {@link Empregado}, herdando seus dados pessoais,
 * método de pagamento, informações sindicais e funcionalidades
 * relacionadas à folha de pagamento.</p>
 */
public class EmpregadoAssalariado extends Empregado {

    /**
     * Valor do salário mensal fixo do empregado.
     */
    private double salarioMensal;

    /**
     * Cria um empregado assalariado sem inicializar explicitamente
     * seus atributos.
     */
    public EmpregadoAssalariado() {
        super();
    }

    /**
     * Cria um empregado assalariado com os dados pessoais,
     * o salário mensal e o método de pagamento informados.
     *
     * <p>O empregado é inicializado com o tipo {@code assalariado},
     * sem sindicalização e com a agenda de pagamento mensal
     * no último dia do mês.</p>
     *
     * @param nome nome do empregado
     * @param endereco endereço do empregado
     * @param salarioMensal valor do salário mensal
     * @param metodoPagamento método utilizado para realizar o pagamento
     */
    public EmpregadoAssalariado(
            String nome,
            String endereco,
            double salarioMensal,
            MetodoPagamento metodoPagamento
    ) {
        super(
                metodoPagamento,
                nome,
                endereco,
                "assalariado",
                false,
                "mensal $"
        );

        this.salarioMensal = salarioMensal;
    }

    /**
     * Cria um empregado assalariado a partir dos dados de outro empregado.
     *
     * <p>Os dados herdados são copiados pelo construtor da classe pai.
     * O tipo é definido como {@code assalariado}, a agenda de pagamento
     * é ajustada para a agenda padrão desta classe e o salário mensal
     * é obtido do salário atual do empregado original.</p>
     *
     * @param empregado empregado cujos dados serão utilizados
     */
    public EmpregadoAssalariado(Empregado empregado) {
        super(empregado);
        this.setTipo("assalariado");
        this.setAgendaPagamento(agendaPadrao());
        this.salarioMensal = empregado.getSalarioAtual();
    }

    /**
     * Retorna o salário mensal do empregado.
     *
     * @return valor do salário mensal
     */
    public double getSalarioMensal() {
        return salarioMensal;
    }

    /**
     * Atualiza o salário mensal do empregado.
     *
     * @param salarioMensal novo valor do salário mensal
     */
    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    /**
     * Retorna a agenda de pagamento padrão para empregados assalariados.
     *
     * <p>A agenda padrão corresponde ao pagamento mensal realizado
     * no último dia de cada mês.</p>
     *
     * @return descrição da agenda padrão, {@code mensal $}
     */
    @Override
    protected String agendaPadrao() {
        return "mensal $";
    }

    /**
     * Calcula o salário bruto correspondente ao período de pagamento.
     *
     * <p>O valor é calculado de acordo com a agenda configurada.
     * Para agendas mensais, corresponde ao salário mensal integral.
     * Para agendas semanais, corresponde à parcela proporcional
     * calculada pela agenda. O resultado é truncado para duas
     * casas decimais, sem arredondamento.</p>
     *
     * @param inicio data inicial do período, inclusiva
     * @param fim data final do período, exclusiva
     * @return salário bruto do período, truncado para duas casas decimais
     */
    @Override
    public double getSalarioBruto(LocalDate inicio, LocalDate fim) {
        return trunca(agenda().valorFixo(getSalarioMensal()));
    }

    /**
     * Retorna o valor textual de um atributo do empregado.
     *
     * <p>Quando o atributo solicitado é {@code salario}, retorna
     * o salário mensal formatado com duas casas decimais e vírgula
     * como separador decimal. Para os demais atributos, delega
     * a consulta à implementação da classe pai.</p>
     *
     * @param atributo nome do atributo que será consultado
     * @return representação textual do atributo solicitado
     * @throws Exception se ocorrer um erro ao consultar um atributo
     *                   na implementação da classe pai
     */
    @Override
    public String getAtributo(String atributo) throws Exception {
        if (atributo.equals("salario")) {
            return String.format(
                    "%.2f",
                    getSalarioMensal()
            ).replace(".", ",");
        }

        return super.getAtributo(atributo);
    }

    /**
     * Altera o salário mensal do empregado a partir de uma representação
     * textual do valor.
     *
     * <p>A conversão é realizada pelo método {@code converteSalario}
     * da classe pai, que valida o preenchimento, o formato numérico
     * e a não negatividade do valor informado.</p>
     *
     * @param valor representação textual do novo salário
     * @throws Exception se o valor for nulo, vazio, não numérico
     *                   ou negativo
     */
    @Override
    protected void alteraSalario(String valor) throws Exception {
        setSalarioMensal(converteSalario(valor));
    }

    /**
     * Retorna o salário mensal atual do empregado.
     *
     * @return valor atual do salário mensal
     */
    @Override
    public double getSalarioAtual() {
        return getSalarioMensal();
    }
}