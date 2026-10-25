package br.ufal.ic.p2.wepayu.Exception;

public class AgendaExiste extends RuntimeException {
    public AgendaExiste() {
        super("Agenda de pagamentos ja existe");
    }
}
