package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.DataInicialPosterior;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoComissionado extends Empregado{
    private double comissao;
    private double salarioMensal;
    private List<ResultadoDeVenda> vendas = new ArrayList<>();
    public EmpregadoComissionado(){super();}//Para o XML
    public EmpregadoComissionado(String nome, String endereco, double salarioMensal, double comissao){
        super(nome, endereco, "comissionado", false);
        this.salarioMensal = salarioMensal;
        this.comissao = comissao;
    }

    public double getVendasRealizadas(LocalDate inicio, LocalDate fim){
        if(inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }
        double valorVendas = 0.0;
        for (ResultadoDeVenda v : vendas){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataCartao = LocalDate.parse(v.getData(), formatter);
            if ((dataCartao.isEqual(inicio) || dataCartao.isAfter(inicio)) && dataCartao.isBefore(fim))
            {
                valorVendas += v.getValor();
            }
        }
        return valorVendas;
    }

    public void adicionarVenda(ResultadoDeVenda v){
        this.vendas.add(v);
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

    public List<ResultadoDeVenda> getVendas() {
        return vendas;
    }

    public void setVendas(List<ResultadoDeVenda> vendas) {
        this.vendas = vendas;
    }
}
