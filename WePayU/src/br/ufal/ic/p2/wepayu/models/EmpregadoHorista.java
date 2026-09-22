package br.ufal.ic.p2.wepayu.models;

public class EmpregadoHorista extends Empregado{
    double salarioPorHora;

    public EmpregadoHorista(){super();}
    public EmpregadoHorista(String nome, String endereco, double salarioPorHora){
        super(nome, endereco, "horista");
        this.salarioPorHora = salarioPorHora;
    }

    public double getSalarioPorHora() {
        return salarioPorHora;
    }

    public void setSalarioPorHora(double salarioPorHora) {
        this.salarioPorHora = salarioPorHora;
    }
}
