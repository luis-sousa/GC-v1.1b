package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
 /* IPP - Instituto Politécnico do Porto */
 /* LEI - Licenciatura em Engenharia Informática*/
 /* Projeto Final 2013/2014 /*
 */
import application.GerarRecibo;
import application.GerarReciboPT2;
import connection.DB;
import dao.AvisoDebitoDAO;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import model.AvisoDebito;
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

/**
 * Classe GerarReciboControllerPT2 está responsável pelo controlo dos eventos
 * relativos á Stage que permite gerar Recibos
 *
 * @author Luís Sousa - 8090228
 */
public class GerarReciboControllerPT2 implements Initializable {

    private ObservableList<AvisoDebito> avisos;
    private ObservableList<AvisoDebito> avisoSel;

    @FXML
    private ListView lvAviso;

    @FXML
    private TextField txtFraccao;

    @FXML
    private Button btFiltrar;

    @FXML
    private Button btReciboGerar;
    @FXML
    private StackPane pane;
    private JScrollPane reportScroll;
    private SwingNode swingNode;

    private static final Map parameters = new HashMap();

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Calendar cal = Calendar.getInstance();

    private final String DB_NAME = getDBName();
    private final String FOLDER_HOME = "doc";
    private final String FOLDER = "avisos";
    private static int idOrc;

    private final String REPORT_PATH = "src/report/";
    private final String REPORT_NAME = "Avisos.jrxml";

    /**
     * Método que permite detetar que avisos estão seleccioandos naquele momento
     */
    private final ListChangeListener<AvisoDebito> avisosSelecionados
            = new ListChangeListener<AvisoDebito>() {
        @Override
        public void onChanged(ListChangeListener.Change<? extends AvisoDebito> c) {
            btReciboGerar.setDisable(false);
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
     * Método que encaminha para a stage GerarRecibo para gerar o respectivo
     *
     * @param event evento
     * @throws ClassNotFoundException erro de classe não encontrada
     * @throws SQLException erro de sql
     * @throws Exception excepcão genérica
     */
    @FXML
    private void btGerarReciboFired(ActionEvent event) throws ClassNotFoundException, SQLException, Exception {
        if (verifyFraccaoAvisosSelEquals()) {
            new GerarReciboController().setAvisosGerar(avisoSel);
            new GerarRecibo().start(new Stage());
            //refreshAvisos();
        } else {
            showErrorDialog("Impossível gerar um recibo para diferentes frações.");
        }

    }

    /**
     * Inicia a classe GerarReciboControllerPT2.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btReciboGerar.setDisable(true);

        lvAviso.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        avisoSel = FXCollections.observableArrayList();
        avisos = FXCollections.observableArrayList();

        lvAviso.setItems(avisos);

        //showListViewCells();
        AvisoDebitoDAO dao;
        dao = new AvisoDebitoDAO();
        for (AvisoDebito av : dao.getAllAvisosDebito(idOrc)) {
            if (av.getResolvido() == 0) { //adiciona os nao resolvidos
                avisos.add(av);
            }
        }
        dao.close();
        if (avisos.size() == 0) {
            btReciboGerar.setDisable(true);
        }

        //add listener
        lvAviso.getSelectionModel().getSelectedItems().addListener(avisosSelecionados);

        createFolderstoPDF();

    }

    /**
     * Método que recebe o id do orçamento seleccionado anteriormente
     *
     * @param idOrcamentoSel identificador do Orçamento
     */
    void setIdOrc(int idOrcamentoSel) {
        GerarReciboControllerPT2.idOrc = idOrcamentoSel;
    }

    /**
     * Método que permite criar o componente swing com o viewer dos reports
     *
     * @param swingNode swing node responsóvel por inserir um componente swing
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
                    .compileReport(REPORT_PATH + REPORT_NAME);

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
            Logger.getLogger(VerAvisoControllerPT2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jrv;
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
     * Método que permite verificar se todos os avisos selecionados pertencem á
     * mesma fraccao
     *
     * @return se sim ou se não
     */
    private boolean verifyFraccaoAvisosSelEquals() {
        boolean result = true;
        String fraccaoTmp = null;

        for (AvisoDebito av : avisoSel) {
            fraccaoTmp = av.getCodFraccao(); //buscar uma fraçcao para cmparação
            break;
        }

        for (AvisoDebito av : avisoSel) {
            if (!fraccaoTmp.equals(av.getCodFraccao())) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Método quer retorna o nome da Base de Dados que está actualmente a ser
     * utilizada
     *
     * @return nome da BD
     */
    private String getDBName() {
        DB db = new DB();
        return db.getName();
    }

    /**
     * Método quer retorna o ano de um orçamento através do seu id
     *
     * @param idOrc identificador do Orçamento
     * @return o ano
     */
    private int getAno(int idOrc) {
        int ano = 0;
        OrcamentoDAO orcDAO = new OrcamentoDAO();
        ano = orcDAO.getOrcamento(idOrc).getAno();
        orcDAO.close();
        return ano;
    }

    /**
     * Método que permite criar as pastas para armazenar os pdf´s gerados pelos
     * reports
     */
    private void createFolderstoPDF() {
        try {
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

        }
    }

    /**
     * Método que devolve as fracções pagantes daquele orçamento
     *
     * @return
     */
    private ArrayList<FraccaoOrcamento> getFraccoesOrcamento() {
        ArrayList<FraccaoOrcamento> fraccoes = null;
        FraccaoOrcamentoDAO dao = new FraccaoOrcamentoDAO();
        fraccoes = dao.getAllFraccoesOrcamento(idOrc);
        return fraccoes;
    }

    /**
     * Método que permite verificar se já existe ou não aquele pdf
     *
     * @param pdfPath caminho do pdf
     * @return se ja existe o pdf
     */
    private boolean verifyPDFExist(String pdfPath) {
        File f = new File(pdfPath);

        return f.exists();
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
        Window owner = GerarReciboPT2.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

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
        //clearInfo();
        String fraccao = txtFraccao.getText();

        AvisoDebitoDAO dao = new AvisoDebitoDAO();
        avisos.clear();

        if (fraccao.equals("")) {
            for (AvisoDebito ad : dao.getAllAvisosDebito(idOrc)) {

                if (ad.getResolvido() == 0) { //adiciona os nao resolvidos (nao têm recibo ainda)
                    avisos.add(ad);
                }

            }
            lvAviso.setItems(avisos);
        } else {
            for (AvisoDebito ad : dao.getAllAvisosDebitoFraccao(idOrc, fraccao)) {
                if (ad.getResolvido() == 0) { //adiciona os nao resolvidos (nao têm recibo ainda)
                    avisos.add(ad);
                }
            }
            lvAviso.setItems(avisos);
        }
        dao.close();
        //showListViewCells();

    }
}
