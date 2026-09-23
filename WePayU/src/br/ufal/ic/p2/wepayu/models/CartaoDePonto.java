package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class CartaoDePonto {
    String empregado;
    String data;
    double horas;

    public CartaoDePonto(){}
    public CartaoDePonto(String empregado, String data, double horas){
        this.data = data;
        this.empregado = empregado;
        this.horas = horas;
    }

    public double getHoras() {
        return horas;
    }

    public String getEmpregado() {
        return empregado;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setEmpregado(String empregado) {
        this.empregado = empregado;
    }

    public void setHoras(double horas) {
        this.horas = horas;
    }
}
