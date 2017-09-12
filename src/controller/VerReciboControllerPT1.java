package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.VerReciboPT1;
import application.VerReciboPT2;
import dao.OrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import model.Orcamento;

/**
 * Classe VerReciboControllerPT1 está responsável pelo controlo dos eventos
 * relativos á Stage que permite escolher um Orçamento para que em seguida possa
 * seguir para a Stage de ver Recibos (VerReciboControllerPT2)
 *
 * @author Luís Sousa - 8090228
 */
public class VerReciboControllerPT1 implements Initializable {

    @FXML
    private TableColumn clVersao;
    @FXML
    private TableColumn clData;
    @FXML
    private TableColumn clAno;

    @FXML
    private Button btProximo;

    @FXML
    private TableView tbOrcamento;

    @FXML
    private ObservableList<Orcamento> orcamento;

    private int idOrcamentoSel;
    private int positionOrcamentoTable;
    private OrcamentoDAO dao;

    /**
     * Método que permite ir para a stage seguinte onde se pode ver os recibos já gerados
     *
     * @param event evento
     * @throws ClassNotFoundException erro classe não encontrada
     * @throws SQLException erro de sql
     * @throws Exception excepção genérica
     */
    @FXML
    private void btNextFired(ActionEvent event) throws ClassNotFoundException, SQLException, Exception {
        new VerReciboControllerPT2().setIdOrc(getIdOrcamentoSel());
        VerReciboPT1.getStage().close();
        new VerReciboPT2().start(new Stage());
    }

    /**
     * Método que retorna o orçamento seleccionado na tabela
     *
     * @return Orçamento seleccionado
     */
    private Orcamento getTableOrcamentoSeleccionado() {
        if (tbOrcamento != null) {
            List<Orcamento> tabela = tbOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final Orcamento orcamentoSeleccionado = tabela.get(0);
                return orcamentoSeleccionado;
            }
        }
        return null;
    }

    /**
     * Listener tableview que entra em ação quando muda o orcamento selecionado
     * na tabela
     */
    private final ListChangeListener<Orcamento> selectorTableOrcamento
            = new ListChangeListener<Orcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Orcamento> c) {

                    try {
                        getIdOrcamentoSel();
                        btProximo.setDisable(false);
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(OrcamentoControllerPT0.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };

    /**
     * Prepara a tabela fazendo o binding entre as colunas da tabela e o atributo do objeto correspondente
     */
    private void iniciarTableView() {
        clVersao.setCellValueFactory(new PropertyValueFactory<Orcamento, String>("versao"));
        clAno.setCellValueFactory(new PropertyValueFactory<Orcamento, Integer>("ano"));
        clData.setCellValueFactory(new PropertyValueFactory<Orcamento, String>("data"));

        orcamento = FXCollections.observableArrayList();
        tbOrcamento.setItems(orcamento);
    }

    /**
     * Inicia a classe verReciboControllerPT1
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.iniciarTableView();

        btProximo.setDisable(true);

        final ObservableList<Orcamento> tabelaOrcamentoSel = tbOrcamento.getSelectionModel().getSelectedItems();
        tabelaOrcamentoSel.addListener(selectorTableOrcamento);

        dao = new OrcamentoDAO();
        for (Orcamento orc : dao.getAllOrcamentos()) {
            if (orc.getEstado().equals("Definitivo")) {
                orcamento.add(orc);
            }
        }

    }

    /**
     * Método que retorna o id do Orçamento seleccionado
     * @return o ido do orçamento
     * @throws ClassNotFoundException erro de classe não encontrada
     * @throws SQLException erro de sql
     */
    private int getIdOrcamentoSel() throws ClassNotFoundException, SQLException {
        dao = new OrcamentoDAO();
        int id;
        try {
            id = dao.getOrcamentoId(getTableOrcamentoSeleccionado().getVersao(), getTableOrcamentoSeleccionado().getAno());
        } catch (Exception ex) {
            id = -1;
        }
        this.idOrcamentoSel = id;
        //System.out.println("ID SEL" + idOrcamentoSel);
        dao.close();
        return id;
    }

}
