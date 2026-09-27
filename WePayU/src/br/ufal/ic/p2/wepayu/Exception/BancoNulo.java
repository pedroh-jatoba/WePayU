package br.ufal.ic.p2.wepayu.Exception;

public class BancoNulo extends RuntimeException {
  public BancoNulo() {
    super("Banco nao pode ser nulo.");
  }
}
