package validation;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Esta classe tem como objectivo criar validator para validar campos
 * introduzidos
 *
 * @author Luís Sousa - 8090228
 */
public class Validator {

    private final String errorName = "Nome Inválido";
    private final String errorMorada = "Morada Inválida";
    private final String errorCodPostal = "Código Postal Inválido";
    private final String errorLocalidade = "Localidade Inválida";
    private final String errorTelefone = "Telefone Inválido";
    private final String errorTelemovel = "Telemovel Inválido";
    private final String errorMail = "Email Inválido";
    private final String errorContribuinte = "Contribuinte Inválido";
    private final String errorCodFraccao = "Codigo Inválido [A-Z]";
    private final String errorPermilagem = "Permilagem Inválida";
    private final String errorRubrica = "Rubrica Inválida";
    private final String errorValor = "Valor Inválido";
    private final String errorAno = "Ano Inválido 20** ou 21**";
    private final String errorData = "Data Inválida - (dd/mm/yyyy)";
    private final String errorVersao = "Versao Inválida - vn.n.n";
    private final String errorObs = "Observação Inválida";
    private final String errorDespesa = "Despesa Inválida";
    private final String errorMontante = "Montante Inválido { >0 e <=99999 } ";
    private final String errorNomeSubOrc = "Nome Sub Orçamento Inválido - ex: 2014-A";
    private final String errorDescricao = "Descrição Inválida";

    private static final String EMAIL_PATTERN
            = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String NOME_PATTERN = "^[a-zA-ZéúíóáÉÚÍÓÁèùìòàÈÙÌÒÀõãñÕÃÑêûîôâÊÛÎÔÂëÿüïöäËYÜÏÖÄçÇºª\\s]{3,50}+$";
    private static final String MORADA_PATTERN = "^[0-9a-zA-ZéúíóáÉÚÍÓÁèùìòàÈÙÌÒÀõãñÕÃÑêûîôâÊÛÎÔÂëÿüïöäËYÜÏÖÄçÇº\\s]{3,75}+$";
    private static final String LOCALIDADE_PATTERN = "^[a-zA-ZéúíóáÉÚÍÓÁèùìòàÈÙÌÒÀõãñÕÃÑêûîôâÊÛÎÔÂëÿüïöäËYÜÏÖÄçÇ\\-\\s]{3,25}+$";
    private static final String TLM_PATTERN = "^91\\d{7}|92\\d{7}|93\\d{7}|96\\d{7}$";
    private static final String CPOSTAL_PATTERN = "\\d{4}-\\d{3}";
    private static final String CODFRACCAO_PATTERN = "^[A-Z]{1}$";
    private static final String RUBRICA_PATTERN = "^[a-zA-ZéúíóáÉÚÍÓÁèùìòàÈÙÌÒÀõãêûîôâÊÛÎÔÂçÇ\\s]{3,25}$";
    private static final String DATA_PATTERN = "^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$";
    private static final String ANO_PATTERN = "^(20|21)\\d{2}$";
    private static final String NOMESUB_PATTERN = "^(20|21)\\d{2}-[a-zA-Z]$";
    private static final String VERSAO_PATTERN = "^v[0-9.]+$";
    private static final String DESPESA_PATTERN = "^[0-9a-zA-ZéúíóáÉÚÍÓÁèùìòàÈÙÌÒÀõãñÕÃÑêûîôâÊÛÎÔÂëÿüïöäËYÜÏÖÄçÇº\\-\\s]{3,50}+$";
    private static final String SO_NUMEROS = "^[0-9]+$";
    private static final String DESCRICAO_RUBRICA_PATTERN = "^[a-zA-ZéúíóáÉÚÍÓÁèùìòàÈÙÌÒÀõãñÕÃÑêûîôâÊÛÎÔÂëÿüïöäËYÜÏÖÄçÇºª\\s]{3,25}+$";

    private Pattern pattern;
    private Matcher matcher;

