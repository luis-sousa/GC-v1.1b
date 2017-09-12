package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.AddCondominio;
import application.AddFraccao;
import dao.FraccaoDAO;
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
import javafx.stage.Window;
import model.Fraccao;
import validation.Validator;

/**
 * Classe Fracção Controller está responsável pelo controlo dos eventos
 * relativos á Stage que representa uma Fracção
 *
 * @author Luís Sousa - 8090228
 */
public class FraccaoController implements Initializable {

    @FXML
    private TextField txtCod;
    @FXML
    private Label lbCodigo;
    @FXML
    private TextField txtNome;
    @FXML
    private Label lbNome;
    @FXML
    private TextField txtMorada;
    @FXML
    private Label lbMorada;
    @FXML
    private TextField txtPostal;
    @FXML
    private Label lbCodPostal;
    @FXML
    private TextField txtLocalidade;
    @FXML
    private Label lbLocalidade;
    @FXML
    private TextField txtTelefone;
    @FXML
    private Label lbTelefone;
    @FXML
    private TextField txtTelemovel;
    @FXML
    private Label lbTelemovel;
    @FXML
    private TextField txtMail;
    @FXML
    private Label lbMail;
    @FXML
    private TextField txtContribuinte;
    @FXML
    private Label lbContribuinte;
    @FXML
    private TextField txtPermilagem;
    @FXML
    private Label lbPermilagem;
    @FXML
    private ComboBox cbTipo;

    // os botoes
    @FXML
    private Button btInserir;
    @FXML
    private Button btEditar;
    @FXML
    private Button btRemover;
    @FXML
    private Button btNova;

    //a tabela e as colunas
    @FXML
    private TableView<Fraccao> tbFraccao;
    @FXML
    private TableColumn clCod;
    @FXML
    private TableColumn clNome;
    @FXML
    private ObservableList<Fraccao> fraccoes;

