package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.AddOrcamentoPT0;
import application.AddOrcamentoPT3;
import application.AddOrcamentoPT4;
import application.DespesaPT2;
import dao.FraccaoDAO;
import dao.FraccaoOrcamentoDAO;
import dao.FraccaoQuotasOrcamentoDAO;
import dao.FraccaoRubricaOrcamentoDAO;
import dao.ObsOrcamentoDAO;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Fraccao;
import model.FraccaoQuotasOrcamento;
import model.ObsOrcamento;
import model.Orcamento;
import model.RubricaOrcamento;
import util.MoneyConverter;
import validation.Validator;

/**
 * Classe OrçamentoControllerPT4 está responsável por apresentar as mensalidades
 * calculadas para cada fracção pagante naquele orçamento
 *
 * @author Luís Sousa - 8090228
 */
public class OrcamentoControllerPT4 implements Initializable {

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
    private Label lbDescricao;

    @FXML
    private Label lbInfo;

    @FXML
    private ComboBox cbFraccao;

    @FXML
    private TextArea txtaObs;

    @FXML
    private Button btRemover;
    @FXML
    private Button btInserir;
    @FXML
    private Button btEditar;
    @FXML
    private Button btNova;

    @FXML
    private ObservableList<FraccaoQuotasOrcamento> fraccoesQuotasOrcamento;

    private FraccaoQuotasOrcamento fraccaoQuotasOrcamento;
    private FraccaoQuotasOrcamentoDAO dao;


    /*------------------------------------------------------------------------*/
    @FXML
    private TableView tbObservacoes;
    @FXML
    private TableColumn clFraccaoObs;
    @FXML
    private TableColumn clObs;

    @FXML
    private ObservableList<ObsOrcamento> observacoesOrcamento;
    @FXML
    private ObservableList<String> fraccoes;

    private ObsOrcamento obs;
    private ObsOrcamentoDAO obsDao;
    private int positionObsTable;
    private static int idOrc; //id do orcamento a manipular
    private static boolean alteracoes;

    /**
     * Método que limpa os campos de introdução de dados
     */
    private void limparCampos() {
        cbFraccao.setValue("");
        txtaObs.setText("");
        lbDescricao.setText("");

    }

    /**
     * Método que permite limpar as mensagens de erro após a introdução de dados
     */
    private void limparMSG() {
        lbDescricao.setText("");
    }

    /**
     * Método que remove o style errors
     */
    private void removerStyles() {
        lbDescricao.getStyleClass().remove("errorTextField");
    }

    /**
     * Método que permite preparar a Stage para a inserção de uma nova
     * observação associada a uma fracção
     *
     * @param event evento
     */
    @FXML
    private void btNewFired(ActionEvent event) {
        limparCampos();
        limparMSG();
        removerStyles();
        cbFraccao.setDisable(false);
        btRemover.setDisable(true);
        btEditar.setDisable(true);
        btInserir.setDisable(false);
    }

