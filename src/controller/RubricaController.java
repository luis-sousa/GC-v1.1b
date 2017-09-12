package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.AddRubrica;
import dao.RubricaDAO;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Window;
import model.Rubrica;
import validation.Validator;

/**
 * Classe Rubrica Controller está responsável pelo controlo dos eventos
 * relativos á Stage que representa uma Rubrica
 *
 * @author Luís Sousa - 8090228
 */
public class RubricaController implements Initializable {
    
    @FXML
    private TextField txtRubrica;
    @FXML
    private Button btRemover;
    @FXML
    private Button btNova;
    @FXML
    private Button btInserir;
    @FXML
    private TableView tbRubrica;
    @FXML
    private Label lbRubrica;
    @FXML
    private TableColumn clRubrica;
    
    @FXML
    private ObservableList<Rubrica> rubricas;
    
    private int positionRubricaTable;
    private Validator validator;
    private ArrayList<String> errors;
    private Rubrica rubrica;
    private RubricaDAO dao;

    /**
     * Método para limpar mensagem erro
     */
    private void limparMSG() {
        lbRubrica.setText("");
    }

    /**
     * Método para limpar os campos
     */
    private void limparCampos() {
        txtRubrica.setText("");
    }

    /**
     * Método para remover os styles
     */
    private void removerStyles() {
        txtRubrica.getStyleClass().remove("errorTextField");
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
        btRemover.setDisable(true);
        btInserir.setDisable(false);
    }

    /**
     * Método para inserir uma rubrica
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) {
        limparMSG();
        removerStyles();
        errors = new ArrayList<String>();
        
        rubrica = new Rubrica();
        rubrica.setNome(txtRubrica.getText());
        errors = rubrica.validate();
        
        if (errors.isEmpty() && !existRubricaTableView(txtRubrica.getText())) {
            rubricas.add(rubrica); //adiconar na tableview
            dao = new RubricaDAO();
            if (dao.adicionarRubrica(rubrica)) { //add na BD 
                dao.close();
                showSucessDialog("Rubrica inserida com sucesso!!!");
            } else {
                showErrorDialog("Rubrica não inserida.");
            }
        } else {
            validator = new Validator();
            if (existRubricaTableView(txtRubrica.getText())) {
                errors.add("rubrica");
            }
            for (String error : errors) {
                if (error.equals("rubrica")) {
                    lbRubrica.setText(validator.getErrorRubrica());
                    txtRubrica.getStyleClass().add("errorTextField");
                }
            }
        }
    }

    /**
     * Método para apagar um rubrica
     *
     * @param event evento
     */
    @FXML
    private void btDeleteFired(ActionEvent event) {
        limparMSG();
        removerStyles();
        
        dao = new RubricaDAO();
        if (dao.verifyRemove(getTableRubricaSeleccionada().getNome())) {
            dao.removerRubrica(getTableRubricaSeleccionada());//remover na bd            
            dao.close();
            rubricas.remove(getTableRubricaSeleccionada()); //apagar na table view
            showSucessDialog("Rubrica Apagada com Sucesso!!!");
        } else {
            showErrorDialog("Rubrica não pode ser removida porque está a ser utilizada num Orçamento");
        }
    }
    /**
     * Listener tableview rubricas
     */
    private final ListChangeListener<Rubrica> selectorTableRubricas
            = new ListChangeListener<Rubrica>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Rubrica> c) {
                    verRubricaSeleccionadaDetails();
                }
            };

    /**
     * Mostra a informação de uma Rubrica
     */
    private void verRubricaSeleccionadaDetails() {
        
        limparMSG();
        removerStyles();
        final Rubrica rub = getTableRubricaSeleccionada();
        positionRubricaTable = rubricas.indexOf(rub);
        
        if (rub != null) {

            // poe os txtfield com os dados correspondentes
            txtRubrica.setText(rub.getNome());

            // poe os botoes num estado especifico 
            if (!rub.getNome().equals("Fundo Comum de Reserva")) {
                btRemover.setDisable(false);
                
            } else {
                btRemover.setDisable(true);
            }
            btInserir.setDisable(true);
            
        }
    }

    /**
     * Devolve a rubrica selecionada na tabela
     *
     * @return Rubrica seleccionada
     */
    private Rubrica getTableRubricaSeleccionada() {
        if (tbRubrica != null) {
            List<Rubrica> tabela = tbRubrica.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final Rubrica rubricaSeleccionada = tabela.get(0);
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
        clRubrica.setCellValueFactory(new PropertyValueFactory<Rubrica, String>("nome"));
        rubricas = FXCollections.observableArrayList();
        tbRubrica.setItems(rubricas);
    }

    /**
     * Método que inicializa o class RuricaController
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);

        // Seleccionar rows
        final ObservableList<Rubrica> tabelaRubricaSel = tbRubrica.getSelectionModel().getSelectedItems();
        tabelaRubricaSel.addListener(selectorTableRubricas);
        
        try {
            dao = new RubricaDAO();
            if (!dao.getAllRubricas().isEmpty()) { // se existem dados na BD faz populate table view
                ArrayList<Rubrica> lista = new ArrayList<Rubrica>();
                lista = dao.getAllRubricas();
                for (Rubrica rub : lista) {
                    rubricas.add(rub); //faz populate na table view
                }
            }
            dao.close();//fechar conexao
        } catch (Exception ex) {
            showErrorDialog("Erro ao preencher a tabela.");
        }
    }

    /**
     * Método que permite verificar se já existe ou não uma rubrica
     *
     * @param nomeRubrica nome de um rubrica
     * @return se existe ou não aquela rubrica
     */
    public boolean existRubricaTableView(String nomeRubrica) {
        boolean exist = false;
        for (Rubrica rb : rubricas) {
            if (rb.getNome().equals(nomeRubrica)) {
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
        Window owner = AddRubrica.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
    
}