    private int positionFraccaoTable;
    private final Validator validator = new Validator();
    private ArrayList<String> errors = new ArrayList<>();
    private FraccaoDAO dao;

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        //limpar textfield
        txtCod.setEditable(true);
        txtCod.setDisable(false);
        txtCod.setText("");
        txtNome.setText("");
        txtMorada.setText("");
        txtPostal.setText("");
        txtLocalidade.setText("");
        txtTelefone.setText("");
        txtTelemovel.setText("");
        txtMail.setText("");
        txtContribuinte.setText("");
        txtPermilagem.setText("");
        cbTipo.setValue("Habitação"); //valor por defeito

    }

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        //limpar msg erro
        lbCodigo.setText("");
        lbNome.setText("");
        lbMorada.setText("");
        lbCodPostal.setText("");
        lbLocalidade.setText("");
        lbTelefone.setText("");
        lbTelemovel.setText("");
        lbMail.setText("");
        lbContribuinte.setText("");
        lbPermilagem.setText("");
    }

    /**
     * Método que remove o style error
     */
    private void removerStyles() {
        txtCod.getStyleClass().remove("errorTextField");
        txtNome.getStyleClass().remove("errorTextField");
        txtMorada.getStyleClass().remove("errorTextField");
        txtPostal.getStyleClass().remove("errorTextField");
        txtLocalidade.getStyleClass().remove("errorTextField");
        txtTelefone.getStyleClass().remove("errorTextField");
        txtTelemovel.getStyleClass().remove("errorTextField");
        txtMail.getStyleClass().remove("errorTextField");
        txtContribuinte.getStyleClass().remove("errorTextField");
        txtPermilagem.getStyleClass().remove("errorTextField");
    }

    /**
     * Método que permite preparar a Stage para a inserção de uma nova fracção
     * limpando os campos necessários
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {
        limparCampos();
        limparMSG();
        removerStyles();
        btEditar.setDisable(true);
        btRemover.setDisable(true);
        btInserir.setDisable(false);
        txtPermilagem.setDisable(false);
    }

    /**
     * Método que permite inserir uma fracção na tableview e na BD
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) {
        limparMSG();
        removerStyles();

        Fraccao fraccao = createFraccao();
        errors = fraccao.validate(txtTelefone.getText(), txtTelemovel.getText(), txtMail.getText());

        if (errors.isEmpty() && !existFraccaoTableView(txtCod.getText())) {
            fraccoes.add(fraccao); //inserir na table view
            dao = new FraccaoDAO();
            if (dao.adicionarFraccao(fraccao)) { //inserir na bd
                showSucessDialog("Fração inserida com sucesso!!!");
                dao.close();//fechar a ligação
            } else {
                showErrorDialog("Fração não inserida.");
            }
        } else {
            showErrors("insert");
        }
    }

    /**
     * Método responsável por mostrar que campos foram mal introduzidos
     */
    private void showErrors(String operationType) {
        if (operationType.equals("insert")) {
            if (existFraccaoTableView(txtCod.getText())) {
                errors.add("codigo");
            }
        }
        for (String error : errors) {
            switch (error) {
                case "codigo":
                    lbCodigo.setText(validator.getErrorCodFraccao());
                    txtCod.getStyleClass().add("errorTextField");
                    break;
                case "nome":
                    lbNome.setText(validator.getErrorName());
                    txtNome.getStyleClass().add("errorTextField");
                    break;
                case "morada":
                    lbMorada.setText(validator.getErrorMorada());
                    txtMorada.getStyleClass().add("errorTextField");
                    break;
                case "codPostal":
                    lbCodPostal.setText(validator.getErrorCodPostal());
                    txtPostal.getStyleClass().add("errorTextField");
                    break;
                case "localidade":
                    lbLocalidade.setText(validator.getErrorLocalidade());
                    txtLocalidade.getStyleClass().add("errorTextField");
                    break;
                case "telefone":
                    lbTelefone.setText(validator.getErrorTelefone());
                    txtTelefone.getStyleClass().add("errorTextField");
                    break;
                case "telemovel":
                    lbTelemovel.setText(validator.getErrorTelemovel());
                    txtTelemovel.getStyleClass().add("errorTextField");
                    break;
                case "mail":
                    lbMail.setText(validator.getErrorMail());
                    txtMail.getStyleClass().add("errorTextField");
                    break;
                case "contribuinte":
                    lbContribuinte.setText(validator.getErrorContribuinte());
                    txtContribuinte.getStyleClass().add("errorTextField");
                    break;
                case "permilagem":
                    lbPermilagem.setText(validator.getErrorPermilagem());
                    txtPermilagem.getStyleClass().add("errorTextField");
                    break;
            }
        }

    }

    /**
     * Método que retorna uma nova fracção com os dados inseridos no formulário
     *
     * @return fracção com os dados que foram inseridos
     */
    private Fraccao createFraccao() {
        Fraccao fraccao = new Fraccao();
        fraccao.cod.set(txtCod.getText());
        fraccao.nome.set(txtNome.getText());
        fraccao.morada.set(txtMorada.getText());
        fraccao.codPostal.set(txtPostal.getText());
        fraccao.localidade.set(txtLocalidade.getText());

        try {
            fraccao.telefone.set(Integer.parseInt(txtTelefone.getText()));
        } catch (NumberFormatException ex) {
            fraccao.telefone.set(-1);
        }

        try {
            fraccao.telemovel.set(Integer.parseInt(txtTelemovel.getText()));
        } catch (NumberFormatException ex) {
            fraccao.telemovel.set(-1);
        }

        fraccao.mail.set(txtMail.getText());
        try {
            fraccao.contribuinte.set(Integer.parseInt(txtContribuinte.getText()));
        } catch (NumberFormatException ex) {
            fraccao.contribuinte.set(0);
        }
        try {
            fraccao.permilagem.set(Float.parseFloat(txtPermilagem.getText()));
        } catch (NumberFormatException ex) {
            fraccao.permilagem.set(0);
        }
        fraccao.tipo.set((String) cbTipo.getValue());

        return fraccao;
    }

    /**
     * Método que permite editar uma fracção na tableview e na BD
     *
     * @param event evento
     */
    @FXML
    private void btEditFired(ActionEvent event) {
        limparMSG();
        removerStyles();

        Fraccao fraccao = createFraccao();
        errors = fraccao.validate(txtTelefone.getText(), txtTelemovel.getText(), txtMail.getText());

        if (errors.isEmpty()) {
            fraccoes.set(positionFraccaoTable, fraccao); //editar na table view
            dao = new FraccaoDAO();
            if (dao.editarFraccao(fraccao)) { //editar na bd
                dao.close();//fechar a ligação
                showSucessDialog("Fração editada com sucesso!!!");
            } else {
                showErrorDialog("Fração não editada.");
            }
        } else {
            showErrors("edit");
        }
    }

    /**
     * Método que permite apagar uma fracção na tableview e na BD
     *
     * @param event evento
     */
    @FXML
    private void btDeleteFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        dao = new FraccaoDAO();
        if (dao.verifyRemove(getTableFraccaoSeleccionada().getCod())) {//remover na bd
            dao.removerFraccao(getTableFraccaoSeleccionada());
            dao.close();
            fraccoes.remove(getTableFraccaoSeleccionada()); //apagar na table view
            showSucessDialog("Fração removida com sucesso!!!");
        } else {
            showErrorDialog("Fração não pode ser removida porque está a ser utilizada.");
        }
    }

    /**
     * Listener table view fraccoes
     */
    private final ListChangeListener<Fraccao> selectorTableFraccoes
            = new ListChangeListener<Fraccao>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Fraccao> c) {

                    verFraccaoSeleccionadaDetails();
//                    if (!dao.verifyEditPerm(getTableFraccaoSeleccionada().getCod())) { //se não pode editar a permilagem
//                        txtPermilagem.setDisable(true);
//                        //txtPermilagem.setEditable(false);
//                    }
//                    else{
//                        txtPermilagem.setDisable(false);
//                    }
                }
            };

    /**
     * Método que devolve a fraccao seleccionada
     *
     * @return Fracção selecionada
     */
    public Fraccao getTableFraccaoSeleccionada() {
        if (tbFraccao != null) {
            List<Fraccao> tabela = tbFraccao.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final Fraccao fraccaoSeleccionada = tabela.get(0);
                return fraccaoSeleccionada;
            }
        }
        return null;
    }

    /**
     * Método que mostra os dados da fraccao selecionada
     */
    private void verFraccaoSeleccionadaDetails() {
        txtCod.setEditable(false);
        txtCod.setDisable(true);
        limparMSG();
        removerStyles();
        final Fraccao fraccao = getTableFraccaoSeleccionada();
        positionFraccaoTable = fraccoes.indexOf(fraccao);

        if (fraccao != null) {

            // poe os txtfield com os dados correspondentes
            txtNome.setText(fraccao.getNome());
            txtCod.setText(fraccao.getCod());
            txtMorada.setText(fraccao.getMorada());
            txtPostal.setText(fraccao.getCodPostal());
            txtLocalidade.setText(fraccao.getLocalidade());
            if (fraccao.getTelefone() == -1) {
                txtTelefone.setText("");
            } else {
                txtTelefone.setText(String.valueOf(fraccao.getTelefone()));
            }

            if (fraccao.getTelemovel() == -1) {
                txtTelemovel.setText("");
            } else {
                txtTelemovel.setText(String.valueOf(fraccao.getTelemovel()));
            }

         
            txtMail.setText(fraccao.getMail());
            txtContribuinte.setText(String.valueOf(fraccao.getContribuinte()));
            if (!dao.verifyEditPerm(fraccao.getCod())) {
                txtPermilagem.setDisable(true);
            } else {
                txtPermilagem.setDisable(false);
            }
            txtPermilagem.setText(String.valueOf(fraccao.getPermilagem()));
            cbTipo.setValue(fraccao.getTipo());

            //...
            // poe os botoes num estado especifico 
            btEditar.setDisable(false);
            btRemover.setDisable(false);
            btInserir.setDisable(true);

        }
    }

    /**
     * Método para iniciar a tabela
     */
    private void iniciarTableView() {
        clCod.setCellValueFactory(new PropertyValueFactory<Fraccao, String>("cod"));
        clNome.setCellValueFactory(new PropertyValueFactory<Fraccao, String>("nome"));
        fraccoes = FXCollections.observableArrayList();
        tbFraccao.setItems(fraccoes);
    }

    /**
     * Inicia a classe Fraccao controller.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btEditar.setDisable(true);
        btRemover.setDisable(true);

        // Seleccionar as linhas na tabela fraccoes
        final ObservableList<Fraccao> tabelaFraccaoSel = tbFraccao.getSelectionModel().getSelectedItems();
        tabelaFraccaoSel.addListener(selectorTableFraccoes);

        dao = new FraccaoDAO();
        if (!dao.getAllFraccoes().isEmpty()) { // se existem dados na BD faz populate table view
            ArrayList<Fraccao> lista = new ArrayList<Fraccao>();
            lista = dao.getAllFraccoes();
            for (Fraccao fraccao : lista) {
                fraccoes.add(fraccao); //faz populate na table view
            }
        }
        dao.close();

    }

    /**
     * Método que verifica se existe uma fracção
     *
     * @param codFraccao identificador de uma fracção
     * @return se existe ou não
     */
    public boolean existFraccaoTableView(String codFraccao) {
        boolean exist = false;
        for (Fraccao fr : fraccoes) {
            if (fr.getCod().equals(codFraccao)) {
                exist = true;
            }
        }
        return exist;
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
        Window owner = AddFraccao.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
}
