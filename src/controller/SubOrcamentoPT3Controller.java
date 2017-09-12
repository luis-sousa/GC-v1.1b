package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */

import application.SubOrcamentoPT2;
import application.SubOrcamentoPT3;
import application.SubOrcamentoPT4;
import dao.AvisoDebitoDAO;
import dao.FracaoRubricaSubOrcamentoDAO;
import dao.FracaoSubOrcamentoDAO;
import dao.RubricaSubOrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.FracaoRubricaSubOrcamento;
import model.FracaoSubOrcamento;
import model.RubricaSubOrcamento;
import validation.Validator;

/**
 * Classe OrçamentoControllerPT3 está responsável pelo controlo dos eventos
 * relativos á Stage onde se define para cada fracção que rubricas é que pagam
 * relativamente aquele orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class SubOrcamentoPT3Controller implements Initializable {

    @FXML
    private ComboBox cbFraccao;
    @FXML
    private ComboBox cbRubrica;

    @FXML
    private Button btRemover;
    @FXML
    private Button btNova;
    @FXML
    private Button btInserir;

    @FXML
    private Label lbFraccao;
    @FXML
    private Label lbInfo;
    @FXML
    private Label lbRubrica;

    @FXML
    private TableView tbFraccaoRubricaOrcamento;
    @FXML
    private TableColumn clFraccao;
    @FXML
    private TableColumn clRubrica;

    @FXML
    private ObservableList<FracaoRubricaSubOrcamento> fracoesRubricasSubOrcamento;
    @FXML
    private ObservableList<String> fracoes;
    @FXML
    private ObservableList<String> rubricas;

    private int positionFraccaoTable;
    private Validator validator;
    private ArrayList<String> errors;
    private FracaoRubricaSubOrcamento fracaoRubricaSubOrcamento;
    private static int idOrc; //id do orcamento a manipular
    private static boolean alteracoes;
    private FracaoRubricaSubOrcamentoDAO dao;
    private String novaFraccaoSelct;

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        lbRubrica.setText("");
        lbFraccao.setText("");
    }

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        cbFraccao.setValue("");
        cbRubrica.setValue("");
    }

    /**
     * Método que remove o style errors
     */
    private void removerStyles() {
        cbFraccao.getStyleClass().remove("errorTextField");
        cbRubrica.getStyleClass().remove("errorTextField");

    }

    /**
     * Método que permite preparar a Stage para a inserção de uma nova fracção
     * associada a uma nova rubrica
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {
        limparCampos();
        limparMSG();
        removerStyles();
        cbFraccao.setDisable(false);
        cbRubrica.setDisable(false);
        btRemover.setDisable(true);
        btInserir.setDisable(false);
    }

    /**
     * Método que permite inserir uma nova fracção associada a uma nova rubrica
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        errors = new ArrayList<String>();

        String nomeFraccao = cbFraccao.getValue().toString();
        String nomeRubrica = cbRubrica.getValue().toString();

        fracaoRubricaSubOrcamento = new FracaoRubricaSubOrcamento();
        fracaoRubricaSubOrcamento.setRubrica(nomeRubrica);
        fracaoRubricaSubOrcamento.setFracao(nomeFraccao);
        fracaoRubricaSubOrcamento.setIdSubOrcamento(idOrc);

        errors = fracaoRubricaSubOrcamento.validate();

        if (errors.isEmpty()) {
            //associar rubrica 
            fracoesRubricasSubOrcamento.add(fracaoRubricaSubOrcamento); //adicionar na tableview
            dao = new FracaoRubricaSubOrcamentoDAO();
            dao.adicionar(fracaoRubricaSubOrcamento); //add na BD 
            dao.close();
            limparCampos();
            showSucessDialog("Associação Fracção/Rubrica definida com sucesso !!!");
            SubOrcamentoPT3Controller.alteracoes = true;
            new SubOrcamentoPT4Controller().setAlteracaoes(SubOrcamentoPT3Controller.alteracoes);
        } else {
            validator = new Validator();
            for (String error : errors) {
                switch (error) {
                    case "rubrica":
                        lbRubrica.setText(validator.getErrorRubrica());
                        cbRubrica.getStyleClass().add("errorTextField");
                        break;
                    case "fraccao":
                        lbFraccao.setText(validator.getErrorCodFraccao());
                        cbFraccao.getStyleClass().add("errorTextField");
                        break;
                }
            }
        }

    }

    /**
     * Método que permite remover uma fracção associada a uma rubrica
     *
     * @param event evento
     */
    @FXML
    private void btRemoveFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();

        dao = new FracaoRubricaSubOrcamentoDAO();
        dao.remover(getTableFraccaoSeleccionada());//remover na bd
        dao.close();
        fracoesRubricasSubOrcamento.remove(getTableFraccaoSeleccionada()); //apagar na table view
        showSucessDialog("Associação Fracção/Rubrica removida com sucesso !!!");
        SubOrcamentoPT3Controller.alteracoes = true;
        new SubOrcamentoPT4Controller().setAlteracaoes(SubOrcamentoPT3Controller.alteracoes);

    }

    /**
     * Método que permite avançar para a Stage seguinte onde se obtêm as
     * mensalidades finais
     *
     * @param event evento
     * @throws Exception
     */
    @FXML
    private void btNextFired(ActionEvent event) throws Exception {
        new SubOrcamentoPT4Controller().setIdOrc(idOrc);
        new SubOrcamentoPT4Controller().setAlteracaoes(SubOrcamentoPT3Controller.alteracoes);
        SubOrcamentoPT3.getStage().close();
        new SubOrcamentoPT4().start(new Stage());
    }

    /**
     * Método que permite ir para a Stage anterior, stage que permitiu associar
     * as fracções pagantes
     *
     * @param event evento
     * @throws Exception
     */
    @FXML
    private void btPreviousFired(ActionEvent event) throws Exception {

        SubOrcamentoPT3.getStage().close();
        new SubOrcamentoPT2().start(new Stage());
        new SubOrcamentoPT2Controller().setAlteracaoes(false);

    }

   /**
     * Listener tableview fraccoesRubricasOrcamento
     */
    private final ListChangeListener<FracaoSubOrcamento> selectorTableFraccoes
            = new ListChangeListener<FracaoSubOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends FracaoSubOrcamento> c) {
                    verFraccaoSeleccionadaDetails();
                }
            };

    /**
     * Mostra os detalhes de uma fracção seleccionada
     */
    private void verFraccaoSeleccionadaDetails() {

        limparMSG();
        removerStyles();
        cbFraccao.setEditable(false);
        cbFraccao.setDisable(true);
        cbRubrica.setEditable(false);
        cbRubrica.setDisable(true);
        final FracaoRubricaSubOrcamento fracao = getTableFraccaoSeleccionada();
        positionFraccaoTable = fracoesRubricasSubOrcamento.indexOf(fracao);

        if (fracao != null) {
            // poe os txtfield com os dados correspondentes
            cbFraccao.setValue(fracao.getFracao());
            cbRubrica.setValue(fracao.getRubrica());
            // poe os botoes num estado especifico 

            btRemover.setDisable(false);

            btInserir.setDisable(true);

        }
    }

    /**
     * Devolve a row da tabela que está seleccionada
     *
     * @return FraccaoRubricaOrcamento
     */
    private FracaoRubricaSubOrcamento getTableFraccaoSeleccionada() {
        if (tbFraccaoRubricaOrcamento != null) {
            List<FracaoRubricaSubOrcamento> tabela = tbFraccaoRubricaOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final FracaoRubricaSubOrcamento fraccaoSeleccionada = tabela.get(0);
                return fraccaoSeleccionada;
            }
        }
        return null;
    }

    /**
     * Método que permite iniciar a tabela
     */
    private void iniciarTableView() {
        //fazer o binding entre as colunas e o respetivo campo do objeto e iniciar a tableview
        clFraccao.setCellValueFactory(new PropertyValueFactory<FracaoRubricaSubOrcamento, String>("fracao"));
        clRubrica.setCellValueFactory(new PropertyValueFactory<FracaoRubricaSubOrcamento, String>("rubrica"));
        fracoesRubricasSubOrcamento = FXCollections.observableArrayList();
        tbFraccaoRubricaOrcamento.setItems(fracoesRubricasSubOrcamento);
    }

    /**
     * Initializes the controller class.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new FracaoRubricaSubOrcamentoDAO();
//        Orcamento orc = dao.getOrcamento(idOrc);
//        lbInfo.setText("Orcamento: " + orc.getAno() + " da data: " + orc.getData() + " versao: " + orc.getVersao());

        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);

        cbFraccao.setValue(""); //importante validar a fraccao
        cbRubrica.setValue(""); //importante validar a rubrica

        //buscar todas as fracoes daquele Sub orcamento para preencher a combo box
        fracoes = FXCollections.observableArrayList();
        FracaoSubOrcamentoDAO fracaoOrcamentoDAO = new FracaoSubOrcamentoDAO();
        ArrayList<FracaoSubOrcamento> lista = new ArrayList<FracaoSubOrcamento>();
        lista = fracaoOrcamentoDAO.getAllFracoesSubOrcamento(idOrc);
        for (FracaoSubOrcamento fraccao : lista) {
            fracoes.add(fraccao.getFracao()); //adiciona fraccoes do orcamento na observable list

        }
        fracaoOrcamentoDAO.close();

        cbFraccao.setItems(fracoes); //preencher combo box com as fraccoes existentes

        //buscar todas as rubricas daquele Sub orcamento para preencher a combo box
        rubricas = FXCollections.observableArrayList();

        RubricaSubOrcamentoDAO rubricasOrcamentoDAO = new RubricaSubOrcamentoDAO();

        ArrayList<RubricaSubOrcamento> lista1 = new ArrayList<RubricaSubOrcamento>();
        lista1 = rubricasOrcamentoDAO.getAllRubricasOrcamento(idOrc);
        for (RubricaSubOrcamento rubrica : lista1) {
            rubricas.add(rubrica.getRubrica()); //adiciona rubricas do orcamento na observable list 

        }
        rubricasOrcamentoDAO.close();//fechar conexao

        cbRubrica.setItems(rubricas); //preencher combo box com as rubricas existentes

        // Seleccionar as linhas na tabela fraccoes
        final ObservableList<FracaoSubOrcamento> tabelaFraccaoSel = tbFraccaoRubricaOrcamento.getSelectionModel().getSelectedItems();
        tabelaFraccaoSel.addListener(selectorTableFraccoes);

        dao = new FracaoRubricaSubOrcamentoDAO();
        if (!dao.getAllFraccoesRubricasOrcamento(idOrc).isEmpty() && alteracoes == false) { // se existem dados na BD faz populate table view
            //System.out.println("nao houve alteracoes");
            ArrayList<FracaoRubricaSubOrcamento> lista2 = new ArrayList<FracaoRubricaSubOrcamento>();
            lista2 = dao.getAllFraccoesRubricasOrcamento(idOrc);
            for (FracaoRubricaSubOrcamento fraccao : lista2) {
                fracoesRubricasSubOrcamento.add(fraccao); //faz populate na table view
            }
        } else if (!dao.getAllFraccoesRubricasOrcamento(idOrc).isEmpty() && alteracoes == true) {
            //System.out.println("Removeu tudo");
            dao.removerAll(idOrc);
            addAllRubricasAndFraccoesOrcamento();
        } else { //add todas rubricas e fraccoes do orcamento
            //System.out.println("estava vazio");
            addAllRubricasAndFraccoesOrcamento();
        }
        dao.close();//fechar conexao

        if (!validarRemoverEditarOrcamento()) { // se nao pode editar orçamento
            disableComponents();
        } else {
            // listen for changes to the fraccao and update the rubrica cbox accordingly.
            cbFraccao.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    //System.out.println("Value is::" + newValue);
                    novaFraccaoSelct = newValue;
                    refreshCBRubricas();
                }
            });
        }

    }

    /**
     * Método que permite verificar se existe ou não aquele associação entre
     * fracção e rubrica
     *
     * @param fraccao
     * @param rubrica
     * @return se existe ou não
     */
    public boolean existFraccaoRubricaTableView(String fraccao, String rubrica) {
        boolean result = false;

        for (FracaoRubricaSubOrcamento fro : fracoesRubricasSubOrcamento) {
            if (fro.getFracao().equals(fraccao) && fro.getRubrica().equals(rubrica)) {
                result = true;
            }

        }
        return result;
    }

    /**
     * Método que faz o resfresh dos values que a combo box poderá ter naquele
     * momento. Vai actualizando conforme as fracções e suas respectivas
     * rubricas se vão inserindo na table view e na BD
     */
    public void refreshCBRubricas() {
        rubricas = FXCollections.observableArrayList();
        RubricaSubOrcamentoDAO rubricaDAO = new RubricaSubOrcamentoDAO();
        for (RubricaSubOrcamento ro : rubricaDAO.getAllRubricasOrcamento(idOrc)) {
            if (!existFraccaoRubricaTableView(this.novaFraccaoSelct, ro.getRubrica())) {
                rubricas.add(ro.getRubrica()); //adiciona rubricas possiveis na observable list
            } else {
                rubricas.remove(ro.getRubrica());
            }
        }
        rubricaDAO.close();

        cbRubrica.setItems(rubricas); //preencher combo box com as rubricas possiveis de adicionar
    }

    /**
     * Método que recebe o id do orçamento a manipular escolhido na stage
     * inicial deste processo
     *
     * @param id id do Orçamento
     */
    public void setIdOrc(int id) {
        SubOrcamentoPT3Controller.idOrc = id;
        //System.out.println("aqui" + idOrc);
    }

    /**
     * Recebe se houve ou não alterações nos forms anteriores
     *
     * @param result resultado das alterações
     */
    public void setAlteracaoes(boolean result) {
        SubOrcamentoPT3Controller.alteracoes = result;
        //System.out.println("HH PT2" + alteracoes);
    }

    /**
     * Adiciona na BD e na tabela todas as combinações possíveis entre fracções
     * e rubricas existentes na BD
     */
    public void addAllRubricasAndFraccoesOrcamento() {
        RubricaSubOrcamentoDAO roDao = new RubricaSubOrcamentoDAO();
        FracaoSubOrcamentoDAO foDao = new FracaoSubOrcamentoDAO();

        for (FracaoSubOrcamento fo : foDao.getAllFracoesSubOrcamento(idOrc)) {
            for (RubricaSubOrcamento ro : roDao.getAllRubricasOrcamento(idOrc)) {
                fracaoRubricaSubOrcamento = new FracaoRubricaSubOrcamento();
                fracaoRubricaSubOrcamento.setIdSubOrcamento(idOrc);
                fracaoRubricaSubOrcamento.setFracao(fo.getFracao());
                fracaoRubricaSubOrcamento.setRubrica(ro.getRubrica());
                fracoesRubricasSubOrcamento.add(fracaoRubricaSubOrcamento); // add todas as rubricas e fraccoes do orcamento na table view
                dao.adicionar(fracaoRubricaSubOrcamento); // add na bd
            }
        }
    }

 /**
     * Método que verifica se um sub orçamento pode ser removido ou editado.Só
     * pode remover ou editar um sub orçamento se ele não tiver dados associados
     * (avisos de débito)
     *
     * @return se pode remover ou editar o orçamento.
     */
    private boolean validarRemoverEditarOrcamento() {
        boolean resultAd = true;
        AvisoDebitoDAO adDAO = new AvisoDebitoDAO();

        if (adDAO.existSubOrcamento(idOrc)) {
            resultAd = false;
        }
        //System.out.println(resultAd);
        adDAO.close();

        return resultAd;
    }

    /**
     * Método que permite fazer disable a certos componentes da janela
     */
    private void disableComponents() {
        cbRubrica.setDisable(true);
        cbFraccao.setDisable(true);
        btRemover.setDisable(true);
        btInserir.setDisable(true);
        btNova.setDisable(true);

        tbFraccaoRubricaOrcamento.setDisable(true);
        tbFraccaoRubricaOrcamento.setEditable(false);
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
        Window owner = SubOrcamentoPT3.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
}
