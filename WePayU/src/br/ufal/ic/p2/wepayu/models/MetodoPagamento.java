package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AtributoInexistente;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoBanco;

/**
 * Representa um método de pagamento utilizado para definir como um empregado
 * recebe sua remuneração.
 *
 * Armazena o tipo de método de pagamento e permite consultar seus atributos,
 * lançando exceções quando um atributo não é aplicável ou não existe.
 */
public class MetodoPagamento {
    private String tipo;

    /**
     * Construtor padrão utilizado para criar um método de pagamento
     * sem definir inicialmente seu tipo.
     */
    public MetodoPagamento() {
    }

    /**
     * Cria um método de pagamento com o tipo informado.
     *
     * @param tipo tipo do método de pagamento
     */
    public MetodoPagamento(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna o tipo do método de pagamento.
     *
     * @return tipo do método de pagamento
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define o tipo do método de pagamento.
     *
     * @param tipo novo tipo do método de pagamento
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna o valor de um atributo do método de pagamento.
     *
     * Os atributos "banco", "agencia" e "contaCorrente" não são aplicáveis
     * a esta implementação e provocam uma exceção. O atributo
     * "metodoPagamento" retorna o tipo configurado.
     *
     * @param atributo nome do atributo a ser consultado
     * @return valor do atributo solicitado
     * @throws EmpregadoNaoBanco caso seja solicitado um atributo bancário
     *                           não aplicável a este método de pagamento
     * @throws AtributoInexistente caso o atributo solicitado não seja reconhecido
     * @throws Exception caso ocorra outro erro durante a consulta
     */
    public String getAtributo(String atributo) throws Exception {
        switch (atributo) {
            case "banco":
            case "agencia":
            case "contaCorrente":
                throw new EmpregadoNaoBanco();

            case "metodoPagamento":
                return getTipo();

            default:
                throw new AtributoInexistente();
        }
    }
}