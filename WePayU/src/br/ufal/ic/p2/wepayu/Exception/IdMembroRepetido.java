package br.ufal.ic.p2.wepayu.Exception;

public class IdMembroRepetido extends RuntimeException {
    public IdMembroRepetido() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
