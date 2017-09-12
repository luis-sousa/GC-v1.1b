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
 * Esta classe tem como objectivo criar uma observação relativamente a um
 * Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class ObsOrcamento {

    private SimpleStringProperty fraccao = new SimpleStringProperty();
    private SimpleStringProperty obs = new SimpleStringProperty();
    private SimpleIntegerProperty idOrc = new SimpleIntegerProperty();

    public String getFraccao() {
        return fraccao.get();
    }

    public int getIdOrc() {
        return idOrc.get();
    }

    public String getObs() {
        return obs.get();
    }

    public void setFraccao(String fraccao) {
        this.fraccao.set(fraccao);
    }

    public void setObs(String obs) {
        this.obs.set(obs);
    }

    public void setIdOrc(Integer idOrc) {
        this.idOrc.set(idOrc);
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

        if (!validator.observacao(this.getObs())) {
            errors.add("observacao");
        }
        return errors;
    }
}
