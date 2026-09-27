package br.ufal.ic.p2.wepayu.Exception;

public class NaoBooleano extends RuntimeException {
    public NaoBooleano() {
        super("Valor deve ser true ou false.");
    }
}