    /**
     * Método contrutor que permite instanciar a classe Validator
     */
    public Validator() {
    }

    public String getErrorAno() {
        return errorAno;
    }
    
      public String getErrorDescricao() {
        return errorDescricao;
    }

    public String getErrorData() {
        return errorData;
    }

    public String getErrorVersao() {
        return errorVersao;
    }

    public String getErrorValor() {
        return errorValor;
    }

    public String getErrorRubrica() {
        return errorRubrica;
    }

    public String getErrorCodFraccao() {
        return errorCodFraccao;
    }

    public String getErrorPermilagem() {
        return errorPermilagem;
    }

    public String getErrorName() {
        return errorName;
    }

    public String getErrorMorada() {
        return errorMorada;
    }

    public String getErrorCodPostal() {
        return errorCodPostal;
    }

    public String getErrorLocalidade() {
        return errorLocalidade;
    }

    public String getErrorTelefone() {
        return errorTelefone;
    }

    public String getErrorTelemovel() {
        return errorTelemovel;
    }

    public String getErrorMail() {
        return errorMail;
    }

    public String getErrorContribuinte() {
        return errorContribuinte;
    }

    public String getErrorObs() {
        return errorObs;
    }

    public String getErrorDespesa() {
        return errorDespesa;
    }

    public String getErrorMontante() {
        return errorMontante;
    }

    public String getErrorNomeSubOrcamento() {
        return errorNomeSubOrc;
    }

    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    //Validacoes
    //
    //retornar false caso campo seja invalido
    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
    /**
     * Método que permite validar um nome
     *
     * @param nome
     * @return boolean
     */
    public boolean nome(String nome) {
        pattern = Pattern.compile(NOME_PATTERN);
        matcher = pattern.matcher(nome);
        return matcher.matches();
    }

    /**
     * Método que permite validar se uma string contém só numeros
     *
     * @param valor string a validar
     * @return se contem só números uma determinada string
     */
    private boolean soNumeros(String valor) {
        pattern = Pattern.compile(SO_NUMEROS);
        matcher = pattern.matcher(valor);
        return matcher.matches();
    }

    /**
     * Método que permite validar uma morada
     *
     * @param morada morada
     * @return boolean
     */
    public boolean morada(String morada) {
        pattern = Pattern.compile(MORADA_PATTERN);
        matcher = pattern.matcher(morada);
        return matcher.matches() && !soNumeros(morada);
    }

    /**
     * Método que permite validar código postal
     *
     * @param codPostal código postal
     * @return boolean se cod postal está válido ou não
     */
    public boolean codPostal(String codPostal) {
        return codPostal.matches(CPOSTAL_PATTERN);

    }

    /**
     * Método que permite validar uma localidade
     *
     * @param localidade localidade
     * @return boolean
     */
    public boolean localidade(String localidade) {
        pattern = Pattern.compile(LOCALIDADE_PATTERN);
        matcher = pattern.matcher(localidade);
        return matcher.matches();
    }

    /**
     * Método que permite validar um telefone
     *
     * @param telefone numero telefone
     * @return boolean
     */
    public boolean telefone(int telefone) {
        return ((telefone >= 210000000 && telefone <= 296999999));
    }

    /**
     * Método que permite validar um telemóvel
     *
     * @param telemovel número telemóvel
     * @return boolean
     */
    public boolean telemovel(int telemovel) {
        pattern = Pattern.compile(TLM_PATTERN);
        matcher = pattern.matcher(String.valueOf(telemovel));
        return matcher.matches() || telemovel == 0; // telemóvel igual a zero quer dizer que o campo ficou em branco
    }

