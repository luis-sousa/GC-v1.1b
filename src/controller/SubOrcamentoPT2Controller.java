package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */

import application.SubOrcamentoPT1;
import application.SubOrcamentoPT2;
import application.SubOrcamentoPT3;
import dao.AvisoDebitoDAO;
import dao.FracaoSubOrcamentoDAO;
import dao.FraccaoOrcamentoDAO;
import dao.SubOrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.FracaoSubOrcamento;
import model.Fraccao;
import model.FraccaoOrcamento;
import validation.Validator;

/**
 * Classe OrçamentoControllerPT2 está responsável pelo controlo dos eventos
 * relativos á Stage que permite associar fracções pagantes a um Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class SubOrcamentoPT2Controller implements Initializable {

    @FXML
    private ComboBox cbFraccao;

    @FXML
    private Button btRemover;
    @FXML
    private Button btNova;
    @FXML
    private Button btInserir;

    @FXML
    private Label lbInfo;
    @FXML
    private Label lbFraccao;

    @FXML
    private TableView tbFraccaoOrcamento;
    @FXML
    private TableColumn clFraccao;

    @FXML
    private ObservableList<FracaoSubOrcamento> fracoesOrcamento;
    @FXML
    private ObservableList<String> fracoes;

    private int positionFraccaoTable;
    private Validator validator;
    private ArrayList<String> errors;
    private FracaoSubOrcamento fracaoSubOrcamento;

    private FracaoSubOrcamentoDAO dao;
    private static int idSubOrc; //id do sub orcamento a manipular
    private static int id; //id do orcamento a que pertence o sub
    private static boolean alteracoes;

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        lbFraccao.setText("");
    }

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        cbFraccao.setValue("");
    }

    /**
     * Método que remove o style errors
     */
    private void removerStyles() {
        cbFraccao.getStyleClass().remove("errorTextField");

    }

    /**
     * Método que permite preparar a Stage para a inserção de uma nova Fracção
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {
        limparCampos();
        limparMSG();
        removerStyles();
        cbFraccao.setDisable(false);
        btRemover.setDisable(true);
        btInserir.setDisable(false);
        refreshCBFraccoes();
    }

    /**
     * Método que permite associar uma Fracção a um Orçamento na table view e na
     * BD, ou seja as fracções pagantes
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        errors = new ArrayList<String>();

        fracaoSubOrcamento = new FracaoSubOrcamento();
        fracaoSubOrcamento.setFracao((String) cbFraccao.getValue());
        fracaoSubOrcamento.setIdSubOrcamento(idSubOrc);
        errors = fracaoSubOrcamento.validate();

        if (errors.isEmpty()) {
            fracoesOrcamento.add(fracaoSubOrcamento); //adicionar na tableview
            dao = new FracaoSubOrcamentoDAO();
            dao.adicionar(fracaoSubOrcamento); //add na BD 
            dao.close();
            showSucessDialog("Fração pagante adicionada com sucesso !!!");
            refreshCBFraccoes();
            SubOrcamentoPT2Controller.alteracoes = true;
            new SubOrcamentoPT3Controller().setAlteracaoes(SubOrcamentoPT2Controller.alteracoes);
        } else {
            validator = new Validator();

            for (String error : errors) {
                if (error.equals("fracao")) {
                    lbFraccao.setText(validator.getErrorCodFraccao());
                }
                cbFraccao.getStyleClass().add("errorTextField");
            }
        }

    }

    /**
     * Método que permite remover/ desassociar uma Fracção de um Orçamento na
     * table view e na BD
     *
     * @param event evento
     */
    @FXML
    private void btRemoveFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();

        dao = new FracaoSubOrcamentoDAO();
        dao.remover(getTableFraccaoSeleccionada());//remover na bd
        dao.close();
        fracoesOrcamento.remove(getTableFraccaoSeleccionada()); //apagar na table view
        showSucessDialog("Fração removida deste Sub Orçamento com sucesso !!!");
        refreshCBFraccoes();
        SubOrcamentoPT2Controller.alteracoes = true;
        new SubOrcamentoPT3Controller().setAlteracaoes(SubOrcamentoPT2Controller.alteracoes);

    }

    /**
     * Método que permite avançar para a Stage seguinte onde se define para cada
     * fracção que rubricas é que pagam relativamente aquele orçamento
     *
     * @param event evento
     * @throws Exception
     */
    @FXML
    private void btNextFired(ActionEvent event) throws Exception {
        limparMSG();
        removerStyles();

        new SubOrcamentoPT3Controller().setIdOrc(idSubOrc);
        new SubOrcamentoPT3Controller().setAlteracaoes(alteracoes);
        SubOrcamentoPT2.getStage().close();
        new SubOrcamentoPT3().start(new Stage());

    }

    /**
     * Método que permite ir para a Stage anterior, stage que permitiu associar
     * rubricas a um Orçamento
     *
     * @param event evento
     * @throws Exception entra na excepção
     */
    @FXML
    private void btPreviousFired(ActionEvent event) throws Exception {
        limparMSG();
        removerStyles();

        SubOrcamentoPT2.getStage().close();
        new SubOrcamentoPT1().start(new Stage());

    }

    /**
     * Listener tableview fracoesOrcamento
     */
    private final ListChangeListener<FracaoSubOrcamento> selectorTableFraccoes
            = new ListChangeListener<FracaoSubOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends FracaoSubOrcamento> c) {
                    verFraccaoSeleccionadaDetails();
                }
            };

    /**
     * Mostra os detalhes da fracção selecionada
     */
    private void verFraccaoSeleccionadaDetails() {

        limparMSG();
        removerStyles();
        cbFraccao.setEditable(false);
        cbFraccao.setDisable(true);
        final FracaoSubOrcamento fracao = getTableFraccaoSeleccionada();
        positionFraccaoTable = fracoesOrcamento.indexOf(fracao);

        if (fracao != null) {
            // poe os txtfield com os dados correspondentes
            cbFraccao.setValue(fracao.getFracao());

            // poe os botoes num estado especifico 
            btRemover.setDisable(false);
            btInserir.setDisable(true);

        }
    }

    //retorna fracaoSubOrcamento selecionada na tableview
    /**
     * Método que devolve a fracção seleccionada
     *
     * @return FraccaoOrcamento
     */
    private FracaoSubOrcamento getTableFraccaoSeleccionada() {
        if (tbFraccaoOrcamento != null) {
            List<FracaoSubOrcamento> tabela = tbFraccaoOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final FracaoSubOrcamento fraccaoSeleccionada = tabela.get(0);
                return fraccaoSeleccionada;
            }
        }
        return null;
    }

    /**
     * Método para iniciar a tabela
     */
    private void iniciarTableView() {
        //fazer o binding entre as colunas e o respetivo campo do objeto e iniciar a tableview
        clFraccao.setCellValueFactory(new PropertyValueFactory<FracaoSubOrcamento, String>("fracao"));
        fracoesOrcamento = FXCollections.observableArrayList();
        tbFraccaoOrcamento.setItems(fracoesOrcamento);
    }

    /**
     * Inicia a classe OrçamentoControllerPT2.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SubOrcamentoPT2Controller.id = getIdOrcamento(idSubOrc);

//        dao = new FracaoSubOrcamentoDAO();
//        Orcamento orc = dao.getOrcamento(idSubOrc);
//        lbInfo.setText("Orcamento: " + orc.getAno() + " da data: " + orc.getData() + " versao: " + orc.getVersao());
        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);
        cbFraccao.setValue(""); //importante validar a fraccao

        //refreshCBFraccoes();
        dao = new FracaoSubOrcamentoDAO();
        if (!dao.getAllFracoesSubOrcamento(idSubOrc).isEmpty()) { // se existem dados na BD faz populate table view
            ArrayList<FracaoSubOrcamento> lista = new ArrayList<FracaoSubOrcamento>();
            lista = dao.getAllFracoesSubOrcamento(idSubOrc);
            for (FracaoSubOrcamento fraccao : lista) {
                fracoesOrcamento.add(fraccao); //faz populate na table view
            }
        } else {

            FraccaoOrcamentoDAO fDAO = new FraccaoOrcamentoDAO();

            for (Fraccao f : fDAO.getAllFraccoes()) {
                fracaoSubOrcamento = new FracaoSubOrcamento();
                fracaoSubOrcamento.setFracao(f.getCod());
                fracaoSubOrcamento.setIdSubOrcamento(idSubOrc);
                dao.adicionar(fracaoSubOrcamento);
                fracoesOrcamento.add(fracaoSubOrcamento);
            }
        }
        dao.close();

        if (!validarRemoverEditarOrcamento()) { // se nao pode editar orçamento
            disableComponents();
        } else {
            refreshCBFraccoes();

            // Seleccionar as linhas na tabela fracoes
            final ObservableList<FracaoSubOrcamento> tabelaFraccaoSel = tbFraccaoOrcamento.getSelectionModel().getSelectedItems();
            tabelaFraccaoSel.addListener(selectorTableFraccoes);
        }

    }

    /**
     * Método que permite verificar se já existe aquela fracção na tabela
     *
     * @param fraccao
     * @return existe ou não
     */
    public boolean existFraccaoTableView(String fraccao) {
        boolean result = false;

        for (FracaoSubOrcamento ro : fracoesOrcamento) {
            if (ro.getFracao().equals(fraccao)) {
                result = true;
            }

        }
        return result;
    }

    /**
     * Método que faz o refresh dos values que a combo box poderá ter naquele
     * momento. Vai actualizando conforme as frações se vão inserindo na table
     * view e na BD
     */
    public void refreshCBFraccoes() {
        //buscar todas as fracoes existem na BD para preencher a combo box
        fracoes = FXCollections.observableArrayList();
        FraccaoOrcamentoDAO fraccaoDAO = new FraccaoOrcamentoDAO();
        ArrayList<Fraccao> lista = new ArrayList<Fraccao>();
        lista = fraccaoDAO.getAllFraccoes();
        for (Fraccao fraccao : lista) {
            if (!existFraccaoTableView(fraccao.getCod())) {
                fracoes.add(fraccao.getCod()); //adiciona fracoes possiveis na observable list
            } else {
                fracoes.remove(fraccao.getCod());
            }
        }
        fraccaoDAO.close();

        cbFraccao.setItems(fracoes); //preencher combo box com as fracoes possiveis de adicionar
    }

    /**
     * Recebe o id do Orçamento que foi escolhido para manipular
     *
     * @param id
     */
    public void setIdOrc(int id) {
        SubOrcamentoPT2Controller.idSubOrc = id;
        //System.out.println("ID manipular" + idSubOrc);
    }

    /**
     * Recebe se houve ou não alterações nos forms anteriores
     *
     * @param result resultado das alterações
     */
    public void setAlteracaoes(boolean result) {
        SubOrcamentoPT2Controller.alteracoes = result;
        //System.out.println("Houve PT1" + alteracoes);
    }

    /**
     * Método que verifica se um sub orçamento pode ser removido ou editado.Só
     * pode remover ou editar um sub orçamento se ele não tiver dados associados
     * (avisos de débito)
     *
     * @return se pode remover ou editar o orçamento.
     */
    private boolean validarRemoverEditarOrcamento() {
        boolean resultAd = true;
        AvisoDebitoDAO adDAO = new AvisoDebitoDAO();

        if (adDAO.existSubOrcamento(idSubOrc)) {
            resultAd = false;
        }
        //System.out.println(resultAd);
        adDAO.close();

        return resultAd;
    }

    /**
     * Método que permite fazer disable a certos componentes da janela
     */
    private void disableComponents() {

        cbFraccao.setDisable(true);
        btRemover.setDisable(true);
        btInserir.setDisable(true);
        btNova.setDisable(true);
        lbFraccao.setDisable(true);
        tbFraccaoOrcamento.setDisable(true);
        tbFraccaoOrcamento.setEditable(false);
    }

       /**
     * Método que permite mostrar um Dialog de sucesso
     *
     * @param msg mensagem aparecer no Dialog
     */
    private void showSucessDialog(String msg) {
        Alert dlg = createAlert(Alert.AlertType.INFORMATION);
        dlg.setTitle("SUCESSO");
        String optionalMasthead = "Mensagem";
        dlg.getDialogPane().setContentText(msg);
        dlg.getDialogPane().setHeaderText(optionalMasthead);

        dlg.show();
    }

    /**
     * Método que permite mostrar um Dialog de Insucesso
     *
     * @param msg mensagem aparecer no Dialog
     */
    private void showErrorDialog(String msg) {
        Alert dlg = createAlert(Alert.AlertType.ERROR);
        dlg.setTitle("INSUCESSO");
        String optionalMasthead = "Erro Encontrado";
        dlg.getDialogPane().setContentText(msg);
        dlg.getDialogPane().setHeaderText(optionalMasthead);
        dlg.show();
    }

    private Alert createAlert(Alert.AlertType type) {
        Window owner = SubOrcamentoPT2.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

    private int getIdOrcamento(int idSubOrcamento) {
        int idO = -1;
        SubOrcamentoDAO dao2 = new SubOrcamentoDAO();
        idO = dao2.getOrcamentoID(idSubOrcamento);
        dao2.close();
        return idO;
    }
}
