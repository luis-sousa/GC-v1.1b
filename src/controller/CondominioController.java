package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
 /* IPP - Instituto Politécnico do Porto */
 /* LEI - Licenciatura em Engenharia Informática*/
 /* Projeto Final 2013/2014 /*
 */
import application.AddCondominio;
import application.Menu;
import dao.CondominioDAO;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Condominio;
//import org.controlsfx.dialog.Dialogs;
import javafx.stage.Modality;
import javafx.stage.Window;
import validation.Validator;

/**
 * Classe Condominio Controller está responsável pelo controlo dos eventos
 * relativos a uma Stage
 *
 * @author Luís Sousa - 8090228
 */
public class CondominioController implements Initializable {

    @FXML
    private TextField txtID;
    @FXML
    private Label lbID;
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

    private final Condominio condominio = new Condominio();
    private final Validator validator = new Validator();
    private ArrayList<String> errors = new ArrayList<>();
    private CondominioDAO dao;

    /**
     * Método que permite remover as mensagens de erro
     */
    private void cleanErrorMsg() {
        lbNome.setText("");
        lbMorada.setText("");
        lbCodPostal.setText("");
        lbLocalidade.setText("");
        lbTelefone.setText("");
        lbTelemovel.setText("");
        lbMail.setText("");
        lbContribuinte.setText("");
    }

    /**
     * Método que permite remover o css de erro dos textfields
     */
    private void cleanErrorStyle() {
        txtNome.getStyleClass().remove("errorTextField");
        txtMorada.getStyleClass().remove("errorTextField");
        txtPostal.getStyleClass().remove("errorTextField");
        txtLocalidade.getStyleClass().remove("errorTextField");
        txtTelefone.getStyleClass().remove("errorTextField");
        txtTelemovel.getStyleClass().remove("errorTextField");
        txtMail.getStyleClass().remove("errorTextField");
        txtContribuinte.getStyleClass().remove("errorTextField");
    }

    /**
     * Método que permite gravar um condomínio na BD
     *
     * @param event evento recebido quando pressionado o botão de gravar um
     * condominio
     */
    @FXML
    private void saveButtonFired(ActionEvent event) {
        cleanErrorMsg();
        cleanErrorStyle();

        createCondominio();

        errors = condominio.validate(txtTelefone.getText(), txtTelemovel.getText());

        if (errors.isEmpty()) { //lista sem erros
            if (dao.haveData()) {
                if (dao.editarCondominio(condominio)) {
                    AddCondominio.getStage().close();
                    showSucessDialog("Condomínio editado com sucesso !!!");
                    Menu.setStageTitle("Gestão Condominio - " + dao.getCondominioObject().getNome());
                } else {
                    AddCondominio.getStage().close();
                    showErrorDialog("Ocorreu um erro ao tentar editar o Condomínio.");
                }
            } else if (dao.adicionarCondominio(condominio)) {
                AddCondominio.getStage().close();
                showSucessDialog("Condomínio inserido com sucesso !!!");
                Menu.setStageTitle("Gestão Condominio - " + dao.getCondominioObject().getNome());
            } else {
                AddCondominio.getStage().close();
                showErrorDialog("Ocorreu um erro ao tentar inserir o Condomínio.");
            }
            dao.close();
        } else { // senao apresenta os erros
            showErrors();
        }
    }

    /**
     * Método que permite criar o condominio com os dados que foram inseridos
     */
    private Condominio createCondominio() {
        dao = new CondominioDAO();
        if (dao.haveData()) {
            condominio.setId(Integer.parseInt(txtID.getText()));
        }
        condominio.setNome(txtNome.getText());
        condominio.setMorada(txtMorada.getText());
        condominio.setCodPostal(txtPostal.getText());
        condominio.setLocalidade(txtLocalidade.getText());

        try {
            condominio.setTelefone(Integer.parseInt(txtTelefone.getText()));
        } catch (NumberFormatException ex) {
            condominio.setTelefone(-1); // set para um valor invalido (-1 é invalido)
        }

        try {
            condominio.setTelemovel(Integer.parseInt(txtTelemovel.getText()));
        } catch (NumberFormatException ex) {
            condominio.setTelemovel(-1);// set para um valor invalido (-1 é invalido)
        }

        condominio.setMail(txtMail.getText());
        try {
            condominio.setContribuinte(Integer.parseInt(txtContribuinte.getText()));
        } catch (NumberFormatException ex) {
            condominio.setContribuinte(0); // set para um valor invalido (zero é invalido)
        }
        return condominio;
    }

    /**
     * Método responsável por mostrar que campos foram mal introduzidos
     */
    private void showErrors() {
        for (String error : errors) {
            switch (error) {
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
            }
        }
    }

    /**
     * Inicia a classe Condominio controller.
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            dao = new CondominioDAO();
            if (dao.getCondominio() != null) {
                try ( //existe condominio
                        ResultSet rs = dao.getCondominio()) {
                    txtID.setText(rs.getString("id"));
                    txtID.setEditable(false);
                    txtID.setDisable(true);
                    txtNome.setText(rs.getString("nome"));
                    txtMorada.setText(rs.getString("morada"));
                    txtPostal.setText(rs.getString("codPostal"));
                    txtLocalidade.setText(rs.getString("localidade"));
                    if (rs.getString("telefone").equals("-1")) {
                        txtTelefone.setText("");
                    } else {
                        txtTelefone.setText(rs.getString("telefone"));
                    }
                    if (rs.getString("telemovel").equals("-1")) {
                        txtTelemovel.setText("");
                    } else {
                        txtTelemovel.setText(rs.getString("telemovel"));
                    }
                    txtMail.setText(rs.getString("email"));
                    txtContribuinte.setText(rs.getString("contribuinte"));
                }
            } else {
                txtID.setVisible(false);
                lbID.setVisible(false);
            }
            dao.close();
        } catch (SQLException ex) {
            Logger.getLogger(CondominioController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Método que permite mostrar um Dialog de sucesso
     *
     * @param msg mensagem aparecer no Dialog
     */
    private void showSucessDialog(String msg) {
        Alert dlg = createAlert(AlertType.INFORMATION);
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
        Alert dlg = createAlert(AlertType.ERROR);
        dlg.setTitle("INSUCESSO");
        String optionalMasthead = "Erro Encontrado";
        dlg.getDialogPane().setContentText(msg);
        dlg.getDialogPane().setHeaderText(optionalMasthead);
        dlg.show();
    }

    private Alert createAlert(AlertType type) {
        Window owner = AddCondominio.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
}
