package br.ufal.ic.p2.wepayu.Exception;

public class DataInicialPosterior extends RuntimeException {
    public DataInicialPosterior() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
