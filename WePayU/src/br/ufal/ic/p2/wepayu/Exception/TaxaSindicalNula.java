package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSindicalNula extends RuntimeException {
    public TaxaSindicalNula() {
        super("Taxa sindical nao pode ser nula.");
    }
}
