package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
 /* IPP - Instituto Politécnico do Porto */
 /* LEI - Licenciatura em Engenharia Informática*/
 /* Projeto Final 2013/2014 /*
 */
import application.VerAvisoPT2;
import connection.DB;
import dao.AvisoDebitoDAO;
import dao.CondominioDAO;
import dao.FraccaoDAO;
import dao.FraccaoOrcamentoDAO;
import dao.OrcamentoDAO;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Pair;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import model.AvisoDebito;
import model.Condominio;
import model.Fraccao;
import model.FraccaoOrcamento;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.view.JasperViewer;
import util.MailSender;

/**
 * Classe VerAvisoControllerPT2 permite ver Avisos de Débito que já foram
 * gerados
 *
 * @author Luís Sousa - 8090228
 */
public class VerAvisoControllerPT2 implements Initializable {

    private ObservableList<AvisoDebito> avisos;
    private ObservableList<AvisoDebito> avisoSel;

    @FXML
    private ListView lvAviso;

    @FXML
    private TextField txtFraccao;

    @FXML
    private Button btFiltrar;
    @FXML
    private Button btMail;

    @FXML
    private StackPane pane;

    @FXML
    private ListView<String> lvInfo;

    private JScrollPane reportScroll;
    private SwingNode swingNode;

    private final Map parameters = new HashMap();

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Calendar cal = Calendar.getInstance();

    private final String REPORT_PATH = "src/report/";
    private final String REPORT_NAME = "Avisos.jrxml";

    private static boolean mailCancelado;
    public static String pass = "";
    private static String email = "";
    private final String DB_NAME = getDBName();
    private final String FOLDER_HOME = "doc";
    private static int idOrc;
    private final String FOLDER = "avisos";

    /**
     * Método listener que está sempre actualizar que avisos estão seleccionados
     */
    private final ListChangeListener<AvisoDebito> avisosSelecionados
            = new ListChangeListener<AvisoDebito>() {
        @Override
        public void onChanged(ListChangeListener.Change<? extends AvisoDebito> c) {
            btMail.setDisable(false);
            pane.getChildren().removeAll();
            pane.getChildren().clear();
            avisoSel = lvAviso.getSelectionModel().getSelectedItems();
            if (avisoSel.size() == 1) {
                try {
                    Image image = new Image(getClass().getResourceAsStream("/report/loader.gif"));
                    pane.getChildren().add(new Label("", new ImageView(image)));
                } catch (Exception ex) {
                    showErrorDialog(ex.getMessage());
                }
            }
            if (avisoSel != null) {
                swingNode = new SwingNode();
                pane.getChildren().add(swingNode);
                if (avisoSel.size() == 1) {
                    for (AvisoDebito s : avisoSel) {
                        createSwingContent(swingNode, s);
                    }
                }
            }
        }
    };

    /**
     * Método que permite filtrar os dados da list view por fracção
     *
     * @param event evento
     * @throws ClassNotFoundException erro de classe não encontrada
     * @throws SQLException erro de dql
     * @throws Exception execepção genérica
     */
    @FXML
    private void btFiltrarFired(ActionEvent event) throws ClassNotFoundException, SQLException, Exception {
        clearInfo();
        String fraccao = txtFraccao.getText();

        AvisoDebitoDAO dao = new AvisoDebitoDAO();
        avisos.clear();

        if (fraccao.equals("")) {
            for (AvisoDebito ad : dao.getAllAvisosDebito(idOrc)) {
                avisos.add(ad);
            }
            lvAviso.setItems(avisos);
        } else {
            for (AvisoDebito ad : dao.getAllAvisosDebitoFraccao(idOrc, fraccao)) {
                avisos.add(ad);
            }
            lvAviso.setItems(avisos);
        }
        dao.close();
        //showListViewCells();
        if (avisoSel.isEmpty()) {
            btMail.setDisable(true);
        }
    }

    /**
     * Método que permite limpar as list view de informação utilizada para
     * mostrar mensagem quando estão a ser enviados emails
     */
    private void clearInfo() {
        if (!lvInfo.getItems().isEmpty()) {
            lvInfo.getItems().clear();
        }
    }

