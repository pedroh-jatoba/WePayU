package br.ufal.ic.p2.wepayu.models;
import br.ufal.ic.p2.wepayu.Exception.DataInicialPosterior;
import br.ufal.ic.p2.wepayu.Exception.HorasNegativas;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public class EmpregadoHorista extends Empregado{
    double salarioPorHora;
    private List<CartaoDePonto> cartoes = new ArrayList<>();
    public EmpregadoHorista(){
        super();
    }
    public EmpregadoHorista(String nome, String endereco, double salarioPorHora, MetodoPagamento metodoPagamento){
        super(metodoPagamento, nome, endereco, "horista", false);
        this.salarioPorHora = salarioPorHora;
    }
    public EmpregadoHorista(Empregado empregado, double salarioPorHora){
        super(empregado);
        this.setTipo("horista");
        this.salarioPorHora = salarioPorHora;
    }
    public EmpregadoHorista(Empregado empregado) {
        super(empregado);
        this.setTipo("horista");
        this.salarioPorHora = empregado.getSalarioAtual();
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

    private LocalDate getDataPrimeiroCartao() {
        LocalDate primeiro = null;
        for (CartaoDePonto c : getCartoes()) {LocalDate d = LocalDate.parse(c.getData(), DateTimeFormatter.ofPattern("d/M/uuuu"));
            if (primeiro == null || d.isBefore(primeiro)) primeiro = d;
        }
        return primeiro;
    }

    public double getDescontosFolha(LocalDate data) {
        if (!isSindicalizado()) return 0.0;

        LocalDate primeiro = getDataPrimeiroCartao();
        if (primeiro == null) return 0.0;

        double divida = 0.0;
        double cobrado = 0.0;
        for (LocalDate d = primeiro.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
             !d.isAfter(data); d = d.plusDays(7)) {

            LocalDate ini = d.minusDays(7);
            double bruto = getSalarioBruto(ini, d);
            double devido = divida + getDescontos(ini, d, 7);

            cobrado = Math.min(bruto, devido);
            divida = devido - cobrado;
        }
        return cobrado;
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
    public double getSalarioBruto(LocalDate inicio, LocalDate fim){
        double horasNormais = getHorasNormaisTrabalhadas(inicio, fim);
        double horasExtras = getHorasExtrasTrabalhadas(inicio, fim);

        double salarioBruto = getSalarioAtual() * horasNormais + getSalarioAtual() * 1.5 * horasExtras;
        return salarioBruto;
    }
    @Override
    public String getAtributo(String atributo) throws Exception {
        if (atributo.equals("salario")) {
            return String.format(
                    "%.2f", getSalarioPorHora()
            ).replace(".", ",");
        }

        return super.getAtributo(atributo);
    }

    @Override
    protected void alteraSalario(String valor) throws Exception {
        setSalarioPorHora(converteSalario(valor));
    }
    @Override
    public double getSalarioAtual() {
        return getSalarioPorHora();
    }


    public void lancaCartao(String emp, String data, String horas) throws Exception {
        double horasConvertidas;

        try {
            horasConvertidas = Double.parseDouble(horas.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new HorasNegativas();
        }

        if (horasConvertidas <= 0) {
            throw new HorasNegativas();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate dataConvertida = converterData(data);
        CartaoDePonto cartao = new CartaoDePonto(emp, dataConvertida.format(formatter), horasConvertidas);

        adicionarCartao(cartao);
    }

}
