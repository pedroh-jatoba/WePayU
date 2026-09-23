package br.ufal.ic.p2.wepayu.Exception;

public class MembroInexistente extends RuntimeException {
    public MembroInexistente() {
        super("Membro nao existe.");
    }
}
