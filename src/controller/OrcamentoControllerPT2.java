package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.AddOrcamentoPT1;
import application.AddOrcamentoPT2;
import application.AddOrcamentoPT3;
import dao.AvisoDebitoDAO;
import dao.DespesaOrcamentoDAO;
import dao.FraccaoDAO;
import dao.FraccaoOrcamentoDAO;
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
import model.Fraccao;
import model.FraccaoOrcamento;
import model.Orcamento;
import validation.Validator;

/**
 * Classe OrçamentoControllerPT2 está responsável pelo controlo dos eventos
 * relativos á Stage que permite associar fracções pagantes a um Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class OrcamentoControllerPT2 implements Initializable {

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
    private ObservableList<FraccaoOrcamento> fraccoesOrcamento;
    @FXML
    private ObservableList<String> fraccoes;

    private int positionFraccaoTable;
    private Validator validator;
    private ArrayList<String> errors;
    private FraccaoOrcamento fraccaoOrcamento;

    private FraccaoOrcamentoDAO dao;
    private static int idOrc; //id do orcamento a manipular
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

        fraccaoOrcamento = new FraccaoOrcamento();
        fraccaoOrcamento.setFraccao((String) cbFraccao.getValue());
        fraccaoOrcamento.setIdOrcamento(idOrc);
        errors = fraccaoOrcamento.validate();

        if (errors.isEmpty()) {
            fraccoesOrcamento.add(fraccaoOrcamento); //adicionar na tableview
            dao = new FraccaoOrcamentoDAO();
            dao.adicionar(fraccaoOrcamento); //add na BD 
            dao.close();
            showSucessDialog("Fracção pagante adicionada com sucesso !!!");
            refreshCBFraccoes();
            OrcamentoControllerPT2.alteracoes = true;
            new OrcamentoControllerPT3().setAlteracaoes(OrcamentoControllerPT2.alteracoes);
        } else {
            validator = new Validator();

            for (String error : errors) {
                if (error.equals("fraccao")) {
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

        dao = new FraccaoOrcamentoDAO();
        dao.remover(getTableFraccaoSeleccionada());//remover na bd
        dao.close();
        fraccoesOrcamento.remove(getTableFraccaoSeleccionada()); //apagar na table view
        showSucessDialog("Fracção removida deste Orçamento com sucesso !!!");
        refreshCBFraccoes();
        OrcamentoControllerPT2.alteracoes = true;
        new OrcamentoControllerPT3().setAlteracaoes(OrcamentoControllerPT2.alteracoes);

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

        new OrcamentoControllerPT3().setIdOrc(idOrc);
        new OrcamentoControllerPT3().setAlteracaoes(alteracoes);
        AddOrcamentoPT2.getStage().close();
        new AddOrcamentoPT3().start(new Stage());

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

        AddOrcamentoPT2.getStage().close();
        new AddOrcamentoPT1().start(new Stage());

    }

    /**
     * Listener tableview fraccoesOrcamento
     */
    private final ListChangeListener<FraccaoOrcamento> selectorTableFraccoes
            = new ListChangeListener<FraccaoOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends FraccaoOrcamento> c) {
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
        final FraccaoOrcamento fraccao = getTableFraccaoSeleccionada();
        positionFraccaoTable = fraccoesOrcamento.indexOf(fraccao);

        if (fraccao != null) {
            // poe os txtfield com os dados correspondentes
            cbFraccao.setValue(fraccao.getFraccao());

            // poe os botoes num estado especifico 
            btRemover.setDisable(false);
            btInserir.setDisable(true);

        }
    }

    //retorna fraccaoOrcamento selecionada na tableview
    /**
     * Método que devolve a fracção seleccionada
     *
     * @return FraccaoOrcamento
     */
    private FraccaoOrcamento getTableFraccaoSeleccionada() {
        if (tbFraccaoOrcamento != null) {
            List<FraccaoOrcamento> tabela = tbFraccaoOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final FraccaoOrcamento fraccaoSeleccionada = tabela.get(0);
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
        clFraccao.setCellValueFactory(new PropertyValueFactory<FraccaoOrcamento, String>("fraccao"));
        fraccoesOrcamento = FXCollections.observableArrayList();
        tbFraccaoOrcamento.setItems(fraccoesOrcamento);
    }

    /**
     * Inicia a classe OrçamentoControllerPT2.
     *
     * @param url url   
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        dao = new FraccaoOrcamentoDAO();
        Orcamento orc = dao.getOrcamento(idOrc);
        lbInfo.setText("Orcamento: " + orc.getAno() + " da data: " + orc.getData() + " versao: " + orc.getVersao());

        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);
        cbFraccao.setValue(""); //importante validar a fraccao

        //refreshCBFraccoes();
        dao = new FraccaoOrcamentoDAO();
        if (!dao.getAllFraccoesOrcamento(idOrc).isEmpty()) { // se existem dados na BD faz populate table view
            ArrayList<FraccaoOrcamento> lista = new ArrayList<FraccaoOrcamento>();
            lista = dao.getAllFraccoesOrcamento(idOrc);
            for (FraccaoOrcamento fraccao : lista) {
                fraccoesOrcamento.add(fraccao); //faz populate na table view
            }
        } else {
            FraccaoDAO fDAO = new FraccaoDAO();
            fDAO.getAllFraccoes();

            for (Fraccao f : fDAO.getAllFraccoes()) {
                fraccaoOrcamento = new FraccaoOrcamento();
                fraccaoOrcamento.setFraccao(f.getCod());
                fraccaoOrcamento.setIdOrcamento(idOrc);
                dao.adicionar(fraccaoOrcamento);
                fraccoesOrcamento.add(fraccaoOrcamento);
            }
        }
        dao.close();

        if (!validarRemoverEditarOrcamento()) { // se nao pode editar orçamento
            disableComponents();
        } else {
            refreshCBFraccoes();

            // Seleccionar as linhas na tabela fraccoes
            final ObservableList<FraccaoOrcamento> tabelaFraccaoSel = tbFraccaoOrcamento.getSelectionModel().getSelectedItems();
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

        for (FraccaoOrcamento ro : fraccoesOrcamento) {
            if (ro.getFraccao().equals(fraccao)) {
                result = true;
            }

        }
        return result;
    }

    /**
     * Método que faz o resfresh dos values que a combo box poderá ter naquele
     * momento. Vai actualizando conforme as fracções se vão inserindo na table
     * view e na BD
     */
    public void refreshCBFraccoes() {
        //buscar todas as fraccoes existem na BD para preencher a combo box
        fraccoes = FXCollections.observableArrayList();
        FraccaoDAO fraccaoDAO = new FraccaoDAO();
        ArrayList<Fraccao> lista = new ArrayList<Fraccao>();
        lista = fraccaoDAO.getAllFraccoes();
        for (Fraccao fraccao : lista) {
            if (!existFraccaoTableView(fraccao.getCod())) {
                fraccoes.add(fraccao.getCod()); //adiciona fraccoes possiveis na observable list
            } else {
                fraccoes.remove(fraccao.getCod());
            }
        }
        fraccaoDAO.close();

        cbFraccao.setItems(fraccoes); //preencher combo box com as fraccoes possiveis de adicionar
    }

    /**
     * Recebe o id do Orçamento que foi escolhido para manipular
     *
     * @param id
     */
    public void setIdOrc(int id) {
        OrcamentoControllerPT2.idOrc = id;
        //System.out.println("ID manipular" + idOrc);
    }

    /**
     * Recebe se houve ou não alterações nos forms anteriores
     *
     * @param result resultado das alterações
     */
    public void setAlteracaoes(boolean result) {
        OrcamentoControllerPT2.alteracoes = result;
        //System.out.println("Houve PT1" + alteracoes);
    }

    /**
     * Método que verifica se um orçamento pode ser removido ou editado.Só pode
     * remover ou editar orçamento se ele não tiver dados associados (avisos de
     * débito) ou despesas
     *
     * @return se pode remover o orçamento.
     */
    private boolean validarRemoverEditarOrcamento() {
        boolean resultDsp = true;
        boolean resultAd = true;

        DespesaOrcamentoDAO Dspdao = new DespesaOrcamentoDAO();
        AvisoDebitoDAO adDAO = new AvisoDebitoDAO();

        if (Dspdao.existOrcamento(idOrc)) {
            resultDsp = false;
        }
        Dspdao.close();

        //System.out.println(resultDsp);
        if (adDAO.existOrcamento(idOrc)) {
            resultAd = false;
        }
        //System.out.println(resultAd);
        adDAO.close();

        return resultDsp && resultAd;
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
        Window owner = AddOrcamentoPT2.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
}
