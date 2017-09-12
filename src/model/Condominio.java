package model;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import java.util.ArrayList;
import validation.Validator;

/**
 * Esta classe tem como objectivo criar um Condominio.
 *
 * @author Luís Sousa - 8090228
 */
public class Condominio {

    private int id;
    private String nome;
    private String morada;
    private String codPostal;
    private String localidade;
    private int telefone;
    private int telemovel;
    private String mail;
    private int contribuinte;

    /**
     * Método construtor que permite criar um Condominio
     *
     * @param id
     * @param nome
     * @param morada
     * @param codPostal
     * @param localidade
     * @param telefone
     * @param telemovel
     * @param mail
     * @param contribuinte
     */
    public Condominio(int id, String nome, String morada, String codPostal, String localidade, int telefone, int telemovel, String mail, int contribuinte) {
        this.id = id;
        this.nome = nome;
        this.morada = morada;
        this.codPostal = codPostal;
        this.localidade = localidade;
        this.telefone = telefone;
        this.telemovel = telemovel;
        this.mail = mail;
        this.contribuinte = contribuinte;
    }

    /**
     * Método construtor
     */
    public Condominio() {
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the morada
     */
    public String getMorada() {
        return morada;
    }

    /**
     * @param morada the morada to set
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }

    /**
     * @return the codPostal
     */
    public String getCodPostal() {
        return codPostal;
    }

    /**
     * @param codPostal the codPostal to set
     */
    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
    }

    /**
     * @return the localidade
     */
    public String getLocalidade() {
        return localidade;
    }

    /**
     * @param localidade the localidade to set
     */
    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    /**
     * @return the telefone
     */
    public int getTelefone() {
        return telefone;
    }

    /**
     * @param telefone the telefone to set
     */
    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    /**
     * @return the telemovel
     */
    public int getTelemovel() {
        return telemovel;
    }

    /**
     * @param telemovel the telemovel to set
     */
    public void setTelemovel(int telemovel) {
        this.telemovel = telemovel;
    }

    /**
     * @return the mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * @return the contribuinte
     */
    public int getContribuinte() {
        return contribuinte;
    }

    /**
     * @param contribuinte the contribuinte to set
     */
    public void setContribuinte(int contribuinte) {
        this.contribuinte = contribuinte;
    }

    /**
     * Método que devolve uma lista com os errors que provêm de campos que foram
     * mal introduzidos
     *
     * @param telefone
     * @param telemovel
     * @return array com erros
     */
    public ArrayList<String> validate(String telefone, String telemovel) {
        ArrayList<String> errors = new ArrayList<>();
        Validator validator = new Validator();
        errors.clear();

        if (!validator.nome(this.nome)) {
            errors.add("nome");
        }
        if (!validator.morada(this.morada)) {
            errors.add("morada");
        }
        if (!validator.codPostal(this.codPostal)) {
            errors.add("codPostal");
        }
        if (!validator.localidade(this.localidade)) {
            errors.add("localidade");
        }

        /*if (telefone.trim().equals("") && telemovel.trim().equals("")) {
            errors.add("telefone");
            errors.add("telemovel");
        }*/

        if (!telefone.trim().equals("")) {
            if (!validator.telefone(this.telefone)) {
                errors.add("telefone");
            }
        }

        if (!telemovel.trim().equals("")) {
            if (!validator.telemovel(this.telemovel)) {
                errors.add("telemovel");
            }
        }

        if (!validator.mail(this.mail)) {
            errors.add("mail");
        }
        if (!validator.contribuinte(this.contribuinte)) {
            errors.add("contribuinte");
        }
        return errors;
    }
}
