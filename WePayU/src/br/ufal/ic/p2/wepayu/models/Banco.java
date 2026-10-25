package br.ufal.ic.p2.wepayu.models;

public class Banco extends MetodoPagamento{
    private String banco;
    private String agencia;
    private String contaCorrente;

    public Banco(){super();}
    public Banco(String banco, String agencia, String contaCorrente){
        super("banco");
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getBanco() {
        return banco;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    @Override
    public String getAtributo(String atributo) throws Exception {
        switch (atributo) {
            case "banco":
                return getBanco();

            case "agencia":
                return getAgencia();

            case "contaCorrente":
                return getContaCorrente();

            default:
                return super.getAtributo(atributo);
        }
    }
}
