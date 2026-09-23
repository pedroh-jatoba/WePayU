package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.Empregado;
import br.ufal.ic.p2.wepayu.models.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.models.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.models.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.models.CartaoDePonto;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.time.LocalDate;
import java.time.format.*;
import java.util.*;

public class Facade {
    private Map<String, Empregado> empregados = new LinkedHashMap<>();
    private int contadorId = 1;
/**
 * Construtor Facade()
 * Abre o Arquivo banco.xml
 * Passa o que está no arquivo para o Map empregados
*/
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

    /**
     * Formatador de data
     * @param dataStr
     * @return
     * @throws Exception
     */
    private LocalDate converterData(String dataStr) throws Exception {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);
            return LocalDate.parse(dataStr, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInvalida();
        }
    }

    /**
     * verifica se o id é nulo
     * @param id
     */
    private void verificaIdNulo(String id)
    {
        if(id == null || id.isEmpty()){
            throw new IdNulo();
        }
    }
/**
 * getEmpregadoPorNome:
 * Pega o nome e o id do empregado
 * Passa para o id para inteiro
 * Procura o nome no Map empregados
 * Se o índice do Map bater com o índice que foi solicitado, retorna a chave para acessar o objeto empregado
 * Se não acha, solta a exceção: Empregado não encontrado.
 */
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
    /** getAtributoEmpregado
     * Pega o id do empregado e o atributo solicitado
     * Se o id é nulo, solta a exceção de id nulo
     * Se o id não aponta para um empregado existente, solta a exceção de empregado não existe
     * Mostra o atributo de acordo com o que foi pedido.
     * Se o atributo solicitado não existe, solta a exceção de atributo inexistente
     * */
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
            String salarioFormatado;
            if(empregado.getTipo().equals("horista")) {
                EmpregadoHorista horista = (EmpregadoHorista) empregado;
                salarioFormatado = String.format("%.2f", horista.getSalarioPorHora());
            }else if(empregado.getTipo().equals("assalariado")){
                EmpregadoAssalariado assalariado = (EmpregadoAssalariado) empregado;
                salarioFormatado = String.format("%.2f", assalariado.getSalarioMensal());
            }
            else{
                EmpregadoComissionado comissionado = (EmpregadoComissionado) empregado;
                salarioFormatado = String.format("%.2f", comissionado.getSalarioMensal());
            }
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

    /**
     * getHorasNormaisTrabalhadas():
     * Passa a data inicial e a data final para um objeto LocalDate
     * Verifica ID do empregado
     * A partir do ID do empregado, acessa o Metodo getHorasNormaisTrabalhadas da classe EmpregadoHorista
     * Pega o valor em double que o metodo retorna e formata para passar no teste
     * @param emp
     * @param dataInicial
     * @param dataFinal
     * @return
     */
    public String getHorasNormaisTrabalhadas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);
        LocalDate inicio;
        LocalDate fim;
        try {
            inicio = LocalDate.parse(dataInicial, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInicialInvalida();
        }
        try {
            fim = LocalDate.parse(dataFinal, formatter);
        } catch (DateTimeParseException e) {
            throw new DataFinalInvalida();
        }
        verificaIdNulo(emp);
        if(!empregados.containsKey(emp)){
            throw new EmpregadoNaoExisteException();
        }
        if(!empregados.get(emp).getTipo().equals("horista")){
            throw new EmpregadoNaoHorista();
        }
        EmpregadoHorista e = (EmpregadoHorista) empregados.get(emp);
        double horasTrabalhadas = e.getHorasNormaisTrabalhadas(inicio, fim);
        if (horasTrabalhadas == (long) horasTrabalhadas) {
            return String.valueOf((long) horasTrabalhadas);
        } else {
            return String.valueOf(horasTrabalhadas).replace(".", ",");
        }
    }
    /**
     * getHorasExtrasTrabalhadas():
     * Passa a data inicial e a data final para um objeto LocalDate
     * Verifica ID do empregado
     * A partir do ID do empregado, acessa o Metodo getHorasExtrasTrabalhadas da classe EmpregadoHorista
     * Pega o valor em double que o metodo retorna e formata para passar no teste
     * @param emp
     * @param dataInicial
     * @param dataFinal
     * @return
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws EmpregadoNaoExisteException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);
        LocalDate inicio;
        LocalDate fim;
        try {
            inicio = LocalDate.parse(dataInicial, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInicialInvalida();
        }
        try {
            fim = LocalDate.parse(dataFinal, formatter);
        } catch (DateTimeParseException e) {
            throw new DataFinalInvalida();
        }
        verificaIdNulo(emp);
        if(!empregados.containsKey(emp)){
            throw new EmpregadoNaoExisteException();
        }
        EmpregadoHorista e = (EmpregadoHorista) empregados.get(emp);
        double horasTrabalhadas = e.getHorasExtrasTrabalhadas(inicio, fim);
        if (horasTrabalhadas == (long) horasTrabalhadas) {
            return String.valueOf((long) horasTrabalhadas);
        } else {
            return String.valueOf(horasTrabalhadas).replace(".", ",");
        }
    }
/**
 * zerarSistema
 * Limpa o Map empregados
 * Volta o contador de ids para 1
 * Apaga o arquivo banco.xml
 */
    public void zerarSistema() {
        empregados.clear();
        contadorId = 1;
        new File("banco.xml").delete();
    }
