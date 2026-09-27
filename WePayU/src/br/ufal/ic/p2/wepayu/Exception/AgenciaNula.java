package br.ufal.ic.p2.wepayu.Exception;

public class AgenciaNula extends RuntimeException {
    public AgenciaNula() {
        super("Agencia nao pode ser nulo.");
    }
}
