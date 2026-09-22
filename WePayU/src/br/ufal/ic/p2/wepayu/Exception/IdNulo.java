package br.ufal.ic.p2.wepayu.Exception;

public class IdNulo extends RuntimeException {
    public IdNulo() {
        super("Identificacao do empregado nao pode ser nula.");
    }
}
