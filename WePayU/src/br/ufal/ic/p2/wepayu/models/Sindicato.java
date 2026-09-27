package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.DataInicialPosterior;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Sindicato {
    private String idMembro;
    private double taxaSindical;
    List<TaxaServico> taxasServicos = new ArrayList<>();

    public Sindicato(){}
    public Sindicato(String idMembro, double taxaSindical){
        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
    }

    public List<TaxaServico> getTaxasServicos() {
        return taxasServicos;
    }

    public double getTaxaSindical() {
        return taxaSindical;
    }

    public String getIdMembro() {
        return idMembro;
    }

    public void setIdMembro(String idMembro) {
        this.idMembro = idMembro;
    }

    public void setTaxaSindical(double taxaSindical) {
        this.taxaSindical = taxaSindical;
    }

    public void setTaxasServicos(List<TaxaServico> taxasServicos) {
        this.taxasServicos = taxasServicos;
    }

    public void adicionarTaxaServico(TaxaServico t){
        taxasServicos.add(t);
    }

    public double getTaxasServico(LocalDate inicio, LocalDate fim){
        if(inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }
        double taxa = 0.0;
        for (TaxaServico t : taxasServicos){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataTaxa = LocalDate.parse(t.getData(), formatter);
            if ((dataTaxa.isEqual(inicio) || dataTaxa.isAfter(inicio)) && dataTaxa.isBefore(fim))
            {
                taxa += t.getValor()/(taxaSindical);
            }
        }
        return taxa;
    }
}
