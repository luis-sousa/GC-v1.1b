package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática */
/* Projeto Final 2013/2014 /*
 */
import application.About;
import application.AddCondominio;
import application.AddFraccao;
import application.AddOrcamentoPT0;
import application.AddRubrica;
import application.DespesaPT1;
import application.GerarAvisoPT1;
import application.GerarReciboPT1;
import application.Menu;
import application.SubOrcamentoPT0;
import application.VerAvisoPT1;
import application.VerDocumentos;
import application.VerReciboPT1;
import connection.DB;
import dao.AvisoDebitoDAO;
import dao.CondominioDAO;
import dao.FraccaoDAO;
import dao.OrcamentoDAO;
import dao.ReciboDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Condominio;
import model.Fraccao;
import util.MoneyConverter;

/**
 * Classe Menu Controller está responsável pelo controlo dos eventos relativos á
 * uma Stage Principal
 *
 * @author Luís Sousa - 8090228
 */
public class MenuController implements Initializable {

    private File file;
    @FXML
    private Label nome;
    @FXML
    private Label morada;
    @FXML
    private Label codPostal;
    @FXML
    private Label localidade;
    @FXML
    private Label telefone;
    @FXML
    private Label telemovel;
    @FXML
    private Label email;
    @FXML
    private Label contribuinte;
    @FXML
    private AnchorPane content;
    @FXML
    private AnchorPane contentScroll;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Button btAbrirCondominio;

    private GridPane gridpane3;

