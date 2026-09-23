package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoNaoSindicalizado extends RuntimeException {
    public EmpregadoNaoSindicalizado() {
        super("Empregado nao eh sindicalizado.");
    }
}
