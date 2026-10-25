package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.Exception.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Representa um empregado no sistema WePayU, armazenando seus dados
 * pessoais, tipo de vínculo, método e agenda de pagamento e informações
 * relacionadas à sindicalização.
 *
 * <p>Esta classe fornece operações para consultar e alterar os atributos
 * do empregado, calcular salários brutos e descontos, processar informações
 * da folha de pagamento e configurar o método de pagamento.</p>
 *
 * <p>Também disponibiliza métodos que podem ser sobrescritos pelas subclasses
 * para implementar regras específicas de cálculo salarial.</p>
 */
public class Empregado {

    /**
     * Nome do empregado.
     */
    private String nome;

    /**
     * Endereço do empregado.
     */
    private String endereco;

    /**
     * Tipo de empregado, utilizado para identificar seu vínculo de trabalho.
     */
    private String tipo;

    /**
     * Indica se o empregado está sindicalizado.
     */
    private boolean sindicalizado;

    /**
     * Método utilizado para realizar o pagamento do empregado.
     */
    private MetodoPagamento metodoPagamento;

    /**
     * Sindicato ao qual o empregado está associado, quando sindicalizado.
     */
    private Sindicato sindicato;

    /**
     * Descrição da agenda de pagamento utilizada pelo empregado.
     */
    private String agendaPagamento;

    /**
     * Cria um empregado sem inicializar explicitamente seus atributos.
     */
    public Empregado() {
    }

    /**
     * Cria um empregado com seus dados pessoais, tipo de vínculo,
     * situação sindical, método de pagamento e agenda de pagamento.
     *
     * @param metodoPagamento método utilizado para realizar o pagamento
     * @param nome nome do empregado
     * @param endereco endereço do empregado
     * @param tipo tipo de vínculo do empregado
     * @param sindicalizado indica se o empregado está sindicalizado
     * @param agendaPagamento descrição da agenda de pagamento
     */
    public Empregado(
            MetodoPagamento metodoPagamento,
            String nome,
            String endereco,
            String tipo,
            boolean sindicalizado,
            String agendaPagamento
    ) {
        this.nome = nome;
        this.endereco = endereco;
        this.tipo = tipo;
        this.sindicalizado = sindicalizado;
        this.metodoPagamento = metodoPagamento;
        this.agendaPagamento = agendaPagamento;
    }

    /**
     * Cria uma cópia dos dados de outro empregado.
     *
     * <p>Os atributos pessoais, o método de pagamento e a agenda
     * são copiados por meio dos respectivos métodos de acesso.
     * Caso o empregado original esteja sindicalizado, sua referência
     * ao sindicato também é copiada.</p>
     *
     * @param empregado empregado cujos dados serão copiados
     */
    public Empregado(Empregado empregado) {
        this.nome = empregado.getNome();
        this.endereco = empregado.getEndereco();
        this.tipo = empregado.getTipo();
        this.sindicalizado = empregado.isSindicalizado();
        this.metodoPagamento = empregado.getMetodoPagamento();
        this.agendaPagamento = empregado.getAgendaPagamento();

        if (this.sindicalizado) {
            this.sindicato = empregado.getSindicato();
        }
    }

    /**
     * Retorna a agenda de pagamento padrão utilizada quando nenhuma
     * agenda foi definida para o empregado.
     *
     * <p>A agenda padrão também é utilizada quando os dados restaurados
     * possuem uma agenda nula.</p>
     *
     * @return descrição da agenda padrão, {@code semanal 5}
     */
    protected String agendaPadrao() {
        return "semanal 5";
    }

    /**
     * Retorna o objeto que representa a agenda de pagamento do empregado.
     *
     * <p>Se a agenda não estiver definida, utiliza a agenda padrão.</p>
     *
     * @return objeto que representa a agenda de pagamento
     */
    public AgendaPagamento agenda() {
        return AgendaPagamento.de(
                agendaPagamento != null
                        ? agendaPagamento
                        : agendaPadrao()
        );
    }

    /**
     * Verifica se a data informada corresponde a um dia de pagamento
     * do empregado.
     *
     * @param data data que será verificada
     * @return {@code true} se a data corresponder a um dia de pagamento;
     *         {@code false} caso contrário
     */
    public boolean ehDiaDePagamento(LocalDate data) {
        return agenda().ehDiaDePagamento(data);
    }

    /**
     * Retorna a data inicial do período de pagamento correspondente
     * à data informada.
     *
     * @param data data do pagamento atual
     * @return data inicial do período de pagamento
     */
    public LocalDate inicioDoPeriodo(LocalDate data) {
        return agenda().inicioDoPeriodo(data);
    }

