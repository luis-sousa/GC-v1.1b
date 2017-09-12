package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.GerarAvisoExtraPT1;
import application.GerarAvisoExtraPT2;
import dao.SubOrcamentoDAO;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.SubOrcamento;

/**
 * Classe GerarAvisoControllerPT1 está responsável pelo controlo dos eventos
 * relativos á Stage que permite escolher um Orçamento para que em seguida possa
 * seguir para a Stage de gerar Avisos (GerarAvisoControllerPT2)
 *
 * @author Luís Sousa - 8090228
 */
public class GerarAvisoExtraControllerPT1 implements Initializable {
    private static int idOrc; //idOrcamento

    @FXML
    private TableColumn clNome;
    @FXML
    private TableColumn clOrcamento;
    @FXML
    private TableColumn clData;

    @FXML
    private Button btProximo;

    @FXML
    private TableView tbOrcamento;

    @FXML
    private ObservableList<SubOrcamento> subOrcamento;

    private int idSubOrcamentoSel;
    private int positionOrcamentoTable;
    private SubOrcamentoDAO dao;

    /**
     * Método que permite ir para a stage seguinte para gerar os avisos para o
     * orçamento que foi escolhido aqui
     *
     * @param event evento
     * @throws ClassNotFoundException erro de classe
     * @throws SQLException erro de sql
     * @throws IOException erro input/output
     */
    @FXML
    private void btNextFired(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {
        new GerarAvisoExtraControllerPT2().setIdOrc(idOrc);
        new GerarAvisoExtraControllerPT2().setIdSubOrc(getIdSubOrcamentoSel());
        GerarAvisoExtraPT1.getStage().close();
        new GerarAvisoExtraPT2().start(new Stage());
    }

    /**
     * Método que devolve o orçamento seleccionado na tabela
     *
     * @return um Orcamento
     */
    private SubOrcamento getTableOrcamentoSeleccionado() {
        if (tbOrcamento != null) {
            List<SubOrcamento> tabela = tbOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final SubOrcamento orcamentoSeleccionado = tabela.get(0);
                return orcamentoSeleccionado;
            }
        }
        return null;
    }

    /**
     * Listener tableview que entra em ação quando muda o subOrcamento
     * selecionado na tabela
     */
    private final ListChangeListener<SubOrcamento> selectorTableOrcamento
            = new ListChangeListener<SubOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends SubOrcamento> c) {

                    getIdSubOrcamentoSel();
                    btProximo.setDisable(false);

                }
            };

    /**
     * Prepara a tabela
     */
    private void iniciarTableView() {
        clNome.setCellValueFactory(new PropertyValueFactory<SubOrcamento, String>("nome"));
        clOrcamento.setCellValueFactory(new PropertyValueFactory<SubOrcamento, String>("idOrcamento"));
        clData.setCellValueFactory(new PropertyValueFactory<SubOrcamento, String>("data"));

        subOrcamento = FXCollections.observableArrayList();
        tbOrcamento.setItems(subOrcamento);
    }

    
    /**
     * Método que recebe o id do Orçamento
     *
     * @param id identificação de um orçamento
     */
    public void setIdOrc(int id) {
        GerarAvisoExtraControllerPT1.idOrc = id;
    }
    /**
     * Inicia a classe GerarAvisoControllerPT1.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.iniciarTableView();

        btProximo.setDisable(true);

        final ObservableList<SubOrcamento> tabelaOrcamentoSel = tbOrcamento.getSelectionModel().getSelectedItems();
        tabelaOrcamentoSel.addListener(selectorTableOrcamento);

        dao = new SubOrcamentoDAO();
        for (SubOrcamento orc : dao.getAllSubOrcamentos(idOrc)) {
            subOrcamento.add(orc);
        }

    }

    /**
     * Método que retorna o id do Orçamento Seleccionado
     *
     * @return id do Orçamento
     */
    private int getIdSubOrcamentoSel() {
        dao = new SubOrcamentoDAO();
        int id;
        try {
            id = dao.getSubOrcamentoId(getTableOrcamentoSeleccionado().getNome(), getTableOrcamentoSeleccionado().getIdOrcamento(), getTableOrcamentoSeleccionado().getData());
        } catch (Exception ex) {
            id = -1;
        }
        this.idSubOrcamentoSel = id;
        //System.out.println("ID SEL" + idSubOrcamentoSel);
        dao.close();
        return id;
    }
}
