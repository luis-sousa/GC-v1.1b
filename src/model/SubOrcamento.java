/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
package model;

import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import validation.Validator;

/**
 * Esta classe tem como objectivo criar um sub Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class SubOrcamento {

    private SimpleIntegerProperty id = new SimpleIntegerProperty();
    private SimpleStringProperty nome = new SimpleStringProperty();
    private SimpleIntegerProperty idOrcamento = new SimpleIntegerProperty();
    private SimpleStringProperty data = new SimpleStringProperty();

    public Integer getId() {
        return id.get();
    }

    public String getNome() {
        return nome.get();
    }

    public int getIdOrcamento() {
        return idOrcamento.get();
    }

    public String getData() {
        return data.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }
    
     public void setIdOrcamento(int id) {
        this.idOrcamento.set(id);
    }

    public void setData(String data) {
        this.data.set(data);
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

        if (!validator.nomeSubOrcamento(this.getNome())) {
            errors.add("nome");
        }

        if (!validator.data(this.getData())) {
            errors.add("data");
        }
        return errors;
    }

    /**
     * Método que devolve uma string com a informação de um Sub Orçamento
     *
     * @return informação de um Orçamento
     */
    @Override
    public String toString() {
        return "" + nome.get();
    }
}
