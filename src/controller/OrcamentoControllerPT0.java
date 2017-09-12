package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.AddOrcamentoPT0;
import application.AddOrcamentoPT1;
import dao.AvisoDebitoDAO;
import dao.DespesaOrcamentoDAO;
import dao.FraccaoOrcamentoDAO;
import dao.FraccaoQuotasOrcamentoDAO;
import dao.FraccaoRubricaOrcamentoDAO;
import dao.OrcamentoDAO;
import dao.RubricaOrcamentoDAO;
import dao.SubOrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import model.Orcamento;
import validation.Validator;

/**
 * Classe OrçamentoControllerPT0 está responsável pelo controlo dos eventos
 * relativos á Stage que permite gerir orçamentos, permitindo escolher um
 * orçamento e fazer o CRUD do mesmo
 *
 * @author Luís Sousa - 8090228
 */
public class OrcamentoControllerPT0 implements Initializable {

    @FXML
    private ComboBox cbEstado;
    @FXML
    private TextField txtAno;
    @FXML
    private TextField txtData;
    @FXML
    private TextField txtVersao;

    @FXML
    private Button btRemover;
    @FXML
    private Button btNovo;
    @FXML
    private Button btProximo;
    @FXML
    private Button btInserir;
    @FXML
    private Button btEditar;

    @FXML
    private Label lbVersao;
    @FXML
    private Label lbAno;
    @FXML
    private Label lbData;
    @FXML
    private Label lbEstado;

    @FXML
    private TableView tbOrcamento;
    @FXML
    private TableColumn clVersao;
    @FXML
    private TableColumn clAno;
    @FXML
    private TableColumn clData;
    @FXML
    private TableColumn clEstado;

    private final int ANO = Calendar.getInstance().get(Calendar.YEAR);

    @FXML
    private ObservableList<Orcamento> orcamento;

