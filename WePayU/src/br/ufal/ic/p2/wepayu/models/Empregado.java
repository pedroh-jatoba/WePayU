package br.ufal.ic.p2.wepayu.models;


public class Empregado {
    private String nome;
    private String endereco;
    private String tipo;
    private boolean sindicalizado;

    public Empregado(){}
    public Empregado(String nome, String endereco, String tipo, boolean sindicalizado){
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.sindicalizado = sindicalizado;
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
}
