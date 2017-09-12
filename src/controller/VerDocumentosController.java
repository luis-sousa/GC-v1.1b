/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
 /* IPP - Instituto Politécnico do Porto */
 /* LEI - Licenciatura em Engenharia Informática*/
 /* Projeto Final 2013/2014 /*
 */
package controller;

import connection.DB;
import dao.OrcamentoDAO;
import dao.SubOrcamentoDAO;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import model.Orcamento;
import model.SubOrcamento;
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

/**
 * Classe VerDocumentosController permite ver vários documentos relacionados com
 * um Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class VerDocumentosController implements Initializable {

    private ObservableList<String> documentos;
    private String docSel;

    private ObservableList<Orcamento> orcamentos;
    private Orcamento orcSel;
    private String SubOrcDOC;
    private boolean cancelReport;

    @FXML
    private ComboBox cbDocumentos;

    @FXML
    private ComboBox cbOrcamento;

    @FXML
    public static Button btVer;

    @FXML
    private StackPane pane;
    private JScrollPane reportScroll;
    private SwingNode swingNode;

    private final Map parameters = new HashMap();

    private final String REPORT_PATH = "src/report/";
    private final String DB_NAME = getDBName();
    private final String FOLDER_HOME = "doc";
    private final String FOLDER = "outros";

    private final String DOC_1 = "Resumo Recebimentos";
    private final String DOC_2 = "Folha de Presenças";
    private final String DOC_3 = "Mapa de Despesas";
    private final String DOC_4 = "Resumo de Contas";
    private final String DOC_5 = "Resumo do Orçamento";
    private final String DOC_6 = "Relatório Final";
    private final String DOC_7 = "Resumo do SubOrçamento";

    private String texto = "";

    /**
     * Método que permite ver os documentos
     *
     * @param event evento
     * @throws ClassNotFoundException erro de classe não encontrada
     * @throws SQLException erro de sql
     * @throws Exception excepção genérica
     */
    @FXML
    private void btVerFired(ActionEvent event) throws ClassNotFoundException, SQLException, Exception {
        cancelReport = false;
        pane.getChildren().removeAll();
        pane.getChildren().clear();

        docSel = (String) cbDocumentos.getSelectionModel().getSelectedItem();
        orcSel = (Orcamento) cbOrcamento.getSelectionModel().getSelectedItem();

        if (docSel != null && orcSel != null) {
            parameters.clear();
            parameters.put("idOrc", orcSel.getId());
            if (docSel.equals(DOC_1)) {
                parameters.put("ano", orcSel.getAno());
            }
            if (docSel.equals(DOC_2)) {
                createDialogTextoPresenca();
                parameters.put("texto", texto);
            }
            if (docSel.equals(DOC_7)) { //resumo de um sub orçamento
                parameters.put("idOrc", orcSel.getId());
                parameters.put("idSubOrc", createDialogChangeSubOrcamento());
            }
            if (cancelReport == false) {
                Image image = new Image(getClass().getResourceAsStream("/report/loader.gif"));
                pane.getChildren().add(new Label("", new ImageView(image)));

                swingNode = new SwingNode();

                pane.getChildren().add(swingNode);

                createSwingContent(swingNode);
            } else {
                //System.out.println("report cancelado pelo utilizador");
            }
        }

    }

    /**
     * Inicia o controller VerDocumentosController.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        documentos = FXCollections.observableArrayList();
        orcamentos = FXCollections.observableArrayList();

        documentos.add(DOC_1);
        documentos.add(DOC_2);
        documentos.add(DOC_3);
        documentos.add(DOC_4);
        documentos.add(DOC_5);
        documentos.add(DOC_6);
        documentos.add(DOC_7);

        cbDocumentos.setItems(documentos);

        OrcamentoDAO dao = new OrcamentoDAO();
        for (Orcamento orc : dao.getAllOrcamentosAprovados()) {
            orcamentos.add(orc);
        }

        cbOrcamento.setItems(orcamentos);

        cbDocumentos.getSelectionModel().selectFirst();
        cbOrcamento.getSelectionModel().selectFirst();

//        cbDocumentos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                if (newValue.equals(DOC_2)) {
//                    try {
//                        new Presenca().start(new Stage());
//                    } catch (Exception ex) {
//                        Logger.getLogger(VerDocumentosController.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//
//        });
        createFolderstoPDF();
        cancelReport = false;
    }

    /**
     * Método que permite criar o componente swing com o viewer dos reports
     *
     * @param swingNode swing node responsável por inserir um componente swing
     * (jasper viewer) no javafx
     * @param recibo recibo
     */
    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String REPORT_NAME = docSel + ".jrxml";
                int ano = orcSel.getAno();
                try {
                    System.out.println("Gerando relatório...");
                    long start = System.currentTimeMillis();

                    DB db = new DB();

                    DefaultJasperReportsContext context
                            = DefaultJasperReportsContext.getInstance();

                    JRPropertiesUtil.getInstance(context)
                            .setProperty(
                                    "net.sf.jasperreports.xpath.executer.factory",
                                    "net.sf.jasperreports.engine.util.xml.JaxenXPathExecuterFactory");

                    JasperReport pathjrxml = JasperCompileManager
                            .compileReport(REPORT_PATH + REPORT_NAME);

                    //Instancia o virtualizador
                    JRAbstractLRUVirtualizer virtualizer = new JRGzipVirtualizer(10);
                    //Set o parametro REPORT_VIRTUALIZER com a instância da virtualização
                    parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

                    JasperPrint print = JasperFillManager.fillReport(pathjrxml, parameters, db.getConnection());
                    db.close();

                    if (docSel.equals(DOC_7)) { //export dos pdfs sub orçamento
                        JasperExportManager.exportReportToPdfFile(print, FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + orcSel + "-" + docSel + " " + SubOrcDOC + ".pdf");
                    } else {
                        // exportacao do relatorio para outro formato, no caso PDF
                        JasperExportManager.exportReportToPdfFile(print, FOLDER_HOME + "/" + DB_NAME + "/" + ano + "/" + FOLDER + "/" + orcSel + "-" + docSel + ".pdf");
                    }
                    JasperViewer.setDefaultLookAndFeelDecorated(true);
                    JasperViewer jrv = new JasperViewer(print, true);

                    if (reportScroll != null) {
                        reportScroll.removeAll();
                    }

                    jrv.setZoomRatio(0.5594f);
                    reportScroll = new JScrollPane(jrv.getContentPane());
                    reportScroll.revalidate();
                    reportScroll.repaint();
                    reportScroll.setPreferredSize(new Dimension((int) (pane.getWidth()), (int) pane.getHeight()));

                    System.out.println("Relatório gerado.");

                    swingNode.setContent(reportScroll);

                    long end = System.currentTimeMillis();

                    NumberFormat formatter = new DecimalFormat("#0.00000");
                    System.out.print("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");

                } catch (JRException ex) {
                    Logger.getLogger(VerDocumentosController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
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
            OrcamentoDAO dao = new OrcamentoDAO();
            for (Orcamento o : dao.getAllOrcamentosAprovados()) {
                File f = new File(FOLDER_HOME + "/" + DB_NAME + "/" + o.getAno() + "/" + FOLDER);
                System.out.println("file" + f.toString());
                if (!f.exists()) {
                    if (f.mkdirs()) {
                        System.out.println("Pastas para os pdf criadas com sucesso");
                    }
                }
            }
        } catch (Exception ex) {

        }

    }

    /**
     * Método que apresenta um dialog com o texto a introduzir para aparecer
     * esse texto na folha de presenças
     */
    private void createDialogTextoPresenca() {
        texto = "";
        /*TextInputDialog dialog = new TextInputDialog(texto);
        dialog.setTitle("INPUT TEXTO");
        dialog.setHeaderText("Introduza o texto para a Folha de Presenças");
        //dialog.setContentText("Please enter your name:");
        //dialog.getDialogPane().setStyle(" -fx-max-width:600px; -fx-max-height: 300px; -fx-pref-width: 600px; -fx-pref-height: 300px;");
        //dialog.getEditor().setMinSize(360, 150);
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            texto = result.get();
        } else {
            texto = "";
        }
         */

        // Create the custom dialog.
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("INPUT TEXTO");
        dialog.setHeaderText("Introduza o texto para a Folha de Presenças - Max:600 caracteres");

        //add buttons
        //ButtonType btok = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the grip with text area.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextArea text = new TextArea();
        text.setPrefRowCount(8);
        text.setPrefWidth(555);
        text.setPromptText("Texto a Introduzir");

        //grid.add(new Label("Texto:"), 0, 0);
        grid.add(text, 1, 0);

        // Enable/Disable login button depending on whether A TEXT was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(ButtonType.OK);

        // Java 8 lambda FOR VALIDATE TEXT LENGHT
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(text.getText().length() > 600);
        });
        dialog.getDialogPane().setContent(grid);

        // Do some validation (using the Java 8 lambda syntax).
        // Request focus on the username field by default.
        Platform.runLater(() -> text.requestFocus());

        // Convert the result to text when the OK button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                texto = text.getText();
            }
            return null;
        });

        dialog.showAndWait();

    }

    /**
     * Método que apresenta um dialog para escolher o sub Orçamento a apresentar
     * no report. Retorna o id do SubOrcamento
     */
    private int createDialogChangeSubOrcamento() {
        SubOrcamentoDAO subDAO = new SubOrcamentoDAO();

        ArrayList<SubOrcamento> choices = new ArrayList<>();

        for (SubOrcamento choice : subDAO.getAllSubOrcamentos(orcSel.getId())) {
            choices.add(choice);
        }

        ChoiceDialog<SubOrcamento> dialog = new ChoiceDialog<>(null, choices);
        dialog.setTitle("Sub Orçamentos");
        dialog.setHeaderText("Selecione uma Sub Orçamento");
        dialog.setContentText("Sub Orçamento:");

        // Traditional way to get the response value.
        Optional<SubOrcamento> result = dialog.showAndWait();
        if (result.isPresent()) {
            //System.out.println(""+result.get());
            this.SubOrcDOC = result.get().getNome();
            cancelReport = false;
            return result.get().getId();
        } else {
            cancelReport = true;
            return -1;
        }

        // The Java 8 way to get the response value (with lambda expression).
        //result.ifPresent(letter -> System.out.println("Your choice: " + letter));
    }
}
