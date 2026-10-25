package br.ufal.ic.p2.wepayu.Exception;

public class SistemaEncerrado extends Exception {
    public SistemaEncerrado() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}