    /**
     * Retorna a quantidade de dias do período de pagamento
     * correspondente à data informada.
     *
     * @param data data do pagamento atual
     * @return quantidade de dias do período
     */
    public int diasDoPeriodo(LocalDate data) {
        return agenda().diasDoPeriodo(data);
    }

    /**
     * Calcula o salário bruto do empregado em um determinado período.
     *
     * <p>Este método fornece uma implementação padrão que retorna
     * zero. As subclasses devem sobrescrevê-lo para implementar
     * o cálculo correspondente ao seu tipo de empregado.</p>
     *
     * @param inicio data inicial do período, inclusiva
     * @param fim data final do período, exclusiva
     * @return salário bruto do período
     */
    public double getSalarioBruto(LocalDate inicio, LocalDate fim) {
        return 0.0;
    }

    /**
     * Calcula os descontos aplicáveis à folha de pagamento
     * na data informada.
     *
     * <p>Por padrão, considera os descontos sindicais referentes
     * ao período de pagamento correspondente.</p>
     *
     * @param data data do pagamento
     * @return valor total dos descontos da folha
     */
    public double getDescontosFolha(LocalDate data) {
        return getDescontos(
                inicioDoPeriodo(data),
                data,
                diasDoPeriodo(data)
        );
    }

    /**
     * Calcula o salário bruto a ser pago na data informada.
     *
     * <p>Se a data não corresponder a um dia de pagamento,
     * retorna zero. Caso contrário, calcula o salário bruto
     * referente ao período correspondente.</p>
     *
     * @param data data em que o pagamento será verificado
     * @return salário bruto do período ou zero se não houver pagamento
     */
    public double calcularBrutoNaData(LocalDate data) {
        if (!ehDiaDePagamento(data)) {
            return 0.0;
        }

        return getSalarioBruto(inicioDoPeriodo(data), data);
    }

    /**
     * Cria um item da folha de pagamento com as informações gerais
     * do empregado.
     *
     * <p>O item contém o nome, o resumo do método de pagamento,
     * o salário bruto e os descontos. As subclasses podem sobrescrever
     * este método para acrescentar informações específicas.</p>
     *
     * @param data data de referência da folha de pagamento
     * @return item da folha preenchido com os dados gerais do empregado
     */
    public ItemFolha calcularItemFolha(LocalDate data) {
        ItemFolha item = new ItemFolha();
        item.nome = getNome();
        item.metodo = getResumoMetodoPagamento();
        item.bruto = getSalarioBruto(inicioDoPeriodo(data), data);
        item.descontos = getDescontosFolha(data);
        return item;
    }

    /**
     * Trunca um valor numérico para duas casas decimais,
     * sem realizar arredondamento para cima.
     *
     * @param v valor numérico que será truncado
     * @return valor truncado para duas casas decimais
     */
    protected static double trunca(double v) {
        return BigDecimal.valueOf(v)
                .setScale(2, RoundingMode.DOWN)
                .doubleValue();
    }

    /**
     * Retorna o nome do empregado.
     *
     * @return nome do empregado
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o endereço do empregado.
     *
     * @return endereço do empregado
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Retorna o tipo de vínculo do empregado.
     *
     * @return tipo do empregado
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Atualiza o nome do empregado.
     *
     * @param nome novo nome do empregado
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Atualiza o endereço do empregado.
     *
     * @param endereco novo endereço do empregado
     */
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    /**
     * Define se o empregado está sindicalizado.
     *
     * @param sindicalizado {@code true} para sindicalizar o empregado;
     *                      {@code false} caso contrário
     */
    public void setSindicalizado(boolean sindicalizado) {
        this.sindicalizado = sindicalizado;
    }

    /**
     * Verifica se o empregado está sindicalizado.
     *
     * @return {@code true} se estiver sindicalizado;
     *         {@code false} caso contrário
     */
    public boolean isSindicalizado() {
        return sindicalizado;
    }

    /**
     * Atualiza o tipo de vínculo do empregado.
     *
     * @param tipo novo tipo de empregado
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna o método de pagamento configurado para o empregado.
     *
     * @return método de pagamento do empregado
     */
    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    /**
     * Gera uma descrição textual do método de pagamento.
     *
     * <p>Para pagamentos bancários, inclui banco, agência e conta
     * corrente. Para pagamentos pelos Correios, inclui o endereço
     * do empregado. Nos demais casos, indica pagamento em mãos.</p>
     *
     * @return resumo textual do método de pagamento
     */
    public String getResumoMetodoPagamento() {
        switch (metodoPagamento.getTipo()) {
            case "banco":
                Banco b = (Banco) metodoPagamento;
                return b.getBanco()
                        + ", Ag. " + b.getAgencia()
                        + " CC " + b.getContaCorrente();

            case "correios":
                return "Correios, " + getEndereco();

            default:
                return "Em maos";
        }
    }

