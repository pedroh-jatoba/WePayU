package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoComissionado extends Empregado{
    private double comissao;
    private double salarioMensal;
    private List<ResultadoDeVenda> vendas = new ArrayList<>();

    public EmpregadoComissionado(){
        super();
    }//Para o XML
    public EmpregadoComissionado(String nome, String endereco, double salarioMensal, double comissao, MetodoPagamento metodoPagamento){
        super(metodoPagamento, nome, endereco, "comissionado", false);
        this.salarioMensal = salarioMensal;
        this.comissao = comissao;
    }
    public EmpregadoComissionado(Empregado empregado, double comissao){
        super(empregado);
        this.setTipo("comissionado");
        this.salarioMensal = empregado.getSalarioAtual();
        this.comissao = comissao;
    }

    public EmpregadoComissionado(Empregado empregado) {
        super(empregado);
        this.setTipo("comissionado");
        this.salarioMensal = empregado.getSalarioAtual();
        this.comissao = 0.0;
    }

    public double getVendasRealizadas(LocalDate inicio, LocalDate fim){
        if(inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }
        double valorVendas = 0.0;
        for (ResultadoDeVenda v : vendas){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataCartao = LocalDate.parse(v.getData(), formatter);
            if ((!dataCartao.isBefore(inicio)) && dataCartao.isBefore(fim))
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

    @Override
    public String getAtributo(String atributo) throws Exception {
        switch (atributo) {
            case "salario":
                return String.format(
                        "%.2f",
                        getSalarioMensal()
                ).replace(".", ",");

            case "comissao":
                return String.format(
                        "%.2f",
                        getComissao()
                ).replace(".", ",");

            default:
                return super.getAtributo(atributo);
        }
    }
    @Override
    public void alteraAtributo(String atributo, String valor)
            throws Exception {

        if ("comissao".equals(atributo)) {
            setComissao(converteComissao(valor));
            return;
        }

        super.alteraAtributo(atributo, valor);
    }
    @Override
    public double getSalarioAtual() {
        return getSalarioMensal();
    }
    @Override
    public void alteraSalario(String valor) throws Exception {
        if (valor == null || valor.isEmpty()) {
            throw new SalarioNulo();
        }

        double salario;

        try {
            salario = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new SalarioNaoNumerico();
        }

        if (salario < 0) {
            throw new SalarioNegativo();
        }

        setSalarioMensal(salario);
    }

    public double getSalarioQuinzenal() {
        return getSalarioMensal() * 12.0 / 52.0 * 2.0;
    }

}
