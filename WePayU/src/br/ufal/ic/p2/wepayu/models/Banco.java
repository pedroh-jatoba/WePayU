package br.ufal.ic.p2.wepayu.models;

/**
 * Representa o método de pagamento por depósito bancário,
 * armazenando o nome do banco, a agência e a conta corrente
 * associados ao empregado.
 *
 * <p>Esta classe estende {@link MetodoPagamento}, identificando
 * o método de pagamento como {@code banco} e disponibilizando
 * operações para consultar e modificar seus dados bancários.</p>
 */
public class Banco extends MetodoPagamento {
    private String banco;
    private String agencia;
    private String contaCorrente;
    /**
     * Cria uma instância de banco utilizando o construtor padrão
     * da classe {@link MetodoPagamento}.
     */
    public Banco() {
        super();
    }
    /**
     * Cria um método de pagamento bancário com os dados informados.
     *
     * <p>A instância é inicializada com o tipo de pagamento
     * {@code banco}.</p>
     *
     * @param banco nome ou identificação do banco
     * @param agencia número da agência bancária
     * @param contaCorrente número da conta corrente
     */
    public Banco(String banco, String agencia, String contaCorrente) {
        super("banco");
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }
    /**
     * Retorna o número da agência bancária.
     *
     * @return número da agência
     */
    public String getAgencia() {
        return agencia;
    }
    /**
     * Retorna o nome ou a identificação do banco.
     *
     * @return nome ou identificação do banco
     */
    public String getBanco() {
        return banco;
    }

    /**
     * Retorna o número da conta corrente.
     *
     * @return número da conta corrente
     */
    public String getContaCorrente() {
        return contaCorrente;
    }
    /**
     * Atualiza o número da agência bancária.
     *
     * @param agencia novo número da agência
     */
    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }
    /**
     * Atualiza o nome ou a identificação do banco.
     *
     * @param banco novo nome ou identificação do banco
     */
    public void setBanco(String banco) {
        this.banco = banco;
    }
    /**
     * Atualiza o número da conta corrente.
     *
     * @param contaCorrente novo número da conta corrente
     */
    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }
    /**
     * Retorna o valor de um atributo do método de pagamento bancário.
     *
     * <p>Quando o atributo solicitado corresponde a {@code banco},
     * {@code agencia} ou {@code contaCorrente}, retorna o valor
     * correspondente. Para os demais atributos, delega a consulta
     * à implementação da classe {@link MetodoPagamento}.</p>
     *
     * @param atributo nome do atributo que será consultado
     * @return valor do atributo solicitado
     * @throws Exception caso ocorra um erro ao consultar o atributo
     *                   na implementação da classe pai
     */
    @Override
    public String getAtributo(String atributo) throws Exception {
        switch (atributo) {
            case "banco":
                return getBanco();

            case "agencia":
                return getAgencia();

            case "contaCorrente":
                return getContaCorrente();

            default:
                return super.getAtributo(atributo);
        }
    }
}