    /**
     * Retorna o sindicato associado ao empregado.
     *
     * @return sindicato do empregado ou {@code null} se não estiver definido
     */
    public Sindicato getSindicato() {
        return sindicato;
    }

    /**
     * Define o sindicato associado ao empregado.
     *
     * @param sindicato sindicato que será associado
     */
    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    /**
     * Retorna o salário atual do empregado.
     *
     * <p>A implementação padrão retorna zero. As subclasses podem
     * sobrescrever este método para fornecer o salário correspondente
     * ao tipo de empregado.</p>
     *
     * @return salário atual do empregado
     */
    public double getSalarioAtual() {
        return 0;
    }

    /**
     * Define a descrição da agenda de pagamento do empregado.
     *
     * @param agendaPagamento descrição da nova agenda
     */
    public void setAgendaPagamento(String agendaPagamento) {
        this.agendaPagamento = agendaPagamento;
    }

    /**
     * Retorna a descrição da agenda de pagamento configurada.
     *
     * @return descrição da agenda ou {@code null} se não estiver definida
     */
    public String getAgendaPagamento() {
        return agendaPagamento;
    }

    /**
     * Retorna o valor textual de um atributo do empregado.
     *
     * <p>Permite consultar atributos pessoais, dados do método
     * de pagamento, informações sindicais e a agenda de pagamento.
     * A consulta de atributos sindicais exige que o empregado
     * esteja sindicalizado.</p>
     *
     * @param atributo nome do atributo que será consultado
     * @return representação textual do valor do atributo
     * @throws EmpregadoNaoComissionado se o atributo solicitado
     *                                  for comissão
     * @throws EmpregadoNaoSindicalizado se for solicitado um atributo
     *                                   sindical sem sindicalização
     * @throws AtributoInexistente se o atributo não for reconhecido
     * @throws Exception se ocorrer um erro ao consultar um atributo
     *                   do método de pagamento ou do sindicato
     */
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

            case "agendaPagamento":
                return agendaPagamento != null
                        ? agendaPagamento
                        : agendaPadrao();

