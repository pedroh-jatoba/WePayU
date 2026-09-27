package br.ufal.ic.p2.wepayu.Exception;

public class TaxaSindicalNaoNumerica extends RuntimeException {
  public TaxaSindicalNaoNumerica() {
    super("Taxa sindical deve ser numerica.");
  }
}
