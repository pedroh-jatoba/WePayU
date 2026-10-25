package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Gera o texto da folha de pagamento dos empregados em uma data específica.
 *
 * Organiza os empregados por categoria, considerando os dias de pagamento
 * definidos em suas agendas e os valores calculados pelas próprias classes
 * de empregado. Apresenta os valores individuais e os totais de cada seção,
 * além do total geral da folha.
 */
public class GeradorDeFolha {

    /** Configuração regional brasileira utilizada na formatação dos valores monetários. */
    private static final Locale BRASIL = new Locale("pt", "BR");

    /** Linha de separação utilizada na formatação das seções da folha de pagamento. */
    private static final String LINHA_IGUAIS =
            "===============================================================================================================================\n";

    /**
     * Gera a folha de pagamento completa para a data informada.
     *
     * A folha contém seções específicas para horistas, assalariados e
     * comissionados, seguidas pelo total geral da folha.
     *
     * @param data data de referência da folha de pagamento
     * @param empregados lista de empregados que podem integrar a folha
     * @return texto formatado contendo a folha de pagamento
     */
    public String gerarFolha(LocalDate data, List<Empregado> empregados) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("FOLHA DE PAGAMENTO DO DIA %s\n", data.toString()));
        sb.append("====================================\n\n");

        gerarSessaoHoristas(sb, pagosNoDia(empregados, "horista", data), data);
        gerarSessaoAssalariados(sb, pagosNoDia(empregados, "assalariado", data), data);
        gerarSessaoComissionados(sb, pagosNoDia(empregados, "comissionado", data), data);

        sb.append(String.format(
                BRASIL,
                "\nTOTAL FOLHA: %.2f\n",
                calcularTotalGeral(empregados, data)
        ));

        return sb.toString();
    }

    /**
     * Calcula o total bruto da folha de pagamento para a data informada,
     * somando os valores calculados para todos os empregados da lista.
     *
     * @param empregados lista de empregados considerados no cálculo
     * @param data data de referência do cálculo
     * @return soma dos valores brutos dos empregados na data informada
     */
    public double calcularTotalGeral(List<Empregado> empregados, LocalDate data) {
        return empregados.stream()
                .mapToDouble(e -> e.calcularBrutoNaData(data))
                .sum();
    }

    /**
     * Seleciona os empregados do tipo informado que recebem na data indicada,
     * ordena-os alfabeticamente pelo nome e calcula seus itens de folha.
     *
     * @param empregados lista de empregados a serem filtrados
     * @param tipo categoria do empregado a ser considerada
     * @param data data de referência para verificar o pagamento
     * @return lista ordenada de itens de folha dos empregados selecionados
     */
    private List<ItemFolha> pagosNoDia(
            List<Empregado> empregados,
            String tipo,
            LocalDate data
    ) {
        return empregados.stream()
                .filter(e -> e.getTipo().equalsIgnoreCase(tipo))
                .filter(e -> e.ehDiaDePagamento(data))
                .sorted(Comparator.comparing(
                        Empregado::getNome,
                        String.CASE_INSENSITIVE_ORDER
                ))
                .map(e -> e.calcularItemFolha(data))
                .collect(Collectors.toList());
    }

    /**
     * Adiciona ao texto da folha um cabeçalho composto pela linha superior,
     * pelo título da seção, pela linha inferior, pelos nomes das colunas
     * e pelo separador das colunas.
     *
     * @param sb construtor de texto que recebe o cabeçalho
     * @param titulo título da seção
     * @param colunas texto contendo os nomes das colunas
     * @param separador linha que separa o cabeçalho dos registros
     */
    private void cabecalho(
            StringBuilder sb,
            String titulo,
            String colunas,
            String separador
    ) {
        sb.append(LINHA_IGUAIS);
        sb.append(titulo);
        sb.append(LINHA_IGUAIS);
        sb.append(colunas);
        sb.append(separador);
    }

    /**
     * Gera a seção dos empregados horistas, incluindo as horas normais,
     * as horas extras, o salário bruto, os descontos, o salário líquido
     * e o método de pagamento de cada empregado.
     *
     * Ao final, apresenta os totais da seção.
     *
     * @param sb construtor de texto que recebe os dados da seção
     * @param itens itens de folha dos empregados horistas que recebem na data
     * @param data data de referência da folha de pagamento
     */
    private void gerarSessaoHoristas(
            StringBuilder sb,
            List<ItemFolha> itens,
            LocalDate data
    ) {
        cabecalho(sb,
                "===================== HORISTAS ================================================================================================\n",
                "Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n",
                "==================================== ===== ===== ============= ========= =============== ======================================\n");

        double totalHoras = 0, totalExtra = 0, totalBruto = 0,
                totalDescontos = 0, totalLiquido = 0;

        for (ItemFolha i : itens) {
            sb.append(String.format(BRASIL,
                    "%-36s %5d %5d %,13.2f %,9.2f %,15.2f %s\n",
                    i.nome, (int) i.horas, (int) i.extras,
                    i.bruto, i.descontos, i.getLiquido(), i.metodo));

            totalHoras += i.horas;
            totalExtra += i.extras;
            totalBruto += i.bruto;
            totalDescontos += i.descontos;
            totalLiquido += i.getLiquido();
        }

        sb.append("\n");

        sb.append(String.format(BRASIL,
                "%-36s %5d %5d %,13.2f %,9.2f %,15.2f\n\n",
                "TOTAL HORISTAS", (int) totalHoras, (int) totalExtra,
                totalBruto, totalDescontos, totalLiquido));
    }

    /**
     * Gera a seção dos empregados assalariados, apresentando o salário bruto,
     * os descontos, o salário líquido e o método de pagamento de cada empregado.
     *
     * Ao final, apresenta os totais da seção.
     *
     * @param sb construtor de texto que recebe os dados da seção
     * @param itens itens de folha dos empregados assalariados que recebem na data
     * @param data data de referência da folha de pagamento
     */
    private void gerarSessaoAssalariados(
            StringBuilder sb,
            List<ItemFolha> itens,
            LocalDate data
    ) {
        cabecalho(sb,
                "===================== ASSALARIADOS ============================================================================================\n",
                "Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n",
                "================================================ ============= ========= =============== ======================================\n");

        double totalBruto = 0, totalDescontos = 0, totalLiquido = 0;

        for (ItemFolha item : itens) {
            sb.append(String.format(BRASIL,
                    "%-48s %13.2f %9.2f %15.2f %s\n",
                    item.nome, item.bruto, item.descontos,
                    item.getLiquido(), item.metodo));

            totalBruto += item.bruto;
            totalDescontos += item.descontos;
            totalLiquido += item.getLiquido();
        }

        sb.append("\n");

        sb.append(String.format(BRASIL,
                "%-48s %13.2f %9.2f %15.2f\n\n",
                "TOTAL ASSALARIADOS", totalBruto, totalDescontos, totalLiquido));
    }

    /**
     * Gera a seção dos empregados comissionados, apresentando a parcela fixa,
     * o valor das vendas, a comissão, o salário bruto, os descontos,
     * o salário líquido e o método de pagamento de cada empregado.
     *
     * Ao final, apresenta os totais da seção.
     *
     * @param sb construtor de texto que recebe os dados da seção
     * @param itens itens de folha dos empregados comissionados que recebem na data
     * @param data data de referência da folha de pagamento
     */
    private void gerarSessaoComissionados(
            StringBuilder sb,
            List<ItemFolha> itens,
            LocalDate data
    ) {
        cabecalho(sb,
                "===================== COMISSIONADOS ===========================================================================================\n",
                "Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n",
                "===================== ======== ======== ======== ============= ========= =============== ======================================\n");

        double totalFixo = 0, totalVendas = 0, totalComissao = 0,
                totalBruto = 0, totalDescontos = 0, totalLiquido = 0;

        for (ItemFolha i : itens) {
            sb.append(String.format(BRASIL,
                    "%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f %s\n",
                    i.nome, i.fixo, i.vendas, i.comissao,
                    i.bruto, i.descontos, i.getLiquido(), i.metodo));

            totalFixo += i.fixo;
            totalVendas += i.vendas;
            totalComissao += i.comissao;
            totalBruto += i.bruto;
            totalDescontos += i.descontos;
            totalLiquido += i.getLiquido();
        }

        sb.append("\n");

        sb.append(String.format(BRASIL,
                "%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f\n",
                "TOTAL COMISSIONADOS", totalFixo, totalVendas, totalComissao,
                totalBruto, totalDescontos, totalLiquido));
    }
}