package model;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import validation.Validator;

/**
 * Esta classe tem como objectivo criar um Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class Orcamento {

    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty versao = new SimpleStringProperty();
    private SimpleIntegerProperty ano = new SimpleIntegerProperty();
    private SimpleStringProperty data = new SimpleStringProperty();
    private SimpleStringProperty estado = new SimpleStringProperty();

    public Integer getId() {
        return id.get();
    }

    public String getVersao() {
        return versao.get();
    }

    public int getAno() {
        return ano.get();
    }

    public String getData() {
        return data.get();
    }

    public String getEstado() {
        return estado.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setVersao(String versao) {
        this.versao.set(versao);
    }

    public void setAno(int ano) {
        this.ano.set(ano);
    }

    public void setData(String data) {
        this.data.set(data);
    }

    public void setEstado(String estado) {
        this.estado.set(estado);
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

        if (!validator.versao(this.getVersao())) {
            errors.add("versao");
        }

        if (!validator.ano(String.valueOf(this.getAno()))) {
            errors.add("ano");
        }

        if (!validator.data(this.getData())) {
            errors.add("data");
        }
        return errors;
    }

    /**
     * Método que devolve uma string com a informação de um Orçamento
     * @return informação de um Orçamento
     */
    @Override
    public String toString() {
        return "" + ano.get();
    }

}
