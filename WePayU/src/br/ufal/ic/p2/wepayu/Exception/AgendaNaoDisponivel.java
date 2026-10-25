package br.ufal.ic.p2.wepayu.Exception;

public class AgendaNaoDisponivel extends RuntimeException {
    public AgendaNaoDisponivel (){
        super("Agenda de pagamento nao esta disponivel");
    }
}
