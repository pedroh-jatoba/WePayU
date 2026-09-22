package br.ufal.ic.p2.wepayu.models;

public class EmpregadoComissionado extends Empregado{
    private double comissao;
    public EmpregadoComissionado(){super();}//Para o XML
    public EmpregadoComissionado(String nome, String endereco, String tipo, double salario, double comissao){
        super(nome, endereco, tipo, salario);
        this.comissao = comissao;
    }

    public double getComissao() {
        return comissao;
    }

    public void setComissao(double comissao) {
        this.comissao = comissao;
    }
}
