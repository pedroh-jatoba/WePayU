package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.DataInicialPosterior;
import br.ufal.ic.p2.wepayu.Exception.HorasNegativas;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Representa um empregado horista, cuja remuneração é calculada com base
 * nas horas normais e extras trabalhadas durante o período de pagamento.
 *
 * O empregado possui uma taxa salarial por hora e mantém uma lista de
 * cartões de ponto utilizados para registrar suas jornadas de trabalho.
 */
public class EmpregadoHorista extends Empregado {
    private double salarioPorHora;
    private List<CartaoDePonto> cartoes = new ArrayList<>();

    /**
     * Construtor padrão utilizado principalmente na desserialização XML.
     */
    public EmpregadoHorista() {
        super();
    }

    /**
     * Cria um empregado horista com os dados informados.
     *
     * @param nome nome do empregado
     * @param endereco endereço do empregado
     * @param salarioPorHora valor pago por hora trabalhada
     * @param metodoPagamento método de pagamento do empregado
     */
    public EmpregadoHorista(String nome, String endereco, double salarioPorHora,
                            MetodoPagamento metodoPagamento) {
        super(metodoPagamento, nome, endereco, "horista", false, "semanal 5");
        this.salarioPorHora = salarioPorHora;
    }

    /**
     * Cria um empregado horista a partir de um empregado existente,
     * definindo o valor da remuneração por hora.
     *
     * @param empregado empregado cujos dados serão utilizados
     * @param salarioPorHora novo valor pago por hora trabalhada
     */
    public EmpregadoHorista(Empregado empregado, double salarioPorHora) {
        super(empregado);
        this.setTipo("horista");
        this.setAgendaPagamento(agendaPadrao());
        this.salarioPorHora = salarioPorHora;
    }

    /**
     * Cria um empregado horista a partir de um empregado existente,
     * utilizando seu salário atual como valor inicial da remuneração por hora.
     *
     * @param empregado empregado cujos dados serão utilizados
     */
    public EmpregadoHorista(Empregado empregado) {
        super(empregado);
        this.setTipo("horista");
        this.setAgendaPagamento(agendaPadrao());
        this.salarioPorHora = empregado.getSalarioAtual();
    }

    /**
     * Retorna a agenda de pagamento padrão dos empregados horistas.
     *
     * @return agenda de pagamento semanal
     */
    @Override
    protected String agendaPadrao() {
        return "semanal 5";
    }

    /**
     * Retorna o valor pago por hora trabalhada.
     *
     * @return salário por hora
     */
    public double getSalarioPorHora() {
        return salarioPorHora;
    }

    /**
     * Define o valor pago por hora trabalhada.
     *
     * @param salarioPorHora novo salário por hora
     */
    public void setSalarioPorHora(double salarioPorHora) {
        this.salarioPorHora = salarioPorHora;
    }

    /**
     * Adiciona um cartão de ponto à lista de registros do empregado.
     *
     * @param cartao cartão de ponto a ser adicionado
     */
    public void adicionarCartao(CartaoDePonto cartao) {
        this.cartoes.add(cartao);
    }

    /**
     * Retorna a data mais antiga registrada nos cartões de ponto.
     *
     * @return data do primeiro cartão registrado ou {@code null} caso
     *         não existam cartões
     */
    private LocalDate getDataPrimeiroCartao() {
        LocalDate primeiro = null;

        for (CartaoDePonto c : getCartoes()) {
            LocalDate d = LocalDate.parse(
                    c.getData(),
                    DateTimeFormatter.ofPattern("d/M/uuuu")
            );

            if (primeiro == null || d.isBefore(primeiro)) {
                primeiro = d;
            }
        }

        return primeiro;
    }

    /**
     * Calcula os descontos do empregado sindicalizado na data informada,
     * considerando possíveis valores pendentes de períodos anteriores.
     *
     * O cálculo percorre os dias de pagamento definidos pela agenda atual.
     * Quando os descontos de um período excedem o salário bruto disponível,
     * o valor restante é acumulado como dívida para o próximo pagamento.
     *
     * @param data data de referência para o cálculo dos descontos
     * @return valor dos descontos cobrado no último período de pagamento
     *         até a data informada
     */
    @Override
    public double getDescontosFolha(LocalDate data) {
        if (!isSindicalizado()) return 0.0;

        LocalDate primeiro = getDataPrimeiroCartao();
        if (primeiro == null) return 0.0;

        double divida = 0.0;
        double cobrado = 0.0;

        for (LocalDate d = primeiro.plusDays(1); !d.isAfter(data); d = d.plusDays(1)) {
            if (!ehDiaDePagamento(d)) continue;

            LocalDate ini = inicioDoPeriodo(d);
            double bruto = getSalarioBruto(ini, d);
            double devido = divida + getDescontos(ini, d, diasDoPeriodo(d));

            cobrado = Math.min(bruto, devido);
            divida = devido - cobrado;
        }

        return cobrado;
    }

    /**
     * Calcula o total de horas normais trabalhadas no intervalo informado.
     * Para cada cartão de ponto, considera no máximo oito horas normais
     * por dia. A data inicial é inclusiva e a data final é exclusiva.
     *
     * @param inicio data inicial do intervalo
     * @param fim data final do intervalo
     * @return total de horas normais trabalhadas
     * @throws DataInicialPosterior caso a data inicial seja posterior à data final
     */
    public double getHorasNormaisTrabalhadas(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }

