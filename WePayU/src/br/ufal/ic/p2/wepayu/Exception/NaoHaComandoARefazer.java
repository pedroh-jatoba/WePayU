package br.ufal.ic.p2.wepayu.Exception;

public class NaoHaComandoARefazer extends Exception {
    public NaoHaComandoARefazer() {
        super("Nao ha comando a refazer.");
    }
}