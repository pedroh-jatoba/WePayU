package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.Exception.*;
import br.ufal.ic.p2.wepayu.models.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;

/**
 * Fachada principal do sistema de folha de pagamento.
 *
 * <p>Centraliza as operações de cadastro e alteração de empregados, controle
 * de vendas, cartões de ponto e taxas sindicais, além da geração da folha
 * de pagamento e da persistência dos dados do sistema.</p>
 */
public class Facade {
    /** Empregados cadastrados no sistema, indexados por seus identificadores. */
    private Map<String, Empregado> empregados = new LinkedHashMap<>();
    /** Próximo identificador numérico disponível para um empregado. */
    private int contadorId = 1;
    /**
     * Inicializa a fachada e carrega os dados persistidos em {@code banco.xml},
     * caso o arquivo exista.
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

    /** Histórico de estados anteriores usado para desfazer operações. */
    private final Deque<byte[]> pilhaDesfazer = new ArrayDeque<>();
    /** Histórico de estados posteriores usado para refazer operações. */
    private final Deque<byte[]> pilhaRefazer = new ArrayDeque<>();
    /** Indica se o sistema foi encerrado e não aceita novas operações comuns. */
    private boolean encerrado = false;

    /**
     * Representa uma operação do sistema que pode lançar uma exceção.
     */
    private interface Comando {
        /**
         * Executa a operação representada pelo comando.
         *
         * @throws Exception se a operação não puder ser concluída
         */
        void executa() throws Exception;
    }

    /**
     * Executa um comando registrando o estado anterior para permitir desfazê-lo. Se ocorrer uma exceção, restaura o estado anterior.
     *
     * @param comando operação a ser executada
     * @return resultado da operação
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    private <T> T executaComandoComRetorno(Callable<T> comando) throws Exception {
        verificaSistemaAberto();
        byte[] antes = tiraFoto();
        T resultado;
        try {
            resultado = comando.call();
        } catch (Exception e) {
            restaura(antes);   // comando falhou: desfaz mudanças parciais e não registra
            throw e;
        }
        pilhaDesfazer.push(antes);
        pilhaRefazer.clear();
        return resultado;
    }

    /**
     * Executa uma operação do sistema e registra o estado anterior para permitir desfazê-la.
     *
     * @param comando operação a ser executada
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    private void executaComando(Comando comando) throws Exception {
        executaComandoComRetorno(() -> {
            comando.executa();
            return null;
        });
    }

    /**
     * Serializa o estado atual dos empregados e do contador de identificadores.
     *
     * @return representação XML do estado atual em bytes
     */
    private byte[] tiraFoto() {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        try (XMLEncoder encoder = new XMLEncoder(saida)) {
            encoder.writeObject(this.empregados);
            encoder.writeObject(this.contadorId);
        }
        return saida.toByteArray();
    }

