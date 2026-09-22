package br.ufal.ic.p2.wepayu.Exception;

public class EnderecoNulo extends RuntimeException {
    public EnderecoNulo() {
        super("Endereco nao pode ser nulo.");
    }
}
