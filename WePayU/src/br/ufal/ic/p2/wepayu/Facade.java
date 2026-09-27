package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;

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
     * Pega o valor da data como string e a retorna como localdata
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
     * @param emp
     * ID do empregado
     * Valor do ID do empregado
     * @throws Exception
     * Retorna a excecao de id nulo ou de Empregado nao existe
     */
    public void verificaEmpregado(String emp) throws Exception {
        if(emp == null || emp.isEmpty()){
            throw new IdNulo();
        }
        if(empregados.get(emp) == null){
            throw new EmpregadoNaoExisteException();
        }
    }

    /**
     *  * Passa para o id para inteiro
     *  * Procura o nome no Map empregados
     *  * Se o índice do Map bater com o índice que foi solicitado, retorna a chave para acessar o objeto empregado
     *  * Se não acha, solta a exceção: Empregado não encontrado.
     * @param nome
     * Nome do Empregado
     * @param indice
     * Indice do empregado
     * @return
     * Retorna a chave para acessar o empregado
     */
    public String getEmpregadoPorNome (String nome, String indice) {
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

    /**
     * @param empregado
     * Recebe o objeto empregado
     * @return
     * Retorna seu valor (se for horista retorna salário por hora, se não retorna mensal)
     */
    /*public double getSalario(Empregado empregado){
        double salario;
        if(empregado.getTipo().equals("horista")) {
            EmpregadoHorista horista = (EmpregadoHorista) empregado;
            salario = horista.getSalarioPorHora();
        }else if(empregado.getTipo().equals("assalariado")){
            EmpregadoAssalariado assalariado = (EmpregadoAssalariado) empregado;
            salario = assalariado.getSalarioMensal();
        }
        else{
            EmpregadoComissionado comissionado = (EmpregadoComissionado) empregado;
            salario = comissionado.getSalarioMensal();
        }
        return salario;
    }
     */

    /**
     * Pega o id do empregado e o atributo solicitado
     * Se o id é nulo, solta a exceção de id nulo
     * Se o id não aponta para um empregado existente, solta a exceção de empregado não existe
     * Mostra o atributo de acordo com o que foi pedido.
     * Se o atributo solicitado não existe, solta a exceção de atributo inexistente
     * @param emp
     * Chave para acessar o empregado
     * @param atributo
     * atributo a ser retornado
     * @return
     * retorna o atributo desejado como string
     * @throws Exception
     * retorna exceçoes
     */
    public String getAtributoEmpregado(String emp, String atributo) throws Exception {
        verificaEmpregado(emp);
        Empregado empregado = empregados.get(emp);
        if (atributo.equals("nome")) {
            return empregado.getNome();
        }
        if (atributo.equals("endereco")) {
            return empregado.getEndereco();
        }
        if (atributo.equals("tipo")) {
            return empregado.getTipo();
        }
        if (atributo.equals("sindicalizado")) {
            if(empregado.isSindicalizado()){
                return "true";
            }
            else {
                return "false";
            }
        }
        if (atributo.equals("salario")) {
            double salario;
            if(empregado.getTipo().equals("horista")) {
                EmpregadoHorista horista = (EmpregadoHorista) empregado;
                salario = horista.getSalarioPorHora();
            }else if(empregado.getTipo().equals("assalariado")){
                EmpregadoAssalariado assalariado = (EmpregadoAssalariado) empregado;
                salario = assalariado.getSalarioMensal();
            }
            else{
                EmpregadoComissionado comissionado = (EmpregadoComissionado) empregado;
                salario = comissionado.getSalarioMensal();
            }
            String salariostr = String.format("%.2f", salario);
            return salariostr.replace(".", ",");
        }
        if (atributo.equals("comissao")) {
            if (empregado.getTipo().equals("comissionado")) {
                EmpregadoComissionado empComissionado = (EmpregadoComissionado) empregado;
                String comissaoFormatada = String.format("%.2f", empComissionado.getComissao());
                return comissaoFormatada.replace(".", ",");
            } else {
                throw new EmpregadoNaoComissionado();
            }
        }
        if(atributo.equals("idSindicato")){
            if(empregado.isSindicalizado()){
                Sindicato s = empregados.get(emp).getSindicato();
                return s.getIdMembro();
            }else {
                throw new EmpregadoNaoSindicalizado();
            }
        }
        if(atributo.equals("metodoPagamento")){
            MetodoPagamento m = empregado.getMetodoPagamento();
            return m.getTipo();
        }
        if(atributo.equals("banco")){
            MetodoPagamento metodoPagamento = empregado.getMetodoPagamento();
            if(!metodoPagamento.getTipo().equals("banco")){
                throw new EmpregadoNaoBanco();
            }
            Banco banco = (Banco) metodoPagamento;
            return banco.getBanco();
        }
        if(atributo.equals("agencia")){
            MetodoPagamento metodoPagamento = empregado.getMetodoPagamento();
            if(!metodoPagamento.getTipo().equals("banco")){
                throw new EmpregadoNaoBanco();
            }
            Banco banco = (Banco) metodoPagamento;
            return banco.getAgencia();
        }
        if(atributo.equals("contaCorrente")){
            MetodoPagamento metodoPagamento = empregado.getMetodoPagamento();
            if(!metodoPagamento.getTipo().equals("banco")){
                throw new EmpregadoNaoBanco();
            }
            Banco banco = (Banco) metodoPagamento;
            return banco.getContaCorrente();
        }
        if(atributo.equals("taxaSindical")){
            if(empregado.isSindicalizado()) {
                Sindicato s = empregados.get(emp).getSindicato();
                String taxaSindical = String.format("%.2f", s.getTaxaSindical());
                return taxaSindical.replace(".", ",");
            } else {
                throw new EmpregadoNaoSindicalizado();
            }
        }
        throw new AtributoInexistente();
    }

    /**
     * @param membro
     * Id de Membro
     * @return
     * Retorna o membro do Sindicato
     */
    public Sindicato buscaMembro(String membro){
        for (Map.Entry<String, Empregado> entrada : empregados.entrySet()) {
            Empregado e = entrada.getValue();
            if(e.isSindicalizado())
            {
                Sindicato s = e.getSindicato();
                if(s.getIdMembro().equals(membro)){
                    return s;
                }
            }
        }
        throw new MembroInexistente();
    }


    /**
     * @param emp
     * Chave para acessar empregado
     * @param atributo
     * Atributo a ser alterado
     * @param valor
     * Valor que o atributo receberá
     * @throws Exception
     */
    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception {
        verificaEmpregado(emp);
        Empregado empregado = (Empregado) empregados.get(emp);

        if (atributo == null || atributo.isEmpty()) {
            throw new AtributoInexistente();
        }

        switch (atributo) {
            case "nome":
                if (valor == null || valor.isEmpty()) {
                    throw new NomeNulo();
                }
                empregado.setNome(valor);
                break;

            case "endereco":
                if (valor == null || valor.isEmpty()) {
                    throw new EnderecoNulo();
                }
                empregado.setEndereco(valor);
                break;

            case "tipo":
                double salario;
                if (valor.equals("assalariado")) {
                    EmpregadoAssalariado assalariado = (EmpregadoAssalariado) empregado;
                    salario = assalariado.getSalarioMensal();
                    empregados.put(emp, new EmpregadoAssalariado(empregado, salario));
                    empregados.get(emp).setTipo("assalariado");
                } else if (valor.equals("comissionado")) {
                    EmpregadoComissionado comissionado = (EmpregadoComissionado) empregado;
                    salario = comissionado.getSalarioMensal();
                    empregados.put(emp, new EmpregadoComissionado(empregado,salario, 0.0));
                    empregados.get(emp).setTipo("comissionado");
                } else if (valor.equals("horista")) {
                    EmpregadoHorista horista = (EmpregadoHorista) empregado;
                    salario = horista.getSalarioPorHora();
                    empregados.put(emp, new EmpregadoHorista(empregado, salario));
                    empregados.get(emp).setTipo("horista");
                } else {
                    throw new TipoInvalido();
                }
                break;

            case "salario":
                if (valor == null || valor.isEmpty()) {
                    throw new SalarioNulo();
                }
                double salarioConvertido;
                try {
                    salarioConvertido = Double.parseDouble(valor.replace(",", "."));
                } catch (NumberFormatException e) {
                    throw new SalarioNaoNumerico();
                }
                if (salarioConvertido < 0) {
                    throw new SalarioNegativo();
                }

                if (empregado instanceof EmpregadoComissionado) {
                    ((EmpregadoComissionado) empregado).setSalarioMensal(salarioConvertido);
                } else if (empregado instanceof EmpregadoAssalariado) {
                    ((EmpregadoAssalariado) empregado).setSalarioMensal(salarioConvertido);
                } else if (empregado instanceof EmpregadoHorista) {
                    ((EmpregadoHorista) empregado).setSalarioPorHora(salarioConvertido);
                }
                break;

            case "comissao":
                if (!empregado.getTipo().equals("comissionado")) {
                    throw new EmpregadoNaoComissionado();
                }
                if (valor == null || valor.isEmpty()) {
                    throw new ComissaoNula();
                }
                double comissaoConvertida;
                try {
                    comissaoConvertida = Double.parseDouble(valor.replace(",", "."));
                } catch (NumberFormatException e) {
                    throw new ComissaoNaoNumerica();
                }
                if (comissaoConvertida < 0) {
                    throw new ComissaoNegativa();
                }
                ((EmpregadoComissionado) empregado).setComissao(comissaoConvertida);
                break;

            case "sindicalizado":
                if (valor.equalsIgnoreCase("true")) {
                    empregado.setSindicalizado(true);
                } else if (valor.equalsIgnoreCase("false")) {
                    empregado.setSindicalizado(false);
                } else {
                    throw new NaoBooleano();
                }
                break;

            case "metodoPagamento":
                if(valor.equals("correios") || valor.equals("emMaos")){
                    MetodoPagamento metodoPagamento = new MetodoPagamento(valor);
                    empregados.get(emp).setMetodoPagamento(metodoPagamento);
                } else if(valor.equals("banco")){
                    throw new MetodoInvalido();
                }
                else {
                    throw new MetodoInvalido();
                }
                break;

            default:
                throw new AtributoInexistente();
        }
    }
    /**
     * @param emp
     * Chave para acessar o empregado
     * @param atributo
     * Atributo a ser alterado
     * @param valor
     * Valor que o atributo receberá
     * @param idSindicato
     * Id de membro do sindicato
     * @param taxaSindical
     * Taxa Sindical
     * @throws Exception
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception {
        verificaEmpregado(emp);
        Empregado empregado = empregados.get(emp);
        if (!atributo.equals("sindicalizado")) {
            throw new AtributoInexistente();
        }
        if(valor.equals("true")) {
            if(idSindicato == null || idSindicato.isEmpty()){
                throw new IdSindicatoNulo();
            }
            if(taxaSindical == null || taxaSindical.isEmpty()){
                throw new TaxaSindicalNula();
            }
            double taxaConvertida;
            try {
                taxaConvertida = Double.parseDouble(taxaSindical.replace(",", "."));
            } catch (NumberFormatException e) {
                throw new TaxaSindicalNaoNumerica();
            }
            if (taxaConvertida < 0) {
                throw new TaxaSindicalNegativo();
            }
            for (Map.Entry<String, Empregado> entrada : empregados.entrySet()) {
                Empregado e = entrada.getValue();
                if(e.isSindicalizado())
                {
                    Sindicato s = e.getSindicato();
                    if(s.getIdMembro().equals(idSindicato)){
                        throw new IdMembroRepetido();
                    }
                }
            }
            Sindicato s = new Sindicato(idSindicato, taxaConvertida);
            empregados.get(emp).setSindicato(s);
            empregados.get(emp).setSindicalizado(true);
        } else {
            throw new TipoInvalido();
        }
    }

    /**
     * @param emp
     * Chave para acessar o empregado
     * @param atributo
     * Atributo a ser alterado (Metodo de Pagamento)
     * @param valor
     * Valor que o atributo receberá (Banco)
     * @param banco
     * Banco
     * @param agencia
     * Agencia
     * @param contaCorrente
     * Conta Corrente
     * @throws Exception
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String banco, String agencia, String contaCorrente) throws Exception {
        verificaEmpregado(emp);

        if (atributo.equals("metodoPagamento")) {
            if (valor.equals("banco")) {
                if (banco == null || banco.isEmpty()) {
                    throw new BancoNulo();
                }
                if (agencia == null || agencia.trim().isEmpty()) {
                    throw new AgenciaNula();
                }
                if (contaCorrente == null || contaCorrente.trim().isEmpty()) {
                    throw new ContaNula();
                }

                Banco b = new Banco(banco, agencia, contaCorrente);
                empregados.get(emp).setMetodoPagamento(b);
                return;
            } else {
                throw new MetodoInvalido();
            }
        }
    }


    /**
     * @param emp
     * Chave para acessar o empregado
     * @param atributo
     * Atributo a ser alterado
     * @param valor
     * Valor que o atributo receberá
     * @param valor2
     * Valor que a comissao ou o salario recebera
     * @throws Exception
     */

    public void alteraEmpregado(String emp, String atributo, String valor, String valor2) throws Exception {
        verificaEmpregado(emp);
        Empregado empregado = empregados.get(emp);
        if (!atributo.equals("tipo")) {
            throw new AtributoInexistente();
        }
        if (!valor.equals("comissionado") && !valor.equals("horista")) {
            throw new TipoInvalido();
        }
        if (valor2 == null || valor2.isEmpty()) {
            throw new ComissaoNula();
        }

        double valorDouble;
        try {
            valorDouble = Double.parseDouble(valor2.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ComissaoNaoNumerica();
        }

        if (valorDouble < 0) {
            throw new ComissaoNegativa();
        }
        empregado.setTipo(valor);
        if(valor.equals("comissionado")){
            EmpregadoComissionado comissionado = new EmpregadoComissionado(empregado, 0, valorDouble);
            empregados.put(emp, comissionado);
        }
        else {
            EmpregadoHorista horista = new EmpregadoHorista(empregado, valorDouble);
            empregados.put(emp, horista);
        }
    }
    /**
     * getHorasNormaisTrabalhadas():
     * Passa a data inicial e a data final para um objeto LocalDate
     * Verifica ID do empregado
     * A partir do ID do empregado, acessa o Metodo getHorasNormaisTrabalhadas da classe EmpregadoHorista
     * Pega o valor em double que o metodo retorna e formata para passar no teste
     * @param emp
     * Chave para acessar o empregado
     * @param dataInicial
     * Data inicial
     * @param dataFinal
     * Data final
     * @return
     * retorna as horas normais trabalhadas em formato de string
     * @throws Exception
     */
    public String getHorasNormaisTrabalhadas (String emp, String dataInicial, String dataFinal) throws Exception {
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
        verificaEmpregado(emp);
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
     * Chave para acessar o empregado
     * @param dataInicial
     * Data inicial
     * @param dataFinal
     * Data final
     * @return
     * retorna as horas extras trabalhadas em formato de string
     */
    public String getHorasExtrasTrabalhadas(String emp, String dataInicial, String dataFinal) throws Exception {
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
        verificaEmpregado(emp);
        if(!empregados.get(emp).getTipo().equals("horista")){
            throw new EmpregadoNaoHorista();
        }
        EmpregadoHorista e = (EmpregadoHorista) empregados.get(emp);
        double horasTrabalhadas = e.getHorasExtrasTrabalhadas(inicio, fim);
        if (horasTrabalhadas == (long) horasTrabalhadas) {
            return String.valueOf((long) horasTrabalhadas);
        } else {
            return String.valueOf(horasTrabalhadas).replace(".", ",");
        }
    }
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(java.time.format.ResolverStyle.STRICT);
        LocalDate inicio, fim;
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
        verificaEmpregado(emp);
        if(!empregados.get(emp).isSindicalizado()){
            throw new EmpregadoNaoSindicalizado();
        }
        Sindicato s = empregados.get(emp).getSindicato();
        double taxasServico = s.getTaxasServico(inicio, fim);
        return String.format("%.2f", taxasServico).replace(".", ",");
    }
    /**
     * getVendasRealizadas():
     * Passa a data inicial e a data final para um objeto LocalDate
     * Verifica ID do empregado
     * A partir do ID do empregado, acessa o Metodo getVendasRealizadas da classe EmpregadoComissionado
     * Pega o valor em double que o metodo retorna e formata para passar no teste
     * @param emp
     * Chave para acessar o empregado
     * @param dataInicial
     * Data inicial a ser levada em conta
     * @param dataFinal
     * Data inicial a ser levada em conta
     * @return
     * Retorna o valor total das vendas em forma de string
     * @throws EmpregadoNaoExisteException
     */
    public String getVendasRealizadas(String emp, String dataInicial, String dataFinal) throws Exception {
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
        verificaEmpregado(emp);
        if(!empregados.get(emp).getTipo().equals("comissionado")){
            throw new EmpregadoNaoComissionado();
        }
        EmpregadoComissionado e = (EmpregadoComissionado) empregados.get(emp);
        double vendasRealizadas = e.getVendasRealizadas(inicio, fim);
        return String.format("%.2f", vendasRealizadas).replace(".", ",");
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
     * @param emp
     * Chave para acessar o empregado
     * @throws Exception
     */
    public void removerEmpregado (String emp) throws Exception{
        verificaEmpregado(emp);
        empregados.remove(emp);
    }
    /**
     * @param emp
     * @param data
     * @param horas
     * @throws Exception
     * Vê se empregado é uma chave para o Map empregados
     * Cria um cartão de ponto
     * Adiciona o cartao na lista
     */
    public void lancaCartao(String emp, String data, String horas) throws Exception{
        verificaEmpregado(emp);
        if(!empregados.get(emp).getTipo().equals("horista")){
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
        EmpregadoHorista h = (EmpregadoHorista) empregados.get(emp);
        CartaoDePonto c = new CartaoDePonto(emp, data, Double.parseDouble(horas.replace(",", ".")));
        h.adicionarCartao(c);
    }

    /**
     * Vê se empregado é uma chave para o Map empregados
     * Cria um resultado de venda
     * Adiciona a venda na lista
     * @param emp
     * @param data
     * @param venda
     * @throws Exception
     */
    public void lancaVenda(String emp, String data, String venda) throws Exception{
        verificaEmpregado(emp);

        if(!empregados.get(emp).getTipo().equals("comissionado")){
            throw new EmpregadoNaoComissionado();
        }

        double vendaConvertida;
        try {
            vendaConvertida = Double.parseDouble(venda.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValorNegativo();
        }

        if (vendaConvertida <= 0) {
            throw new ValorNegativo();
        }
        converterData(data);
        EmpregadoComissionado c = (EmpregadoComissionado) empregados.get(emp);
        ResultadoDeVenda v = new ResultadoDeVenda(emp, data, Double.parseDouble(venda.replace(",", ".")));
        c.adicionarVenda(v);
    }


    public void lancaTaxaServico(String membro, String data, String valor) throws Exception{
        if(membro == null || membro.isEmpty()){
            throw new IdMembroNulo();
        }
        Sindicato m = buscaMembro(membro);

        double valorConvertido;
        try {
            valorConvertido = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValorNegativo();
        }

        if (valorConvertido <= 0) {
            throw new ValorNegativo();
        }
        converterData(data);
        TaxaServico t = new TaxaServico(data, Double.parseDouble(valor.replace(",", ".")));
        m.adicionarTaxaServico(t);
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
        MetodoPagamento metodoPagamento = new MetodoPagamento("emMaos");
        // GErrar ID
        String id = String.valueOf(contadorId++);
        if(tipo.equals("horista")){
            EmpregadoHorista novo = new EmpregadoHorista(nome, endereco, salarioConvertido, metodoPagamento);
            empregados.put(id, novo);
        } else {
            EmpregadoAssalariado novo = new EmpregadoAssalariado(nome, endereco, salarioConvertido, metodoPagamento);
            empregados.put(id, novo);
        }

        return id;
    }
    /**
     * @param nome
     * Nome do empregado
     * @param endereco
     * Endereco do
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
        MetodoPagamento metodoPagamento = new MetodoPagamento("emMaos");
        String id = String.valueOf(contadorId++);
        EmpregadoComissionado novo = new EmpregadoComissionado(nome, endereco, salarioConvertido, comissaoConvertida, metodoPagamento);

        empregados.put(id, novo);

        return id;
    }
}