    /**
     * Método utilizado para permitir o envio de email´s criando um serviço e
     * consequentemente uma task para o envio de email
     *
     * @param event evento
     * @throws Exception excepção genérica
     */
    @FXML
    private void btSendMailFired(ActionEvent event) throws Exception {

        clearInfo();
        if (!avisoSel.isEmpty()) {

            //new Mail().start(new Stage());
            //new Mail().start(new Stage());
            //btSendMailFired(event);
            createDialogLoginMail();

            if (!VerAvisoControllerPT2.mailCancelado) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lvInfo.getItems().add("A enviar ...");
                    }
                });
                ArrayList<AvisoDebito> avisosEscolhidos = new ArrayList<>();
                for (AvisoDebito aviso2 : avisoSel) {
                    avisosEscolhidos.add(aviso2);

                }
                //System.out.println("pass"+password);

                int ano = getAno(idOrc);

                Service<Void> service = new Service<Void>() {
                    boolean result = false;

                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {

                                //Background work    
                                System.out.println("backgound work");

                                FraccaoDAO dao = new FraccaoDAO();
                                MailSender mail = new MailSender();

                                for (AvisoDebito av : avisosEscolhidos) {
                                    mail.setFromEmail(email);
                                    mail.setPassword(pass);
                                    mail.setSubject("Aviso de Débito Condomínio");
                                    if (!verifyPDFExist(FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + av.getCodFraccao() + "/" + av.getIdAviso() + "-" + ano + "-fraccao" + av.getCodFraccao() + "-mes" + av.getMes() + ".pdf")) {
                                        createReportAndPDF(av);
                                    }
                                    //System.out.println(av.getIdAviso());
                                    mail.setBody("Segue em anexo um aviso de débito relativo ao mês " + av.getMes() + " de " + ano);
                                    mail.setToEmail(dao.getMail(av.getCodFraccao()));
                                    mail.setAttachmentPath(FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + av.getCodFraccao() + "/" + av.getIdAviso() + "-" + ano + "-fraccao" + av.getCodFraccao() + "-mes" + av.getMes() + ".pdf");

                                    result = mail.send();

                                    final CountDownLatch latch = new CountDownLatch(1);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                //FX Stuff done here
                                                if (result) {
                                                    lvInfo.getItems().add("Email para a fracão " + av.getCodFraccao() + " referente ao aviso do mês " + av.getMes() + " do ano " + ano + " enviado com sucesso!!!");
                                                } else {
                                                    lvInfo.getItems().add("Email para a fracão " + av.getCodFraccao() + " referente ao aviso do mês " + av.getMes() + " do ano " + ano + " não enviado.");
                                                }
                                            } finally {
                                                latch.countDown();
                                            }
                                        }
                                    });
                                    latch.await();
                                    System.out.println("saiu");
                                }
                                dao.close();
                                return null;
                            }
                        };
                    }
                };
                service.start();
            } else {
                lvInfo.getItems().add("Operação cancelada");
            }
        } else {
            lvInfo.getItems().add("Selecione um ou mais avisos de débitos para os poder enviar por email");
        }
    }

    /**
     * Inicia a classe VerAvisoControllerPT2.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        CondominioDAO cDAO = new CondominioDAO();
        Condominio c = cDAO.getCondominioObject();
        email = c.getMail();
        cDAO.close();

        lvAviso.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        avisos = FXCollections.observableArrayList();

        lvAviso.setItems(avisos);

        //showListViewCells();
        AvisoDebitoDAO dao;
        dao = new AvisoDebitoDAO();
        for (AvisoDebito av : dao.getAllAvisosDebito(idOrc)) {
            avisos.add(av);
        }
        dao.close();

        avisoSel = lvAviso.getSelectionModel().getSelectedItems();
        if (avisoSel.isEmpty()) {
            btMail.setDisable(true);
        }

        //add listener avisos
        lvAviso.getSelectionModel().getSelectedItems().addListener(avisosSelecionados);
        createFolderstoPDF();

    }

    /**
     * Método que recebe o id do orçamento seleccionado anteriormente
     *
     * @param idOrcamentoSel identificador do Orçamento
     */
    void setIdOrc(int idOrcamentoSel) {
        VerAvisoControllerPT2.idOrc = idOrcamentoSel;
    }

    /**
     * Método que permite criar o componente swing com o viewer dos reports
     *
     * @param swingNode swing node responsável por inserir um componente swing
     * (jasper viewer) no javafx
     * @param aD aviso de débito
     */
    private void createSwingContent(final SwingNode swingNode, AvisoDebito aD) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                try {

                    JasperViewer jrv = createReportAndPDF(aD);
                    jrv.setZoomRatio(0.5594f);
                    JasperViewer.setDefaultLookAndFeelDecorated(true);

                    if (reportScroll != null) {
                        reportScroll.removeAll();
                    }

                    reportScroll = new JScrollPane(jrv.getContentPane());
                    reportScroll.revalidate();
                    reportScroll.repaint();
                    reportScroll.setPreferredSize(new Dimension((int) (pane.getWidth()), (int) pane.getHeight()));

                    System.out.println("Relatório gerado.");

                    swingNode.setContent(reportScroll);

                    long end = System.currentTimeMillis();

                    NumberFormat formatter = new DecimalFormat("#0.00000");
                    System.out.print("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");

                } catch (SQLException | ClassNotFoundException ex) {
                    Logger.getLogger(VerAvisoControllerPT2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * Método que permite criar o report e exportar para pdf
     *
     * @param aD aviso de débito
     * @return JasperViewer componente swing
     * @throws ClassNotFoundException erro de classe não encontrada
     * @throws SQLException erro de sql conction
     */
    private JasperViewer createReportAndPDF(AvisoDebito aD) throws ClassNotFoundException, SQLException {
        JasperViewer jrv = null;
        int ano;
        try {
            parameters.clear();
            parameters.put("id", aD.getIdAviso());

            System.out.println("Gerando relatório...");

            DB db = new DB();

            DefaultJasperReportsContext context
                    = DefaultJasperReportsContext.getInstance();

            JRPropertiesUtil.getInstance(context)
                    .setProperty(
                            "net.sf.jasperreports.xpath.executer.factory",
                            "net.sf.jasperreports.engine.util.xml.JaxenXPathExecuterFactory");

            JasperReport pathjrxml = JasperCompileManager
                    .compileReport(this.REPORT_PATH + this.REPORT_NAME);

            //Instancia o virtualizador
            JRAbstractLRUVirtualizer virtualizer = new JRGzipVirtualizer(10);
            //Set o parametro REPORT_VIRTUALIZER com a instância da virtualização
            parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

            JasperPrint print = JasperFillManager.fillReport(pathjrxml, parameters, db.getConnection());

            // exportacao do relatorio para outro formato, no caso PDF
            ano = getAno(idOrc);
            if (!verifyPDFExist(FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + aD.getCodFraccao() + "/" + aD.getIdAviso() + "-" + ano + "-fraccao" + aD.getCodFraccao() + "-mes" + aD.getMes() + ".pdf")) {
                JasperExportManager.exportReportToPdfFile(print, FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + aD.getCodFraccao() + "/" + aD.getIdAviso() + "-" + ano + "-fraccao" + aD.getCodFraccao() + "-mes" + aD.getMes() + ".pdf");
            }

            jrv = new JasperViewer(print, true);
            db.close();
        } catch (JRException ex) {

        }
        return jrv;
    }

    /**
     * Método que retorna o ano do Orçamento
     *
     * @param idOrc idOrcamento
     * @return ano
     */
    private int getAno(int idOrc) {
        int ano = 0;
        OrcamentoDAO orcDAO = new OrcamentoDAO();
        ano = orcDAO.getOrcamento(idOrc).getAno();
        orcDAO.close();
        return ano;
    }

    /**
     * Método que permite verificar se já existe ou não aquele pdf
     *
     * @param pdfPath caminho do pdf
     * @return se já existe pdf
     */
    private boolean verifyPDFExist(String pdfPath) {
        File f = new File(pdfPath);

        return f.exists();
    }

    /**
     * Método que devolve as fracções que estão associadas a um Orçamento, ou
     * seja as fracções pagantes
     *
     * @return FraccaoOrcamento - fracções pagantes
     */
    private ArrayList<Fraccao> getFraccoesOrcamento() {
        ArrayList<Fraccao> fraccoes = null;
        FraccaoDAO dao = new FraccaoDAO();
        fraccoes = dao.getAllFraccoes();
        return fraccoes;
    }

    /**
     * Método que devolve o nome da Base de Dados atual
     *
     * @return nome da BD
     */
    private String getDBName() {
        DB db = new DB();
        return db.getName();
    }

    /**
     * Método que permite criar as pastas para armazenar os pdf´s gerados pelos
     * reports
     */
    private void createFolderstoPDF() {
        try {
            int ano = getAno(idOrc);
            for (Fraccao fr : getFraccoesOrcamento()) {
                File f = new File(FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + fr.getCod());
                //System.out.println("file" + f.toString());
                if (!f.exists()) {
                    if (f.mkdirs()) {
                        //System.out.println("Pastas para os pdf criadas com sucesso");
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * Método finalize
     *
     * @throws Throwable execepcão
     */
    @Override
    protected void finalize() throws Throwable {
        System.out.println("chegou ao finalize!!!!!!!!!!!!!!!!!");
        try {
            System.gc();
            if (pane != null) {
                pane.getChildren().removeAll();
            }
            if (reportScroll != null) {
                reportScroll.removeAll();
            }
            if (swingNode != null) {
                swingNode.getContent().removeAll();
            }
        } catch (Throwable t) {
            throw t;
        } finally {
            super.finalize();
        }
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
        Window owner = VerAvisoPT2.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

    /**
     * Método que apresenta um dialog para pedir a password e o email
     */
    private void createDialogLoginMail() {

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Introduza as credenciais do Condominio");

        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(this.getClass().getResource("/images/login_mail.png").toString()));

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Email");
        username.setText(email);
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        //password.setText(pass);

        grid.add(new Label("Email:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            } else {
                VerAvisoControllerPT2.mailCancelado = true;
            }
            return null;
        });

        Optional<Pair<String, String>> resultado = dialog.showAndWait();

        resultado.ifPresent(usernamePassword -> {
            //System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
            VerAvisoControllerPT2.mailCancelado = false;
            VerAvisoControllerPT2.email = username.getText();
            VerAvisoControllerPT2.pass = password.getText();

        });

    }

}
