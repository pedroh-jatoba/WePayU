package br.ufal.ic.p2.wepayu.Exception;

public class ValorNegativo extends RuntimeException {
    public ValorNegativo() {
        super("Valor deve ser positivo.");
    }
}
