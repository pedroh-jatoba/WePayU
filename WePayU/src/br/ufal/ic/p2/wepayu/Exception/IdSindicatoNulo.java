package br.ufal.ic.p2.wepayu.Exception;

public class IdSindicatoNulo extends RuntimeException {
    public IdSindicatoNulo() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}
