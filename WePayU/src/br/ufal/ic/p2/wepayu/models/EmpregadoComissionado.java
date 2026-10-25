package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um empregado comissionado, que recebe uma parcela fixa do salário
 * mensal e uma parcela variável calculada com base nas vendas realizadas.
 *
 * A remuneração é calculada de acordo com a agenda de pagamento semanal,
 * considerando o salário fixo proporcional ao período e as comissões sobre
 * as vendas realizadas nesse intervalo.
 */
public class EmpregadoComissionado extends Empregado {

    /** Percentual de comissão aplicado sobre o valor das vendas realizadas. */
    private double comissao;

    /** Salário mensal fixo do empregado comissionado. */
    private double salarioMensal;

    /** Lista de vendas registradas para o empregado. */
    private List<ResultadoDeVenda> vendas = new ArrayList<>();

    /**
     * Construtor padrão utilizado principalmente na desserialização XML.
     */
    public EmpregadoComissionado() {
        super();
    }

    /**
     * Cria um empregado comissionado com os dados informados.
     *
     * @param nome nome do empregado
     * @param endereco endereço do empregado
     * @param salarioMensal salário mensal fixo
     * @param comissao percentual de comissão sobre as vendas
     * @param metodoPagamento método de pagamento do empregado
     */
    public EmpregadoComissionado(String nome, String endereco, double salarioMensal,
                                 double comissao, MetodoPagamento metodoPagamento) {
        super(metodoPagamento, nome, endereco, "comissionado", false, "semanal 2 5");
        this.salarioMensal = salarioMensal;
        this.comissao = comissao;
    }

    /**
     * Cria um empregado comissionado a partir de um empregado existente,
     * definindo a comissão informada e preservando os demais dados.
     *
     * @param empregado empregado cujos dados serão utilizados
     * @param comissao percentual de comissão sobre as vendas
     */
    public EmpregadoComissionado(Empregado empregado, double comissao) {
        super(empregado);
        this.setTipo("comissionado");
        this.setAgendaPagamento(agendaPadrao());
        this.salarioMensal = empregado.getSalarioAtual();
        this.comissao = comissao;
    }

    /**
     * Cria um empregado comissionado a partir de um empregado existente,
     * definindo inicialmente a comissão como zero.
     *
     * @param empregado empregado cujos dados serão utilizados
     */
    public EmpregadoComissionado(Empregado empregado) {
        super(empregado);
        this.setTipo("comissionado");
        this.setAgendaPagamento(agendaPadrao());
        this.salarioMensal = empregado.getSalarioAtual();
        this.comissao = 0.0;
    }

    /**
     * Retorna a agenda de pagamento padrão dos empregados comissionados.
     *
     * @return agenda de pagamento semanal
     */
    @Override
    protected String agendaPadrao() {
        return "semanal 2 5";
    }

    /**
     * Calcula o valor total das vendas realizadas dentro do intervalo informado.
     * A data inicial é inclusiva e a data final é exclusiva.
     *
     * @param inicio data inicial do intervalo
     * @param fim data final do intervalo
     * @return soma dos valores das vendas no intervalo
     * @throws DataInicialPosterior caso a data inicial seja posterior à data final
     */
    public double getVendasRealizadas(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }

        double valorVendas = 0.0;

        for (ResultadoDeVenda v : vendas) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataCartao = LocalDate.parse(v.getData(), formatter);

