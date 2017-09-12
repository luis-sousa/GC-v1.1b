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
 * Esta classe tem como objectivo criar uma Despesa.
 *
 * @author Luís Sousa - 8090228
 */
public class DespesaOrcamento {

    private SimpleStringProperty despesa = new SimpleStringProperty();
    private SimpleStringProperty rubrica = new SimpleStringProperty();
    private SimpleStringProperty data = new SimpleStringProperty();
    private SimpleFloatProperty montanteEuros = new SimpleFloatProperty();
    private SimpleIntegerProperty montanteCentimos = new SimpleIntegerProperty();
    private SimpleIntegerProperty idOrcamento = new SimpleIntegerProperty();

    public String getDespesa() {
        return despesa.get();
    }

    public void setDespesa(String despesa) {
        this.despesa.set(despesa);
    }

    public String getRubrica() {
        return rubrica.get();
    }

    public void setRubrica(String rubrica) {
        this.rubrica.set(rubrica);
    }

    public String getData() {
        return data.get();
    }

    public void setData(String data) {
        this.data.set(data);
    }

    public Float getMontanteEuros() {
        return montanteEuros.get();
    }

    public void setMontanteEuros(Float montante) {
        this.montanteEuros.set(montante);
    }

    public int getIdOrcamento() {
        return idOrcamento.get();
    }

    public void setIdOrcamento(int idOrc) {
        this.idOrcamento.set(idOrc);
    }

    public Integer getMontanteCentimos() {
        return montanteCentimos.get();
    }

    public void setMontanteCentimos(Integer montante) {
        this.montanteCentimos.set(montante);
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

        if (!validator.despesa(this.getDespesa())) {
            errors.add("despesa");
        }

        if (!validator.data(this.getData())) {
            errors.add("data");
        }

        if (!validator.montante(this.getMontanteEuros())) {
            errors.add("montante");
        }
        return errors;
    }

}
