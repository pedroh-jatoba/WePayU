package br.ufal.ic.p2.wepayu.Exception;

public class NaoHaComandoADesfazer extends Exception {
    public NaoHaComandoADesfazer() {
        super("Nao ha comando a desfazer.");
    }
}