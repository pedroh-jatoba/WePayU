package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSindicalNegativo extends RuntimeException {
    public TaxaSindicalNegativo() {
        super("Taxa sindical deve ser nao-negativa.");
    }
}
