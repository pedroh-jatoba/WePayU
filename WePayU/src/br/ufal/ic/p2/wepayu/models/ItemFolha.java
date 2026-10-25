package br.ufal.ic.p2.wepayu.models;

/** Valores calculados de um empregado em uma folha. Campos que não se aplicam ao tipo ficam em 0. */
public class ItemFolha {
    public String nome;
    public String metodo;

    // horista
    public double horas;
    public double extras;

    // comissionado
    public double fixo;
    public double vendas;
    public double comissao;

    // todos
    public double bruto;
    public double descontos;

    public double getLiquido() {
        return bruto - descontos;
    }
}