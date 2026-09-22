package br.ufal.ic.p2.wepayu.Exception;

public class SalarioNegativo extends RuntimeException {
    public SalarioNegativo() {
        super("Salario deve ser nao-negativo.");
    }
}
