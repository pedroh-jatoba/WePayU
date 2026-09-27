package br.ufal.ic.p2.wepayu.Exception;

public class EmpregadoNaoBanco extends RuntimeException {
  public EmpregadoNaoBanco() {
    super("Empregado nao recebe em banco.");
  }
}
