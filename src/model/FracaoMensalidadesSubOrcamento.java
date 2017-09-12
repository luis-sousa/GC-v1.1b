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
public class FracaoMensalidadesSubOrcamento {

    private SimpleStringProperty fracao = new SimpleStringProperty();
    private SimpleStringProperty nome = new SimpleStringProperty();
    private SimpleFloatProperty perm = new SimpleFloatProperty();
    private SimpleFloatProperty mensalEuros = new SimpleFloatProperty();
    private SimpleFloatProperty anualEuros = new SimpleFloatProperty();
    private SimpleIntegerProperty mensalCentimos = new SimpleIntegerProperty();
    private SimpleIntegerProperty anualCentimos = new SimpleIntegerProperty();
    private SimpleIntegerProperty ano = new SimpleIntegerProperty();
    private SimpleIntegerProperty idSubOrcamento = new SimpleIntegerProperty();

    public String getFracao() {
        return fracao.get();
    }

    public void setFracao(String fraccao) {
        this.fracao.set(fraccao);
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

    public Integer getIdSubOrcamento() {
        return idSubOrcamento.get();
    }

    public void setIdSubOrcamento(Integer id) {
        this.idSubOrcamento.set(id);
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
