package br.ufal.ic.p2.wepayu.models;


public class Empregado {
    private String nome;
    private String endereco;
    private String tipo;

    public Empregado(){}
    public Empregado(String nome, String endereco, String tipo){
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
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

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
