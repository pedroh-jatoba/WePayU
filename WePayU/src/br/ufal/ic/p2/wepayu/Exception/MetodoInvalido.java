package br.ufal.ic.p2.wepayu.Exception;

public class MetodoInvalido extends RuntimeException {
    public MetodoInvalido() {
        super("Metodo de pagamento invalido.");
    }
}
