package model;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */

/**
 * Esta classe tem como objectivo criar um Recibo
 *
 * @author Luís Sousa - 8090228
 */
public class Recibo {

    private int idRecibo;
    private String data;
    private int totalRecibo;

    /**
     * Método contrutor que permite criar um recibo
     */
    public Recibo() {
    }

    /**
     * Método contrutor que permite criar um recibo com toda a informação
     *
     * @param idRecibo identificador do recibo
     * @param data data do recibo
     * @param totalRecibo total pago
     */
    public Recibo(int idRecibo, String data, int totalRecibo) {
        this.idRecibo = idRecibo;
        this.data = data;
        this.totalRecibo = totalRecibo;
    }

    public int getIdRecibo() {
        return idRecibo;
    }

    public void setIdRecibo(int idRecibo) {
        this.idRecibo = idRecibo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getTotalRecibo() {
        return totalRecibo;
    }

    public void setTotalRecibo(int totalRecibo) {
        this.totalRecibo = totalRecibo;
    }

    /**
     * Método que devolve uma string com a informação de um Recibo
     *
     * @return informação de um Orçamento
     */
    @Override
    public String toString() {
        return "Nº: " + getIdRecibo() + " | " + "Data: " + getData();
    }

}
