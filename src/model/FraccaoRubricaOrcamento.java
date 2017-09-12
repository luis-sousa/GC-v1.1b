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
 * Esta classe tem como objectivo criar uma rubrica associada a uma fracção
 * relativamente a um orçamento.
 *
 * @author Luís Sousa - 8090228
 */
public class FraccaoRubricaOrcamento {

    private SimpleStringProperty fraccao = new SimpleStringProperty();
    private SimpleStringProperty rubrica = new SimpleStringProperty();
    private SimpleIntegerProperty idOrcamento = new SimpleIntegerProperty();

    public String getFraccao() {
        return fraccao.get();
    }

    public void setFraccao(String fraccao) {
        this.fraccao.set(fraccao);
    }

    public String getRubrica() {
        return rubrica.get();
    }

    public void setRubrica(String rubrica) {
        this.rubrica.set(rubrica);
    }

    public Integer getIdOrcamento() {
        return idOrcamento.get();
    }

    public void setIdOrcamento(Integer id) {
        this.idOrcamento.set(id);
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

        if (!validator.codigoFraccao(this.getFraccao())) {
            errors.add("fraccao");
        }
        return errors;
    }

}
