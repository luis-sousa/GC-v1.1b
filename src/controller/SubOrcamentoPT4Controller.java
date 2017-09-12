package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */

import application.SubOrcamentoPT0;
import application.SubOrcamentoPT3;
import application.SubOrcamentoPT4;
import dao.FracaoMensalidadesSubOrcamentoDAO;
import dao.FracaoRubricaSubOrcamentoDAO;
import dao.FracaoSubOrcamentoDAO;
import dao.FraccaoDAO;
import dao.RubricaSubOrcamentoDAO;
import dao.SubOrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.FracaoMensalidadesSubOrcamento;
import model.FracaoRubricaSubOrcamento;
import model.FracaoSubOrcamento;
import model.Fraccao;
import model.RubricaSubOrcamento;
import util.MoneyConverter;

/**
 * Classe SubOrçamentoControllerPT4 está responsável por apresentar as
 * mensalidades calculadas para cada fração pagante naquele sub orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class SubOrcamentoPT4Controller implements Initializable {

    @FXML
    private TableView tbFraccaoQuotasOrcamento;
    @FXML
    private TableColumn clFraccao;
    @FXML
    private TableColumn clNome;
    @FXML
    private TableColumn clPerm;
    @FXML
    private TableColumn clMensal;
    @FXML
    private TableColumn clAnual;
    @FXML
    private TableColumn clAno;

    @FXML
    private Label lbInfo;

    @FXML
    private Button btRemover;
    @FXML
    private Button btInserir;
    @FXML
    private Button btEditar;
    @FXML
    private Button btNova;

    @FXML
    private ObservableList<FracaoMensalidadesSubOrcamento> fraccoesQuotasOrcamento;

    private FracaoMensalidadesSubOrcamento fraccaoQuotasOrcamento;
    private FracaoMensalidadesSubOrcamentoDAO dao;


    /*------------------------------------------------------------------------*/
    private static int idSubOrc; //id do sub orcamento a manipular
    private static int id; //id do orcamento a que pertence o sub
    private static boolean alteracoes;

    /**
     * Método que permite preparar a Stage para a inserção de uma nova
     * observação associada a uma fracção
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {

        btRemover.setDisable(true);
        btEditar.setDisable(true);
        btInserir.setDisable(false);
    }

    /**
     * Método que permite ir para a Stage anterior, stage que permitiu associar
     * uma fracção a uma rubrica
     *
     * @param event evento
     * @throws Exception
     */
    @FXML
    private void btPreviousFired(ActionEvent event) throws Exception {
        new SubOrcamentoPT3Controller().setAlteracaoes(false);
        SubOrcamentoPT4.getStage().close();
        new SubOrcamentoPT3().start(new Stage());

    }

    /**
     * Método que permite ir para a Stage onde se começou todo o processo de
     * definição de um orçamento
     *
     * @param event evento
     * @throws Exception
     */
    @FXML
    private void btFinishFired(ActionEvent event) throws Exception {
        SubOrcamentoPT4.getStage().close();
        new SubOrcamentoPT0().start(new Stage());
    }

    /**
     * Método quer permite iniciar a tabela das mensalidades
     */
    private void iniciarTableView() {
        //fazer o binding entre as colunas e o respetivo campo do objeto e iniciar a tableview
        clFraccao.setCellValueFactory(new PropertyValueFactory<FracaoMensalidadesSubOrcamento, String>("fracao"));
        clNome.setCellValueFactory(new PropertyValueFactory<FracaoMensalidadesSubOrcamento, String>("nome"));
        clPerm.setCellValueFactory(new PropertyValueFactory<FracaoMensalidadesSubOrcamento, Float>("perm"));
        clMensal.setCellValueFactory(new PropertyValueFactory<FracaoMensalidadesSubOrcamento, Float>("mensalEuros"));
        clAnual.setCellValueFactory(new PropertyValueFactory<FracaoMensalidadesSubOrcamento, Float>("anualEuros"));
        clAno.setCellValueFactory(new PropertyValueFactory<FracaoMensalidadesSubOrcamento, Integer>("ano"));
        fraccoesQuotasOrcamento = FXCollections.observableArrayList();
        tbFraccaoQuotasOrcamento.setItems(fraccoesQuotasOrcamento);
    }

    /**
     * Initializes the OrcamentoControllerPT4 class.
     *
     * @param url url
     * @param rb resourcebundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SubOrcamentoPT4Controller.id = getIdOrcamento(idSubOrc);

//        dao = new FracaoMensalidadesSubOrcamentoDAO();
//        Orcamento orc = dao.getOrcamento(idOrc);
//        lbInfo.setText("Orcamento: " + orc.getAno() + " da data: " + orc.getData() + " versao: " + orc.getVersao());
        // Inicializar a tabela
        this.iniciarTableView();

        /* ++++++++++++++++++++++++++++++++++++++--------------------------- */
        try {
            dao = new FracaoMensalidadesSubOrcamentoDAO();
            ArrayList<Fraccao> fraccoesPagantes = new ArrayList<Fraccao>();
            fraccoesPagantes = getAllFraccoesPagantes(idSubOrc);
            if (dao.haveData(idSubOrc) && alteracoes == false) {
                //System.out.println("nao houve alterações);
                for (Fraccao fr : fraccoesPagantes) {
                    for (FracaoMensalidadesSubOrcamento fco : dao.getAllFraccoesQuotasOrcamento(idSubOrc)) {
                        if (fr.getCod().equals(fco.getFracao())) {
                            fco.setPerm(fr.getPermilagem());
                            fco.setNome(fr.getNome());
                            fco.setAno(dao.getOrcamento(id).getAno()); // ????????????????????????
                            fraccoesQuotasOrcamento.add(fco);
                        }
                    }
                }

            } else if (dao.haveData(idSubOrc) && alteracoes == true) {
                //System.out.println("removeu tudinho");
                dao.removerAll(idSubOrc);
                fraccoesQuotasOrcamento.clear();
                calcAndSaveResults(idSubOrc);
            } else {
                //System.out.println("vazio");
                calcAndSaveResults(idSubOrc);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            showErrorDialog(ex.getMessage());
        }
        dao.close();

    }

    /**
     * Método que devolve todas as fracções que pagam para um determinado
     * Orçamento
     *
     * @param idO identificação de um orçamento
     * @return lista com as fracções pagantes
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public ArrayList<Fraccao> getAllFraccoesPagantes(int idO) throws ClassNotFoundException, SQLException {
        FraccaoDAO frDao = new FraccaoDAO();
        FracaoSubOrcamentoDAO frOrcDao = new FracaoSubOrcamentoDAO();

        ArrayList<FracaoSubOrcamento> listaNomeFracccao = new ArrayList<>();
        listaNomeFracccao = frOrcDao.getAllFracoesSubOrcamento(idO);

        ArrayList<Fraccao> listafraccoes = new ArrayList<>();
        listafraccoes = frDao.getAllFraccoes();

        ArrayList<Fraccao> novaLista = new ArrayList<>();

        for (Fraccao f : listafraccoes) {
            for (FracaoSubOrcamento fo : listaNomeFracccao) {
                if (fo.getFracao().equals(f.getCod())) {
                    novaLista.add(f);
                }
            }
        }

        return novaLista;
    }

    /**
     * Método que devolve todas as rubricas que estão associdas a uma fracção
     * para um determinado Orçamento
     *
     * @param codFraccao identificação de um fracção
     * @param idO identificação de um orçamento
     * @return lista com rubricas
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public ArrayList<RubricaSubOrcamento> getRubricas(String codFraccao, int idO) throws ClassNotFoundException, SQLException {
        FracaoRubricaSubOrcamentoDAO froDAO = new FracaoRubricaSubOrcamentoDAO();
        RubricaSubOrcamentoDAO dao = new RubricaSubOrcamentoDAO();

        ArrayList<FracaoRubricaSubOrcamento> lista = new ArrayList<>();
        lista = froDAO.getAllFraccoesRubricasOrcamento(idO);

        ArrayList<String> nomeRubricas = new ArrayList<String>();
        ArrayList<RubricaSubOrcamento> rubricas = new ArrayList<RubricaSubOrcamento>();
        ArrayList<RubricaSubOrcamento> newRubricas = new ArrayList<RubricaSubOrcamento>();
        rubricas = dao.getAllRubricasOrcamento(idO);

        for (FracaoRubricaSubOrcamento fr : lista) {
            if (fr.getFracao().equals(codFraccao)) {
                nomeRubricas.add(fr.getRubrica()); // nome da rubrica
            }

        }

        for (RubricaSubOrcamento rub : rubricas) {
            for (String a : nomeRubricas) {
                if (rub.getRubrica().equals(a)) {
                    newRubricas.add(rub);
                }
            }
        }

        return newRubricas;
    }

    /**
     * Método que devolve uma lista de fraccoes que têm uma determinada rubrica
     * num Sub orcamento
     *
     * @param nomeRubrica nome de um rubrica
     * @param idO identificação de um Orcamento
     * @return lista com fracções
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public ArrayList<Fraccao> getFraccoes(String nomeRubrica, int idO) throws ClassNotFoundException, SQLException {

        FracaoRubricaSubOrcamentoDAO froDAO = new FracaoRubricaSubOrcamentoDAO();
        FraccaoDAO dao = new FraccaoDAO();

        ArrayList<FracaoRubricaSubOrcamento> lista = new ArrayList<>();
        lista = froDAO.getAllFraccoesRubricasOrcamento(idO);

        ArrayList<String> nomeFraccoes = new ArrayList<String>();
        ArrayList<Fraccao> fraccoes = new ArrayList<Fraccao>();
        ArrayList<Fraccao> newFraccoes = new ArrayList<Fraccao>();

        fraccoes = dao.getAllFraccoes();

        for (FracaoRubricaSubOrcamento fr : lista) {
            if (fr.getRubrica().equals(nomeRubrica)) {
                nomeFraccoes.add(fr.getFracao()); // nome das fraccoes 
            }

        }

        for (Fraccao fraccao : fraccoes) {
            for (String a : nomeFraccoes) {
                if (fraccao.getCod().equals(a)) {
                    newFraccoes.add(fraccao);
                }
            }
        }
        return newFraccoes;
    }

    /**
     * Método que permite calcular as mensalidades para as fracções pagantes
     * daquele orçamento
     *
     * @param idO identificação de um Orçamento
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void calcAndSaveResults(int idO) throws ClassNotFoundException, SQLException {

        int ano = dao.getOrcamento(id).getAno(); //????????????
        ArrayList<Fraccao> fraccoesPagantes = new ArrayList<Fraccao>();
        ArrayList<Fraccao> fraccoesRubrica = new ArrayList<Fraccao>();
        fraccoesPagantes = getAllFraccoesPagantes(idO);
        String codPagante;
        String rubrica;
        float resultado = 0;
        float valorRubrica;
        float somaPermilagemRubrica = 0;// soma das permilagens de uma rubrica
        ArrayList<RubricaSubOrcamento> rubricas = new ArrayList<RubricaSubOrcamento>();

        for (Fraccao f2 : fraccoesPagantes) {
            System.out.println("Fraccao a tratar" + f2.getCod());
            resultado = 0;
            System.out.println("Resultado Inicial" + resultado);
            codPagante = f2.getCod();//fraccao que vai ser usada para os calculos
            rubricas = this.getRubricas(codPagante, idO); //rubricas para a fraccao
            for (RubricaSubOrcamento rub : rubricas) {//percorrer rubricas da fraccao pagante
                rubrica = rub.getRubrica();//rubrica a tratar
                System.out.println("Rubrica a tratar" + rubrica);
                valorRubrica = rub.getValorEuros();
                System.out.println("Valor rubrica" + valorRubrica);
                somaPermilagemRubrica = 0;
                System.out.println("Soma Permilagem Inicial" + somaPermilagemRubrica);
                fraccoesRubrica = this.getFraccoes(rubrica, idO); // fraccoes k tem aquela rubrica
                for (Fraccao fraccao1 : fraccoesRubrica) {//percorrer fraccoes com aquela rubrica
                    somaPermilagemRubrica = somaPermilagemRubrica + fraccao1.getPermilagem();
                    System.out.println("Soma Permilagem" + somaPermilagemRubrica);
                }
                System.out.println("Que resultado chegou aqui" + resultado);
                resultado = resultado + ((valorRubrica / somaPermilagemRubrica) * f2.getPermilagem());
                System.out.println("Resultado na rubrica " + rub.getRubrica() + resultado);
            }
            System.out.println("O resultado da fraccao" + f2.getCod() + "é:" + resultado);
            fraccaoQuotasOrcamento = new FracaoMensalidadesSubOrcamento();

            fraccaoQuotasOrcamento.setFracao(f2.getCod());
            fraccaoQuotasOrcamento.setNome(f2.getNome());
            fraccaoQuotasOrcamento.setPerm(f2.getPermilagem());
            float resultadoformat = round(resultado, 2);
            fraccaoQuotasOrcamento.setMensalEuros(round((resultado / 12), 2));
            fraccaoQuotasOrcamento.setAnualEuros(resultadoformat);
            fraccaoQuotasOrcamento.setMensalCentimos(MoneyConverter.getCentimos(fraccaoQuotasOrcamento.getMensalEuros()));
            fraccaoQuotasOrcamento.setAnualCentimos(MoneyConverter.getCentimos(fraccaoQuotasOrcamento.getAnualEuros()));
            fraccaoQuotasOrcamento.setAno(ano);
            fraccaoQuotasOrcamento.setIdSubOrcamento(idO);
            fraccoesQuotasOrcamento.add(fraccaoQuotasOrcamento);
            dao.adicionar(fraccaoQuotasOrcamento);
        }
    }

    /**
     * Método que permite arredonfar os cálculos
     *
     * @param valueToRound valor arredondar
     * @param numberOfDecimalPlaces numero de casas decimais
     * @return valor depois de arredondado
     */
    private float round(float valueToRound, int numberOfDecimalPlaces) {
        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = valueToRound * multipicationFactor;
        return (float) (Math.round(interestedInZeroDPs) / multipicationFactor);
    }

    /**
     * Método que recebe o id do Orçamento a manipular
     *
     * @param id identificador do Orçamento
     */
    public void setIdOrc(int id) {
        SubOrcamentoPT4Controller.idSubOrc = id;
        //System.out.println("aqui" + idOrc);
    }

    /**
     * Recebe dos forms anteriores se houve alterações ou não
     *
     * @param result resultado das alterações
     */
    public void setAlteracaoes(boolean result) {
        SubOrcamentoPT4Controller.alteracoes = result;
        //System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHhhhhh PT3" + alteracoes);
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
        Window owner = SubOrcamentoPT4.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }

    private int getIdOrcamento(int idSubOrcamento) {
        int idO = -1;
        SubOrcamentoDAO dao2 = new SubOrcamentoDAO();
        idO = dao2.getOrcamentoID(idSubOrcamento);
        dao2.close();
        return idO;
    }

}
