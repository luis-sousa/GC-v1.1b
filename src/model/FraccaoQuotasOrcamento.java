package model;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Esta classe tem como objectivo definir uma mensalidade de uma fracção.
 *
 * @author Luís Sousa - 8090228
 */
public class FraccaoQuotasOrcamento {

    private SimpleStringProperty fraccao = new SimpleStringProperty();
    private SimpleStringProperty nome = new SimpleStringProperty();
    private SimpleFloatProperty perm = new SimpleFloatProperty();
    private SimpleFloatProperty mensalEuros = new SimpleFloatProperty();
    private SimpleIntegerProperty mensalCentimos = new SimpleIntegerProperty();
    private SimpleFloatProperty anualEuros = new SimpleFloatProperty();
    private SimpleIntegerProperty anualCentimos = new SimpleIntegerProperty();
    private SimpleIntegerProperty ano = new SimpleIntegerProperty();
    private SimpleIntegerProperty idOrcamento = new SimpleIntegerProperty();

    public String getFraccao() {
        return fraccao.get();
    }

    public void setFraccao(String fraccao) {
        this.fraccao.set(fraccao);
    }

    public String getNome() {
        return nome.get();
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public Float getPerm() {
        return perm.get();
    }

    public void setPerm(Float perm) {
        this.perm.set(perm);
    }

    public Float getMensalEuros() {
        return mensalEuros.get();
    }

    public void setMensalEuros(Float mensal) {
        this.mensalEuros.set(mensal);
    }

    public Float getAnualEuros() {
        return anualEuros.get();
    }

    public void setAnualEuros(Float anual) {
        this.anualEuros.set(anual);
    }

    public Integer getAno() {
        return ano.get();
    }

    public void setAno(Integer ano) {
        this.ano.set(ano);
    }

    public Integer getIdOrcamento() {
        return idOrcamento.get();
    }

    public void setIdOrcamento(Integer id) {
        this.idOrcamento.set(id);
    }

    public Integer getMensalCentimos() {
        return mensalCentimos.get();
    }

    public void setMensalCentimos(Integer mensal) {
        this.mensalCentimos.set(mensal);
    }

    public Integer getAnualCentimos() {
        return anualCentimos.get();
    }

    public void setAnualCentimos(Integer anual) {
        this.anualCentimos.set(anual);
    }
}
