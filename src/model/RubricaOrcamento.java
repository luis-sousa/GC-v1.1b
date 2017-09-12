package model;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import java.util.ArrayList;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import validation.Validator;

/**
 * Esta classe tem como objectivo criar uma Rubrica associada a um Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class RubricaOrcamento {

    private SimpleStringProperty rubrica = new SimpleStringProperty();
    private SimpleFloatProperty valorEuros = new SimpleFloatProperty();
    private SimpleIntegerProperty valorCentimos = new SimpleIntegerProperty();
    private SimpleIntegerProperty idOrcamento = new SimpleIntegerProperty();

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

    public Float getValorEuros() {
        return valorEuros.get();
    }

    public void setValorEuros(Float valor) {
        this.valorEuros.set(valor);
    }

    public Integer getValorCentimos() {
        return valorCentimos.get();
    }

    public void setValorCentimos(int valor) {
        this.valorCentimos.set(valor);
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

        if (!validator.valor(this.getValorEuros())) {
            errors.add("valor");
        }
        return errors;
    }

}
