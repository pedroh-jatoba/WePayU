package br.ufal.ic.p2.wepayu.models;

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
}