/**
 * encerrarSistema()
 * reescreve o arquivo banco.xml com o Map empregados e com o valor do contador
 */
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
/**
 * removerEmpregado()
 * Verifica se o id é nulo ou vazio
 * Verifica se o id aponta para um empregado existente
 * Remove o empregado
 */
    public void removerEmpregado (String i) throws Exception{
        if(i ==  null || i.isEmpty()){
            throw new IdNulo();
        }
        if(!empregados.containsKey(i)){
            throw new EmpregadoNaoExisteException();
        }

        empregados.remove(i);
    }

    /**
     * @param empregado
     * @param data
     * @param horas
     * @throws Exception
     * Vê se empregado é uma chave para o Map empregados
     * Cria um cartão de ponto
     * Adiciona o cartao na lista
     */
    public void lancaCartao(String empregado, String data, String horas) throws Exception{
        if(empregado == null || empregado.isEmpty()){
            throw new IdNulo();
        }

        if(!empregados.containsKey(empregado)){
            throw new EmpregadoNaoExisteException();
        }

        if(!empregados.get(empregado).getTipo().equals("horista")){
            throw new EmpregadoNaoHorista();
        }

        double horasConvertidas;
        try {
            horasConvertidas = Double.parseDouble(horas.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new HorasNegativas();
        }

        if (horasConvertidas <= 0) {
            throw new HorasNegativas();
        }
        converterData(data);
        EmpregadoHorista h = (EmpregadoHorista) empregados.get(empregado);
        CartaoDePonto c = new CartaoDePonto(empregado, data, Double.parseDouble(horas.replace(",", ".")));
        h.adicionarCartao(c);
    }
/**
 * criarEmpregado(Com 4 atributos)
 * Função que cria o objeto empregado de tipo horário ou assalariado
 * Verifica nulidade de nome, salário e endereço
 * Verifica se o tipo existe
 * Verifica se o empregado não é comissionado (se for comissionado, deveria ser passado o atributo comissão)
 * Verifiva se a string salário é um número
 * Verifica se salario não é negativo
 * Gera a chave para acessar o empregado no map
 * Se tipo = "horista", cria o objeto empregado horista e o joga no Map
 * Senão, cria objeto empregado assalariado e joga no map
 * Joga o objeto empregado no map
 * Retorna o id do empregado criado
 */
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
        if(tipo.equals("horista")){
            EmpregadoHorista novo = new EmpregadoHorista(nome, endereco, salarioConvertido);
            empregados.put(id, novo);
        } else {
            EmpregadoAssalariado novo = new EmpregadoAssalariado(nome, endereco, salarioConvertido);
            empregados.put(id, novo);
        }

        return id;
    }
    /**
     * @param nome
     * @param endereco
     * @param tipo
     * @param salario
     * @param comissao
     * @return
     * @throws Exception
     * criarEmpregado(Com 5 atributos)
     * Função que cria o objeto empregado de tipo comissionado
     * Verifica nulidade de nome, salário, comissão e endereço
     * Verifica se o tipo existe
     * Verifica se o empregado não é horrário ou assalariado
     * Verifiva se a string salário é um número
     * Verifica se salario não é negativo
     * Verifiva se a string comissão é um número
     * Verifica se comissão não é negativa
     * Gera a chave para acessar o empregado no map
     * Cria o objeto empregadoComissionado
     * Joga o objeto empregadoComissionado no map
     * Retorna o id do empregado criado
     */
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
        EmpregadoComissionado novo = new EmpregadoComissionado(nome, endereco, salarioConvertido, comissaoConvertida);

        empregados.put(id, novo);

        return id;
    }
}
