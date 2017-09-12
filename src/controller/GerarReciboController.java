package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
 /* IPP - Instituto Politécnico do Porto */
 /* LEI - Licenciatura em Engenharia Informática*/
 /* Projeto Final 2013/2014 /*
 */
import application.AddCondominio;
import application.GerarRecibo;
import application.GerarReciboPT2;
import dao.AvisoDebitoDAO;
import dao.ReciboDAO;
import dao.ReciboLinhaDAO;
import dao.RubricaSubOrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.AvisoDebito;
import model.Recibo;
import model.ReciboLinha;
import model.RubricaSubOrcamento;
import util.MoneyConverter;

/**
 * Classe GerarReciboController está responsável por gerar Recibos
 *
 * @author Luís Sousa - 8090228
 */
public class GerarReciboController implements Initializable {

    private static final ObservableList<AvisoDebito> avisos = FXCollections.observableArrayList();
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Calendar cal = Calendar.getInstance();
    private static TextField tx;
    private GridPane gridpane;

    @FXML
    private Button btGerar;

    @FXML
    private AnchorPane anchorPane;

    /**
     * Método que permite gerar o respetivo recibo
     *
     * @param event evento
     * @throws ClassNotFoundException erro classe não encontrada
     * @throws SQLException erro sql
     * @throws Exception execpção generica
     */
    @FXML
    private void btGerarFired(ActionEvent event) throws ClassNotFoundException, SQLException, Exception {
        clearErrorLabel();
        try {
            if (validateTXT()) {

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Valores");
                alert.setHeaderText("Confirmação dos Valores");
                alert.setContentText("Deseja gerar o recibo ?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    ReciboDAO rDao = new ReciboDAO();
                    Recibo reciboTMP = new Recibo();
                    AvisoDebitoDAO avDao = new AvisoDebitoDAO();
                    ReciboLinha reciboLinha = new ReciboLinha();
                    int idOrc = 0;

                    reciboTMP.setTotalRecibo(0);
                    reciboTMP.setData(dateFormat.format(cal.getTime()));
                    rDao.adicionarRecibo(reciboTMP); //recibo temporario criado á "espera" do(s) recibosLinha pois um recibo pode fazer parte de varios avisos de debito

                    int idReciboTemp = rDao.getIDRecibo(reciboTMP);
                    float somaRecibo = 0;
                    float valorIntroduzido;

                    for (AvisoDebito ad : avisos) {
                        idOrc = ad.getIdOrcamento();
                        ReciboLinhaDAO rlDAO = new ReciboLinhaDAO();
                        reciboLinha.setIdAviso(ad.getIdAviso());
                        reciboLinha.setIdRecibo(idReciboTemp);
                        valorIntroduzido = getValueTextField("txt" + ad.getIdAviso());
                        somaRecibo += valorIntroduzido;
                        reciboLinha.setValorPago(MoneyConverter.getCentimos(valorIntroduzido));
                        if (ad.getIdSubOrcamento() == 0) { // é so orçamento 
                            reciboLinha.setDescricao("Pagamento da mensalidade.");
                        } else { //é um subOrçamento
                            reciboLinha.setDescricao("Pagamento extra relativo a: " + getAllRubricas(ad.getIdSubOrcamento()) + ".");
                        }

                        //System.out.println(reciboLinha.getValorPago());
                        rlDAO.adicionarLinha(reciboLinha);

                        //mudar o estado do aviso se necessario
                        int valor = rlDAO.calcValorPorPagar(ad.getIdAviso());
                        //System.out.println("valor" + valor);
                        rlDAO.close();
                        if (valor == 0) {//estado = resolvido
                            avDao.setEstadoAviso(ad.getIdAviso(), 1);
                        }

                    }
                    //set total recibo
                    rDao.editTotalRecibo(MoneyConverter.getCentimos(somaRecibo), idReciboTemp);
                    rDao.close();
                    showSucessDialog("Recibo inserido com sucesso !!!");
                    GerarRecibo.getStage().close();  //mudei estas 3 linhas ggggggggggggggggggggggggggfddddddddddddddddddjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
                    GerarReciboPT2.getStage().close();
                    new GerarReciboControllerPT2().setIdOrc(idOrc);
                    new GerarReciboPT2().start(new Stage());
                } else {
                    // ... user chose CANCEL or closed the dialog
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            showErrorDialog("Ocorreu o seguinte erro ao gerar o Recibo: " + ex.getMessage());
        }
    }

    private String getAllRubricas(int idSub) {
        String frase = "";
        RubricaSubOrcamentoDAO dao3 = new RubricaSubOrcamentoDAO();
        int ct = 1;
        for (RubricaSubOrcamento rubrica : dao3.getAllRubricasOrcamento(idSub)) {
            if (ct == 1) {
                frase = frase + rubrica.getDescricao();
            } else {
                frase = frase + " ; " + rubrica.getDescricao();
            }
            ct += 1;
        }
        dao3.close();
        return frase;
    }

    /**
     * Inicia a classe GerarReciboController
     *
     * @param url url
     * @param rb resourcebundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if (avisos.size() > 0) {
                createLayout();
            } else {
                btGerar.setDisable(true);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(GerarReciboController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Recebo os avisos seleccionados na stage anterior , para trabalhar sobre
     * esses avisos nesta stage
     *
     * @param avisoSel avisos selecionados
     */
    public void setAvisosGerar(ObservableList<AvisoDebito> avisoSel) {
        avisos.clear();
        for (AvisoDebito av : avisoSel) {
            //System.out.println(av.getIdAviso());
            avisos.add(av);
        }
    }

    /**
     * Método que permite criar o layout desta stage pois tem de ser um layout
     * dinãmico conforme os avisos que provêm da Stage anterior
     *
     * @throws ClassNotFoundException erro de classe não encontrada
     * @throws SQLException erro sql
     */
    private void createLayout() throws ClassNotFoundException, SQLException {
        String labelText;
        int row = 0;
        ReciboLinhaDAO dao = new ReciboLinhaDAO();

        gridpane = new GridPane();
        gridpane.setPadding(new Insets(20, 0, 0, 50));
        gridpane.setHgap(10);
        gridpane.setVgap(4);

        for (AvisoDebito av : avisos) {
            if (av.getIdSubOrcamento() != 0) { //se for um aviso de um sub Orçamento
                labelText = "Fração: " + av.getCodFraccao() + " Mês: " + getMonth(av.getMes()) + " Extra";
            } else {
                labelText = "Fração: " + av.getCodFraccao() + " Mês: " + getMonth(av.getMes());
            }

            gridpane.add(new Label(labelText), 1, row);  // column=1 row=0
            tx = new TextField();
            tx.setId("txt" + av.getIdAviso()); //set id para depois posteriormente ir buscar o que foi introduzido
            tx.setText(String.valueOf(MoneyConverter.getEuros(dao.calcValorPorPagar(av.getIdAviso()))));
            gridpane.add(tx, 2, row); // column=2 row=0
            Label lb = new Label();
            lb.setId("lb" + av.getIdAviso()); // set label id label error
            lb.getStyleClass().add("errorLabel");
            gridpane.add(lb, 3, row);
            //adiciona o botão na última linha
            if (avisos.size() == row + 1) {
                gridpane.add(btGerar, 1, row + 1); //column=1 row=last
            }
            row += 1;
        }
        dao.close();
        anchorPane.getChildren().add(gridpane);
        anchorPane.requestLayout();
    }

    /**
     * Método que devolve o valor introduzido num textfield através do id de um
     * node to tipo textfield
     *
     * @param id id do textfield
     * @return valor introduzido
     */
    private float getValueTextField(String id) {
        float valorIntroduzido = -1;
        for (Node node : gridpane.getChildren()) {
            //System.out.println("Id: " + node.getId());
            if (node instanceof TextField) {
                TextField tx2 = (TextField) node;
                if (node.getId().equals(id)) {
                    try {
                        valorIntroduzido = Float.parseFloat(tx2.getText());
                    } catch (NumberFormatException ex) {
                        valorIntroduzido = -1;
                    }
                    //System.out.println("valor int" + valorIntroduzido);
                }
            }
        }
        return valorIntroduzido;
    }

    /**
     * Método que apresenta os respectivos erros nas respectivas label´s
     *
     * @param labelID identificador da label
     * @param valor valor maximo que deve ser introduzido
     */
    private void setMsgErrorLabel(String labelID, float valor) {
        for (Node node : gridpane.getChildren()) {
            if (node instanceof Label) {
                Label lb2 = (Label) node;
                if (lb2.getId() != null) {
                    if (lb2.getId().equals(labelID)) {
                        lb2.setText("Valor Invalido >0 e <=" + valor);
                    }
                }
            }
        }
    }

    /**
     * Limpar mensagens de erro
     */
    private void clearErrorLabel() {
        for (Node node : gridpane.getChildren()) {
            if (node instanceof Label) {
                Label lb2 = (Label) node;
                if (lb2.getId() != null) {
                    lb2.setText("");
                }
            }
        }
    }

    /**
     * Método que valida os txtfields
     *
     * @return se está tudo bem introduzido
     * @throws ClassNotFoundException erro classe
     * @throws SQLException erro sql
     */
    private boolean validateTXT() throws ClassNotFoundException, SQLException {
        float valorIntroduzido;
        float valorPorPagar;
        boolean result = true;
        ReciboLinhaDAO dao = new ReciboLinhaDAO();
        for (AvisoDebito ad : avisos) {
            valorIntroduzido = getValueTextField("txt" + ad.getIdAviso());
            valorPorPagar = MoneyConverter.getEuros(dao.calcValorPorPagar(ad.getIdAviso()));
            if (valorIntroduzido == -1 || valorIntroduzido > valorPorPagar || valorIntroduzido <= 0) {
                setMsgErrorLabel("lb" + ad.getIdAviso(), valorPorPagar);
                result = false;
            }
        }
        dao.close();
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
        Window owner = GerarRecibo.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

    /**
     * Método que através de um numero devolve o respetivo mês
     *
     * @param number numero de um mês
     * @return mês
     */
    private String getMonth(int number) {
        String month;

        switch (number) {
            case 1:
                month = "Janeiro";
                break;
            case 2:
                month = "Fevereiro";
                break;
            case 3:
                month = "Março";
                break;
            case 4:
                month = "Abril";
                break;
            case 5:
                month = "Maio";
                break;
            case 6:
                month = "Junho";
                break;
            case 7:
                month = "Julho";
                break;
            case 8:
                month = "Agosto";
                break;
            case 9:
                month = "Setembro";
                break;
            case 10:
                month = "Outubro";
                break;
            case 11:
                month = "Novembro";
                break;
            case 12:
                month = "Dezembro";
                break;
            default:
                month = null;
                break;

        }
        return month;
    }
}
