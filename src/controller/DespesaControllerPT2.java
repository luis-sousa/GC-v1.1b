package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.AddCondominio;
import application.DespesaPT1;
import application.DespesaPT2;
import dao.DespesaOrcamentoDAO;
import dao.RubricaOrcamentoDAO;
import dao.RubricaSubOrcamentoDAO;
import dao.SubOrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import model.DespesaOrcamento;
import model.RubricaOrcamento;
import model.RubricaSubOrcamento;
import model.SubOrcamento;
import util.MoneyConverter;
import validation.Validator;

/**
 * Classe DespesaControllerP2 está responsável pelo controlo dos eventos
 * relativos á Stage que permite fazer o CRUD de despesas do Orçamento escolhido
 * anteriormente
 *
 * @author Luís Sousa - 8090228
 */
public class DespesaControllerPT2 implements Initializable {

    @FXML
    private TableColumn clDespesa;
    @FXML
    private TableColumn clRubrica;
    @FXML
    private TableColumn clData;
    @FXML
    private TableColumn clMontante;

    @FXML
    private ComboBox cbRubrica;
    @FXML
    private TextField txtDespesa;
    @FXML
    private TextField txtData;
    @FXML
    private TextField txtMontante;

    @FXML
    private Label lbDespesa;
    @FXML
    private Label lbData;
    @FXML
    private Label lbMontante;

    @FXML
    private Button btNova;
    @FXML
    private Button btInserir;
    @FXML
    private Button btEditar;
    @FXML
    private Button btRemover;

    @FXML
    private TableView tbDespesaOrcamento;

    @FXML
    private ObservableList<DespesaOrcamento> despesas;

    @FXML
    private ObservableList<String> rubrica;

    private int idDespesaSel;
    private int positionDespesaTable;
    private DespesaOrcamentoDAO dao;
    private DespesaOrcamento despesa;
    private static int idOrc; //id do despesas a manipular

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Calendar cal = Calendar.getInstance();

