package br.ufal.ic.p2.wepayu.models;


public class Empregado {
    private String nome;
    private String endereco;
    private String tipo;
    private boolean sindicalizado;
    private MetodoPagamento metodoPagamento;
    private Sindicato sindicato;

    public Empregado(){}
    public Empregado(MetodoPagamento metodoPagamento, String nome, String endereco, String tipo, boolean sindicalizado){
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.sindicalizado = sindicalizado;
        this.metodoPagamento = metodoPagamento;
    }
    public Empregado(Empregado empregado){
        this.nome = empregado.getNome();
        this.endereco = empregado.getEndereco();
        this.tipo = empregado.getTipo();
        this.sindicalizado = empregado.isSindicalizado();
        this.metodoPagamento = empregado.getMetodoPagamento();
        if(this.sindicalizado){
            this.sindicato = empregado.getSindicato();
        }
    }
    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTipo() {
        return tipo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setSindicalizado(boolean sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    public boolean isSindicalizado() {
        return sindicalizado;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }



    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
}
