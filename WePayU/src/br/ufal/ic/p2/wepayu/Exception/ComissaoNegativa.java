package br.ufal.ic.p2.wepayu.Exception;

public class ComissaoNegativa extends RuntimeException {
    public ComissaoNegativa() {
        super("Comissao deve ser nao-negativa.");
    }
}
