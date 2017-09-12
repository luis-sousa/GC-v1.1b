package model;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
/**
 * Esta classe tem como objectivo criar uma linha do Recibo pois um recibo pode
 * estar associado a vários débitos
 *
 * @author Luís Sousa - 8090228
 */
public class ReciboLinha {

    private int idReciboLinha;
    private int idRecibo;
    private int idAviso;
    private int valorPago;
    private String descricao;

    public ReciboLinha(int idReciboLinha, int idRecibo, int idAviso, int valorPago, String descricao) {
        this.idReciboLinha = idReciboLinha;
        this.idRecibo = idRecibo;
        this.idAviso = idAviso;
        this.valorPago = valorPago;
        this.descricao = descricao;
    }

    /**
     * Método contrutor que permite criar uma linha do Recibo
     */
    public ReciboLinha() {
    }

    public int getIdReciboLinha() {
        return idReciboLinha;
    }

    public void setIdReciboLinha(int idReciboLinha) {
        this.idReciboLinha = idReciboLinha;
    }

    public int getIdRecibo() {
        return idRecibo;
    }

    public void setIdRecibo(int idRecibo) {
        this.idRecibo = idRecibo;
    }

    public int getIdAviso() {
        return idAviso;
    }

    public void setIdAviso(int idAviso) {
        this.idAviso = idAviso;
    }

    public int getValorPago() {
        return valorPago;
    }

    public void setValorPago(int valorPago) {
        this.valorPago = valorPago;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