            default:
                throw new AtributoInexistente();
        }
    }

    /**
     * Calcula os descontos sindicais aplicáveis ao empregado
     * durante um período.
     *
     * <p>Se o empregado não estiver sindicalizado, nenhum desconto
     * será aplicado. Caso esteja, são somadas as taxas de serviço
     * do período e a taxa sindical multiplicada pela quantidade
     * de dias informada.</p>
     *
     * @param inicio data inicial do período
     * @param fim data final do período
     * @param dias quantidade de dias considerada no cálculo da taxa sindical
     * @return valor total dos descontos sindicais
     */
    public double getDescontos(LocalDate inicio, LocalDate fim, int dias) {
        if (!sindicalizado) {
            return 0.0;
        }

        return sindicato.getTaxasServico(inicio, fim)
                + sindicato.getTaxaSindical() * dias;
    }

    /**
     * Define o método de pagamento do empregado.
     *
     * @param metodoPagamento novo método de pagamento
     */
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    /**
     * Altera o valor de um atributo do empregado após validar
     * as restrições aplicáveis.
     *
     * <p>Permite modificar o nome, o endereço, o salário, a situação
     * sindical, o método de pagamento e a agenda de pagamento.
     * A alteração de alguns atributos é delegada a métodos específicos
     * ou pode ser restrita conforme o tipo de empregado.</p>
     *
     * @param atributo nome do atributo que será alterado
     * @param valor novo valor do atributo
     * @throws NomeNulo se o nome for nulo ou vazio
     * @throws EnderecoNulo se o endereço for nulo ou vazio
     * @throws TipoInvalido se houver tentativa de alterar o tipo
     *                      por este método
     * @throws EmpregadoNaoComissionado se o empregado não possuir
     *                                  comissão aplicável
     * @throws NaoBooleano se o valor de sindicalização não for
     *                     {@code true} ou {@code false}
     * @throws AgendaNaoDisponivel se a agenda não estiver cadastrada
     * @throws AtributoInexistente se o atributo não for reconhecido
     * @throws Exception se ocorrer um erro durante a alteração
     */
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

            case "agendaPagamento":
                if (!AgendaPagamento.valida(valor)) {
                    throw new AgendaNaoDisponivel();
                }

                setAgendaPagamento(AgendaPagamento.normaliza(valor));
                break;

            default:
                throw new AtributoInexistente();
        }
    }

    /**
     * Altera o salário do empregado.
     *
     * <p>A implementação padrão lança uma exceção indicando que
     * o atributo não está disponível. As subclasses podem sobrescrever
     * este método para implementar a alteração salarial.</p>
     *
     * @param valor representação textual do novo salário
     * @throws AtributoInexistente se a alteração não estiver
     *                             implementada para este tipo
     */
    protected void alteraSalario(String valor) throws Exception {
        throw new AtributoInexistente();
    }

    /**
     * Altera o método de pagamento para uma modalidade sem dados
     * bancários adicionais.
     *
     * <p>São aceitos os valores {@code correios} e {@code emMaos}.
     * Qualquer outro valor resulta em uma exceção.</p>
     *
     * @param valor identificação do método de pagamento desejado
     * @throws MetodoInvalido se o método informado não for aceito
     */
    protected void alteraMetodoPagamento(String valor) throws Exception {
        if ("correios".equals(valor) || "emMaos".equals(valor)) {
            setMetodoPagamento(new MetodoPagamento(valor));
        } else {
            throw new MetodoInvalido();
        }
    }

    /**
     * Converte uma representação textual em um valor de salário.
     *
     * <p>O método aceita vírgula ou ponto como separador decimal
     * e valida se o valor informado é numérico e não negativo.</p>
     *
     * @param valor representação textual do salário
     * @return valor numérico do salário
     * @throws SalarioNulo se o valor for nulo ou vazio
     * @throws SalarioNaoNumerico se o valor não representar
     *                            um número válido
     * @throws SalarioNegativo se o salário for negativo
     */
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

    /**
     * Converte uma representação textual em um valor de comissão.
     *
     * <p>O método aceita vírgula ou ponto como separador decimal
     * e valida se o valor informado é numérico e não negativo.</p>
     *
     * @param valor representação textual da comissão
     * @return valor numérico da comissão
     * @throws ComissaoNula se o valor for nulo ou vazio
     * @throws ComissaoNaoNumerica se o valor não representar
     *                             um número válido
     * @throws ComissaoNegativa se a comissão for negativa
     */
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

    /**
     * Associa o empregado a um sindicato e define sua taxa sindical.
     *
     * <p>O método cria uma instância de {@link Sindicato} utilizando
     * o identificador e a taxa informados, associa essa instância
     * ao empregado e marca o empregado como sindicalizado.</p>
     *
     * @param idSindicato identificador do sindicato
     * @param taxa taxa sindical aplicada ao empregado
     */
    public void sindicalizar(String idSindicato, double taxa) {
        Sindicato sindicato = new Sindicato(idSindicato, taxa);

        setSindicato(sindicato);
        setSindicalizado(true);
    }

    /**
     * Altera o método de pagamento para a modalidade bancária.
     *
     * <p>O método valida o tipo de pagamento e verifica se os dados
     * do banco, da agência e da conta corrente foram informados.
     * Após a validação, cria e associa um objeto {@link Banco}
     * ao empregado.</p>
     *
     * @param valor identificação do método de pagamento, que deve ser
     *              {@code banco}
     * @param banco nome ou identificação do banco
     * @param agencia número da agência bancária
     * @param contaCorrente número da conta corrente
     * @throws MetodoInvalido se o método informado não for bancário
     * @throws BancoNulo se o banco for nulo ou vazio
     * @throws AgenciaNula se a agência for nula ou vazia
     * @throws ContaNula se a conta corrente for nula ou vazia
     */
    public void alteraMetodoPagamento(
            String valor,
            String banco,
            String agencia,
            String contaCorrente
    ) throws Exception {

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

    /**
     * Converte uma data textual para um objeto {@link LocalDate}.
     *
     * <p>A data deve seguir o formato {@code d/M/uuuu}, com dia,
     * mês e ano numéricos. A validação estrita impede datas
     * inexistentes no calendário.</p>
     *
     * @param dataStr data representada como texto
     * @return objeto correspondente à data informada
     * @throws DataInvalida se o texto não representar uma data válida
     */
    public LocalDate converterData(String dataStr) throws Exception {
        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("d/M/uuuu")
                            .withResolverStyle(
                                    java.time.format.ResolverStyle.STRICT
                            );

            return LocalDate.parse(dataStr, formatter);
        } catch (DateTimeParseException e) {
            throw new DataInvalida();
        }
    }
}