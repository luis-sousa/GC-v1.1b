package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.VerReciboPT2;
import connection.DB;
import dao.CondominioDAO;
import dao.FraccaoDAO;
import dao.FraccaoOrcamentoDAO;
import dao.OrcamentoDAO;
import dao.ReciboDAO;
import java.awt.Dimension;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import javafx.scene.control.ButtonBar;
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
import model.Condominio;
import model.Fraccao;
import model.FraccaoOrcamento;
import model.Recibo;
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
 * Classe VerReciboControllerPT2 permite ver Recibos que já foram gerados
 *
 * @author Luís Sousa - 8090228
 */
public class VerReciboControllerPT2 implements Initializable {

    private ObservableList<Recibo> recibos;
    private ObservableList<Recibo> reciboSel;

    @FXML
    private ListView lvRecibo;

    @FXML
    private TextField txtFraccao;

    @FXML
    private Button btFiltrar;

    @FXML
    private Button btMail;

    @FXML
    private ListView lvInfo;

    @FXML
    private StackPane pane;
    private JScrollPane reportScroll;
    private SwingNode swingNode;

    private final Map parameters = new HashMap();

    private final String REPORT_PATH = "src/report/";
    private final String REPORT_NAME = "Recibos.jrxml";

    private static boolean mailCancelado;
    public static String pass="";
    private static String email="";
    private final String DB_NAME = getDBName();
    private final String FOLDER_HOME = "doc";
    private static int idOrc;
    private final String FOLDER = "recibos";

