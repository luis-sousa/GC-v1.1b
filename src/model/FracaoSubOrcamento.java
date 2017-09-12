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
 * Esta classe tem como objectivo criar uma Fraccao relativamente a um Sub
 * Orçamento.
 *
 * @author Luís Sousa - 8090228
 */
public class FracaoSubOrcamento {

    private SimpleStringProperty fracao = new SimpleStringProperty();
    private SimpleIntegerProperty idSubOrcamento = new SimpleIntegerProperty();

    public String getFracao() {
        return fracao.get();
    }

    public void setFracao(String fraccao) {
        this.fracao.set(fraccao);
    }

    public Integer getIdSubOrcamento() {
        return idSubOrcamento.get();
    }

    public void setIdSubOrcamento(Integer id) {
        this.idSubOrcamento.set(id);
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

        if (!validator.codigoFraccao(this.getFracao())) {
            errors.add("fracao");
        }
        return errors;
    }

}
