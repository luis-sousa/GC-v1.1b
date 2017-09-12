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
 * Esta classe tem como objectivo criar uma rubrica associada a uma fração
 * relativamente a um sub orçamento.
 *
 * @author Luís Sousa - 8090228
 */
public class FracaoRubricaSubOrcamento {

    private SimpleStringProperty fracao = new SimpleStringProperty();
    private SimpleStringProperty rubrica = new SimpleStringProperty();
    private SimpleIntegerProperty idSubOrcamento = new SimpleIntegerProperty();

    public String getFracao() {
        return fracao.get();
    }

    public void setFracao(String fracao) {
        this.fracao.set(fracao);
    }

    public String getRubrica() {
        return rubrica.get();
    }

    public void setRubrica(String rubrica) {
        this.rubrica.set(rubrica);
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

        if (!validator.rubrica(this.getRubrica())) {
            errors.add("rubrica");
        }

        if (!validator.codigoFraccao(this.getFracao())) {
            errors.add("fracao");
        }
        return errors;
    }
}