    private final Validator validator = new Validator();
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        txtDespesa.setText("");
        //cbRubrica.setValue("");
        cbRubrica.getSelectionModel().selectFirst();
        //txtData.setText("");
        txtData.setText(dateFormat.format(cal.getTime()));
        txtMontante.setText("");
    }

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        //limpar msg erro
        lbDespesa.setText("");
        lbData.setText("");
        lbMontante.setText("");

    }

    /**
     * Método que permite preparar a Stage para a inserção de uma nova Despesa
     * limpando os campos necessários
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {
        limparCampos();
        cbRubrica.getSelectionModel().selectFirst();
        cbRubrica.setDisable(false);
        btEditar.setDisable(true);
        btRemover.setDisable(true);
        btInserir.setDisable(false);
        txtData.setText(dateFormat.format(cal.getTime()));
    }

    /**
     * Método que permite inserir uma Despesa na tableview e na BD
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        int exist = 0;
        despesa = new DespesaOrcamento();
        despesa.setDespesa(txtDespesa.getText());
        despesa.setRubrica(cbRubrica.getValue().toString());
        despesa.setData(txtData.getText());
        try {
            despesa.setMontanteEuros(Float.parseFloat(txtMontante.getText()));
            despesa.setMontanteCentimos(MoneyConverter.getCentimos(despesa.getMontanteEuros()));
        } catch (NumberFormatException ex) {
            despesa.setMontanteEuros(0.0f);
        }
        despesa.setIdOrcamento(idOrc);

        errors = despesa.validate();

        if (existDespesaAdicionar(despesa.getDespesa(), despesa.getRubrica(), despesa.getData())) {
            errors.add("despesa");
            exist = 1;
        }

        if (errors.isEmpty()) {
            despesas.add(despesa); //add na tabela
            dao = new DespesaOrcamentoDAO();
            dao.adicionarDespesa(despesa); //adicionar na BD
            dao.close();
            showSucessDialog("Despesa inserida com Sucesso !!!");
        } else {
            for (String error : errors) {
                switch (error) {
                    case "despesa":
                        lbDespesa.setText(validator.getErrorDespesa());
                        if (exist == 1) {
                            lbDespesa.setText("Despesa já existente nessa data.");
                        }
                        // txtCod.getStyleClass().add("errorTextField");
                        break;
                    case "data":
                        lbData.setText(validator.getErrorData());
                        //txtNome.getStyleClass().add("errorTextField");
                        break;
                    case "montante":
                        lbMontante.setText(validator.getErrorMontante());
                        //txtMorada.getStyleClass().add("errorTextField");
                        break;
                }
            }
        }
    }

    /**
     * Método que permite editar uma Despesa na tableview e na BD
     *
     * @param event evento
     */
    @FXML
    private void btEditFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        int exist = 0;
        despesa = new DespesaOrcamento();
        despesa.setDespesa(txtDespesa.getText());
        despesa.setRubrica(cbRubrica.getValue().toString());
        despesa.setData(txtData.getText());
        try {
            despesa.setMontanteEuros(Float.parseFloat(txtMontante.getText()));
            despesa.setMontanteCentimos(MoneyConverter.getCentimos(despesa.getMontanteEuros()));
        } catch (NumberFormatException ex) {
            despesa.setMontanteEuros(0.0f);
        }
        despesa.setIdOrcamento(idOrc);

        errors = despesa.validate();

        if (existDespesaEditar(despesa.getDespesa(), despesa.getRubrica(), despesa.getData())) {
            errors.add("despesa");
            exist = 1;
        }

        if (errors.isEmpty()) {
            dao = new DespesaOrcamentoDAO();
            dao.editarDespesa(despesa, idOrc, getIdDespesaSel()); //editar na BD
            dao.close();
            despesas.set(positionDespesaTable, despesa); // editar na tabela
            showSucessDialog("Despesa editada com sucesso !!!");
        } else {
            for (String error : errors) {
                switch (error) {
                    case "despesa":
                        lbDespesa.setText(validator.getErrorDespesa());
                        if (exist == 1) {
                            lbDespesa.setText("Despesa já existente nessa data.");
                        }
                        // txtCod.getStyleClass().add("errorTextField");
                        break;
                    case "data":
                        lbData.setText(validator.getErrorData());
                        //txtNome.getStyleClass().add("errorTextField");
                        break;
                    case "montante":
                        lbMontante.setText(validator.getErrorMontante());
                        //txtMorada.getStyleClass().add("errorTextField");
                        break;
                }
            }
        }

    }

    /**
     * Método que permite remover uma Despesa na tableview e na BD
     *
     * @param event evento
     */
    @FXML
    private void btRemoveFired(ActionEvent event) throws SQLException, ClassNotFoundException {

        dao = new DespesaOrcamentoDAO();
        dao.removerDespesa(getIdDespesaSel()); //remover na BD
        dao.close();
        despesas.remove(getTableDespesaSeleccionada()); //apagar na table view
        limparCampos();
        showSucessDialog("Despesa removida com sucesso !!!");
    }

    /**
     * Método que permite ir para a Stage anterior, stage que permitiu escolher
     * o Orçamento a gerir despesas
     *
     * @param event evento
     * @throws Exception entra na excepção
     */
    @FXML
    private void btPreviousFired(ActionEvent event) throws Exception {
        DespesaPT2.getStage().close();
        new DespesaPT1().start(new Stage());
    }

    /**
     * Devolve a despesa seleccionada na tabela
     *
     * @return Despesa
     */
    private DespesaOrcamento getTableDespesaSeleccionada() {
        if (tbDespesaOrcamento != null) {
            List<DespesaOrcamento> tabela = tbDespesaOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final DespesaOrcamento orcamentoSeleccionado = tabela.get(0);
                return orcamentoSeleccionado;
            }
        }
        return null;
    }

    /**
     * Listener na tabela que entra em ação conforme se vão seleccionando as
     * despesas na tabela
     */
    private final ListChangeListener<DespesaOrcamento> selectorTableOrcamento
            = new ListChangeListener<DespesaOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends DespesaOrcamento> c) {
                    verDespesaSeleccionadaDetails();
                    try {
                        getIdDespesaSel();
                    } catch (ClassNotFoundException | SQLException ex) {
                        Logger.getLogger(OrcamentoControllerPT0.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };

    /**
     * Mostra os detalhes da despesa seleccionada
     */
    private void verDespesaSeleccionadaDetails() {
        limparMSG();
        final DespesaOrcamento dp = getTableDespesaSeleccionada();
        positionDespesaTable = despesas.indexOf(dp);

        if (dp != null) {

            // poe os txtfield com os dados correspondentes
            cbRubrica.setValue(dp.getRubrica());
            txtMontante.setText(Float.toString(dp.getMontanteEuros()));
            txtData.setText(dp.getData());
            txtDespesa.setText(dp.getDespesa());

            // poe os botoes num estado especifico 
            btRemover.setDisable(false);
            btEditar.setDisable(false);
            btNova.setDisable(false);
            btInserir.setDisable(true);

        }
    }

    /**
     * Preparar a tabela de Despesas
     */
    private void iniciarTableView() {
        clDespesa.setCellValueFactory(new PropertyValueFactory<DespesaOrcamento, String>("despesa"));
        clRubrica.setCellValueFactory(new PropertyValueFactory<DespesaOrcamento, String>("rubrica"));
        clData.setCellValueFactory(new PropertyValueFactory<DespesaOrcamento, String>("data"));
        clMontante.setCellValueFactory(new PropertyValueFactory<DespesaOrcamento, Float>("montanteEuros"));

        despesas = FXCollections.observableArrayList();
        tbDespesaOrcamento.setItems(despesas);
    }

    /**
     * Inicia a DespesaControllerPT2.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.iniciarTableView();

        final ObservableList<DespesaOrcamento> tabelaOrcamentoSel = tbDespesaOrcamento.getSelectionModel().getSelectedItems();
        tabelaOrcamentoSel.addListener(selectorTableOrcamento);

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);
        btEditar.setDisable(true);

        cbRubrica.setValue(""); //importante validar a rubrica

        //buscar todas as rubricas daquele orcamento para preencher a combo box
        rubrica = FXCollections.observableArrayList();
        RubricaOrcamentoDAO rubricasOrcamentoDAO = new RubricaOrcamentoDAO();
        for (RubricaOrcamento rb1 : rubricasOrcamentoDAO.getAllRubricasOrcamento(idOrc)) {
            rubrica.add(rb1.getRubrica()); //adiciona rubricas do orcamento na observable list
        }
        rubricasOrcamentoDAO.close();

        //buscar todas as rubricas dos sub orçamentos daquele orçamento para preencher a combo box
        SubOrcamentoDAO dao3 = new SubOrcamentoDAO();
        RubricaSubOrcamentoDAO rubDAO = new RubricaSubOrcamentoDAO();
        for (SubOrcamento rb1 : dao3.getAllSubOrcamentos(idOrc)) {
            for (RubricaSubOrcamento rb2 : rubDAO.getAllRubricasOrcamento(rb1.getId())) {
                if (!existRubricaComboBox(rb2.getRubrica())) {
                    rubrica.add(rb2.getRubrica()); //adiciona rubricas dos  sub orcamento na observable list
                }
            }

        }
        rubDAO.close();
        dao3.close();

        cbRubrica.setItems(rubrica); //preencher combo box com as rubricas existentes

        cbRubrica.getSelectionModel().selectFirst();

        dao = new DespesaOrcamentoDAO();
        if (!dao.getAllDespesas(idOrc).isEmpty()) { // se existem dados na BD faz populate table view

            for (DespesaOrcamento dp : dao.getAllDespesas(idOrc)) {
                despesas.add(dp); //faz populate na table view
            }
        }
        dao.close();

    }

    private boolean existRubricaComboBox(String rubricaSD) {
        boolean result = false;

        for (String s : rubrica) {
            if (s.equals(rubricaSD)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Método que retorna o id da despesa seleccionada
     *
     * @return id da despesa
     */
    private int getIdDespesaSel() throws ClassNotFoundException, SQLException {
        dao = new DespesaOrcamentoDAO();
        int id;
        try {
            id = dao.getDespesaId(getTableDespesaSeleccionada());
        } catch (Exception ex) {
            id = -1;
        }
        this.idDespesaSel = id;
        //System.out.println("ID Despesa" + idDespesaSel);
        dao.close();
        return id;
    }

    /**
     * Método que recebeu do form anterior o id do Orçamento a manipular
     *
     * @param id
     */
    public void setIdOrc(int id) {
        DespesaControllerPT2.idOrc = id;
        //System.out.println("aqui" + idOrc);
    }

    /**
     * Método que verifica se já existe aquela despesa na hora de adicionar uma
     * despesa
     *
     * @param despesa despesa
     * @param rubrica rubrica
     * @param data data da despesa
     * @return se existe ou não
     */
    private boolean existDespesaAdicionar(String despesa, String rubrica, String data) {
        boolean result = false;

        for (DespesaOrcamento despesa1 : despesas) {
            if (despesa1.getDespesa().equals(despesa) && despesa1.getRubrica().equals(rubrica) && despesa1.getData().equals(data)) {
                result = true;  //ja existe aquela despesa com aquela rubrica naquela data
                break;
            }
        }
        return result;
    }

    /**
     * Método que verifica se já existe aquela despesa na hora de editar uma
     * despesa
     *
     * @param despesa despesa
     * @param rubrica rubrica
     * @param data data da despesa
     * @return se existe ou não
     */
    private boolean existDespesaEditar(String despesa, String rubrica, String data) {
        boolean result = false;

        for (DespesaOrcamento despesa1 : despesas) {
            if (!despesa1.getDespesa().equals(getTableDespesaSeleccionada().getDespesa())
                    || !despesa1.getRubrica().equals(getTableDespesaSeleccionada().getRubrica())
                    || !despesa1.getData().equals(getTableDespesaSeleccionada().getData())) {//verificar se existe em todas as despedas da tabela na despesa que queremos editar pois poderemos querer manter o nome da despesa a editar
                if (despesa1.getDespesa().equals(despesa) && despesa1.getRubrica().equals(rubrica) && despesa1.getData().equals(data)) {
                    result = true;  //ja existe aquele estado para aquele ano
                    break;
                }
            }
        }
        return result;
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
        Window owner = DespesaPT2.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
}
