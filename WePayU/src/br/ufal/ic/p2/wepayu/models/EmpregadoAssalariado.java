package br.ufal.ic.p2.wepayu.models;

public class EmpregadoAssalariado extends Empregado{
    double salarioMensal;
    public EmpregadoAssalariado(){super();}
    public EmpregadoAssalariado(String nome, String endereco, double salarioMensal){
        super(nome, endereco, "assalariado", false);
        this.salarioMensal = salarioMensal;
    }

    public double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }
}
