package br.ufal.ic.p2.wepayu.models;

/**
 * Representa o resultado de uma venda realizada por um empregado,
 * armazenando a identificação do empregado, a data e o valor da venda.
 */
public class ResultadoDeVenda {
    private String empregado;
   private String data;

    /** Valor monetário da venda. */
    private double valor;

    /**
     * Construtor padrão da classe.
     */
    public ResultadoDeVenda() {}

    /**
     * Cria um resultado de venda com o empregado, a data e o valor informados.
     *
     * @param empregado identificação do empregado responsável pela venda
     * @param data data em que a venda foi realizada
     * @param valor valor monetário da venda
     */
    public ResultadoDeVenda(String empregado, String data, double valor) {
        this.valor = valor;
        this.data = data;
        this.empregado = empregado;
    }

    /**
     * Retorna a identificação do empregado responsável pela venda.
     *
     * @return identificação do empregado
     */
    public String getEmpregado() {
        return empregado;
    }

    /**
     * Retorna a data em que a venda foi realizada.
     *
     * @return data da venda
     */
    public String getData() {
        return data;
    }

    /**
     * Retorna o valor monetário da venda.
     *
     * @return valor da venda
     */
    public double getValor() {
        return valor;
    }

    /**
     * Define a identificação do empregado responsável pela venda.
     *
     * @param empregado identificação do empregado
     */
    public void setEmpregado(String empregado) {
        this.empregado = empregado;
    }

    /**
     * Define a data em que a venda foi realizada.
     *
     * @param data data da venda
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Define o valor monetário da venda.
     *
     * @param valor valor da venda
     */
    public void setValor(double valor) {
        this.valor = valor;
    }
}