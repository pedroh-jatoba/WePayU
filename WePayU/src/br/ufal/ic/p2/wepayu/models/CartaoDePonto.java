package br.ufal.ic.p2.wepayu.models;

/**
 * Representa um cartão de ponto de um empregado, armazenando
 * sua identificação, a data do registro e a quantidade de horas
 * trabalhadas.
 *
 * <p>Esta classe permite criar registros de ponto e consultar
 * ou modificar seus atributos por meio de métodos acessores.</p>
 */
public class CartaoDePonto {
    private String empregado;
    private String data;
    private double horas;
    /**
     * Cria um cartão de ponto sem inicializar seus atributos.
     */
    public CartaoDePonto() {}
    /**
     * Cria um cartão de ponto com a identificação do empregado,
     * a data do registro e a quantidade de horas trabalhadas.
     *
     * @param empregado identificação do empregado
     * @param data data em que o ponto foi registrado
     * @param horas quantidade de horas trabalhadas
     */
    public CartaoDePonto(String empregado, String data, double horas) {
        this.data = data;
        this.empregado = empregado;
        this.horas = horas;
    }
    /**
     * Retorna a quantidade de horas trabalhadas registrada.
     *
     * @return quantidade de horas trabalhadas
     */
    public double getHoras() {
        return horas;
    }
    /**
     * Retorna a identificação do empregado associado ao cartão.
     *
     * @return identificação do empregado
     */
    public String getEmpregado() {
        return empregado;
    }
    /**
     * Retorna a data do registro de ponto.
     *
     * @return data do registro
     */
    public String getData() {
        return data;
    }
    /**
     * Atualiza a data do registro de ponto.
     *
     * @param data nova data do registro
     */
    public void setData(String data) {
        this.data = data;
    }
    /**
     * Atualiza a identificação do empregado associado ao cartão.
     *
     * @param empregado nova identificação do empregado
     */
    public void setEmpregado(String empregado) {
        this.empregado = empregado;
    }
    /**
     * Atualiza a quantidade de horas trabalhadas registrada.
     *
     * @param horas nova quantidade de horas trabalhadas
     */
    public void setHoras(double horas) {
        this.horas = horas;
    }
}