        double horasNormais = 0.0;

        for (CartaoDePonto c : cartoes) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataCartao = LocalDate.parse(c.getData(), formatter);

            if ((dataCartao.isEqual(inicio) || dataCartao.isAfter(inicio))
                    && dataCartao.isBefore(fim)) {
                if (c.getHoras() > 8) {
                    horasNormais += 8;
                } else {
                    horasNormais += c.getHoras();
                }
            }
        }

        return horasNormais;
    }

    /**
     * Retorna a lista de cartões de ponto do empregado.
     *
     * @return lista de cartões de ponto
     */
    public List<CartaoDePonto> getCartoes() {
        return cartoes;
    }

    /**
     * Substitui a lista de cartões de ponto do empregado.
     *
     * @param cartoes nova lista de cartões de ponto
     */
    public void setCartoes(List<CartaoDePonto> cartoes) {
        this.cartoes = cartoes;
    }

    /**
     * Calcula o total de horas extras trabalhadas no intervalo informado.
     * Para cada cartão de ponto, considera como extras as horas que excedem
     * oito horas trabalhadas no dia. A data inicial é inclusiva e a data
     * final é exclusiva.
     *
     * @param inicio data inicial do intervalo
     * @param fim data final do intervalo
     * @return total de horas extras trabalhadas
     * @throws DataInicialPosterior caso a data inicial seja posterior à data final
     */
    public double getHorasExtrasTrabalhadas(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }

        double horasExtras = 0.0;

        for (CartaoDePonto c : cartoes) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataCartao = LocalDate.parse(c.getData(), formatter);

            if ((dataCartao.isEqual(inicio) || dataCartao.isAfter(inicio))
                    && dataCartao.isBefore(fim)) {
                if (c.getHoras() > 8) {
                    horasExtras += c.getHoras() - 8;
                }
            }
        }

        return horasExtras;
    }

    /**
     * Calcula o salário bruto do empregado no intervalo informado,
     * remunerando as horas normais pela taxa horária e as horas extras
     * com adicional de cinquenta por cento.
     *
     * @param inicio data inicial do período
     * @param fim data final do período
     * @return salário bruto correspondente ao intervalo
     */
    @Override
    public double getSalarioBruto(LocalDate inicio, LocalDate fim) {
        double horasNormais = getHorasNormaisTrabalhadas(inicio, fim);
        double horasExtras = getHorasExtrasTrabalhadas(inicio, fim);

        return getSalarioAtual() * horasNormais
                + getSalarioAtual() * 1.5 * horasExtras;
    }

    /**
     * Calcula os dados do item da folha de pagamento para a data informada,
     * preenchendo as quantidades de horas normais e extras trabalhadas
     * no período correspondente.
     *
     * @param data data de referência para o cálculo da folha
     * @return item da folha de pagamento calculado
     */
    @Override
    public ItemFolha calcularItemFolha(LocalDate data) {
        ItemFolha item = super.calcularItemFolha(data);
        LocalDate inicio = inicioDoPeriodo(data);
        item.horas = getHorasNormaisTrabalhadas(inicio, data);
        item.extras = getHorasExtrasTrabalhadas(inicio, data);
        return item;
    }

    /**
     * Retorna o valor de um atributo do empregado em formato textual.
     * O atributo específico tratado por esta classe é "salario".
     *
     * @param atributo nome do atributo solicitado
     * @return salário por hora formatado com duas casas decimais e vírgula
     *         como separador decimal, ou valor obtido da classe base
     * @throws Exception caso ocorra um erro ao consultar o atributo
     */
    @Override
    public String getAtributo(String atributo) throws Exception {
        if (atributo.equals("salario")) {
            return String.format(
                    "%.2f", getSalarioPorHora()
            ).replace(".", ",");
        }

        return super.getAtributo(atributo);
    }

    /**
     * Altera o salário por hora utilizando o valor textual informado.
     *
     * @param valor novo salário por hora em formato textual
     * @throws Exception caso ocorra um erro ao converter o salário
     */
    @Override
    protected void alteraSalario(String valor) throws Exception {
        setSalarioPorHora(converteSalario(valor));
    }

    /**
     * Retorna o salário atual do empregado, representado pelo valor pago
     * por hora trabalhada.
     *
     * @return salário por hora
     */
    @Override
    public double getSalarioAtual() {
        return getSalarioPorHora();
    }

    /**
     * Registra um cartão de ponto para o empregado, convertendo as horas
     * informadas e a data para os formatos utilizados pelo sistema.
     *
     * @param emp identificador do empregado associado ao cartão
     * @param data data do registro de ponto
     * @param horas quantidade de horas trabalhadas em formato textual
     * @throws Exception caso a quantidade de horas seja inválida ou ocorra
     *                   um erro durante o processamento dos dados
     * @throws HorasNegativas caso o valor informado não possa ser convertido
     *                        em número ou seja menor ou igual a zero
     */
    public void lancaCartao(String emp, String data, String horas) throws Exception {
        double horasConvertidas;

        try {
            horasConvertidas = Double.parseDouble(horas.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new HorasNegativas();
        }

        if (horasConvertidas <= 0) {
            throw new HorasNegativas();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate dataConvertida = converterData(data);
        CartaoDePonto cartao = new CartaoDePonto(
                emp,
                dataConvertida.format(formatter),
                horasConvertidas
        );

        adicionarCartao(cartao);
    }
}