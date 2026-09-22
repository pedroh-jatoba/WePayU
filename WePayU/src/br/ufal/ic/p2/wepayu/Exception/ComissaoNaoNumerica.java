package br.ufal.ic.p2.wepayu.Exception;

public class ComissaoNaoNumerica extends RuntimeException {
    public ComissaoNaoNumerica() {
        super("Comissao deve ser numerica.");
    }
}
