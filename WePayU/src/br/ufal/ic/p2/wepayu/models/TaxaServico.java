package br.ufal.ic.p2.wepayu.models;

/**
 * Representa uma taxa de serviço, armazenando a data de registro
 * e o valor correspondente.
 */
public class TaxaServico {
    private String data;
    private double valor;

    /**
     * Construtor padrão da classe.
     */
    public TaxaServico() {}

    /**
     * Cria uma taxa de serviço com a data e o valor informados.
     *
     * @param data data de registro da taxa de serviço
     * @param valor valor monetário da taxa de serviço
     */
    public TaxaServico(String data, double valor) {
        this.data = data;
        this.valor = valor;
    }

    /**
     * Retorna a data de registro da taxa de serviço.
     *
     * @return data da taxa de serviço
     */
    public String getData() {
        return data;
    }

    /**
     * Retorna o valor monetário da taxa de serviço.
     *
     * @return valor da taxa de serviço
     */
    public double getValor() {
        return valor;
    }

    /**
     * Define a data de registro da taxa de serviço.
     *
     * @param data data da taxa de serviço
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Define o valor monetário da taxa de serviço.
     *
     * @param valor valor da taxa de serviço
     */
    public void setValor(double valor) {
        this.valor = valor;
    }
}