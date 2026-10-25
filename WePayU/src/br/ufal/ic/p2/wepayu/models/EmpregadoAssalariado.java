package br.ufal.ic.p2.wepayu.models;

public class EmpregadoAssalariado extends Empregado{
    private double salarioMensal;
    public EmpregadoAssalariado(){super();}
    public EmpregadoAssalariado(String nome, String endereco, double salarioMensal, MetodoPagamento metodoPagamento){
        super(metodoPagamento, nome, endereco, "assalariado", false);
        this.salarioMensal = salarioMensal;
    }
    public EmpregadoAssalariado(Empregado empregado) {
        super(empregado);
        this.setTipo("assalariado");
        this.salarioMensal = empregado.getSalarioAtual();
    }

    public double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(double salarioMensal) {
        this.salarioMensal = salarioMensal;
    }
    @Override
    public String getAtributo(String atributo) throws Exception {
        if (atributo.equals("salario")) {
            return String.format(
                    "%.2f", getSalarioMensal()
            ).replace(".", ",");
        }

        return super.getAtributo(atributo);
    }

    @Override
    protected void alteraSalario(String valor) throws Exception {
        setSalarioMensal(converteSalario(valor));
    }
    @Override
    public double getSalarioAtual() {
        return getSalarioMensal();
    }
}
