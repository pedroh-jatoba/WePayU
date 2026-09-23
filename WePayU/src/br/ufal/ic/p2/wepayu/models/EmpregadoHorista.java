package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.Exception.DataInicialPosterior;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class EmpregadoHorista extends Empregado{
    double salarioPorHora;
    private List<CartaoDePonto> cartoes = new ArrayList<>();
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

    public void adicionarCartao(CartaoDePonto cartao) {
        this.cartoes.add(cartao);
    }

    public double getHorasNormaisTrabalhadas(LocalDate inicio, LocalDate fim){
        if(inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }
        double horasNormais = 0.0;
        for (CartaoDePonto c : cartoes){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataCartao = LocalDate.parse(c.getData(), formatter);
            if ((dataCartao.isEqual(inicio) || dataCartao.isAfter(inicio)) && dataCartao.isBefore(fim))
            {
                if(c.getHoras() > 8)
                {
                    horasNormais += 8;
                }
                else {
                    horasNormais += c.getHoras();
                }
            }
        }
        return horasNormais;
    }

    public List<CartaoDePonto> getCartoes() {
        return cartoes;
    }

    public void setCartoes(List<CartaoDePonto> cartoes) {
        this.cartoes = cartoes;
    }

    public double getHorasExtrasTrabalhadas(LocalDate inicio, LocalDate fim){
        if(inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }
        double horasExtras = 0.0;
        for (CartaoDePonto c : cartoes){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataCartao = LocalDate.parse(c.getData(), formatter);
            if ((dataCartao.isEqual(inicio) || dataCartao.isAfter(inicio)) && dataCartao.isBefore(fim))
            {
                if(c.getHoras() > 8)
                {
                    horasExtras += c.getHoras() - 8;
                }
            }
        }
        return horasExtras;
    }
}
