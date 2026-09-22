package br.ufal.ic.p2.wepayu.Exception;

public class SalarioNulo extends RuntimeException {
    public SalarioNulo() {
        super("Salario nao pode ser nulo.");
    }
}
