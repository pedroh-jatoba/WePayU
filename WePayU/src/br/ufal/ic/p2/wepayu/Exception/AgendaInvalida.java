package br.ufal.ic.p2.wepayu.Exception;

public class AgendaInvalida extends RuntimeException {
    public AgendaInvalida() {
        super("Descricao de agenda invalida");
    }
}