    /**
     * Método que permite inserir uma nova observação
     *
     * @param event evento
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @FXML
    private void btInsertFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        obs = new ObsOrcamento();
        obs.setFraccao(cbFraccao.getValue().toString());
        obs.setIdOrc(idOrc);
        obs.setObs(txtaObs.getText().trim());

        ArrayList<String> errors = new ArrayList<>();
        errors = obs.validate();

        if (errors.isEmpty()) {
            observacoesOrcamento.add(obs); //inserir na tabela
            obsDao = new ObsOrcamentoDAO();
            obsDao.adicionarObs(obs);
            obsDao.close();
            refreshCBFraccoes();
            limparCampos();
            showSucessDialog("Observação inserida com sucesso!!!");
        } else {
            Validator vl = new Validator();

            for (String error : errors) {
                if (error.equals("observacao")) {
                    lbDescricao.setText(vl.getErrorObs());
                    lbDescricao.getStyleClass().add("errorTextField");
                }
            }
        }
    }

    /**
     * Método que permite remover uma nova observação
     *
     * @param event evento
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @FXML
    private void btRemoveFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        obsDao = new ObsOrcamentoDAO();
        obsDao.removerOrcamento(getTableObsSeleccionada());
        obsDao.close();
        observacoesOrcamento.remove(getTableObsSeleccionada());
        refreshCBFraccoes();
        limparCampos();
        showSucessDialog("Observação removida com sucesso !!!");
    }

    /**
     * Método que permite editar uma nova observação
     *
     * @param event evento
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @FXML
    private void btEditFired(ActionEvent event) throws ClassNotFoundException, SQLException {
        limparMSG();
        removerStyles();
        obs = new ObsOrcamento();
        obs.setFraccao(cbFraccao.getValue().toString());
        obs.setIdOrc(idOrc);
        obs.setObs(txtaObs.getText());

        ArrayList<String> errors = new ArrayList<>();
        errors = obs.validate();

        if (errors.isEmpty()) {
            observacoesOrcamento.set(positionObsTable, obs); //editar na tabela
            obsDao = new ObsOrcamentoDAO();
            obsDao.editarObs(obs); //editar na BD
            obsDao.close();
            refreshCBFraccoes();
            limparCampos();
            showSucessDialog("Observação editada com sucesso !!!");
        } else {
            Validator vl = new Validator();
            for (String error : errors) {
                if (error.equals("observacao")) {
                    lbDescricao.setText(vl.getErrorObs());
                    lbDescricao.getStyleClass().add("errorTextField");
                }
            }

        }
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
        new OrcamentoControllerPT3().setAlteracaoes(false);
        AddOrcamentoPT4.getStage().close();
        new AddOrcamentoPT3().start(new Stage());

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
        AddOrcamentoPT4.getStage().close();
        new AddOrcamentoPT0().start(new Stage());
    }

    /**
     * Listener tableview fraccoesOrcamento
     */
    private final ListChangeListener<ObsOrcamento> selectorTableObservacoes
            = new ListChangeListener<ObsOrcamento>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends ObsOrcamento> c) {

                    if (observacoesOrcamento.size() == 1) {
                        tableSizeOne();
                    } else {
                        verObservacaoSeleccionadaDetails();
                    }
                }
            };

    /**
     * Mostra os detalhes da observação seleccionada
     */
    private void verObservacaoSeleccionadaDetails() {

        cbFraccao.setEditable(false);
        cbFraccao.setDisable(true);
        final ObsOrcamento ob = getTableObsSeleccionada();
        positionObsTable = observacoesOrcamento.indexOf(ob);

        if (ob != null) {
            // poe os txtfield com os dados correspondentes
            cbFraccao.setValue(ob.getFraccao());
            txtaObs.setText(ob.getObs());

            // poe os botoes num estado especifico 
            btRemover.setDisable(false);
            btEditar.setDisable(false);
            btInserir.setDisable(true);
        }
    }

    /**
     * Devolve uma observação
     *
     * @return ObsOrcamento
     */
    private ObsOrcamento getTableObsSeleccionada() {
        if (tbObservacoes != null) {
            List<ObsOrcamento> tabela = tbObservacoes.getSelectionModel().getSelectedItems();
            if (tabela.size() == 1) {
                final ObsOrcamento obsSeleccionada = tabela.get(0);
                return obsSeleccionada;
            }
        }
        return null;
    }

    /**
     * Método quer permite iniciar a tabela das mensalidades
     */
    private void iniciarTableView() {
        //fazer o binding entre as colunas e o respetivo campo do objeto e iniciar a tableview
        clFraccao.setCellValueFactory(new PropertyValueFactory<FraccaoQuotasOrcamento, String>("fraccao"));
        clNome.setCellValueFactory(new PropertyValueFactory<FraccaoQuotasOrcamento, String>("nome"));
        clPerm.setCellValueFactory(new PropertyValueFactory<FraccaoQuotasOrcamento, Float>("perm"));
        clMensal.setCellValueFactory(new PropertyValueFactory<FraccaoQuotasOrcamento, Float>("mensalEuros"));
        clAnual.setCellValueFactory(new PropertyValueFactory<FraccaoQuotasOrcamento, Float>("anualEuros"));
        clAno.setCellValueFactory(new PropertyValueFactory<FraccaoQuotasOrcamento, Integer>("ano"));
        fraccoesQuotasOrcamento = FXCollections.observableArrayList();
        tbFraccaoQuotasOrcamento.setItems(fraccoesQuotasOrcamento);
    }

    /**
     * Método que permite iniciar a tabela das observações
     */
    private void iniciarTableViewObservations() {
        clFraccaoObs.setCellValueFactory(new PropertyValueFactory<ObsOrcamento, String>("fraccao"));
        clObs.setCellValueFactory(new PropertyValueFactory<ObsOrcamento, String>("obs"));
        observacoesOrcamento = FXCollections.observableArrayList();
        tbObservacoes.setItems(observacoesOrcamento);
    }

    /**
     * Initializes the OrcamentoControllerPT4 class.
     *
     * @param url url
     * @param rb resourcebundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        dao = new FraccaoQuotasOrcamentoDAO();
        Orcamento orc = dao.getOrcamento(idOrc);
        lbInfo.setText("Orcamento: " + orc.getAno() + " da data: " + orc.getData() + " versao: " + orc.getVersao());

        // Inicializar a tabela
        this.iniciarTableView();

        this.iniciarTableViewObservations();

        btInserir.setDisable(true);
        btEditar.setDisable(true);
        btRemover.setDisable(true);

        /* ++++++++++++++++++++++++++++++++++++++--------------------------- */
        try {
            dao = new FraccaoQuotasOrcamentoDAO();
            ArrayList<Fraccao> fraccoesPagantes = new ArrayList<Fraccao>();
            fraccoesPagantes = getAllFraccoesPagantes(idOrc);
            if (dao.haveData(idOrc) && alteracoes == false) {
                //System.out.println("nao houve alterações");
                for (Fraccao fr : fraccoesPagantes) {
                    // System.out.println("entrou aqui");
                    for (FraccaoQuotasOrcamento fco : dao.getAllFraccoesQuotasOrcamento(idOrc)) {
                        //System.out.println("entrou aqui 2");
                        if (fr.getCod().equals(fco.getFraccao())) {
                            //System.out.println("entrou aqui 3");
                            fco.setPerm(fr.getPermilagem());
                            fco.setNome(fr.getNome());
                            fco.setAno(dao.getOrcamento(idOrc).getAno());
                            fraccoesQuotasOrcamento.add(fco);
                        }
                    }
                }

            } else if (dao.haveData(idOrc) && alteracoes == true) {
                //System.out.println("removeu tudinho");
                dao.removerAll(idOrc);
                fraccoesQuotasOrcamento.clear();
                calcAndSaveResults(idOrc);
            } else {
                //System.out.println("vazio");
                calcAndSaveResults(idOrc);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            showErrorDialog(ex.getMessage());
        }
        dao.close();

        /*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        // Seleccionar as linhas na tabela fraccoes
        final ObservableList<ObsOrcamento> tabelaObservacoesSel = tbObservacoes.getSelectionModel().getSelectedItems();
        tabelaObservacoesSel.addListener(selectorTableObservacoes);

        obsDao = new ObsOrcamentoDAO();
        if (obsDao.haveData(idOrc)) {
            for (ObsOrcamento ob : obsDao.getObservacoes(idOrc)) {
                observacoesOrcamento.add(ob);
            }
        }
        obsDao.close();
        refreshCBFraccoes();
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
        FraccaoOrcamentoDAO frOrcDao = new FraccaoOrcamentoDAO();
        ArrayList<Fraccao> novaLista = new ArrayList<>();

        novaLista = frOrcDao.getFracoesPagantesOrcamento(idO);
        frOrcDao.close();

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
    public ArrayList<RubricaOrcamento> getRubricas(String codFraccao, int idO) throws ClassNotFoundException, SQLException {
        FraccaoRubricaOrcamentoDAO froDAO = new FraccaoRubricaOrcamentoDAO();

        ArrayList<RubricaOrcamento> newRubricas = new ArrayList<RubricaOrcamento>();

        newRubricas = froDAO.getRubricaFracaoOrcamento(codFraccao, idO);
        froDAO.close();

        return newRubricas;
    }

    /**
     * Método que devolve uma lista de fraccoes que têm uma determinada rubrica
     * num orcamento
     *
     * @param nomeRubrica nome de um rubrica
     * @param idO identificação de um Orcamento
     * @return lista com fracções
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public ArrayList<Fraccao> getFraccoes(String nomeRubrica, int idO) throws ClassNotFoundException, SQLException {

        FraccaoRubricaOrcamentoDAO froDAO = new FraccaoRubricaOrcamentoDAO();
        ArrayList<Fraccao> newFraccoes = new ArrayList<Fraccao>();
        newFraccoes = froDAO.getFracoesRubricaOrcamento(nomeRubrica, idO);
        froDAO.close();
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
        int ano = dao.getOrcamento(idO).getAno();
        ArrayList<Fraccao> fraccoesPagantes = new ArrayList<Fraccao>();
        ArrayList<Fraccao> fraccoesRubrica = new ArrayList<Fraccao>();
        fraccoesPagantes = getAllFraccoesPagantes(idO);
        String codPagante;
        String rubrica;
        float resultado = 0;
        float valorRubrica;
        float somaPermilagemRubrica = 0;// soma das permilagens de uma rubrica
        ArrayList<RubricaOrcamento> rubricas = new ArrayList<RubricaOrcamento>();

        for (Fraccao f2 : fraccoesPagantes) {
            System.out.println("Fraccao a tratar" + f2.getCod());
            resultado = 0;
            System.out.println("Resultado Inicial" + resultado);
            codPagante = f2.getCod();//fraccao que vai ser usada para os calculos
            rubricas = this.getRubricas(codPagante, idO); //rubricas para a fraccao
            for (RubricaOrcamento rub : rubricas) {//percorrer rubricas da fraccao pagante
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
            fraccaoQuotasOrcamento = new FraccaoQuotasOrcamento();

            fraccaoQuotasOrcamento.setFraccao(f2.getCod());
            fraccaoQuotasOrcamento.setNome(f2.getNome());
            fraccaoQuotasOrcamento.setPerm(f2.getPermilagem());
            float resultadoformat = round(resultado, 2);
            fraccaoQuotasOrcamento.setMensalEuros(round((resultado / 12), 2));
            fraccaoQuotasOrcamento.setAnualEuros(resultadoformat);
//            System.out.println("EUROOOOOOS" + fraccaoQuotasOrcamento.getMensalEuros());
//            System.out.println("CENTIMOOOOOOOOOOS" + fraccaoQuotasOrcamento.getAnualEuros());
            fraccaoQuotasOrcamento.setMensalCentimos(MoneyConverter.getCentimos(fraccaoQuotasOrcamento.getMensalEuros()));
            fraccaoQuotasOrcamento.setAnualCentimos(MoneyConverter.getCentimos(fraccaoQuotasOrcamento.getAnualEuros()));

            fraccaoQuotasOrcamento.setAno(ano);
            fraccaoQuotasOrcamento.setIdOrcamento(idO);
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
     * Método que permite verificar se existe aquela fracção na tabela
     * observações
     *
     * @param fraccao
     * @return
     */
    public boolean existFraccaoTableView(String fraccao) {
        boolean result = false;

        for (ObsOrcamento ob : observacoesOrcamento) {
            if (ob.getFraccao().equals(fraccao)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Refresh combo box com as fracções que ainda não têm observações
     */
    public void refreshCBFraccoes() {
        //buscar todas as fraccoes existem na BD para preencher a combo box
        fraccoes = FXCollections.observableArrayList();
        FraccaoDAO fraccaoDAO = new FraccaoDAO();
        for (Fraccao fraccao : fraccaoDAO.getAllFraccoes()) {
            if (!existFraccaoTableView(fraccao.getCod())) {
                fraccoes.add(fraccao.getCod()); //adiciona fraccoes possiveis na observable list
            } else {
                fraccoes.remove(fraccao.getCod());
            }
        }
        fraccaoDAO.close();

        cbFraccao.setItems(fraccoes); //preencher combo box com as fraccoes possiveis de adicionar
    }

    /**
     * Método que recebe o id do Orçamento a manipular
     *
     * @param id identificador do Orçamento
     */
    public void setIdOrc(int id) {
        OrcamentoControllerPT4.idOrc = id;
        //System.out.println("aqui" + idOrc);
    }

    /**
     * Evento que permite ver os detalhes quando a tabela só tem um registo
     */
    private void tableSizeOne() {
        tbObservacoes.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verObservacaoSeleccionadaDetails();
            }
        });
    }

    /**
     * Recebe dos forms anteriores se houve alterações ou não
     *
     * @param result resultado das alterações
     */
    public void setAlteracaoes(boolean result) {
        OrcamentoControllerPT4.alteracoes = result;
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
        Window owner = AddOrcamentoPT4.getStage();
        Alert dlg = new Alert(type, "");
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.initOwner(owner);
        return dlg;
    }
}
