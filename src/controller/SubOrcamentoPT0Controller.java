/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import application.SubOrcamentoPT0;
import application.SubOrcamentoPT1;
import dao.AvisoDebitoDAO;
import dao.OrcamentoDAO;
import dao.SubOrcamentoDAO;
import java.net.URL;
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
import model.SubOrcamento;
import validation.Validator;

/**
 * FXML Controller class
 *
 * @author luis__000
 */
public class SubOrcamentoPT0Controller implements Initializable {

    @FXML
    private ComboBox cbOrcamento;
    @FXML
    private TextField txtData;
    @FXML
    private TextField txtNome;

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
    private Label lbNome;
    @FXML
    private Label lbOrcamento;
    @FXML
    private Label lbData;

    @FXML
    private TableView tbOrcamento;
    @FXML
    private TableColumn clNome;
    @FXML
    private TableColumn clOrcamento;
    @FXML
    private TableColumn clData;

    private final int ANO = Calendar.getInstance().get(Calendar.YEAR);
    private Orcamento orcSel;

    private int idSubOrcamentoSel;
    private int positionOrcamentoTable;
    private Validator validator;
    private ArrayList<String> errors = new ArrayList<>();

    private SubOrcamento orc;
    private SubOrcamentoDAO dao;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Calendar cal = Calendar.getInstance();

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        lbNome.setText("");
        lbData.setText("");
        lbOrcamento.setText("");
    }

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        txtNome.setText(ANO + "-A");
        txtData.setText(dateFormat.format(cal.getTime()));
        cbOrcamento.getSelectionModel().selectFirst();
    }

    /**
     * Método que remove o style error
     */
    private void removerStyles() {
        txtNome.getStyleClass().remove("errorTextField");
        txtData.getStyleClass().remove("errorTextField");
        cbOrcamento.getStyleClass().remove("errorTextField");
    }

    @FXML
    private ObservableList<SubOrcamento> subOrcamento;

    @FXML
    private ObservableList<Orcamento> orcamento;

    /**
     * Método que permite preparar a Stage para a inserção de um sub orcamento
     * limpando os campos necessários
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {
        limparCampos();
        limparMSG();
        removerStyles();
        cbOrcamento.setDisable(false);
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        btInserir.setDisable(false);
        txtData.setText(dateFormat.format(cal.getTime()));
    }

    /**
     * Método que permite inserir um Sub Orçamento na table view e na BD
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) {
        int exist = 0;
        limparMSG();
        removerStyles();

        orc = new SubOrcamento();
        orc.setNome(txtNome.getText());
//        System.out.println("a" + cbOrcamento.getSelectionModel().getSelectedItem());
        orcSel = (Orcamento) cbOrcamento.getSelectionModel().getSelectedItem();
        orc.setIdOrcamento(orcSel.getId());
        orc.setData(txtData.getText());
        errors = orc.validate();
        System.out.println("nome:" + orc.getNome() + "id:" + orc.getIdOrcamento());

        if (existNome(txtNome.getText(), orc.getIdOrcamento())) {
            errors.add("nome");
            exist = 1;
        }

        if (errors.isEmpty()) {
            subOrcamento.add(orc); //adicionar na tableview
            dao = new SubOrcamentoDAO();
            dao.adicionarOrcamento(orc); //add na BD 
            dao.close();
            showSucessDialog("Sub Orçamento inserido com sucesso !!!");
            btProximo.setDisable(true);
        } else {
            validator = new Validator();

            for (String error : errors) {
                switch (error) {
                    case "nome":
                        lbNome.setText(validator.getErrorNomeSubOrcamento());
                        txtNome.getStyleClass().add("errorTextField");
                        if (exist == 1) {
                            lbNome.setText("Sub Orçamento já exixtente.");
                        }
                        break;
                    case "data":
                        lbData.setText(validator.getErrorData());
                        txtData.getStyleClass().add("errorTextField");
                        break;
                    case "orcamento":
                        lbOrcamento.setText("Escolha um Orçamento.");
                        cbOrcamento.getStyleClass().add("errorTextField");
                        break;

                }

            }

        }

    }

    /**
     * Método quer permite remover um sub Orçamento
     *
     * @param event
     */
    @FXML
    private void btRemoveFired(ActionEvent event) {
        limparMSG();
        removerStyles();
        btInserir.setDisable(true);
        btEditar.setDisable(true);
        btRemover.setDisable(true);

        if (validarRemoverEditarOrcamento()) { //se pode remover orçamento
            dao = new SubOrcamentoDAO();
            dao.removerOrcamento(getIdSubOrcamentoSel());//remover na bd
            dao.close();
            subOrcamento.remove(getTableSubOrcamentoSeleccionado()); //apagar na table view
            showSucessDialog("Orçamento apagado com sucesso !!!");
            limparCampos();
            btProximo.setDisable(true);
        } else {
            showErrorDialog("Não pode remover este sub Orçamento porque tem avisos de débito associados.");
        }
    }

    /**
     * Método que permite editar um sub Orçamento
     *
     * @param event
     */
    @FXML
    private void btEditFired(ActionEvent event) {
        if (validarRemoverEditarOrcamento()) { //se pode editar este sub orçamento
            int exist = 0;
            limparMSG();
            removerStyles();
            errors = new ArrayList<String>();
            orc = new SubOrcamento();
            orc.setNome(txtNome.getText());
            //System.out.println("orcamentoANO: " + cbOrcamento.getSelectionModel().getSelectedItem());
            OrcamentoDAO dao2 = new OrcamentoDAO();
            int idOrc = dao2.getOrcamentoId(Integer.parseInt(cbOrcamento.getValue().toString()), "Definitivo");
            dao2.close();
            //System.out.println("idORCamento="+idOrc);
            orc.setIdOrcamento(idOrc);
            orc.setData(txtData.getText());
            errors = orc.validate();

            if (existNomeEdit(txtNome.getText(), orc.getIdOrcamento())) {
                errors.add("nome");
                exist = 1;
            }
            //System.out.println("IDSUBORC:"+getIdSubOrcamentoSel());

            if (errors.isEmpty()) {
                dao = new SubOrcamentoDAO();
                dao.editarOrcamento(orc, getIdSubOrcamentoSel()); //add na BD 
                dao.close();
                subOrcamento.set(positionOrcamentoTable, orc); //adicionar na tableview
                showSucessDialog("Sub Orçamento editado com sucesso !!!");
                btProximo.setDisable(true);
            } else {
                validator = new Validator();

                for (String error : errors) {
                    switch (error) {
                        case "nome":
                            lbNome.setText(validator.getErrorNomeSubOrcamento());
                            txtNome.getStyleClass().add("errorTextField");
                            if (exist == 1) {
                                lbNome.setText("Sub Orçamento já existente.");
                            }
                            break;
                        case "data":
                            lbData.setText(validator.getErrorData());
                            txtData.getStyleClass().add("errorTextField");
                            break;
                        case "orcamento":
                            lbOrcamento.setText("Escolha um Orçamento.");
                            cbOrcamento.getStyleClass().add("errorTextField");
                            break;

                    }

                }
            }
        } else {
            showErrorDialog("Não pode editar este Sub Orçamento porque tem dados associados.");
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
        new SubOrcamentoPT1Controller().setIdOrc(getIdSubOrcamentoSel());
        SubOrcamentoPT0.getStage().close();
        new SubOrcamentoPT1().start(new Stage());
    }

    /**
     * Listener tableview que entra em ação quando muda o orcamento selecionado
     * na tabela
     */
    private final ListChangeListener<SubOrcamento> selectorTableOrcamento
            = new ListChangeListener<SubOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends SubOrcamento> c) {
                    verOrcamentoSeleccionadoDetails();

                    getIdSubOrcamentoSel();
                }
            };

    /**
     * Método que retorna o id do Sub Orçamento Seleccionado
     *
     * @return id do Orçamento
     */
    private int getIdSubOrcamentoSel() {
        dao = new SubOrcamentoDAO();
        int id;
        try {
            id = dao.getSubOrcamentoId(getTableSubOrcamentoSeleccionado().getNome(), getTableSubOrcamentoSeleccionado().getIdOrcamento(), getTableSubOrcamentoSeleccionado().getData());
        } catch (Exception ex) {
            id = -1;
        }
        this.idSubOrcamentoSel = id;
        //System.out.println("ID SEL" + idOrcamentoSel);
        dao.close();
        return id;
    }

    /**
     * Método retorna o ano de um orçamento através do ID
     *
     * @param id ID Orcamento
     * @return ano do Orçamento
     */
    private int getAnoOrcamento(int id) {
        int ano = -1;
        OrcamentoDAO orc1 = new OrcamentoDAO();
        ano = orc1.getOrcamento(id).getAno();
        return ano;
    }

    /**
     * Método que mostra os dados do Orçamento seleccionado
     */
    private void verOrcamentoSeleccionadoDetails() {

        limparMSG();
        removerStyles();
        cbOrcamento.setDisable(false);
        final SubOrcamento orcmt = getTableSubOrcamentoSeleccionado();
        positionOrcamentoTable = subOrcamento.indexOf(orcmt);

        if (orcmt != null) {
            // poe os txtfield com os dados correspondentes
            txtNome.setText(orcmt.getNome());
            txtData.setText(orcmt.getData());
            cbOrcamento.setValue(getAnoOrcamento(orcmt.getIdOrcamento()));
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
    private SubOrcamento getTableSubOrcamentoSeleccionado() {
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
     * Método para iniciar a tabela
     */
    private void iniciarTableView() {
        //fazer o binding entre as colunas e o respetivo campo do objeto e inicia a tableview
        clNome.setCellValueFactory(new PropertyValueFactory<SubOrcamento, String>("nome"));
        clOrcamento.setCellValueFactory(new PropertyValueFactory<SubOrcamento, String>("idOrcamento"));
        clData.setCellValueFactory(new PropertyValueFactory<SubOrcamento, String>("data"));

        subOrcamento = FXCollections.observableArrayList();
        tbOrcamento.setItems(subOrcamento);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        orcamento = FXCollections.observableArrayList();
        OrcamentoDAO dao2 = new OrcamentoDAO();
        for (Orcamento orc : dao2.getAllOrcamentosAprovados()) {
            orcamento.add(orc);
        }
        cbOrcamento.setItems(orcamento);
        cbOrcamento.getSelectionModel().selectFirst();

        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        btProximo.setDisable(true);
        txtData.setEditable(false);
        txtData.setDisable(true);
        dao = new SubOrcamentoDAO();
        if (!dao.getAllSubOrcamentos().isEmpty()) { // se existem dados na BD faz populate table view
            for (SubOrcamento orc1 : dao.getAllSubOrcamentos()) {

                subOrcamento.add(orc1); //faz populate na table view
            }
        }
        dao.close();

        // Seleccionar as linhas na tabela sub orçamento
        final ObservableList<SubOrcamento> tabelaOrcamentoSel = tbOrcamento.getSelectionModel().getSelectedItems();
        tabelaOrcamentoSel.addListener(selectorTableOrcamento);

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
        Window owner = SubOrcamentoPT0.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

    /**
     * Método que permite verificar se já existe aquele nome de sub orçamento
     * para um determinado Orçamento
     *
     * @param nome nome do Sub Orçamento
     * @param idOrcamento ID
     * @return se existe
     */
    private boolean existNome(String nome, int idOrcamento) {
//        dao = new SubOrcamentoDAO();
//        boolean result = dao.existSubOrcamentoNome(nome, idOrcamento);
//        dao.close();
//        return result;

        boolean result = false;

        for (SubOrcamento orcmt : subOrcamento) {
            if (orcmt.getIdOrcamento() == idOrcamento && orcmt.getNome().equals(nome)) {
                result = true;  //ja existe aquele nome para aquele orçamento
                break;
            }
        }

        return result;
    }

    /**
     * Método que permite verificar se já existe aquele nome para aquele sub
     * orçamento na hora de editar um sub Orçamento
     *
     * @param nome nome Orçamento
     * @param idOrcamento idOrcamento
     * @return se existe
     */
    private boolean existNomeEdit(String nome, int idOrcamento) {
        boolean result = false;

        for (SubOrcamento orcmt : subOrcamento) {
            if (!orcmt.getNome().equals(getTableSubOrcamentoSeleccionado().getNome())) {//verificar se existe em todos os orcamentos da tabela menos no que queremos editar 
                if (orcmt.getIdOrcamento() == idOrcamento && orcmt.getNome().equals(nome)) {
                    result = true;  //ja existe aquele nome para aquele sub orcamento
                    break;
                }
            }
        }
        return result;
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

        if (adDAO.existSubOrcamento(getIdSubOrcamentoSel())) {
            resultAd = false;
        }
        //System.out.println(resultAd);
        adDAO.close();

        return resultAd;
    }



}