    @SuppressWarnings("unchecked")
    /**
     * Restaura os empregados e o contador de identificadores a partir de uma cópia serializada.
     *
     * @param foto estado serializado a ser restaurado
     */
    private void restaura(byte[] foto) {
        try (XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(foto))) {
            this.empregados = (Map<String, Empregado>) decoder.readObject();
            this.contadorId = (int) decoder.readObject();
        }
    }

    /**
     * Verifica se o sistema continua aberto.
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    private void verificaSistemaAberto() throws Exception {
        if (encerrado) throw new SistemaEncerrado();
    }

    /**
     * Desfaz a última operação registrada.
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    public void undo() throws Exception {
        verificaSistemaAberto();
        if (pilhaDesfazer.isEmpty()) throw new NaoHaComandoADesfazer();
        pilhaRefazer.push(tiraFoto());
        restaura(pilhaDesfazer.pop());
    }

    /**
     * Refaz a última operação desfeita.
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    public void redo() throws Exception {
        verificaSistemaAberto();
        if (pilhaRefazer.isEmpty()) throw new NaoHaComandoARefazer();
        pilhaDesfazer.push(tiraFoto());
        restaura(pilhaRefazer.pop());
    }

    /**
     * Retorna a quantidade de empregados cadastrados.
     *
     * @return quantidade de empregados em formato textual
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    public String getNumeroDeEmpregados() throws Exception {
        verificaSistemaAberto();
        return String.valueOf(empregados.size());
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
        if (emp == null || emp.isEmpty()) {
            throw new IdNulo();
        }
        if (empregados.get(emp) == null) {
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
    public String getEmpregadoPorNome (String nome, String indice) throws Exception {
        verificaSistemaAberto();
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
        verificaSistemaAberto();
        verificaEmpregado(emp);

        Empregado empregado = empregados.get(emp);

        return empregado.getAtributo(atributo);
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
     * Cria uma instância de empregado do tipo especificado, preservando os dados do empregado recebido.
     *
     * @param empregado empregado cujos dados serão usados
     * @param tipo tipo de empregado desejado
     * @return novo empregado do tipo solicitado
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    private Empregado criaEmpregadoPorTipo(
            Empregado empregado, String tipo) throws Exception {

        switch (tipo) {
            case "assalariado":
                return new EmpregadoAssalariado(empregado);

            case "comissionado":
                return new EmpregadoComissionado(empregado, 0.0);

            case "horista":
                return new EmpregadoHorista(empregado);

            default:
                throw new TipoInvalido();
        }
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
        executaComando(() -> {
            verificaEmpregado(emp);

            Empregado empregado = empregados.get(emp);

            if ("tipo".equals(atributo)) {
                if (!"assalariado".equals(valor) && !"horista".equals(valor) && !"comissionado".equals(valor)) {
                    throw new TipoInvalido();
                }

                Empregado novoEmpregado;

                switch (valor) {
                    case "assalariado":
                        novoEmpregado = new EmpregadoAssalariado(empregado);
                        break;

                    case "horista":
                        novoEmpregado = new EmpregadoHorista(empregado);
                        break;

                    case "comissionado":
                        novoEmpregado = new EmpregadoComissionado(empregado);
                        break;

                    default:
                        throw new TipoInvalido();
                }

                empregados.put(emp, novoEmpregado);
                return;
            }

            empregado.alteraAtributo(atributo, valor);
        });
    }

    /**
     * Valida o identificador sindical e verifica se ele já está associado a outro empregado.
     *
     * @param idSindicato identificador sindical a validar
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    private void verificaIdSindicatoDisponivel(String idSindicato) throws Exception {

        if (idSindicato == null || idSindicato.isEmpty()) {
            throw new IdSindicatoNulo();
        }

        for (Empregado e : empregados.values()) {
            if (e.isSindicalizado()) {
                Sindicato sindicato = e.getSindicato();

                if (sindicato.getIdMembro().equals(idSindicato)) {
                    throw new IdMembroRepetido();
                }
            }
        }
    }

    /**
     * Converte e valida o valor textual da taxa sindical.
     *
     * @param taxaSindical valor da taxa, aceitando vírgula ou ponto decimal
     * @return valor numérico da taxa sindical
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    private double converteTaxaSindical(String taxaSindical)
            throws Exception {

        if (taxaSindical == null || taxaSindical.isEmpty()) {
            throw new TaxaSindicalNula();
        }

        double taxa;

        try {
            taxa = Double.parseDouble(taxaSindical.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new TaxaSindicalNaoNumerica();
        }

        if (taxa < 0) {
            throw new TaxaSindicalNegativo();
        }

        return taxa;
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
        executaComando(() -> {
            verificaEmpregado(emp);
            if (!"sindicalizado".equals(atributo)) {
                throw new AtributoInexistente();
            }
            if (!"true".equals(valor)) {
                throw new TipoInvalido();
            }
            verificaIdSindicatoDisponivel(idSindicato);

            double taxa = converteTaxaSindical(taxaSindical);

            Empregado empregado = empregados.get(emp);
            empregado.sindicalizar(idSindicato, taxa);
        });
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
        executaComando(() -> {
            verificaEmpregado(emp);

            if (!"metodoPagamento".equals(atributo)) {
                throw new AtributoInexistente();
            }
            if (!"banco".equals(valor)) {
                throw new MetodoInvalido();
            }
            empregados.get(emp).alteraMetodoPagamento(valor, banco, agencia, contaCorrente);
        });
    }

    /**
     * Converte e valida o valor textual da comissão.
     *
     * @param valor valor da comissão, aceitando vírgula ou ponto decimal
     * @return valor numérico da comissão
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    private double converteComissao(String valor) throws Exception {
        if (valor == null || valor.isEmpty()) {
            throw new ComissaoNula();
        }

        double valorConvertido;

        try {
            valorConvertido = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ComissaoNaoNumerica();
        }

        if (valorConvertido < 0) {
            throw new ComissaoNegativa();
        }

        return valorConvertido;
    }
    /**
     * Cria uma instância de empregado do tipo especificado, preservando os dados do empregado recebido.
     *
     * @param empregado empregado cujos dados serão usados
     * @param tipo tipo de empregado desejado
     * @return novo empregado do tipo solicitado
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    private Empregado criaEmpregadoPorTipo(
            Empregado empregado,
            String tipo,
            double valor) throws Exception {

        switch (tipo) {
            case "comissionado":
                return new EmpregadoComissionado(empregado, valor);

            case "horista":
                return new EmpregadoHorista(empregado, valor);

            default:
                throw new TipoInvalido();
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
        executaComando(() -> {
            verificaEmpregado(emp);

            if (!"tipo".equals(atributo)) {
                throw new AtributoInexistente();
            }

            if (!"comissionado".equals(valor) && !"horista".equals(valor)) {
                throw new TipoInvalido();
            }

            if (valor2 == null || valor2.trim().isEmpty()) {
                if ("comissionado".equals(valor)) {
                    throw new ComissaoNula();
                }
                throw new SalarioNulo();
            }

            double valorConvertido;

            try {
                valorConvertido = Double.parseDouble(valor2.replace(",", "."));
            } catch (NumberFormatException e) {
                if ("comissionado".equals(valor)) {
                    throw new ComissaoNaoNumerica();
                }
                throw new SalarioNaoNumerico();
            }

            if (valorConvertido < 0) {
                if ("comissionado".equals(valor)) {
                    throw new ComissaoNegativa();
                }
                throw new SalarioNegativo();
            }

            Empregado empregado = empregados.get(emp);
            Empregado novoEmpregado;

            if ("comissionado".equals(valor)) {
                novoEmpregado = new EmpregadoComissionado(
                        empregado, valorConvertido);
            } else {
                novoEmpregado = new EmpregadoHorista(
                        empregado, valorConvertido);
            }

            empregados.put(emp, novoEmpregado);
        });
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
        verificaSistemaAberto();
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
        verificaSistemaAberto();
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
    /**
     * Calcula o total das taxas de serviço de um empregado sindicalizado no período informado.
     *
     * @param emp identificador do empregado
     * @param dataInicial início do período no formato dia/mês/ano
     * @param dataFinal fim do período no formato dia/mês/ano
     * @return total das taxas de serviço com duas casas decimais
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception {
        verificaSistemaAberto();
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
        verificaSistemaAberto();
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
    public void zerarSistema() throws Exception {
        encerrado = false;
        executaComando(() -> {
            empregados.clear();
            contadorId = 1;
            new File("banco.xml").delete();
            AgendaPagamento.reiniciar();
        });
    }
    /**
     * encerrarSistema()
     * reescreve o arquivo banco.xml com o Map empregados e com o valor do contador
     */
    public void encerrarSistema() throws Exception {
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
        pilhaDesfazer.clear();
        pilhaRefazer.clear();
        encerrado = true;
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
        executaComando(() -> {
            verificaEmpregado(emp);
            empregados.remove(emp);
        });
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

    public void lancaCartao(String emp, String data, String horas) throws Exception {
        executaComando(() -> {
            verificaEmpregado(emp);

            Empregado empregado = empregados.get(emp);

            if (!empregado.getTipo().equals("horista")) {
                throw new EmpregadoNaoHorista();
            }

            EmpregadoHorista horista = (EmpregadoHorista) empregado;
            horista.lancaCartao(emp, data, horas);
        });
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
        executaComando(() -> {
            verificaEmpregado(emp);

            if (!empregados.get(emp).getTipo().equals("comissionado")) {
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
        });
    }


    /**
     * Registra uma taxa de serviço para um membro sindicalizado.
     *
     * @param membro identificador do membro sindicalizado
     * @param data data da taxa
     * @param valor valor da taxa de serviço
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    public void lancaTaxaServico(String membro, String data, String valor) throws Exception{
        executaComando(() -> {
            if (membro == null || membro.isEmpty()) {
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
        });
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
        return executaComandoComRetorno(() -> {
            if (nome == null || nome.isEmpty()) {
                throw new NomeNulo();
            } else if (salario == null || salario.isEmpty()) {
                throw new SalarioNulo();
            } else if (endereco == null || endereco.isEmpty()) {
                throw new EnderecoNulo();
            } else if (!tipo.equals("horista") && !tipo.equals("assalariado") && !tipo.equals("comissionado")) {
                throw new TipoInvalido();
            } else if (tipo.equals("comissionado")) {
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
            if (tipo.equals("horista")) {
                EmpregadoHorista novo = new EmpregadoHorista(nome, endereco, salarioConvertido, metodoPagamento);
                empregados.put(id, novo);
            } else {
                EmpregadoAssalariado novo = new EmpregadoAssalariado(nome, endereco, salarioConvertido, metodoPagamento);
                empregados.put(id, novo);
            }

            return id;
        });
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
        return executaComandoComRetorno(() -> {
            if (nome == null || nome.isEmpty()) {
                throw new NomeNulo();
            } else if (salario == null || salario.isEmpty()) {
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
        });
    }

    /**
     * Calcula o valor total da folha de pagamento para a data informada.
     *
     * @param dataString data de referência no formato dia/mês/ano
     * @return valor total da folha formatado como texto
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    public String totalFolha(String dataString) throws Exception {
        verificaSistemaAberto();
        LocalDate data = converterData(dataString);

        List<Empregado> listaEmpregados =
                new ArrayList<>(empregados.values());

        GeradorDeFolha gerador = new GeradorDeFolha();

        double total = gerador.calcularTotalGeral(listaEmpregados, data);

        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", total);
    }

    /**
     * Gera a folha de pagamento para a data informada e grava o conteúdo no arquivo de saída.
     *
     * @param dataString data de referência no formato dia/mês/ano
     * @param saida caminho do arquivo no qual a folha será gravada
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    public void rodaFolha(String dataString, String saida) throws Exception {
        executaComando(() -> {
            try {
                LocalDate data = converterData(dataString);

                List<Empregado> listaEmpregados =
                        new ArrayList<>(empregados.values());

                GeradorDeFolha gerador = new GeradorDeFolha();
                String conteudo = gerador.gerarFolha(data, listaEmpregados);

                Files.writeString(Paths.get(saida), conteudo);

            } catch (IOException e) {
                throw new RuntimeException(
                        "Erro ao salvar a folha: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Cria uma agenda de pagamentos com a descrição informada.
     *
     * @param descricao descrição da agenda de pagamentos
     * @throws Exception se ocorrer um erro de validação ou execução da operação
     */
    public void criarAgendaDePagamentos(String descricao) throws Exception {
        AgendaPagamento.criar(descricao);
    }
}
