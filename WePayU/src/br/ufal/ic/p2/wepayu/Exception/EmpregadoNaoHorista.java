package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoNaoHorista extends RuntimeException {
    public EmpregadoNaoHorista() {
        super("Empregado nao eh horista.");
    }
}
