package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class GeradorDeFolha {
    private static final Locale BRASIL = new Locale("pt", "BR");
    private static final LocalDate PRIMEIRO_PAGTO_COMISSIONADO = LocalDate.of(2005, 1, 14);

    private static final String LINHA_IGUAIS =
            "===============================================================================================================================\n";

    // ------------------------------------------------------------------
    // Regras de data de pagamento
    // ------------------------------------------------------------------

    private boolean diaDePagarHorista(LocalDate data) {
        return data.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    private boolean diaDePagarAssalariado(LocalDate data) {
        return data.getDayOfMonth() == data.lengthOfMonth();
    }

    private boolean diaDePagarComissionado(LocalDate data) {
        long dias = ChronoUnit.DAYS.between(PRIMEIRO_PAGTO_COMISSIONADO, data);
        return dias >= 0 && dias % 14 == 0;
    }

    // ------------------------------------------------------------------
    // Utilitários
    // ------------------------------------------------------------------

    /** Trunca (não arredonda) para duas casas decimais. */
    private double trunca(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.DOWN).doubleValue();
    }

    /** Empregados do tipo informado, em ordem alfabética de nome (como no arquivo esperado). */
    private List<Empregado> doTipoOrdenadosPorNome(List<Empregado> empregados, String tipo) {
        return empregados.stream()
                .filter(e -> e.getTipo().equalsIgnoreCase(tipo))
                .sorted(Comparator.comparing(Empregado::getNome, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Folha completa
    // ------------------------------------------------------------------

    public String gerarFolha(LocalDate data, List<Empregado> empregados) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("FOLHA DE PAGAMENTO DO DIA %s\n", data.toString()));
        sb.append("====================================\n\n");

        gerarSessaoHoristas(sb, empregados, data);
        gerarSessaoAssalariados(sb, empregados, data);
        gerarSessaoComissionados(sb, empregados, data);

        double totalFolha = calcularTotalGeral(empregados, data);
        sb.append(String.format(BRASIL, "\nTOTAL FOLHA: %.2f\n", totalFolha));

        return sb.toString();
    }

    public double calcularTotalGeral(List<Empregado> empregados, LocalDate data) {
        double totalGeral = 0.0;

        for (Empregado emp : empregados) {
            String tipo = emp.getTipo().toLowerCase();

            if (tipo.equals("horista") && diaDePagarHorista(data)) {
                EmpregadoHorista horista = (EmpregadoHorista) emp;
                totalGeral += horista.getSalarioBruto(data.minusDays(7), data);

            } else if (tipo.equals("assalariado") && diaDePagarAssalariado(data)) {
                EmpregadoAssalariado assalariado = (EmpregadoAssalariado) emp;
                totalGeral += assalariado.getSalarioMensal();

            } else if (tipo.equals("comissionado") && diaDePagarComissionado(data)) {
                EmpregadoComissionado comissionado = (EmpregadoComissionado) emp;
                LocalDate dataInicio = data.minusDays(14);

                double fixo = trunca(comissionado.getSalarioQuinzenal());
                double vendas = comissionado.getVendasRealizadas(dataInicio, data);
                double comissao = trunca(comissionado.getComissao() * vendas);

                totalGeral += fixo + comissao;
            }
        }

        return totalGeral;
    }

    // ------------------------------------------------------------------
    // Seções do arquivo
    // ------------------------------------------------------------------

    private void gerarSessaoHoristas(StringBuilder sb, List<Empregado> empregados, LocalDate data) {
        sb.append(LINHA_IGUAIS);
        sb.append("===================== HORISTAS ================================================================================================\n");
        sb.append(LINHA_IGUAIS);
        sb.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
        sb.append("==================================== ===== ===== ============= ========= =============== ======================================\n");

        double totalHoras = 0.0;
        double totalExtra = 0.0;
        double totalBruto = 0.0;
        double totalDescontos = 0.0;
        double totalLiquido = 0.0;

        if (diaDePagarHorista(data)) {
            LocalDate dataInicio = data.minusDays(7);

            for (Empregado emp : doTipoOrdenadosPorNome(empregados, "horista")) {
                EmpregadoHorista horista = (EmpregadoHorista) emp;

                double horas = horista.getHorasNormaisTrabalhadas(dataInicio, data);
                double extra = horista.getHorasExtrasTrabalhadas(dataInicio, data);
                double bruto = horista.getSalarioBruto(dataInicio, data);
                double descontos = horista.getDescontosFolha(data);
                double liquido = bruto - descontos;

                sb.append(String.format(BRASIL,
                        "%-36s %5d %5d %,13.2f %,9.2f %,15.2f %s\n",
                        horista.getNome(), (int) horas, (int) extra, bruto, descontos, liquido,
                        horista.getResumoMetodoPagamento()));

                totalHoras += horas;
                totalExtra += extra;
                totalBruto += bruto;
                totalDescontos += descontos;
                totalLiquido += liquido;
            }
        }
        sb.append("\n");

        sb.append(String.format(BRASIL,
                "%-36s %5d %5d %,13.2f %,9.2f %,15.2f\n\n",
                "TOTAL HORISTAS", (int) totalHoras, (int) totalExtra, totalBruto, totalDescontos, totalLiquido));
    }

    private void gerarSessaoAssalariados(StringBuilder sb, List<Empregado> empregados, LocalDate data) {
        sb.append(LINHA_IGUAIS);
        sb.append("===================== ASSALARIADOS ============================================================================================\n");
        sb.append(LINHA_IGUAIS);
        sb.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
        sb.append("================================================ ============= ========= =============== ======================================\n");

        double totalBruto = 0.0;
        double totalDescontos = 0.0;
        double totalLiquido = 0.0;

        if (diaDePagarAssalariado(data)) {
            for (Empregado emp : doTipoOrdenadosPorNome(empregados, "assalariado")) {
                EmpregadoAssalariado assalariado = (EmpregadoAssalariado) emp;

                double bruto = assalariado.getSalarioMensal();
                double descontos = assalariado.getDescontos(data.withDayOfMonth(1), data, data.lengthOfMonth());
                double liquido = bruto - descontos;

                sb.append(String.format(BRASIL, "%-48s %13.2f %9.2f %15.2f %s\n",
                        assalariado.getNome(), bruto, descontos, liquido,
                        assalariado.getResumoMetodoPagamento()));

                totalBruto += bruto;
                totalDescontos += descontos;
                totalLiquido += liquido;
            }
        }
        sb.append("\n");

        sb.append(String.format(BRASIL, "%-48s %13.2f %9.2f %15.2f\n\n",
                "TOTAL ASSALARIADOS", totalBruto, totalDescontos, totalLiquido));
    }

    private void gerarSessaoComissionados(StringBuilder sb, List<Empregado> empregados, LocalDate data) {
        sb.append(LINHA_IGUAIS);
        sb.append("===================== COMISSIONADOS ===========================================================================================\n");
        sb.append(LINHA_IGUAIS);
        sb.append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n");
        sb.append("===================== ======== ======== ======== ============= ========= =============== ======================================\n");

        double totalFixo = 0.0;
        double totalVendas = 0.0;
        double totalComissao = 0.0;
        double totalBruto = 0.0;
        double totalDescontos = 0.0;
        double totalLiquido = 0.0;

        if (diaDePagarComissionado(data)) {
            LocalDate dataInicio = data.minusDays(14);

            for (Empregado emp : doTipoOrdenadosPorNome(empregados, "comissionado")) {
                EmpregadoComissionado comissionado = (EmpregadoComissionado) emp;

                double fixo = trunca(comissionado.getSalarioQuinzenal());
                double vendas = comissionado.getVendasRealizadas(dataInicio, data);
                double comissao = trunca(comissionado.getComissao() * vendas);
                double bruto = fixo + comissao;
                double descontos = comissionado.getDescontos(dataInicio, data, 14);
                double liquido = bruto - descontos;

                sb.append(String.format(BRASIL,
                        "%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f %s\n",
                        comissionado.getNome(), fixo, vendas, comissao, bruto, descontos, liquido,
                        comissionado.getResumoMetodoPagamento()));

                totalFixo += fixo;
                totalVendas += vendas;
                totalComissao += comissao;
                totalBruto += bruto;
                totalDescontos += descontos;
                totalLiquido += liquido;
            }
        }
        sb.append("\n");

        // Só um \n no fim: o "\nTOTAL FOLHA" de gerarFolha já cria a linha em branco.
        sb.append(String.format(BRASIL,
                "%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f\n",
                "TOTAL COMISSIONADOS", totalFixo, totalVendas, totalComissao, totalBruto, totalDescontos, totalLiquido));
    }
}