            if ((!dataCartao.isBefore(inicio)) && dataCartao.isBefore(fim)) {
                valorVendas += v.getValor();
            }
        }

        return valorVendas;
    }

    /**
     * Calcula a parcela fixa do salário referente ao período de pagamento atual,
     * de acordo com a agenda de pagamento.
     *
     * @return valor fixo do salário no período, após o truncamento
     */
    public double getSalarioFixoDoPeriodo() {
        return trunca(agenda().valorFixo(getSalarioMensal()));
    }

    /**
     * Calcula a comissão correspondente às vendas realizadas no intervalo informado.
     *
     * @param inicio data inicial do período
     * @param fim data final do período
     * @return valor da comissão no período, após o truncamento
     */
    public double getComissaoDoPeriodo(LocalDate inicio, LocalDate fim) {
        return trunca(getComissao() * getVendasRealizadas(inicio, fim));
    }

    /**
     * Calcula o salário bruto do empregado no intervalo informado, somando
     * a parcela fixa e a comissão sobre as vendas do período.
     *
     * @param inicio data inicial do período
     * @param fim data final do período
     * @return salário bruto correspondente ao intervalo
     */
    @Override
    public double getSalarioBruto(LocalDate inicio, LocalDate fim) {
        return getSalarioFixoDoPeriodo() + getComissaoDoPeriodo(inicio, fim);
    }

    /**
     * Calcula os dados do item da folha de pagamento para a data informada,
     * preenchendo a parcela fixa, o total de vendas e a comissão do período.
     *
     * @param data data de referência para o cálculo da folha
     * @return item da folha de pagamento calculado
     */
    @Override
    public ItemFolha calcularItemFolha(LocalDate data) {
        ItemFolha item = super.calcularItemFolha(data);
        LocalDate inicio = inicioDoPeriodo(data);
        item.fixo = getSalarioFixoDoPeriodo();
        item.vendas = getVendasRealizadas(inicio, data);
        item.comissao = getComissaoDoPeriodo(inicio, data);
        return item;
    }

    /**
     * Calcula uma estimativa do salário quinzenal com base no salário mensal.
     * Mantido para compatibilidade com implementações anteriores.
     *
     * @return valor estimado do salário quinzenal
     */
    public double getSalarioQuinzenal() {
        return getSalarioMensal() * 12.0 / 52.0 * 2.0;
    }

    /**
     * Adiciona uma venda à lista de vendas do empregado.
     *
     * @param v resultado da venda a ser registrado
     */
    public void adicionarVenda(ResultadoDeVenda v) {
        this.vendas.add(v);
    }

    /**
     * Retorna o percentual de comissão do empregado.
     *
     * @return percentual de comissão
     */
    public double getComissao() {
        return comissao;
    }

    /**
     * Define o percentual de comissão do empregado.
     *
     * @param comissao novo percentual de comissão
     */
    public void setComissao(double comissao) {
        this.comissao = comissao;
    }

    /**
     * Retorna o salário mensal fixo do empregado.
     *
     * @return salário mensal
     */
    public double getSalarioMensal() {
        return salarioMensal;
    }

    /**
     * Define o salário mensal fixo do empregado.
     *
     * @param salarioMensal novo salário mensal
     */
    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }

    /**
     * Retorna a lista de vendas registradas para o empregado.
     *
     * @return lista de resultados de vendas
     */
    public List<ResultadoDeVenda> getVendas() {
        return vendas;
    }

    /**
     * Substitui a lista de vendas registradas para o empregado.
     *
     * @param vendas nova lista de resultados de vendas
     */
    public void setVendas(List<ResultadoDeVenda> vendas) {
        this.vendas = vendas;
    }

    /**
     * Retorna o valor de um atributo do empregado comissionado em formato textual.
     * Os atributos específicos desta classe são "salario" e "comissao".
     *
     * @param atributo nome do atributo solicitado
     * @return valor do atributo formatado com duas casas decimais e vírgula
     *         como separador decimal, ou valor obtido da classe base
     * @throws Exception caso ocorra um erro ao consultar o atributo
     */
    @Override
    public String getAtributo(String atributo) throws Exception {
        switch (atributo) {
            case "salario":
                return String.format(
                        "%.2f",
                        getSalarioMensal()
                ).replace(".", ",");

            case "comissao":
                return String.format(
                        "%.2f",
                        getComissao()
                ).replace(".", ",");

            default:
                return super.getAtributo(atributo);
        }
    }

    /**
     * Altera o valor de um atributo do empregado comissionado.
     * O atributo específico tratado por esta classe é "comissao".
     * Os demais atributos são encaminhados à classe base.
     *
     * @param atributo nome do atributo a ser alterado
     * @param valor novo valor textual do atributo
     * @throws Exception caso ocorra um erro durante a alteração
     */
    @Override
    public void alteraAtributo(String atributo, String valor) throws Exception {
        if ("comissao".equals(atributo)) {
            setComissao(converteComissao(valor));
            return;
        }

        super.alteraAtributo(atributo, valor);
    }

    /**
     * Retorna o salário atual do empregado comissionado.
     *
     * @return salário mensal fixo
     */
    @Override
    public double getSalarioAtual() {
        return getSalarioMensal();
    }

    /**
     * Altera o salário mensal fixo do empregado utilizando o valor textual informado.
     *
     * @param valor novo salário em formato textual
     * @throws Exception caso ocorra um erro ao converter o salário
     */
    @Override
    public void alteraSalario(String valor) throws Exception {
        setSalarioMensal(converteSalario(valor));
    }
}