    /**
     * Método que permite validar um email
     *
     * @param mail email
     * @return boolean
     */
    public boolean mail(String mail) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mail);
        return matcher.matches();
    }

    /**
     * Método que permite validar um numero de contribuinte
     *
     * @param contribuinte número de contribuinte
     * @return boolean
     */
    public boolean contribuinte(int contribuinte) {
        String number = String.valueOf(contribuinte);

        // 9 digitos obrigatórios
        if (number.length() != 9) {
            return false;
        } else if (number.equals("123456789")) {
            return false;
        }

        // Começar com 1, 2, 5, 6, 8 ou 9
        if (!"125689".contains(number.charAt(0) + "")) {
            return false;
        }

        // Ciclo que vai construir o array de inteiros para cada digito do nif
        int[] numerosArray = new int[9];
        for (int i = 0; i < 9; i++) {
            numerosArray[i] = Integer.parseInt(number.charAt(i) + "");
        }

        // Calcular se é válido número a número do array dos números
        float resultado = 0.0f;
        for (int i = 0, j = 9; i < 9; i++, j--) {
            resultado += (j * numerosArray[i]);
            //System.out.println(resultado + " = " + j + " x " + numerosArray[i]);
        }

        return ((resultado % 11) == 0.0f);
//          return contribuinte >= 100000000 && contribuinte <= 999999999;
    }

    /**
     * Método que permite validar uma permilagem
     *
     * @param permilagem permilagem
     * @return boolean
     */
    public boolean permilagem(Float permilagem) {
        return permilagem > 0 && permilagem <= 9999;
    }

    /**
     * Método que permite validar a identificação de uma fracção
     *
     * @param cod código de um fracção
     * @return boolean
     */
    public boolean codigoFraccao(String cod) {
        pattern = Pattern.compile(CODFRACCAO_PATTERN);
        matcher = pattern.matcher(cod);
        return matcher.matches();
    }

    /**
     * Método que permite validar uma rubrica
     *
     * @param nome rubrica
     * @return boolean
     */
    public boolean rubrica(String nome) {
        pattern = Pattern.compile(RUBRICA_PATTERN);
        matcher = pattern.matcher(nome);
        return matcher.matches();
    }

    /**
     * Método que permite validar um ano
     *
     * @param ano ano
     * @return boolean
     */
    public boolean ano(String ano) {
        pattern = Pattern.compile(ANO_PATTERN);
        matcher = pattern.matcher(ano);
        return matcher.matches();
    }

    /**
     * Método que permite validar uma data
     *
     * @param data data de validade
     * @return boolean
     */
    public boolean data(String data) {
        pattern = Pattern.compile(DATA_PATTERN);
        matcher = pattern.matcher(data);
        return matcher.matches();
    }

    /**
     * Método que permite validar uma versão
     *
     * @param versao versão de um orçamento
     * @return boolean
     */
    public boolean versao(String versao) {
        pattern = Pattern.compile(VERSAO_PATTERN);
        matcher = pattern.matcher(versao);
        return matcher.matches();
    }

    /**
     * Método que permite validar um pagamento
     *
     * @param valor valor de um pagamento
     * @return boolean
     */
    public boolean valor(Float valor) {
        return valor >= 0;
    }

    /**
     * Método que permite validar um orçamento
     *
     * @param obs observação
     * @return boolean
     */
    public boolean observacao(String obs) {
        return !obs.trim().equals("");
    }

    /**
     * Método que permite validar um nome de uma despesa
     *
     * @param despesa despesa
     * @return boolean
     */
    public boolean despesa(String despesa) {
        pattern = Pattern.compile(DESPESA_PATTERN);
        matcher = pattern.matcher(despesa);
        return matcher.matches() && !soNumeros(despesa);
    }

    /**
     * Método que permite validar um montante
     *
     * @param montante valor
     * @return boolean
     */
    public boolean montante(Float montante) {
        return montante > 0 && montante <= 99999;
    }

    public boolean nomeSubOrcamento(String nome) {
        pattern = Pattern.compile(NOMESUB_PATTERN);
        matcher = pattern.matcher(nome);
        return matcher.matches();
    }

    public boolean descricaoRubrica(String rubrica) {
        pattern = Pattern.compile(DESCRICAO_RUBRICA_PATTERN);
        matcher = pattern.matcher(rubrica);
        return matcher.matches();
    }

}
