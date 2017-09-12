package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.AddOrcamentoPT0;
import application.AddOrcamentoPT1;
import application.AddOrcamentoPT2;
import application.DespesaPT2;
import dao.AvisoDebitoDAO;
import dao.DespesaOrcamentoDAO;
import dao.RubricaDAO;
import dao.RubricaOrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Orcamento;
import model.Rubrica;
import model.RubricaOrcamento;
import util.MoneyConverter;
import validation.Validator;

/**
 * Classe OrçamentoControllerPT1 está responsável pelo controlo dos eventos
 * relativos á Stage que permite associar rubricas a um Orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class OrcamentoControllerPT1 implements Initializable {
    
    @FXML
    private ComboBox cbRubrica;
    @FXML
    private TextField txtValor;
    
    @FXML
    private Button btRemover;
    @FXML
    private Button btInserir;
    @FXML
    private Button btEditar;
    @FXML
    private Button btNova;
    
    @FXML
    private Label lbInfo;
    @FXML
    private Label lbRubrica;
    @FXML
    private Label lbValor;
    
    @FXML
    private TableView tbRubricaOrcamento;
    @FXML
    private TableColumn clRubrica;
    @FXML
    private TableColumn clValor;
    
    @FXML
    private ObservableList<RubricaOrcamento> rubricasOrcamento;
    @FXML
    private ObservableList<String> rubricas;
    
    private final float FCR_PERCENTAGEM = 0.10f;
    
    private int positionRubricaTable;
    private Validator validator;
    private ArrayList<String> errors;
    private RubricaOrcamento rubricaOrcamento;
    private RubricaOrcamentoDAO dao;
    private static int idOrc; //id do orcamento a manipular
    private static boolean alteracoes;

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        lbRubrica.setText("");
        lbValor.setText("");
    }

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        cbRubrica.setValue("");
        txtValor.setText("");
    }

    /**
     * Método que remove o style error
     */
    private void removerStyles() {
        cbRubrica.getStyleClass().remove("errorTextField");
        txtValor.getStyleClass().remove("errorTextField");
        
    }

    /**
     * Método que permite preparar a Stage para a inserção de uma nova rubrica
     * limpando os campos necessários
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {
        limparCampos();
        limparMSG();
        removerStyles();
        cbRubrica.setDisable(false);
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        btInserir.setDisable(false);
        refreshCBRubricas();
    }

    /**
     * Método que permite associar uma Rubrica a um Orçamento na table view e na
     * BD
     *
     * @param event evento
     */
    @FXML
    private void btInsertFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        errors = new ArrayList<String>();
        
        String rubrica = (String) cbRubrica.getValue();
        
        rubricaOrcamento = new RubricaOrcamento();
        rubricaOrcamento.setRubrica(rubrica);
        try {
            rubricaOrcamento.setValorEuros(Float.parseFloat(txtValor.getText()));
            rubricaOrcamento.setValorCentimos(MoneyConverter.getCentimos(rubricaOrcamento.getValorEuros()));
        } catch (NumberFormatException ex) {
            rubricaOrcamento.setValorEuros(-1.f);
        }
        rubricaOrcamento.setIdOrcamento(idOrc);
        errors = rubricaOrcamento.validate();
        
        if (errors.isEmpty()) {
            rubricasOrcamento.add(rubricaOrcamento); //adicionar na tableview
            dao = new RubricaOrcamentoDAO();
            dao.adicionar(rubricaOrcamento); //add na BD 
            dao.close();
            showSucessDialog("Rubrica associada com sucesso ao Orçamento !!!");
            refreshCBRubricas();
            updateFCRValue();
            //indica ao orçamento seguinte que houve alterações
            OrcamentoControllerPT1.alteracoes = true;
            new OrcamentoControllerPT2().setAlteracaoes(OrcamentoControllerPT1.alteracoes);
        } else {
            validator = new Validator();
            for (String error : errors) {
                switch (error) {
                    case "rubrica":
                        lbRubrica.setText(validator.getErrorRubrica());
                        cbRubrica.getStyleClass().add("errorTextField");
                        break;
                    case "valor":
                        lbValor.setText(validator.getErrorValor());
                        txtValor.getStyleClass().add("errorTextField");
                        break;
                }
            }
        }
    }

    /**
     * Método que permite desassociar uma Rubrica de um Orçamento na table view
     * e na BD
     *
     * @param event evento
     */
    @FXML
    private void btRemoveFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        
        dao = new RubricaOrcamentoDAO();
        dao.remover(cbRubrica.getValue().toString(), idOrc);//remover na bd
        dao.close();
        rubricasOrcamento.remove(getTableRubricaSeleccionada()); //apagar na table view
        showSucessDialog("Rubrica apagada com sucesso neste Orçamento !!!");
        refreshCBRubricas();
        updateFCRValue();
        OrcamentoControllerPT1.alteracoes = true;
        new OrcamentoControllerPT2().setAlteracaoes(OrcamentoControllerPT1.alteracoes);
    }

    /**
     * Método que permite editar uma Rubrica associada a um Orçamento na table
     * view e na BD
     *
     * @param event
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @FXML
    private void btEditFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        
        errors = new ArrayList<String>();
        
        rubricaOrcamento = new RubricaOrcamento();
        rubricaOrcamento.setRubrica((String) cbRubrica.getValue());
        try {
            rubricaOrcamento.setValorEuros(Float.parseFloat(txtValor.getText()));
            rubricaOrcamento.setValorCentimos(MoneyConverter.getCentimos(rubricaOrcamento.getValorEuros()));
        } catch (NumberFormatException ex) {
            rubricaOrcamento.setValorEuros(-1.f);
        }
        rubricaOrcamento.setIdOrcamento(idOrc);
        errors = rubricaOrcamento.validate();
        
        if (errors.isEmpty()) {
            rubricasOrcamento.set(positionRubricaTable, rubricaOrcamento); //adicionar na tableview
            dao = new RubricaOrcamentoDAO();
            dao.editar(rubricaOrcamento); //add na BD 
            dao.close();
            showSucessDialog("Rubrica editada com sucesso !!!");
            refreshCBRubricas();
            updateFCRValue();
            OrcamentoControllerPT1.alteracoes = true;
            new OrcamentoControllerPT2().setAlteracaoes(OrcamentoControllerPT1.alteracoes);
        } else {
            validator = new Validator();
            
            for (String error : errors) {
                switch (error) {
                    case "rubrica":
                        lbRubrica.setText(validator.getErrorRubrica());
                        cbRubrica.getStyleClass().add("errorTextField");
                        break;
                    case "valor":
                        lbValor.setText(validator.getErrorValor());
                        txtValor.getStyleClass().add("errorTextField");
                        break;
                }
            }
        }
    }

    /**
     * Método que permite avançar para a Stage seguinte onde se definem as
     * fracções pagantes
     *
     * @param event evento
     * @throws Exception
     */
    @FXML
    private void btNextFired(ActionEvent event) throws Exception {
        limparMSG();
        removerStyles();
        //envia para a próxima Stage se houve alterações ou não neste form
        new OrcamentoControllerPT2().setAlteracaoes(OrcamentoControllerPT1.alteracoes);
        new OrcamentoControllerPT2().setIdOrc(idOrc);//passo id do orcamento a manipular
        AddOrcamentoPT1.getStage().close();
        new AddOrcamentoPT2().start(new Stage());
    }

    /**
     * Fecha esta Stage e vai para a Stage anterior
     *
     * @param event
     * @throws Exception
     */
    @FXML
    private void btPreviousFired(ActionEvent event) throws Exception {
        limparMSG();
        removerStyles();
        AddOrcamentoPT1.getStage().close();
        new AddOrcamentoPT0().start(new Stage());
    }

    /**
     * Listener tableview rubricasOrcamento
     */
    private final ListChangeListener<RubricaOrcamento> selectorTableRubricas
            = new ListChangeListener<RubricaOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends RubricaOrcamento> c) {
                    verRubricaSeleccionadaDetails();
                }
            };

    /**
     * Mostra os detalhes de um rubrica seleccionada
     */
    private void verRubricaSeleccionadaDetails() {
        
        limparMSG();
        removerStyles();
        cbRubrica.setEditable(false);
        cbRubrica.setDisable(true);
        final RubricaOrcamento rubrica = getTableRubricaSeleccionada();
        positionRubricaTable = rubricasOrcamento.indexOf(rubrica);
        
        if (rubrica != null) {

            // poe os txtfield com os dados correspondentes
            cbRubrica.setValue(rubrica.getRubrica());
            txtValor.setText(Float.toString(rubrica.getValorEuros()));

            // poe os botoes num estado especifico 
            if (rubrica.getRubrica().equals("Fundo Comum de Reserva")) {
                btRemover.setDisable(true);
                btEditar.setDisable(true);
            } else {
                btRemover.setDisable(false);
                btEditar.setDisable(false);
            }
            
            btInserir.setDisable(true);
            
        }
    }

    /**
     * Devolve a Rubrica seleccionada na table view
     *
     * @return RubricaOrcamento
     */
    private RubricaOrcamento getTableRubricaSeleccionada() {
        if (tbRubricaOrcamento != null) {
            List<RubricaOrcamento> tabela = tbRubricaOrcamento.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final RubricaOrcamento rubricaSeleccionada = tabela.get(0);
                return rubricaSeleccionada;
            }
        }
        return null;
    }

    /**
     * Método para iniciar a tabela
     */
    private void iniciarTableView() {
        //fazer o binding entre as colunas e o respetivo campo do objeto Rubrica e iniciar a tableview
        clRubrica.setCellValueFactory(new PropertyValueFactory<RubricaOrcamento, String>("rubrica"));
        clValor.setCellValueFactory(new PropertyValueFactory<RubricaOrcamento, Float>("valorEuros"));
        rubricasOrcamento = FXCollections.observableArrayList();
        tbRubricaOrcamento.setItems(rubricasOrcamento);
    }

    /**
     * Inicia a classe OrçamentoControllerPT1.
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        OrcamentoControllerPT1.alteracoes = false;
        dao = new RubricaOrcamentoDAO();
        Orcamento orc = dao.getOrcamento(idOrc);
        lbInfo.setText("Orcamento: " + orc.getAno() + " da data: " + orc.getData() + " versao: " + orc.getVersao());

        // Inicializar a tabela
        this.iniciarTableView();

        // Botoes que nao se podem seleccionar
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        cbRubrica.setValue(""); //importante validar a rubrica
        dao = new RubricaOrcamentoDAO();
        if (!dao.getAllRubricasOrcamento(idOrc).isEmpty()) { // se existem dados na BD faz populate table view
            ArrayList<RubricaOrcamento> lista = new ArrayList<RubricaOrcamento>();
            lista = dao.getAllRubricasOrcamento(idOrc);
            for (RubricaOrcamento rubrica : lista) {
                rubricasOrcamento.add(rubrica); //faz populate na table view
            }
        } else {//senao insere todas as rubricas
            RubricaDAO rDAO = new RubricaDAO();
            rDAO.getAllRubricas();
            
            for (Rubrica r : rDAO.getAllRubricas()) {
                rubricaOrcamento = new RubricaOrcamento();
                rubricaOrcamento.setRubrica(r.getNome());
                rubricaOrcamento.setValorEuros(0.f);
                rubricaOrcamento.setValorCentimos(0);
                rubricaOrcamento.setIdOrcamento(idOrc);
                dao.adicionar(rubricaOrcamento);
                rubricasOrcamento.add(rubricaOrcamento);
            }
        }
        dao.close();
        
        if (!validarRemoverEditarOrcamento()) { // se nao pode editar orçamento
            disableComponents();
        } else {
            refreshCBRubricas();
            updateFCRValue();

            // Seleccionar as linhas na tabela fraccoes
            final ObservableList<RubricaOrcamento> tabelaRubricaSel = tbRubricaOrcamento.getSelectionModel().getSelectedItems();
            tabelaRubricaSel.addListener(selectorTableRubricas);
        }
        
    }

    /**
     * Método que verifica se já existe ou não uma rubrica na table view
     *
     * @param rubrica
     * @return se existe rubrica
     */
    public boolean existRubricaTableView(String rubrica) {
        boolean result = false;
        
        for (RubricaOrcamento ro : rubricasOrcamento) {
            if (ro.getRubrica().equals(rubrica)) {
                result = true;
            }
            
        }
        return result;
    }

    /**
     * Método que faz o resfresh dos values que a combo box poderá ter naquele
     * momento. Vai actualizando conforme as rubricas se vão inserindo na table
     * view e na BD
     */
    public void refreshCBRubricas() {
        //buscar todas as rubricas existem na BD para preencher a combo box
        rubricas = FXCollections.observableArrayList();
        RubricaDAO rubricaDAO = new RubricaDAO();
        ArrayList<Rubrica> lista = new ArrayList<Rubrica>();
        lista = rubricaDAO.getAllRubricas();
        for (Rubrica rubrica : lista) {
            if (!existRubricaTableView(rubrica.getNome())) {
                rubricas.add(rubrica.getNome()); //adiciona rubricas possiveis na observable list
            } else {
                rubricas.remove(rubrica.getNome());
            }
            
        }
        rubricaDAO.close();
        
        cbRubrica.setItems(rubricas); //preencher combo box com as rubricas possiveis de adicionar
    }

    /**
     * Método que calcula o Fundo Comum de Reserva
     *
     * @return
     */
    public float calcNewFCR() {
        float soma = 0;
        ArrayList<RubricaOrcamento> lista = new ArrayList<RubricaOrcamento>();
        dao = new RubricaOrcamentoDAO();
        lista = dao.getAllRubricasOrcamento(idOrc);
        for (RubricaOrcamento rubrica : lista) {
            if (!rubrica.getRubrica().equals("Fundo Comum de Reserva")) {
                soma += rubrica.getValorEuros();
            }
            
        }
        return MoneyConverter.round((float) (soma * FCR_PERCENTAGEM), 2);
    }

    /**
     * Método que retorno a posição da rubrica fundo comum de reserva na table
     * view
     *
     * @param rubrica
     * @return posição do Fundo Comum de Reserva
     */
    public int getpositionFCR(String rubrica) {
        int position = -1;
        for (RubricaOrcamento ro : rubricasOrcamento) {
            if (ro.getRubrica().equals("Fundo Comum de Reserva")) {
                position = rubricasOrcamento.indexOf(ro);
            }
        }
        return position;
    }

    /**
     * Método que edita o valor do Fundo Comum de Reserva na tabela e na BD
     */
    public void updateFCRValue() {
        int position = getpositionFCR("Fundo Comum de Reserva");
        rubricaOrcamento = new RubricaOrcamento();
        rubricaOrcamento.setRubrica("Fundo Comum de Reserva");
        rubricaOrcamento.setValorEuros(calcNewFCR());
        rubricaOrcamento.setValorCentimos(MoneyConverter.getCentimos(rubricaOrcamento.getValorEuros()));
        rubricaOrcamento.setIdOrcamento(idOrc);
        rubricasOrcamento.set(position, rubricaOrcamento);
        dao = new RubricaOrcamentoDAO();
        dao.editar(rubricaOrcamento);
        
    }

    /**
     * Recebe o id do Orçamento a manipular escolhido no formulario anterior
     *
     * @param id id do Orçamento a manipular
     */
    public void setIdOrc(int id) {
        OrcamentoControllerPT1.idOrc = id;
        //System.out.println("aqui" + idOrc);
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
        btRemover.setDisable(true);
        btInserir.setDisable(true);
        btEditar.setDisable(true);
        btNova.setDisable(true);
        
        tbRubricaOrcamento.setDisable(true);
        tbRubricaOrcamento.setEditable(false);
        
        txtValor.setDisable(true);
        txtValor.setEditable(false);
        
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
        Window owner = AddOrcamentoPT1.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
    
}
