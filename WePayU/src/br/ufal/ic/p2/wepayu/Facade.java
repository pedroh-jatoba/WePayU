package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.*;

public class Facade {
    private Map<String, Empregado> empregados = new LinkedHashMap<>();
    private int contadorId = 1;

    public Facade() {
        try{
            File banco = new File("banco.xml");
            if (banco.exists()) {
                FileInputStream f = new FileInputStream(banco);
                XMLDecoder decoder = new XMLDecoder(f);

                this.empregados = (Map<String, Empregado>) decoder.readObject();
                this.contadorId = (int) decoder.readObject();

                decoder.close();
                f.close();
            }
        }catch (Exception e){}
    }

    public String getEmpregadoPorNome (String nome, String indice) throws EmpregadoNaoExisteException {

        int i = Integer.parseInt(indice);
        int cont = 0;

        for (Map.Entry<String, Empregado> entrada : empregados.entrySet()) {
            Empregado e = entrada.getValue();
            if(e.getNome().equals(nome)){
                cont++;
                if(cont == i){
                    return entrada.getKey();
                }
            }
        }
        throw new EmpregadoNaoEncontrado();
    }

    public String getAtributoEmpregado(String e, String atributo) throws Exception {
        if (e == null || e.isEmpty()) {
            throw new IdNulo();
        }

        Empregado empregado = empregados.get(e);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }

        if (atributo.equals("nome")) {
            return empregado.getNome();
        } else if (atributo.equals("endereco")) {
            return empregado.getEndereco();
        } else if (atributo.equals("tipo")) {
            return empregado.getTipo();
        } else if (atributo.equals("sindicalizado")) {
            return "false";
        } else if (atributo.equals("salario")) {
            String salarioFormatado = String.format("%.2f", empregado.getSalario());
            return salarioFormatado.replace(".", ",");
        } else if (atributo.equals("comissao")) {
            if (empregado instanceof EmpregadoComissionado) {
                EmpregadoComissionado empComissionado = (EmpregadoComissionado) empregado;
                String comissaoFormatada = String.format("%.2f", empComissionado.getComissao());
                return comissaoFormatada.replace(".", ",");

            } else {
                throw new AtributoInexistente();
            }
        }
        throw new AtributoInexistente();
    }

    public void zerarSistema() {
        empregados.clear();
        contadorId = 1;
        new File("banco.xml").delete();
    }

    public void encerrarSistema(){
        try {
            FileOutputStream f = new FileOutputStream("banco.xml");
            XMLEncoder encoder = new XMLEncoder(f);

            encoder.writeObject(this.empregados);
            encoder.writeObject(this.contadorId);

            encoder.close();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        if(nome == null || nome.isEmpty()) {
            throw new NomeNulo();
        } else if(salario == null || salario.isEmpty()){
            throw new SalarioNulo();
        } else if (endereco == null || endereco.isEmpty()) {
            throw new EnderecoNulo();
        } else if (!tipo.equals("horista") && !tipo.equals("assalariado") && !tipo.equals("comissionado")) {
            throw new TipoInvalido();
        } else if(tipo.equals("comissionado")) {
            throw new TipoNaoAplicavel();
        }

        double salarioConvertido;
        try {
            salarioConvertido = Double.parseDouble(salario.replace(",", "."));//troca vírgula por ponto
        } catch (NumberFormatException e) {
            throw new SalarioNaoNumerico();
        }

        if (salarioConvertido < 0) {
            throw new SalarioNegativo();
        }

        // GErrar ID
        String id = String.valueOf(contadorId++);
        Empregado novo = new Empregado(nome, endereco, tipo, salarioConvertido);

        empregados.put(id, novo);

        return id;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        if(nome == null || nome.isEmpty()) {
            throw new NomeNulo();
        } else if(salario == null || salario.isEmpty()){
            throw new SalarioNulo();
        } else if (endereco == null || endereco.isEmpty()) {
            throw new EnderecoNulo();
        } else if (!tipo.equals("horista") && !tipo.equals("assalariado") && !tipo.equals("comissionado")) {
            throw new TipoInvalido();
        } else if (comissao == null || comissao.isEmpty()) {
            throw new ComissaoNula();
        } else if (tipo.equals("horista") || tipo.equals("assalariado")) {
            throw new TipoNaoAplicavel();
        }

        double salarioConvertido;
        try {
            salarioConvertido = Double.parseDouble(salario.replace(",", "."));//troca vírgula por ponto
        } catch (NumberFormatException e) {
            throw new SalarioNaoNumerico();
        }

        if (salarioConvertido < 0) {
            throw new SalarioNegativo();
        }

        double comissaoConvertida;
        try {
            comissaoConvertida = Double.parseDouble(comissao.replace(",", "."));//troca vírgula por ponto
        } catch (NumberFormatException e) {
            throw new ComissaoNaoNumerica();
        }

        if (comissaoConvertida < 0) {
            throw new ComissaoNegativa();
        }
        String id = String.valueOf(contadorId++);
        Empregado novo = new EmpregadoComissionado(nome, endereco, tipo, salarioConvertido, comissaoConvertida);

        empregados.put(id, novo);

        return id;
    }
}
