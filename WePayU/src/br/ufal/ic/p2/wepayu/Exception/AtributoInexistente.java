package br.ufal.ic.p2.wepayu.Exception;

public class AtributoInexistente extends RuntimeException {
    public AtributoInexistente() {
        super("Atributo nao existe.");
    }
}
