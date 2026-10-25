package br.ufal.ic.p2.wepayu.models;


import br.ufal.ic.p2.wepayu.Exception.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    public String getResumoMetodoPagamento() {
        switch (metodoPagamento.getTipo()) {
            case "banco":
                Banco b = (Banco) metodoPagamento;
                return b.getBanco() + ", Ag. " + b.getAgencia() + " CC " + b.getContaCorrente();
            case "correios":
                return "Correios, " + getEndereco();
            default:
                return "Em maos";
        }
    }

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    public double getSalarioAtual() {
        return 0;
    }

    public String getAtributo(String atributo) throws Exception {
        switch (atributo) {
            case "nome":
                return getNome();

            case "endereco":
                return getEndereco();

            case "tipo":
                return getTipo();

            case "sindicalizado":
                return String.valueOf(isSindicalizado());

            case "metodoPagamento":
                return getMetodoPagamento().getTipo();

            case "comissao":
                throw new EmpregadoNaoComissionado();

            case "banco":
            case "agencia":
            case "contaCorrente":
                return metodoPagamento.getAtributo(atributo);

            case "idSindicato":
            case "taxaSindical":
                if (!isSindicalizado()) {
                    throw new EmpregadoNaoSindicalizado();
                }
                return sindicato.getAtributo(atributo);

            default:
                throw new AtributoInexistente();
        }
    }
    public double getDescontos(LocalDate inicio, LocalDate fim, int dias) {
        if (!sindicalizado) return 0.0;
        return sindicato.getTaxasServico(inicio, fim) + sindicato.getTaxaSindical() * dias;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public void alteraAtributo(String atributo, String valor)
            throws Exception {

        switch (atributo) {
            case "nome":
                if (valor == null || valor.isEmpty()) {
                    throw new NomeNulo();
                }
                setNome(valor);
                break;

            case "endereco":
                if (valor == null || valor.isEmpty()) {
                    throw new EnderecoNulo();
                }
                setEndereco(valor);
                break;

            case "tipo":
                throw new TipoInvalido();

            case "salario":
                alteraSalario(valor);
                break;

            case "comissao":
                throw new EmpregadoNaoComissionado();

            case "sindicalizado":
                if (valor == null) {
                    throw new NaoBooleano();
                }

                if (valor.equalsIgnoreCase("true")) {
                    setSindicalizado(true);
                } else if (valor.equalsIgnoreCase("false")) {
                    setSindicalizado(false);
                } else {
                    throw new NaoBooleano();
                }
                break;

            case "metodoPagamento":
                alteraMetodoPagamento(valor);
                break;

            default:
                throw new AtributoInexistente();
        }
    }

    protected void alteraSalario(String valor) throws Exception {
        throw new AtributoInexistente();
    }

    protected void alteraMetodoPagamento(String valor) throws Exception {
        if ("correios".equals(valor) || "emMaos".equals(valor)) {
            setMetodoPagamento(new MetodoPagamento(valor));
        } else {
            throw new MetodoInvalido();
        }
    }

    protected double converteSalario(String valor) throws Exception {
        if (valor == null || valor.isEmpty()) {
            throw new SalarioNulo();
        }

        double salario;

        try {
            salario = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new SalarioNaoNumerico();
        }

        if (salario < 0) {
            throw new SalarioNegativo();
        }

        return salario;
    }

    protected double converteComissao(String valor) throws Exception {
        if (valor == null || valor.isEmpty()) {
            throw new ComissaoNula();
        }

        double comissao;

        try {
            comissao = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ComissaoNaoNumerica();
        }

        if (comissao < 0) {
            throw new ComissaoNegativa();
        }

        return comissao;
    }
    public void sindicalizar(String idSindicato, double taxa) {
        Sindicato sindicato = new Sindicato(idSindicato, taxa);

        setSindicato(sindicato);
        setSindicalizado(true);
    }

    public void alteraMetodoPagamento(String valor, String banco, String agencia, String contaCorrente) throws Exception {

        if (!"banco".equals(valor)) {
            throw new MetodoInvalido();
        }

        if (banco == null || banco.isEmpty()) {
            throw new BancoNulo();
        }

        if (agencia == null || agencia.trim().isEmpty()) {
            throw new AgenciaNula();
        }

        if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
            throw new ContaNula();
        }

        setMetodoPagamento(new Banco(banco, agencia, contaCorrente));
    }
    public LocalDate converterData(String dataStr) throws Exception {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);
            return LocalDate.parse(dataStr, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInvalida();
        }
    }
}
