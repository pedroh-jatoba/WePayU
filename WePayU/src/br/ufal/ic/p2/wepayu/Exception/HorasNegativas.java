package br.ufal.ic.p2.wepayu.Exception;

public class HorasNegativas extends RuntimeException {
    public HorasNegativas() {
        super("Horas devem ser positivas.");
    }
}
