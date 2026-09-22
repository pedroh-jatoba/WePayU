package br.ufal.ic.p2.wepayu.models;

public class EmpregadoComissionado extends Empregado{
    private double comissao;
    private double salarioMensal;
    public EmpregadoComissionado(){super();}//Para o XML
    public EmpregadoComissionado(String nome, String endereco, double salarioMensal, double comissao){
        super(nome, endereco, "comissionado");
        this.salarioMensal = salarioMensal;
        this.comissao = comissao;
    }

    public double getComissao() {
        return comissao;
    }

    public void setComissao(double comissao) {
        this.comissao = comissao;
    }

    public double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }
}