    private int idOrcamentoSel;
    private int positionOrcamentoTable;
    private Validator validator;
    private ArrayList<String> errors = new ArrayList<>();
    private Orcamento orc;
    private OrcamentoDAO dao;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Calendar cal = Calendar.getInstance();
    private final String DEFAULT_ESTADO = "Proposta";

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        lbVersao.setText("");
        lbAno.setText("");
        lbData.setText("");
        lbEstado.setText("");
    }

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        txtAno.setText("");
        txtData.setText("");
        txtVersao.setText("");
        cbEstado.setValue(DEFAULT_ESTADO);
    }

    /**
     * Método que remove o style error
     */
    private void removerStyles() {
        txtAno.getStyleClass().remove("errorTextField");
        txtData.getStyleClass().remove("errorTextField");
        txtVersao.getStyleClass().remove("errorTextField");
        cbEstado.getStyleClass().remove("errorTextField");
    }

    /**
     * Método que permite preparar a Stage para a inserção de um Orcamento
     * limpando os campos necessários
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {
        limparCampos();
        limparMSG();
        removerStyles();
        cbEstado.setDisable(false);
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        btInserir.setDisable(false);
        txtVersao.setText("v");
        cbEstado.setValue("Proposta");
        txtAno.setText(String.valueOf(ANO));
        txtData.setText(dateFormat.format(cal.getTime()));
    }

    /**
     * Método que permite inserir um Orçamento ou Proposta de Orçamento na table
     * view e na BD
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        int exist = 0;
        limparMSG();
        removerStyles();
        int anoInserted;

        orc = new Orcamento();
        orc.setVersao(txtVersao.getText());
        try {
            orc.setAno(Integer.parseInt(txtAno.getText()));
        } catch (NumberFormatException ex) {
            orc.setAno(-1);
        }

        anoInserted = orc.getAno();
        orc.setData(txtData.getText());
        orc.setEstado(cbEstado.getValue().toString());
        errors = orc.validate();

        if (existVersion(txtVersao.getText(), anoInserted)) {
            errors.add("versao");
            exist = 1;
        }

        if (orc.getEstado().equals("Definitivo")) {
            errors.add("estado");

        }

        if (errors.isEmpty()) {
            orcamento.add(orc); //adicionar na tableview
            dao = new OrcamentoDAO();
            dao.adicionarOrcamento(orc); //add na BD 
            dao.close();
            showSucessDialog("Orçamento inserido com sucesso !!!");
            btProximo.setDisable(true);
        } else {
            validator = new Validator();

            for (String error : errors) {
                switch (error) {
                    case "versao":
                        lbVersao.setText(validator.getErrorVersao());
                        txtVersao.getStyleClass().add("errorTextField");
                        if (exist == 1) {
                            lbVersao.setText("Versão já existente para esse ano.");
                        }
                        break;
                    case "ano":
                        lbAno.setText(validator.getErrorAno());
                        txtAno.getStyleClass().add("errorTextField");
                        break;
                    case "data":
                        lbData.setText(validator.getErrorData());
                        txtData.getStyleClass().add("errorTextField");
                        break;
                    case "estado":
                        lbEstado.setText("Não pode definir já este Estado.");
                        cbEstado.getStyleClass().add("errorTextField");
                        break;

                }

            }

        }
    }

    /**
     * Método quer permite remover um Orçamento
     *
     * @param event
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @FXML
    private void btRemoveFired(ActionEvent event) {
        limparMSG();
        removerStyles();
        btInserir.setDisable(true);
        btEditar.setDisable(true);
        btRemover.setDisable(true);

        if (validarRemoverEditarOrcamento()) { //se pode remover orçamento
            dao = new OrcamentoDAO();
            dao.removerOrcamento(getIdOrcamentoSel());//remover na bd
            dao.close();
            orcamento.remove(getTableOrcamentoSeleccionado()); //apagar na table view
            showSucessDialog("Orçamento apagado com sucesso !!!");
            limparCampos();
            btProximo.setDisable(true);
        } else {
            showErrorDialog("Não pode remover este orçamento porque está ou esteve em uso e tem dados associados.");
        }
    }

    /**
     * Método que permite editar um Orçamento
     *
     * @param event
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @FXML
    private void btEditFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        if (validarRemoverEditarOrcamento()) { //se pode editar este orçamento
            int exist = 0;
            int anoInserted = 0;
            limparMSG();
            removerStyles();
            cbEstado.getStyleClass().remove("errorTextField");
            errors = new ArrayList<String>();
            orc = new Orcamento();
            orc.setVersao(txtVersao.getText());
            try {
                orc.setAno(Integer.parseInt(txtAno.getText()));
            } catch (NumberFormatException ex) {
                orc.setAno(-1);
            }
            anoInserted = orc.getAno();
            orc.setData(txtData.getText());
            orc.setEstado(cbEstado.getValue().toString());
            errors = orc.validate();
            if (existVersionEdit(txtVersao.getText(), anoInserted)) {
                errors.add("versao");
                exist = 1;
            }
            if (orc.getEstado().equals("Definitivo")) {

                if (!validarEstadoDefinitivoOrcamento()) {
                    errors.add("estado2");
                }
                if (existStatEdit(orc.getEstado(), anoInserted)) {
                    errors.add("estado1");
                }

            }
            if (errors.isEmpty()) {
                dao = new OrcamentoDAO();
                dao.editarOrcamento(orc, getIdOrcamentoSel()); //add na BD 
                dao.close();
                orcamento.set(positionOrcamentoTable, orc); //adicionar na tableview
                showSucessDialog("Orçamento editado com sucesso !!!");
                btProximo.setDisable(true);
            } else {
                validator = new Validator();
                for (String error : errors) {
                    switch (error) {
                        case "versao":
                            lbVersao.setText(validator.getErrorVersao());
                            txtVersao.getStyleClass().add("errorTextField");
                            if (exist == 1) {
                                lbVersao.setText("Versão já existente para esse ano.");
                            }
                            break;
                        case "ano":
                            lbAno.setText(validator.getErrorAno());
                            txtAno.getStyleClass().add("errorTextField");
                            break;
                        case "data":
                            lbData.setText(validator.getErrorData());
                            txtData.getStyleClass().add("errorTextField");
                            break;
                        case "estado2":
                            lbEstado.setText("Orçamento incompleto para definir esse estado");
                            cbEstado.getStyleClass().add("errorTextField");
                            break;
                        case "estado1":
                            lbEstado.setText("Já tem um orcamento definitivo para esse ano");
                            cbEstado.getStyleClass().add("errorTextField");
                            break;
                    }
                }
            }
        } else {
            showErrorDialog("Não pode editar este orçamento porque está ou esteve em uso e tem dados associados.");
        }
    }

    /**
     * Método que permite avançar para a Stage seguinte onde se começa a formar
     * dados para no final calcular as mensalidades de um Orçamento
     *
     * @param event
     * @throws Exception
     */
    @FXML
    private void btNextFired(ActionEvent event) throws Exception {
        limparMSG();
        removerStyles();
        new OrcamentoControllerPT1().setIdOrc(getIdOrcamentoSel());
        AddOrcamentoPT0.getStage().close();
        new AddOrcamentoPT1().start(new Stage());
    }

    /**
     * Listener tableview que entra em ação quando muda o orcamento selecionado
     * na tabela
     */
    private final ListChangeListener<Orcamento> selectorTableOrcamento
            = new ListChangeListener<Orcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Orcamento> c) {
                    verOrcamentoSeleccionadoDetails();

                    getIdOrcamentoSel();
                }
            };

    /**
     * Método que mostra os dados do Orçamento seleccionado
     */
    private void verOrcamentoSeleccionadoDetails() {

        limparMSG();
        removerStyles();
        cbEstado.setDisable(false);
        final Orcamento orcmt = getTableOrcamentoSeleccionado();
        positionOrcamentoTable = orcamento.indexOf(orcmt);

        if (orcmt != null) {
            // poe os txtfield com os dados correspondentes
            txtVersao.setText(orcmt.getVersao());
            txtData.setText(orcmt.getData());
            txtAno.setText(String.valueOf(orcmt.getAno()));
            cbEstado.setValue(orcmt.getEstado());
            // poe os botoes num estado especifico 
            btRemover.setDisable(false);
            btEditar.setDisable(false);
            btProximo.setDisable(false);
            btInserir.setDisable(true);
        }
    }

    /**
     * Método que devolve o Orçamento seleccionado na table view
     *
     * @return orçamento selecionado
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
     * Método para iniciar a tabela
     */
    private void iniciarTableView() {
        //fazer o binding entre as colunas e o respetivo campo do objeto e inicia a tableview
        clVersao.setCellValueFactory(new PropertyValueFactory<Orcamento, String>("versao"));
        clAno.setCellValueFactory(new PropertyValueFactory<Orcamento, Integer>("ano"));
        clData.setCellValueFactory(new PropertyValueFactory<Orcamento, String>("data"));
        clEstado.setCellValueFactory(new PropertyValueFactory<Orcamento, String>("estado"));
        orcamento = FXCollections.observableArrayList();
        tbOrcamento.setItems(orcamento);
    }

    /**
     * Inicia a classe OrçamentoControllerPT0.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        btProximo.setDisable(true);
        txtVersao.setText("v");
        cbEstado.setValue(DEFAULT_ESTADO);
        txtAno.setText(String.valueOf(ANO));
        txtData.setText(dateFormat.format(cal.getTime()));
        txtData.setEditable(false);
        txtData.setDisable(true);
        dao = new OrcamentoDAO();
        if (!dao.getAllOrcamentos().isEmpty()) { // se existem dados na BD faz populate table view
            for (Orcamento orc1 : dao.getAllOrcamentos()) {
                orcamento.add(orc1); //faz populate na table view
            }
        }
        dao.close();

        // Seleccionar as linhas na tabela fraccoes
        final ObservableList<Orcamento> tabelaOrcamentoSel = tbOrcamento.getSelectionModel().getSelectedItems();
        tabelaOrcamentoSel.addListener(selectorTableOrcamento);

    }

    /**
     * Método que retorna o id do Orçamento Seleccionado
     *
     * @return id do Orçamento
     */
    private int getIdOrcamentoSel() {
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

    /**
     * Método que permite verificar se já existe um orçamento aprovado para
     * aquele ano
     *
     * @param estado estado do orçamento (Definitivo / Proposta)
     * @param ano ano
     * @return se existe
     */
    private boolean existStat(String estado, int ano) {
        boolean result = false;
        for (Orcamento orcmt : orcamento) {
            if (orcmt.getAno() == ano && orcmt.getEstado().equals(estado)) {
                result = true;  //ja existe aquela estado para aquele ano
                break;
            }
        }
        return result;
    }

    /**
     * Método que permite verificar se já existe um orçamento aprovado para
     * aquele ano na hora de editar um Orçamento
     *
     * @param estado estado do orçamento (Definitivo / Proposta)
     * @param ano ano
     * @return se existe
     */
    private boolean existStatEdit(String estado, int ano) {
        boolean result = false;

        for (Orcamento orcmt : orcamento) {
            if (!orcmt.getEstado().equals(getTableOrcamentoSeleccionado().getEstado()) || orcmt.getAno() != getTableOrcamentoSeleccionado().getAno()) {//verificar se existe em todos os orcamentos da tabela menos o que queremos editar pois poderemos querer manter o estado no orcamento a editar
                if (orcmt.getAno() == ano && orcmt.getEstado().equals(estado)) {
                    result = true;  //ja existe aquele estado para aquele ano
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Método que permite verificar se já existe aquela versão para aquele ano
     *
     * @param versao versao do Orçamento
     * @param ano ano
     * @return se existe
     */
    private boolean existVersion(String versao, int ano) {
        boolean result = false;

        for (Orcamento orcmt : orcamento) {
            if (orcmt.getAno() == ano && orcmt.getVersao().equals(versao)) {
                result = true;  //ja existe aquela versao para aquele ano
                break;
            }
        }

        return result;
    }

    /**
     * Método que permite verificar se já existe aquela versão para aquele ano
     * na hora de editar um Orçamento
     *
     * @param versao versão do Orçamento
     * @param ano ano
     * @return se existe
     */
    private boolean existVersionEdit(String versao, int ano) {
        boolean result = false;

        for (Orcamento orcmt : orcamento) {
            if (!orcmt.getVersao().equals(getTableOrcamentoSeleccionado().getVersao())) {//verificar se existe em todos os orcamentos da tabela menos o que queremos editar pois poderemos querer manter o ano e a versao no orcamento a editar
                if (orcmt.getAno() == ano && orcmt.getVersao().equals(versao)) {
                    result = true;  //ja existe aquela versao para aquele ano
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Método que verifica se o orçamento seleccionado pode ter o estado de
     * definitivo. Para tero estado Definitivo deve estar totalmente preenchido
     * com dados nas tabelas RubricaOrcamente, FraccaoOrcamento,
     * FraccaoRubricaOrcamente FraccaoQuotasOrcamento
     *
     *
     * @return se pode mudar o estado para definitivo para um determinado
     * Orçamento
     */
    private boolean validarEstadoDefinitivoOrcamento() {
        boolean resultRO = false;
        boolean resultFO = false;
        boolean resultFRO = false;
        boolean resultFQO = false;

        RubricaOrcamentoDAO daoRO = new RubricaOrcamentoDAO();
        FraccaoOrcamentoDAO daoFO = new FraccaoOrcamentoDAO();
        FraccaoRubricaOrcamentoDAO daoFRO = new FraccaoRubricaOrcamentoDAO();
        FraccaoQuotasOrcamentoDAO daoFQO = new FraccaoQuotasOrcamentoDAO();

        if (daoRO.existOrcamento(getIdOrcamentoSel())) {
            resultRO = true;
        }
        daoRO.close();

        //System.out.println(resultRO);
        if (daoFO.existOrcamento(getIdOrcamentoSel())) {
            resultFO = true;
        }
        daoFO.close();

        //System.out.println(resultFO);
        if (daoFRO.existOrcamento(getIdOrcamentoSel())) {
            resultFRO = true;
        }
        daoFRO.close();

        //System.out.println(resultFRO);
        if (daoFQO.existOrcamento(getIdOrcamentoSel())) {
            resultFQO = true;
        }
        daoFQO.close();

        //System.out.println(resultFQO);
        //System.out.println(resultRO && resultFO && resultFRO && resultFQO);
        return resultRO && resultFO && resultFRO && resultFQO;

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
        boolean resultSubOrc = true;

        DespesaOrcamentoDAO Dspdao = new DespesaOrcamentoDAO();
        AvisoDebitoDAO adDAO = new AvisoDebitoDAO();
        SubOrcamentoDAO subDAO = new SubOrcamentoDAO();

        if (Dspdao.existOrcamento(getIdOrcamentoSel())) {
            resultDsp = false;
        }
        Dspdao.close();

        //System.out.println(resultDsp);
        if (adDAO.existOrcamento(getIdOrcamentoSel())) {
            resultAd = false;
        }
        //System.out.println(resultAd);
        adDAO.close();

        if (subDAO.existOrcamento(getIdOrcamentoSel())) {
            resultSubOrc = false;
        }
        //System.out.println(resultAd);
        subDAO.close();
        return resultDsp && resultAd && resultSubOrc;
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
        Window owner = AddOrcamentoPT0.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
}
