package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoNaoComissionado extends RuntimeException {
    public EmpregadoNaoComissionado() {
        super("Empregado nao eh comissionado.");
    }
}
