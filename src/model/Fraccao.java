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
 * Esta classe tem como objectivo criar uma Fraccao.
 *
 * @author Luís Sousa - 8090228
 */
public class Fraccao {

    public SimpleStringProperty cod = new SimpleStringProperty();
    public SimpleStringProperty nome = new SimpleStringProperty();
    public SimpleStringProperty morada = new SimpleStringProperty();
    public SimpleStringProperty codPostal = new SimpleStringProperty();
    public SimpleStringProperty localidade = new SimpleStringProperty();
    public SimpleIntegerProperty telefone = new SimpleIntegerProperty();
    public SimpleIntegerProperty telemovel = new SimpleIntegerProperty();
    public SimpleStringProperty mail = new SimpleStringProperty();
    public SimpleIntegerProperty contribuinte = new SimpleIntegerProperty();
    public SimpleFloatProperty permilagem = new SimpleFloatProperty();
    public SimpleStringProperty tipo = new SimpleStringProperty();

    public String getCod() {
        return cod.get();
    }

    public String getNome() {
        return nome.get();
    }

    public String getMorada() {
        return morada.get();
    }

    public String getCodPostal() {
        return codPostal.get();
    }

    public String getLocalidade() {
        return localidade.get();
    }

    public Integer getTelefone() {
        return telefone.get();
    }

    public Integer getTelemovel() {
        return telemovel.get();
    }

    public String getMail() {
        return mail.get();
    }

    public Integer getContribuinte() {
        return contribuinte.get();
    }

    public Float getPermilagem() {
        return permilagem.get();
    }

    public String getTipo() {
        return tipo.get();
    }

    /**
     * Método que devolve uma lista com os errors que provêm de campos que foram
     * mal introduzidos
     *
     * @param telefone telefone introduzido
     * @param telemovel telemovel introduzido
     * @param email email introduzido
     * @return array com erros
     */
    public ArrayList<String> validate(String telefone, String telemovel, String email) {
        ArrayList<String> errors = new ArrayList<>();
        Validator validator = new Validator();
        errors.clear();

        if (!validator.codigoFraccao(getCod())) {
            errors.add("codigo");
        }
        if (!validator.nome(getNome())) {
            errors.add("nome");
        }
        if (!validator.morada(getMorada())) {
            errors.add("morada");
        }
        if (!validator.codPostal(getCodPostal())) {
            errors.add("codPostal");
        }
        if (!validator.localidade(getLocalidade())) {
            errors.add("localidade");
        }

        /*if (telefone.trim().equals("") && telemovel.trim().equals("")) {
            errors.add("telefone");
            errors.add("telemovel");
        }*/

        if (!telefone.trim().equals("")) {
            if (!validator.telefone(this.getTelefone())) {
                errors.add("telefone");
            }
        }

        if (!telemovel.trim().equals("")) {
            if (!validator.telemovel(this.getTelemovel())) {
                errors.add("telemovel");
            }
        }

        if (!email.trim().equals("")) { // se email != vazio valida
            if (!validator.mail(getMail())) {
                errors.add("mail");
            }
        }
        
        if (!validator.contribuinte(getContribuinte())) {
            errors.add("contribuinte");
        }
        if (!validator.permilagem(getPermilagem())) {
            errors.add("permilagem");
        }
        return errors;
    }

}
