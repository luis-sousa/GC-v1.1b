package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.SubOrcamentoPT0;
import application.SubOrcamentoPT1;
import application.SubOrcamentoPT2;
import dao.AvisoDebitoDAO;
import dao.RubricaDAO;
import dao.RubricaSubOrcamentoDAO;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Rubrica;
import model.RubricaSubOrcamento;
import util.MoneyConverter;
import validation.Validator;

/**
 * Classe OrçamentoControllerPT1 está responsável pelo controlo dos eventos
 * relativos á Stage que permite associar rubricas a um Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class SubOrcamentoPT1Controller implements Initializable {

    @FXML
    private TextField txtDescricao;
    @FXML
    private TextField txtValor;

    @FXML
    private Button btRemover;
    @FXML
    private Button btInserir;
    @FXML
    private Button btEditar;
    @FXML
    private Button btNova;

    @FXML
    private Label lbInfo;
    @FXML
    private Label lbRubrica;
    @FXML
    private Label lbDescricao;
    @FXML
    private Label lbValor;

    @FXML
    private ComboBox cbRubrica;

    @FXML
    private TableView tbRubricaOrcamento;
    @FXML
    private TableColumn clRubrica;
    @FXML
    private TableColumn clValor;
    @FXML
    private TableColumn clDescricao;

    @FXML
    private ObservableList<RubricaSubOrcamento> rubricasOrcamento;

    @FXML
    private ObservableList<String> rubricas;

    private int positionRubricaTable;
    private Validator validator;
    private ArrayList<String> errors;
    private RubricaSubOrcamento rubricaOrcamento;
    private RubricaSubOrcamentoDAO dao;
    private static int idOrc; //id do orcamento a manipular
    private static boolean alteracoes;

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        lbDescricao.setText("");
        lbRubrica.setText("");
        lbValor.setText("");
    }

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        cbRubrica.setValue("");
        txtDescricao.setText("");
        txtValor.setText("");
    }

    /**
     * Método que remove o style error
     */
    private void removerStyles() {
        cbRubrica.getStyleClass().remove("errorTextField");
        txtDescricao.getStyleClass().remove("errorTextField");
        txtValor.getStyleClass().remove("errorTextField");

    }

    /**
     * Método que permite preparar a Stage para a inserção de uma nova rubrica
     * limpando os campos necessários
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {

        limparCampos();
        limparMSG();
        removerStyles();
        cbRubrica.setDisable(false);
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        btInserir.setDisable(false);
        refreshCBRubricas();
    }

    /**
     * Método que faz o resfresh dos values que a combo box poderá ter naquele
     * momento. Vai actualizando conforme as rubricas se vão inserindo na table
     * view e na BD
     */
    public void refreshCBRubricas() {
        //buscar todas as rubricas existem na BD para preencher a combo box
        rubricas = FXCollections.observableArrayList();
        RubricaDAO rubricaDAO = new RubricaDAO();
        ArrayList<Rubrica> lista = new ArrayList<Rubrica>();
        lista = rubricaDAO.getAllRubricas();
        for (Rubrica rubrica : lista) {
            if (!existRubricaTableView(rubrica.getNome())) {
                rubricas.add(rubrica.getNome()); //adiciona rubricas possiveis na observable list
            } else {
                rubricas.remove(rubrica.getNome());
            }

        }
        rubricaDAO.close();

        cbRubrica.setItems(rubricas); //preencher combo box com as rubricas possiveis de adicionar
    }

    /**
     * Método que permite associar uma Rubrica a um Orçamento na table view e na
     * BD
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        errors = new ArrayList<String>();

        String rubrica = (String) cbRubrica.getValue();

        rubricaOrcamento = new RubricaSubOrcamento();
        rubricaOrcamento.setRubrica(rubrica);
        rubricaOrcamento.setDescricao(txtDescricao.getText());
        try {
            rubricaOrcamento.setValorEuros(Float.parseFloat(txtValor.getText()));
            rubricaOrcamento.setValorCentimos(MoneyConverter.getCentimos(rubricaOrcamento.getValorEuros()));
        } catch (NumberFormatException ex) {
            rubricaOrcamento.setValorEuros(-1.f);
        }
        rubricaOrcamento.setIdSubOrcamento(idOrc);
        errors = rubricaOrcamento.validate();

//        if (existRubricaTableView(rubrica)) {
//            errors.add("rubricaExistente");
//        }
        if (errors.isEmpty()) {
            rubricasOrcamento.add(rubricaOrcamento); //adicionar na tableview
            dao = new RubricaSubOrcamentoDAO();
            dao.adicionar(rubricaOrcamento); //add na BD 
            dao.close();
            showSucessDialog("Rubrica associada com sucesso ao Sub Orçamento !!!");
            refreshCBRubricas();
            //indica ao orçamento seguinte que houve alterações
            SubOrcamentoPT1Controller.alteracoes = true;
            new SubOrcamentoPT2Controller().setAlteracaoes(SubOrcamentoPT1Controller.alteracoes);
            
        } else {
            validator = new Validator();
            for (String error : errors) {
                switch (error) {
                    case "rubrica":
                        lbRubrica.setText(validator.getErrorRubrica());
                        cbRubrica.getStyleClass().add("errorTextField");
                        break;
//                    case "rubricaExistente":
//                        lbRubrica.setText("Rubrica já existente");
//                        cbRubrica.getStyleClass().add("errorTextField");
//                        break;
                    case "descricao":
                        lbDescricao.setText(validator.getErrorDescricao());
                        txtDescricao.getStyleClass().add("errorTextField");
                        break;
                    case "valor":
                        lbValor.setText(validator.getErrorValor());
                        txtValor.getStyleClass().add("errorTextField");
                        break;
                }
            }
        }
    }

    /**
     * Método que permite desassociar uma Rubrica de um Orçamento na table view
     * e na BD
     *
     * @param event evento
     */
    @FXML
    private void btRemoveFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();

        dao = new RubricaSubOrcamentoDAO();
        dao.remover((String) cbRubrica.getValue(), idOrc);//remover na bd
        dao.close();
        rubricasOrcamento.remove(getTableRubricaSeleccionada()); //apagar na table view
        showSucessDialog("Rubrica apagada com sucesso neste Sub Orçamento !!!");
        refreshCBRubricas();
        SubOrcamentoPT1Controller.alteracoes = true;
        new SubOrcamentoPT2Controller().setAlteracaoes(SubOrcamentoPT1Controller.alteracoes);
    }

    /**
     * Método que permite editar uma Rubrica associada a um Orçamento na table
     * view e na BD
     *
     * @param event
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @FXML
    private void btEditFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();

        errors = new ArrayList<String>();

        rubricaOrcamento = new RubricaSubOrcamento();
        rubricaOrcamento.setRubrica((String) cbRubrica.getValue());
        rubricaOrcamento.setDescricao(txtDescricao.getText());
        try {
            rubricaOrcamento.setValorEuros(Float.parseFloat(txtValor.getText()));
            rubricaOrcamento.setValorCentimos(MoneyConverter.getCentimos(rubricaOrcamento.getValorEuros()));
        } catch (NumberFormatException ex) {
            rubricaOrcamento.setValorEuros(-1.f);
        }
        rubricaOrcamento.setIdSubOrcamento(idOrc);
        errors = rubricaOrcamento.validate();

        if (errors.isEmpty()) {
            rubricasOrcamento.set(positionRubricaTable, rubricaOrcamento); //adicionar na tableview
            dao = new RubricaSubOrcamentoDAO();
            dao.editar(rubricaOrcamento); //add na BD 
            dao.close();
            showSucessDialog("Rubrica editada com sucesso !!!");
            refreshCBRubricas();
            SubOrcamentoPT1Controller.alteracoes = true;
            new SubOrcamentoPT2Controller().setAlteracaoes(SubOrcamentoPT1Controller.alteracoes);
        } else {
            validator = new Validator();

            for (String error : errors) {
                switch (error) {
                    case "rubrica":
                        lbRubrica.setText(validator.getErrorRubrica());
                        cbRubrica.getStyleClass().add("errorTextField");
                        break;
                    case "descricao":
                        lbDescricao.setText(validator.getErrorDescricao());
                        txtDescricao.getStyleClass().add("errorTextField");
                        break;
                    case "valor":
                        lbValor.setText(validator.getErrorValor());
                        txtValor.getStyleClass().add("errorTextField");
                        break;
                }
            }
        }
    }

    /**
     * Método que permite avançar para a Stage seguinte onde se definem as
     * fracções pagantes
     *
     * @param event evento
     * @throws Exception
     */
    @FXML
    private void btNextFired(ActionEvent event) throws Exception {
        limparMSG();
        removerStyles();
        //envia para a próxima Stage se houve alterações ou não neste form
        new SubOrcamentoPT2Controller().setAlteracaoes(SubOrcamentoPT1Controller.alteracoes);
        new SubOrcamentoPT2Controller().setIdOrc(idOrc);//passo id do orcamento a manipular
        SubOrcamentoPT1.getStage().close();
        new SubOrcamentoPT2().start(new Stage());
    }

    /**
     * Fecha esta Stage e vai para a Stage anterior
     *
     * @param event
     * @throws Exception
     */
    @FXML
    private void btPreviousFired(ActionEvent event) throws Exception {
        limparMSG();
        removerStyles();
        SubOrcamentoPT1.getStage().close();
        new SubOrcamentoPT0().start(new Stage());
    }

    /**
     * Listener tableview rubricasOrcamento
     */
    private final ListChangeListener<RubricaSubOrcamento> selectorTableRubricas
            = new ListChangeListener<RubricaSubOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends RubricaSubOrcamento> c) {
                    verRubricaSeleccionadaDetails();
                }
            };

    /**
     * Mostra os detalhes de um rubrica seleccionada
     */
    private void verRubricaSeleccionadaDetails() {
        cbRubrica.setDisable(true);

        limparMSG();
        removerStyles();
//        txtRubrica.setEditable(false);
//        txtRubrica.setDisable(true);
        final RubricaSubOrcamento rubrica = getTableRubricaSeleccionada();
        positionRubricaTable = rubricasOrcamento.indexOf(rubrica);

        if (rubrica != null) {

            // poe os txtfield com os dados correspondentes
            cbRubrica.setValue(rubrica.getRubrica());
            txtDescricao.setText(rubrica.getDescricao());
            txtValor.setText(Float.toString(rubrica.getValorEuros()));

            btInserir.setDisable(true);
            btRemover.setDisable(false);
            btEditar.setDisable(false);

        }
    }

    /**
     * Devolve a Rubrica seleccionada na table view
     *
     * @return RubricaOrcamento
     */
    private RubricaSubOrcamento getTableRubricaSeleccionada() {
        if (tbRubricaOrcamento != null) {
            List<RubricaSubOrcamento> tabela = tbRubricaOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final RubricaSubOrcamento rubricaSeleccionada = tabela.get(0);
                return rubricaSeleccionada;
            }
        }
        return null;
    }

    /**
     * Método para iniciar a tabela
     */
    private void iniciarTableView() {
        //fazer o binding entre as colunas e o respetivo campo do objeto Rubrica e iniciar a tableview
        clRubrica.setCellValueFactory(new PropertyValueFactory<RubricaSubOrcamento, String>("rubrica"));
        clDescricao.setCellValueFactory(new PropertyValueFactory<RubricaSubOrcamento, String>("descricao"));
        clValor.setCellValueFactory(new PropertyValueFactory<RubricaSubOrcamento, Float>("valorEuros"));
        rubricasOrcamento = FXCollections.observableArrayList();
        tbRubricaOrcamento.setItems(rubricasOrcamento);
    }

    /**
     * Inicia a classe OrçamentoControllerPT1.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SubOrcamentoPT1Controller.alteracoes = false;
//        dao = new RubricaSubOrcamentoDAO();
//        Orcamento orc = dao.getOrcamento(idOrc);
//        lbInfo.setText("Orcamento: " + orc.getAno() + " da data: " + orc.getData() + " versao: " + orc.getVersao());

        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        cbRubrica.setValue(""); //importante validar a rubrica
        dao = new RubricaSubOrcamentoDAO();
        if (!dao.getAllRubricasOrcamento(idOrc).isEmpty()) { // se existem dados na BD faz populate table view
            ArrayList<RubricaSubOrcamento> lista = new ArrayList<RubricaSubOrcamento>();
            lista = dao.getAllRubricasOrcamento(idOrc);
            for (RubricaSubOrcamento rubrica : lista) {
                rubricasOrcamento.add(rubrica); //faz populate na table view
            }
        }

        dao.close();

        if (!validarRemoverEditarOrcamento()) { // se nao pode editar orçamento
            disableComponents();
        } else {
            refreshCBRubricas();

            final ObservableList<RubricaSubOrcamento> tabelaRubricaSel = tbRubricaOrcamento.getSelectionModel().getSelectedItems();
            tabelaRubricaSel.addListener(selectorTableRubricas);
        }

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

        if (adDAO.existSubOrcamento(idOrc)) {
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

        cbRubrica.setDisable(true);
        txtDescricao.setDisable(true);
        btRemover.setDisable(true);
        btInserir.setDisable(true);
        btEditar.setDisable(true);
        btNova.setDisable(true);

        tbRubricaOrcamento.setDisable(true);
        tbRubricaOrcamento.setEditable(false);

        txtValor.setDisable(true);
        txtValor.setEditable(false);

    }

    /**
     * Método que verifica se já existe ou não uma rubrica na table view
     *
     * @param rubrica
     * @return se existe rubrica
     */
    public boolean existRubricaTableView(String rubrica) {
        boolean result = false;

        for (RubricaSubOrcamento ro : rubricasOrcamento) {
            if (ro.getRubrica().equals(rubrica)) {
                result = true;
            }

        }
        return result;
    }

    /**
     * Recebe o id do Sub Orçamento a manipular escolhido no formulario anterior
     *
     * @param id id do Orçamento a manipular
     */
    public void setIdOrc(int id) {
        SubOrcamentoPT1Controller.idOrc = id;
        //System.out.println("aqui" + idOrc);
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
        Window owner = SubOrcamentoPT1.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

}
