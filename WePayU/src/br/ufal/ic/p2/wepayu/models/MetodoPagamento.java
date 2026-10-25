package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AtributoInexistente;
import br.ufal.ic.p2.wepayu.Exception.EmpregadoNaoBanco;

import javax.swing.*;

public class MetodoPagamento {
    private String tipo;
    public MetodoPagamento(){}
    public MetodoPagamento(String tipo){
        this.tipo = tipo;
    }
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAtributo(String atributo) throws Exception {
        switch (atributo) {
            case "banco":
            case "agencia":
            case "contaCorrente":
                throw new EmpregadoNaoBanco();

            case "metodoPagamento":
                return getTipo();

            default:
                throw new AtributoInexistente();
        }
    }

}
