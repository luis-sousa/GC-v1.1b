package model;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import java.util.ArrayList;
import validation.Validator;

/**
 * Esta classe tem como objectivo criar um Aviso de Débito.
 *
 * @author Luís Sousa - 8090228
 */
public class AvisoDebito {

    private int idAviso;
    private int idOrcamento;
    private String codFraccao;
    private int mes;
    private String data;
    private String dataLimite;
    private String descricao;
    private int valorPagar;
    private int totalEmDivida;
    private int resolvido;
    private int idSubOrcamento;

    /**
     * Método construtor vazio
     */
    public AvisoDebito() {
    }

    /**
     * Método construtor que permite criar um aviso
     *
     * @param idAviso
     * @param idOrcamento
     * @param codFraccao
     * @param mes
     * @param data
     * @param dataLimite
     * @param descricao
     * @param valorPagar
     * @param totalEmDivida
     * @param resolvido
     * @param idSubOrcamento
     */
    public AvisoDebito(int idAviso, int idOrcamento, String codFraccao, int mes, String data, String dataLimite, String descricao, int valorPagar, int totalEmDivida, int resolvido, int idSubOrcamento) {
        this.idAviso = idAviso;
        this.idOrcamento = idOrcamento;
        this.codFraccao = codFraccao;
        this.mes = mes;
        this.data = data;
        this.dataLimite = dataLimite;
        this.descricao = descricao;
        this.valorPagar = valorPagar;
        this.totalEmDivida = totalEmDivida;
        this.resolvido = resolvido;
        this.idSubOrcamento = idSubOrcamento;
    }

    /**
     * Método que permite obter o ID do Aviso
     *
     * @return ID do aviso
     */
    public int getIdAviso() {
        return idAviso;
    }

    /**
     * Método que permite modificar o ID do Aviso
     *
     * @param idAviso identificador de um aviso
     */
    public void setIdAviso(int idAviso) {
        this.idAviso = idAviso;
    }

    /**
     * Método que permite obter o ID do Orcamento
     *
     * @return ID do Orçamento
     */
    public int getIdOrcamento() {
        return idOrcamento;
    }

    public void setIdOrcamento(int idOrcamento) {
        this.idOrcamento = idOrcamento;
    }

    /**
     * Método que permite obter o código da Fração do Aviso
     *
     * @return código da fracção
     */
    public String getCodFraccao() {
        return codFraccao;
    }

    public void setCodFraccao(String codFraccao) {
        this.codFraccao = codFraccao;
    }

    /**
     * Método que permite obter o mês do Aviso
     *
     * @return mês
     */
    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * Método que permite obter a data do Aviso
     *
     * @return data
     */
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * Método que permite obter a data limite de pagamento do Aviso
     *
     * @return data limite para liquidar o débito
     */
    public String getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(String dataLimite) {
        this.dataLimite = dataLimite;
    }

    /**
     * Método que permite obter descrição do Aviso
     *
     * @return descrição do aviso de débito
     */
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Método que permite obter o valor a pagar do Aviso
     *
     * @return valor a pagar do aviso
     */
    public int getValorPagar() {
        return valorPagar;
    }

    public void setValorPagar(int valorPagar) {
        this.valorPagar = valorPagar;
    }

    /**
     * Método que permite obter o total em dívida para aquela fracção
     *
     * @return total em divida
     */
    public int getTotalEmDivida() {
        return totalEmDivida;
    }

    public void setTotalEmDivida(int totalEmDivida) {
        this.totalEmDivida = totalEmDivida;
    }

    /**
     * Método que permite obter se o aviso está resolvido-1 ou não resolvido-0
     *
     * @return estado do aviso de débito
     */
    public int getResolvido() {
        return resolvido;
    }

    public void setResolvido(int resolvido) {
        this.resolvido = resolvido;
    }

    /**
     * Método que permite obter o id do SubOrçamento
     *
     * @return estado de envio de um aviso
     */
    public int getIdSubOrcamento() {
        return idSubOrcamento;
    }

    public void setIdSubOrcamento(int idSubOrcamento) {
        this.idSubOrcamento = idSubOrcamento;
    }

    /**
     * Método que devolve uma lista com os errors que provêm de campos que foram
     * mal introduzidos
     *
     * @return array com erros
     */
    public ArrayList<String> validate() {
        ArrayList<String> errors = new ArrayList<>();
        Validator validator = new Validator();
        errors.clear();

//        if (!validator.data(this.dataLimite)) {
//            errors.add("nome");
//        }
        return errors;
    }

    /**
     * Método que devolve uma string com a informação de uma aviso de Débito
     *
     * @return uma string com os dados de um aviso
     */
    @Override
    public String toString() {
        String resultado;
        if (this.getIdSubOrcamento() == 0) {
            resultado = "Fração: " + codFraccao + " | Mês: " + getMonth(mes);
        } else {
            resultado = "Fração: " + codFraccao + " | Mês: " + getMonth(mes) + " - Aviso Extra";
        }
        return resultado;
    }

    /**
     * Método que através de um numero devolve o respetivo mês
     *
     * @param number numero de um mês
     * @return mês
     */
    private String getMonth(int number) {
        String month;

        switch (number) {
            case 1:
                month = "Janeiro";
                break;
            case 2:
                month = "Fevereiro";
                break;
            case 3:
                month = "Março";
                break;
            case 4:
                month = "Abril";
                break;
            case 5:
                month = "Maio";
                break;
            case 6:
                month = "Junho";
                break;
            case 7:
                month = "Julho";
                break;
            case 8:
                month = "Agosto";
                break;
            case 9:
                month = "Setembro";
                break;
            case 10:
                month = "Outubro";
                break;
            case 11:
                month = "Novembro";
                break;
            case 12:
                month = "Dezembro";
                break;
            default:
                month = null;
                break;

        }
        return month;
    }

}