    /**
     * Método listener que está sempre actualizar que recibos estão
     * seleccionados
     */
    private final ListChangeListener<Recibo> recibosSelecionados
            = new ListChangeListener<Recibo>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends Recibo> c) {
                    btMail.setDisable(false);
                    pane.getChildren().removeAll();
                    pane.getChildren().clear();
                    reciboSel = lvRecibo.getSelectionModel().getSelectedItems(); //obter recibos seleccionados

                    if (reciboSel.size() == 1) {
                        try {
                            Image image = new Image(getClass().getResourceAsStream("/report/loader.gif"));
                            pane.getChildren().add(new Label("", new ImageView(image)));
                        } catch (Exception ex) {
                            showErrorDialog(ex.getMessage());
                        }
                    }
                    if (reciboSel != null) {
                        swingNode = new SwingNode();
                        pane.getChildren().add(swingNode);
                        if (reciboSel.size() == 1) {
                            for (Recibo r : reciboSel) {
                                createSwingContent(swingNode, r);
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

        ReciboDAO dao = new ReciboDAO();
        recibos.clear();

        if (fraccao.equals("")) {

            for (Recibo ad : dao.getAllRecibos(idOrc)) {
                recibos.add(ad);
            }
            lvRecibo.setItems(recibos);
        } else {

            for (Recibo ad : dao.getAllRecibos(idOrc, fraccao)) {
                recibos.add(ad);
            }
            lvRecibo.setItems(recibos);
        }
        dao.close();

        //showListViewCells();
        if (reciboSel.isEmpty()) {
            btMail.setDisable(true);
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
        if (!reciboSel.isEmpty()) {

            createDialogLoginMail();

            if (!VerReciboControllerPT2.mailCancelado) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lvInfo.getItems().add("A enviar ...");
                    }
                });

                ArrayList<Recibo> recibosEscolhidos = new ArrayList<>();
                for (Recibo recibo : reciboSel) {
                    recibosEscolhidos.add(recibo);
                }
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
                                CondominioDAO cDAO = new CondominioDAO();
                                Condominio c = cDAO.getCondominioObject();
                                String email = c.getMail();
                                cDAO.close();

                                for (Recibo recibo : recibosEscolhidos) {
                                    ReciboDAO rDAO = new ReciboDAO();
                                    String codFraccao = rDAO.getCodFraccao(recibo.getIdRecibo());
                                    rDAO.close();

                                    mail.setFromEmail(email);
                                    mail.setPassword(pass);
                                    mail.setSubject("Recibo Condomínio");
                                    if (!verifyPDFExist(FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + codFraccao + "/" + recibo.getIdRecibo() + "-" + ano + "-fraccao" + codFraccao + ".pdf")) {
                                        createReportAndPDF(recibo);
                                    }
                                    //System.out.println(av.getIdAviso());
                                    mail.setBody("Segue em anexo um recibo de pagamento datado de " + recibo.getData() + " relativo ao Orçamento de " + ano);
                                    mail.setToEmail(dao.getMail(codFraccao));
                                    mail.setAttachmentPath(FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + codFraccao + "/" + recibo.getIdRecibo() + "-" + ano + "-fraccao" + codFraccao + ".pdf");

                                    result = mail.send();

                                    final CountDownLatch latch = new CountDownLatch(1);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                //FX Stuff done here
                                                if (result) {
                                                    lvInfo.getItems().add("Email para a fraccão " + codFraccao + " referente ao recibo nº " + recibo.getIdRecibo() + " do ano " + ano + " enviado com sucesso!!!");
                                                } else {
                                                    lvInfo.getItems().add("Email para a fraccão " + codFraccao + " referente ao recibo nº " + recibo.getIdRecibo() + " do ano " + ano + " não enviado.");
                                                }
                                            } finally {
                                                latch.countDown();
                                            }
                                        }
                                    });
                                    latch.await();
                                    System.out.println("saiu");
                                    //Keep with the background work
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
            lvInfo.getItems().add("Selecione um ou mais recibos para os poder enviar por email");
        }
    }

    /**
     * Método que permite criar o componente swing com o viewer dos reports
     *
     * @param swingNode swing node responsável por inserir um componente swing
     * (jasper viewer) no javafx
     * @param recibo recibo
     */
    private void createSwingContent(final SwingNode swingNode, Recibo recibo) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                try {

                    JasperViewer jrv = createReportAndPDF(recibo);
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
     * @param recibo recibo
     * @return JasperViewer componente swing
     * @throws ClassNotFoundException erro de classe não encontrada
     * @throws SQLException erro de sql conction
     */
    private JasperViewer createReportAndPDF(Recibo recibo) throws ClassNotFoundException, SQLException {
        String fraccao;
        int ano;
        JasperViewer jrv = null;
        try {
            parameters.clear();
            parameters.put("idRecibo", recibo.getIdRecibo());

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
            ReciboDAO rDAO = new ReciboDAO();
            fraccao = rDAO.getCodFraccao(recibo.getIdRecibo());
            rDAO.close();
            if (!verifyPDFExist(FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + fraccao + "/" + recibo.getIdRecibo() + "-" + ano + "-fraccao" + fraccao + ".pdf")) {
                JasperExportManager.exportReportToPdfFile(print, FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + fraccao + "/" + recibo.getIdRecibo() + "-" + ano + "-fraccao" + fraccao + ".pdf");
            }

            jrv = new JasperViewer(print, true);
            db.close();
        } catch (JRException ex) {
            
        }
        return jrv;
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
     * Método que devolve as fracções que estão associadas a um Orçamento, ou
     * seja as fracções pagantes
     *
     * @return FraccaoOrcamento - fracções pagantes
     */
    private ArrayList<Fraccao> getFraccoesOrcamento() {
        //ArrayList<FraccaoOrcamento> fraccoes = null;
        //FraccaoOrcamentoDAO dao = new FraccaoOrcamentoDAO();
        //fraccoes = dao.getAllFraccoesOrcamento(idOrc);
        //return fraccoes;
        
         ArrayList<Fraccao> fraccoes = null;
        FraccaoDAO dao = new FraccaoDAO();
        fraccoes = dao.getAllFraccoes();
        return fraccoes;
    }

    /**
     * Método que permite criar as pastas para armazenar os pdf´s gerados pelos
     * reports
     */
    private void createFolderstoPDF() {
       /* try {
            int ano = getAno(idOrc);
            for (FraccaoOrcamento fr : getFraccoesOrcamento()) {
                File f = new File(FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + fr.getFraccao());
                //System.out.println("file" + f.toString());
                if (!f.exists()) {
                    if (f.mkdirs()) {
                        //System.out.println("Pastas para os pdf criadas com sucesso");
                    }
                }
            }
        } catch (Exception ex) {

        }*/
       
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
     * Método que permite limpar as list view de informação utilizada para
     * mostrar mensagem quando estão a ser enviados emails
     */
    private void clearInfo() {
        if (!lvInfo.getItems().isEmpty()) {
            lvInfo.getItems().clear();
        }
    }

    /**
     * Inicia a classe VerReciboControllerPT2.
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

        lvRecibo.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        recibos = FXCollections.observableArrayList();

        lvRecibo.setItems(recibos);

        //showListViewCells();
        ReciboDAO dao;
        dao = new ReciboDAO();
        for (Recibo av : dao.getAllRecibos(idOrc)) {
            recibos.add(av);
        }
        dao.close();

        reciboSel = lvRecibo.getSelectionModel().getSelectedItems();

        if (reciboSel.isEmpty()) {
            btMail.setDisable(true);
        }
        //add listener
        lvRecibo.getSelectionModel().getSelectedItems().addListener(recibosSelecionados);
        createFolderstoPDF();
    }

    /**
     * Método que recebe o id do orçamento seleccionado anteriormente
     *
     * @param idOrcamentoSel identificador do Orçamento
     */
    void setIdOrc(int idOrcamentoSel) {
        VerReciboControllerPT2.idOrc = idOrcamentoSel;
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
        Window owner = VerReciboPT2.getStage();
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
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
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
                VerReciboControllerPT2.mailCancelado = true;
            }
            return null;
        });

        Optional<Pair<String, String>> resultado = dialog.showAndWait();

        resultado.ifPresent(usernamePassword -> {
            //System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
            VerReciboControllerPT2.mailCancelado = false;
            VerReciboControllerPT2.email = username.getText();
            VerReciboControllerPT2.pass = password.getText();

        });
    }

}
