package br.ufal.ic.p2.wepayu.Exception;

public class ContaNula extends RuntimeException {
    public ContaNula() {
        super("Conta corrente nao pode ser nulo.");
    }
}