    /**
     * Método que permite criar uma nova BD
     *
     * @param event evento recebido quando se pretende criar uma nova BD
     */
    @FXML
    private void newBDFired(ActionEvent event) throws IOException, ClassNotFoundException, SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Criar nova BD ...");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DB files (*.db)", "*.db");
        fileChooser.getExtensionFilters().add(extFilter);
        File defaultDirectory = new File("db");
        fileChooser.setInitialDirectory(defaultDirectory);
        //mostrar o dialog fileChooser
        file = fileChooser.showSaveDialog(null);

        DB db = new DB();

        try {
            if (file != null) {
                if (file.exists()) {
                    file.setWritable(true);
                    if (file.delete()) {
                        db.create(file.getName());
                        db.setConnection(file.getName());
                        Menu.setStageTitle("Gestão Condominio - Sem Nome");
                        changeCondominioInfo();
                        changeFraccoesInfo();
                        db.close();
                    } else {
                        showErrorDialog("Não tem permissões para apagar esta BD porque está a ser utilizada.");
                    }
                } else {
                    db.create(file.getName());
                    db.setConnection(file.getName());
                    Menu.setStageTitle("Gestão Condominio - Sem Nome");
                    changeCondominioInfo();
                    changeFraccoesInfo();
                    db.close();
                }
            }
        } catch (Exception ex) {
            showErrorDialog("Ocorreu um erro inesperado...");
        }
    }

    /**
     * Método que permite abrim uma BD ou seja um condomínio
     *
     * @param event evento
     */
    @FXML
    private void condOpenFired(ActionEvent event) throws IOException, Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir condominio");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DB files (*.db)", "*.db");
        fileChooser.getExtensionFilters().add(extFilter);
        File defaultDirectory = new File("db");
        fileChooser.setInitialDirectory(defaultDirectory);
        file = fileChooser.showOpenDialog(null);
        DB db = new DB();
        if (file != null) {
            db.setConnection(file.getName());
            CondominioDAO dao = new CondominioDAO();
            changeCondominioInfo();
            changeFraccoesInfo();
            if (dao.haveData()) {
                Menu.setStageTitle("Gestão Condominio - " + dao.getCondominioObject().getNome());
            } else {
                Menu.setStageTitle("Gestão Condominio - Sem Nome");
            }
        }
        db.close();
    }

    /**
     * Método que permite abrir uma nova Stage para inserir ou editar um
     * Condomínio
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void condInsertFired(ActionEvent event) throws IOException, Exception {
        new AddCondominio().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para fazer as quatro operações
     * básicas sobre uma Fracção
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void fracaoInsertFired(ActionEvent event) throws IOException, Exception {
        new AddFraccao().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para fazer as quatro operações
     * básicas sobre uma Rubrica
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void rubricaInsertFired(ActionEvent event) throws IOException, Exception {
        new AddRubrica().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para fazer as quatro operações
     * básicas sobre um Orçamento
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void orcamentoDefineFired(ActionEvent event) throws IOException, Exception {
        new AddOrcamentoPT0().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para adicionar um sub Orçamento
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void subOrcamentoDefineFired(ActionEvent event) throws IOException, Exception {
        new SubOrcamentoPT0().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para fazer as quatro operações
     * básicas sobre uma Despesa
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void despesasFired(ActionEvent event) throws IOException, Exception {

        new DespesaPT1().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para gerar Avisos
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void gerarAvisosFired(ActionEvent event) throws IOException, Exception {

        new GerarAvisoPT1().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para ver Avisos de Débito
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void verAvisosFired(ActionEvent event) throws IOException, Exception {

        new VerAvisoPT1().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para gerar Avisos de Débito
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void gerarRecibosFired(ActionEvent event) throws IOException, Exception {

        new GerarReciboPT1().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para ver Recibos
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void verRecibosFired(ActionEvent event) throws IOException, Exception {

        new VerReciboPT1().start(new Stage());
    }

    /**
     * Método que permite abrir uma nova Stage para ver Outros Documentos
     *
     * @param event evento
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void verDocumentosFired(ActionEvent event) throws IOException, Exception {

        new VerDocumentos().start(new Stage());
    }

    /**
     * Método que permite abrir a Stage Principal do programa
     *
     * @param url url
     * @param rb resourceBundle
     */
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        changeCondominioInfo();
        changeFraccoesInfo();

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
        Window owner = Menu.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

    /**
     * Método que permite ao clicar no respetivo botão abrir uma nova base de
     * dados sobre um condomínio
     *
     * @param event evento
     * @throws Exception excepção genérica
     */
    @FXML
    private void btCondominioFired(ActionEvent event) throws Exception {
        this.condOpenFired(event);
    }

    /**
     * Método que permite ao clicar no respetivo botão abrir a gestão de
     * orçamentos sobre um condomínio
     *
     * @param event evento
     */
    @FXML
    private void btOrcamentoFired(ActionEvent event) {
        try {
            new AddOrcamentoPT0().start(new Stage());
        } catch (Exception ex) {
        }
    }

    /**
     * Método que permite ao clicar no respetivo botão abrir uma janela que
     * permite gerar avisos
     *
     * @param event evento
     */
    @FXML
    private void btGerarAvisosFired(ActionEvent event) {
        try {
            new GerarAvisoPT1().start(new Stage());
        } catch (Exception ex) {
        }
    }

    /**
     * Método que permite ao clicar no respetivo botão abrir uma janela que
     * permite ver avisos
     *
     * @param event evento
     */
    @FXML
    private void btVerAvisosFired(ActionEvent event) {
        try {
            new VerAvisoPT1().start(new Stage());
        } catch (Exception ex) {
        }
    }

    /**
     * Método que permite ao clicar no respetivo botão abrir uma janela que
     * permite gerar recibos
     *
     * @param event evento
     */
    @FXML
    private void btGerarRecibosFired(ActionEvent event) {
        try {
            new GerarReciboPT1().start(new Stage());
        } catch (Exception ex) {
        }
    }

    /**
     * Método que permite ao clicar no respetivo botão abrir uma janela que
     * permite ver recibos
     *
     * @param event evento
     */
    @FXML
    private void btVerRecibosFired(ActionEvent event) {
        try {
            new VerReciboPT1().start(new Stage());
        } catch (Exception ex) {
        }
    }

    /**
     * Método que permite ao clicar no botão de refresh força a atualização dos
     * dados da janela principal
     *
     * @param event evento
     */
    @FXML
    private void btRefreshFired(ActionEvent event) {
        try {
            changeCondominioInfo();
            changeFraccoesInfo();
        } catch (Exception ex) {
            showErrorDialog(ex.getMessage());
        }
    }

    /**
     * Método que permite abrir a Stage About da aplicação
     *
     * @param event evento
     */
    @FXML
    private void AboutFired(ActionEvent event) {
        try {
            new About().start(new Stage());
        } catch (Exception ex) {
        }
    }

    /**
     * Método que permite mudar a informação do condomínio atual
     */
    public void changeCondominioInfo() {
        CondominioDAO dao = new CondominioDAO();
        if (dao.haveData()) {
            Condominio cond = dao.getCondominioObject();
            nome.setText(cond.getNome());
            morada.setText(cond.getMorada());
            codPostal.setText(cond.getCodPostal());
            localidade.setText(cond.getLocalidade());
            if (cond.getTelefone() == -1) {
                telefone.setText("Sem telefone");
            } else {
                telefone.setText(String.valueOf(cond.getTelefone()));
            }
            if (cond.getTelemovel() == -1) {
                telemovel.setText("Sem telemóvel");
            } else {
                telemovel.setText(String.valueOf(cond.getTelemovel()));
            }
            email.setText(cond.getMail());
            contribuinte.setText(String.valueOf(cond.getContribuinte()));
            dao.close();
        } else {
            nome.setText("");
            morada.setText("");
            codPostal.setText("");
            localidade.setText("");
            telefone.setText("");
            telemovel.setText("");
            email.setText("");
            contribuinte.setText("");
        }

    }

    /**
     * Método que permite mudar a informação das frações existentes para o
     * condomínio atual
     *
     */
    public void changeFraccoesInfo() {
        contentScroll.getChildren().clear();

        FraccaoDAO dao = new FraccaoDAO();

        gridpane3 = new GridPane();

        int row = 0;
        int col = 0;
        int limiteFraccoesLinha = 3;

        GridPane gridpane = null;
        for (Fraccao fr : dao.getAllFraccoes()) {

            if (col == limiteFraccoesLinha) {
                row += 1;
                col = 0;
            }
            gridpane = new GridPane();
            gridpane.setId(fr.getCod());
            gridpane.setPadding(new Insets(20, 0, 0, 50));
            gridpane.setHgap(10);
            gridpane.setVgap(4);

            gridpane.add(new Label("Fração:  " + fr.getCod()), 0, 0);
            gridpane.add(new Label("Nome:  " + fr.getNome()), 0, 1);
            gridpane.add(new Label("Morada:  " + fr.getMorada()), 0, 2);
            gridpane.add(new Label("Código Postal:  " + fr.getCodPostal() + " " + fr.getLocalidade()), 0, 3);
            if (fr.getTelefone() == -1) {
                gridpane.add(new Label("Tlf:  Sem telefone"), 0, 4);
            } else {
                gridpane.add(new Label("Tlf:  " + String.valueOf(fr.getTelefone())), 0, 4);
            }
            if (fr.getTelemovel() == -1) {
                gridpane.add(new Label("Tlm:  Sem telemóvel"), 0, 5);
            } else {
                gridpane.add(new Label("Tlm:  " + String.valueOf(fr.getTelemovel())), 0, 5);
            }
            gridpane.add(new Label("Mail: " + fr.getMail()), 0, 6);
            gridpane.add(new Label("Contribuinte:  " + String.valueOf(fr.getContribuinte())), 0, 7);
            gridpane.add(new Label("Permilagem:  " + String.valueOf(fr.getPermilagem())), 0, 8);
            gridpane.add(new Label("Tipo de Fração:  " + fr.getTipo()), 0, 9);
            Label l = new Label("Total em Divida:  " + getTotalEmDivida(fr.getCod()));
            l.getStyleClass().add("errorLabel");
            gridpane.add(l, 0, 11);

            gridpane3.add(gridpane, col, row);
            col += 1;
        }
        scroll.setContent(gridpane3);

    }

    /**
     * Método que retorna o total em divida para uma fração independente do orçamento
     * 
     *
     * @param codFracao identificação da fração
     * @return total em debito
     */
    private String getTotalEmDivida(String codFracao) {
        //int year = Calendar.getInstance().get(Calendar.YEAR);
        String resultado = "";
        OrcamentoDAO orcDao = new OrcamentoDAO();
        //int idOrc = orcDao.getOrcamentoId("Definitivo"); //verificar se existe orçamento definitivo
        orcDao.close();

        AvisoDebitoDAO dao = new AvisoDebitoDAO();
        ReciboDAO rDAO = new ReciboDAO();

        //if (idOrc != -1) {
            resultado = String.valueOf(MoneyConverter.getEuros(dao.somaValorPagarAteMomentoAvisos(codFracao) - rDAO.somaPagoAteMomento(codFracao)) + "€");
            dao.close();
            rDAO.close();
        //} else {
        //    resultado = "sem informação";
        //}
        return resultado;
    }
}
