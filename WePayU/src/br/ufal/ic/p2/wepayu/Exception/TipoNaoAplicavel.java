package br.ufal.ic.p2.wepayu.Exception;

public class TipoNaoAplicavel extends RuntimeException {
    public TipoNaoAplicavel() {
        super("Tipo nao aplicavel.");
    }
}
