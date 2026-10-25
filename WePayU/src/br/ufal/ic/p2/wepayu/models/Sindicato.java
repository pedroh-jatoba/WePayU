package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.AtributoInexistente;
import br.ufal.ic.p2.wepayu.Exception.DataInicialPosterior;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o sindicato associado a um empregado, armazenando sua identificação,
 * a taxa sindical e as taxas de serviço registradas.
 */
public class Sindicato {
    private String idMembro;
    private double taxaSindical;
    private List<TaxaServico> taxasServicos = new ArrayList<>();

    /**
     * Construtor padrão da classe.
     */
    public Sindicato() {}

    /**
     * Cria um sindicato com a identificação do membro e a taxa sindical informadas.
     *
     * @param idMembro identificação do membro do sindicato
     * @param taxaSindical valor da taxa sindical
     */
    public Sindicato(String idMembro, double taxaSindical) {
        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
    }

    /**
     * Retorna a lista de taxas de serviço registradas.
     *
     * @return lista de taxas de serviço
     */
    public List<TaxaServico> getTaxasServicos() {
        return taxasServicos;
    }

    /**
     * Retorna o valor da taxa sindical.
     *
     * @return valor da taxa sindical
     */
    public double getTaxaSindical() {
        return taxaSindical;
    }

    /**
     * Retorna a identificação do membro do sindicato.
     *
     * @return identificação do membro
     */
    public String getIdMembro() {
        return idMembro;
    }

    /**
     * Define a identificação do membro do sindicato.
     *
     * @param idMembro identificação do membro
     */
    public void setIdMembro(String idMembro) {
        this.idMembro = idMembro;
    }

    /**
     * Define o valor da taxa sindical.
     *
     * @param taxaSindical valor da taxa sindical
     */
    public void setTaxaSindical(double taxaSindical) {
        this.taxaSindical = taxaSindical;
    }

    /**
     * Define a lista de taxas de serviço do sindicato.
     *
     * @param taxasServicos lista de taxas de serviço
     */
    public void setTaxasServicos(List<TaxaServico> taxasServicos) {
        this.taxasServicos = taxasServicos;
    }

    /**
     * Adiciona uma taxa de serviço à lista de taxas registradas.
     *
     * @param t taxa de serviço a ser adicionada
     */
    public void adicionarTaxaServico(TaxaServico t) {
        taxasServicos.add(t);
    }

    /**
     * Calcula a soma das taxas de serviço registradas a partir da data inicial
     * até antes da data final.
     *
     * @param inicio data inicial do período
     * @param fim data final do período
     * @return soma dos valores das taxas de serviço no período informado
     * @throws DataInicialPosterior se a data inicial for posterior à data final
     */
    public double getTaxasServico(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new DataInicialPosterior();
        }
        double taxa = 0.0;
        for (TaxaServico t : taxasServicos) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate dataTaxa = LocalDate.parse(t.getData(), formatter);
            if ((dataTaxa.isEqual(inicio) || dataTaxa.isAfter(inicio)) && dataTaxa.isBefore(fim)) {
                taxa += t.getValor();
            }
        }
        return taxa;
    }

    /**
     * Retorna o valor de um atributo do sindicato com base no nome informado.
     *
     * @param atributo nome do atributo a ser consultado
     * @return valor do atributo como texto; a taxa sindical é formatada com
     *         duas casas decimais e vírgula como separador decimal
     * @throws AtributoInexistente se o atributo informado não for reconhecido
     */
    public String getAtributo(String atributo) throws Exception {
        switch (atributo) {
            case "idSindicato":
                return getIdMembro();

            case "taxaSindical":
                return String.format(
                        "%.2f", getTaxaSindical()
                ).replace(".", ",");

            default:
                throw new AtributoInexistente();
        }
    }
}