package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.AddOrcamentoPT2;
import application.AddOrcamentoPT3;
import application.AddOrcamentoPT4;
import dao.AvisoDebitoDAO;
import dao.DespesaOrcamentoDAO;
import dao.FraccaoOrcamentoDAO;
import dao.FraccaoRubricaOrcamentoDAO;
import dao.RubricaOrcamentoDAO;
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
import model.FraccaoOrcamento;
import model.FraccaoRubricaOrcamento;
import model.Orcamento;
import model.RubricaOrcamento;
import validation.Validator;

/**
 * Classe OrçamentoControllerPT3 está responsável pelo controlo dos eventos
 * relativos á Stage onde se define para cada fracção que rubricas é que pagam
 * relativamente aquele orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class OrcamentoControllerPT3 implements Initializable {

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
    private ObservableList<FraccaoRubricaOrcamento> fraccoesRubricasOrcamento;
    @FXML
    private ObservableList<String> fraccoes;
    @FXML
    private ObservableList<String> rubricas;

    private int positionFraccaoTable;
    private Validator validator;
    private ArrayList<String> errors;
    private FraccaoRubricaOrcamento fraccaoRubricaOrcamento;
    private static int idOrc; //id do orcamento a manipular
    private static boolean alteracoes;
    private FraccaoRubricaOrcamentoDAO dao;
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

        fraccaoRubricaOrcamento = new FraccaoRubricaOrcamento();
        fraccaoRubricaOrcamento.setRubrica(nomeRubrica);
        fraccaoRubricaOrcamento.setFraccao(nomeFraccao);
        fraccaoRubricaOrcamento.setIdOrcamento(idOrc);

        errors = fraccaoRubricaOrcamento.validate();

        if (errors.isEmpty()) {
            //associar rubrica 
            fraccoesRubricasOrcamento.add(fraccaoRubricaOrcamento); //adicionar na tableview
            dao = new FraccaoRubricaOrcamentoDAO();
            dao.adicionar(fraccaoRubricaOrcamento); //add na BD 
            dao.close();
            limparCampos();
            showSucessDialog("Associação Fracção/Rubrica definida com sucesso !!!");
            OrcamentoControllerPT3.alteracoes = true;
            new OrcamentoControllerPT4().setAlteracaoes(OrcamentoControllerPT3.alteracoes);
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

        dao = new FraccaoRubricaOrcamentoDAO();
        dao.remover(getTableFraccaoSeleccionada());//remover na bd
        dao.close();
        fraccoesRubricasOrcamento.remove(getTableFraccaoSeleccionada()); //apagar na table view
        showSucessDialog("Associação Fracção/Rubrica removida com sucesso !!!");
        OrcamentoControllerPT3.alteracoes = true;
        new OrcamentoControllerPT4().setAlteracaoes(OrcamentoControllerPT3.alteracoes);

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
        new OrcamentoControllerPT4().setIdOrc(idOrc);
        new OrcamentoControllerPT4().setAlteracaoes(OrcamentoControllerPT3.alteracoes);
        AddOrcamentoPT3.getStage().close();
        new AddOrcamentoPT4().start(new Stage());
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

        AddOrcamentoPT3.getStage().close();
        new AddOrcamentoPT2().start(new Stage());
        new OrcamentoControllerPT2().setAlteracaoes(false);

    }

    /**
     * Listener tableview fraccoesRubricasOrcamento
     */
    private final ListChangeListener<FraccaoOrcamento> selectorTableFraccoes
            = new ListChangeListener<FraccaoOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends FraccaoOrcamento> c) {
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
        final FraccaoRubricaOrcamento fraccao = getTableFraccaoSeleccionada();
        positionFraccaoTable = fraccoesRubricasOrcamento.indexOf(fraccao);

        if (fraccao != null) {
            // poe os txtfield com os dados correspondentes
            cbFraccao.setValue(fraccao.getFraccao());
            cbRubrica.setValue(fraccao.getRubrica());
            // poe os botoes num estado especifico 
            if (fraccao.getRubrica().equals("Fundo Comum de Reserva")) {
                btRemover.setDisable(true);
            } else {
                btRemover.setDisable(false);
            }
            btInserir.setDisable(true);

        }
    }

    /**
     * Devolve a row da tabela que está seleccionada
     *
     * @return FraccaoRubricaOrcamento
     */
    private FraccaoRubricaOrcamento getTableFraccaoSeleccionada() {
        if (tbFraccaoRubricaOrcamento != null) {
            List<FraccaoRubricaOrcamento> tabela = tbFraccaoRubricaOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final FraccaoRubricaOrcamento fraccaoSeleccionada = tabela.get(0);
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
        clFraccao.setCellValueFactory(new PropertyValueFactory<FraccaoRubricaOrcamento, String>("fraccao"));
        clRubrica.setCellValueFactory(new PropertyValueFactory<FraccaoRubricaOrcamento, String>("rubrica"));
        fraccoesRubricasOrcamento = FXCollections.observableArrayList();
        tbFraccaoRubricaOrcamento.setItems(fraccoesRubricasOrcamento);
    }

    /**
     * Initializes the controller class.
     *
     * @param url url   
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new FraccaoRubricaOrcamentoDAO();
        Orcamento orc = dao.getOrcamento(idOrc);
        lbInfo.setText("Orcamento: " + orc.getAno() + " da data: " + orc.getData() + " versao: " + orc.getVersao());

        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);

        cbFraccao.setValue(""); //importante validar a fraccao
        cbRubrica.setValue(""); //importante validar a rubrica

        //buscar todas as fraccoes daquele orcamento para preencher a combo box
        fraccoes = FXCollections.observableArrayList();
        FraccaoOrcamentoDAO fraccaoOrcamentoDAO = new FraccaoOrcamentoDAO();
        ArrayList<FraccaoOrcamento> lista = new ArrayList<FraccaoOrcamento>();
        lista = fraccaoOrcamentoDAO.getAllFraccoesOrcamento(idOrc);
        for (FraccaoOrcamento fraccao : lista) {
            fraccoes.add(fraccao.getFraccao()); //adiciona fraccoes do orcamento na observable list

        }
        fraccaoOrcamentoDAO.close();

        cbFraccao.setItems(fraccoes); //preencher combo box com as fraccoes existentes

        //buscar todas as rubricas daquele orcamento para preencher a combo box
        rubricas = FXCollections.observableArrayList();

        RubricaOrcamentoDAO rubricasOrcamentoDAO = new RubricaOrcamentoDAO();

        ArrayList<RubricaOrcamento> lista1 = new ArrayList<RubricaOrcamento>();
        lista1 = rubricasOrcamentoDAO.getAllRubricasOrcamento(idOrc);
        for (RubricaOrcamento rubrica : lista1) {
            rubricas.add(rubrica.getRubrica()); //adiciona rubricas do orcamento na observable list 

        }
        rubricasOrcamentoDAO.close();//fechar conexao

        cbRubrica.setItems(rubricas); //preencher combo box com as rubricas existentes

        // Seleccionar as linhas na tabela fraccoes
        final ObservableList<FraccaoOrcamento> tabelaFraccaoSel = tbFraccaoRubricaOrcamento.getSelectionModel().getSelectedItems();
        tabelaFraccaoSel.addListener(selectorTableFraccoes);

        dao = new FraccaoRubricaOrcamentoDAO();
        if (!dao.getAllFraccoesRubricasOrcamento(idOrc).isEmpty() && alteracoes == false) { // se existem dados na BD faz populate table view
            //System.out.println("nao houve alteracoes");
            ArrayList<FraccaoRubricaOrcamento> lista2 = new ArrayList<FraccaoRubricaOrcamento>();
            lista2 = dao.getAllFraccoesRubricasOrcamento(idOrc);
            for (FraccaoRubricaOrcamento fraccao : lista2) {
                fraccoesRubricasOrcamento.add(fraccao); //faz populate na table view
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

        for (FraccaoRubricaOrcamento fro : fraccoesRubricasOrcamento) {
            if (fro.getFraccao().equals(fraccao) && fro.getRubrica().equals(rubrica)) {
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
        RubricaOrcamentoDAO rubricaDAO = new RubricaOrcamentoDAO();
        for (RubricaOrcamento ro : rubricaDAO.getAllRubricasOrcamento(idOrc)) {
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
        OrcamentoControllerPT3.idOrc = id;
        //System.out.println("aqui" + idOrc);
    }

    /**
     * Recebe se houve ou não alterações nos forms anteriores
     *
     * @param result resultado das alterações
     */
    public void setAlteracaoes(boolean result) {
        OrcamentoControllerPT3.alteracoes = result;
        //System.out.println("HH PT2" + alteracoes);
    }

    /**
     * Adiciona na BD e na tabela todas as combinações possíveis entre fracções
     * e rubricas existentes na BD
     */
    public void addAllRubricasAndFraccoesOrcamento() {
        RubricaOrcamentoDAO roDao = new RubricaOrcamentoDAO();
        FraccaoOrcamentoDAO foDao = new FraccaoOrcamentoDAO();

        for (FraccaoOrcamento fo : foDao.getAllFraccoesOrcamento(idOrc)) {
            for (RubricaOrcamento ro : roDao.getAllRubricasOrcamento(idOrc)) {
                fraccaoRubricaOrcamento = new FraccaoRubricaOrcamento();
                fraccaoRubricaOrcamento.setIdOrcamento(idOrc);
                fraccaoRubricaOrcamento.setFraccao(fo.getFraccao());
                fraccaoRubricaOrcamento.setRubrica(ro.getRubrica());
                fraccoesRubricasOrcamento.add(fraccaoRubricaOrcamento); // add todas as rubricas e fraccoes do orcamento na table view
                dao.adicionar(fraccaoRubricaOrcamento); // add na bd
            }
        }
    }

    /**
     * Método que verifica se um orçamento pode ser removido ou editado.Só pode
     * remover ou editar orçamento se ele não tiver dados associados (avisos de
     * débito) ou despesas
     *
     * @return se pode remover o orçamento.
     */
    private boolean validarRemoverEditarOrcamento() {
        boolean resultDsp = true;
        boolean resultAd = true;

        DespesaOrcamentoDAO Dspdao = new DespesaOrcamentoDAO();
        AvisoDebitoDAO adDAO = new AvisoDebitoDAO();

        if (Dspdao.existOrcamento(idOrc)) {
            resultDsp = false;
        }
        Dspdao.close();

        //System.out.println(resultDsp);
        if (adDAO.existOrcamento(idOrc)) {
            resultAd = false;
        }
        //System.out.println(resultAd);
        adDAO.close();

        return resultDsp && resultAd;
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
        Window owner = AddOrcamentoPT3.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
}
