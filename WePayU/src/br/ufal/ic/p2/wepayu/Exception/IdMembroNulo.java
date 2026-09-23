package br.ufal.ic.p2.wepayu.Exception;

public class IdMembroNulo extends RuntimeException {
    public IdMembroNulo() {
        super("Identificacao do membro nao pode ser nula.");
    }
}
