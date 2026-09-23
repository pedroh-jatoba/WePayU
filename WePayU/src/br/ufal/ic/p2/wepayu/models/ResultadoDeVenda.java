package br.ufal.ic.p2.wepayu.models;

public class ResultadoDeVenda {
    private String empregado;
    private String data;
    private double valor;
    public ResultadoDeVenda(){}
    public ResultadoDeVenda(String empregado, String data, double valor){
        this.valor = valor;
        this.data = data;
        this.empregado = empregado;
    }

    public String getEmpregado() {
        return empregado;
    }

    public String getData() {
        return data;
    }

    public double getValor() {
        return valor;
    }

    public void setEmpregado(String empregado) {
        this.empregado = empregado;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }


}

