package controller;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import application.GerarAvisoExtraPT1;
import application.GerarAvisoPT1;
import application.GerarAvisoPT2;
import dao.AvisoDebitoDAO;
import dao.FraccaoOrcamentoDAO;
import dao.FraccaoQuotasOrcamentoDAO;
import dao.OrcamentoDAO;
import dao.ReciboDAO;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.AvisoDebito;
import model.FraccaoOrcamento;
import model.Orcamento;

/**
 * Classe GerarAvisoControllerPT2 está responsável pelo controlo dos eventos
 * relativos á Stage que permite gerar Avisos de débitos
 *
 * @author Luís Sousa - 8090228
 */
public class GerarAvisoControllerPT2 implements Initializable {

    @FXML
    private Button btGerar;
    @FXML
    private Label lbOrcamentoInfo;
    @FXML
    private ListView lvFraccoes;
    @FXML
    private ListView lvMeses;
    @FXML
    private ListView lvLog;

    @FXML
    private TextField txtDiaLimite;

    private static int idOrc; //id do orcamento
    private Orcamento or;

    private ObservableList<String> meses; //prencher meses
    private ObservableList<String> mesSel;

    private ObservableList<String> fraccoes; //preencher fraccoes
    private ObservableList<String> fraccaoSel;

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Calendar cal = Calendar.getInstance();

    /**
     * Método que permite ir para a Stage anterior, a stage de escolha do
     * orçamento a gerar avisos
     *
     * @param event evento
     * @throws Exception excepção ao abrir a stage
     */
    @FXML
    private void btPreviousFired(ActionEvent event) throws Exception {
        GerarAvisoPT2.getStage().close();
        new GerarAvisoPT1().start(new Stage());
    }

    /**
     * Método que permite ir para a Stage de gerar avisos extra(sub orçamento)
     * para um determinado orçamento.
     *
     * @param event evento
     * @throws Exception excepção ao abrir a stage
     */
    @FXML
    private void btGerarExtraFired(ActionEvent event) throws Exception {
        new GerarAvisoExtraControllerPT1().setIdOrc(idOrc);
        GerarAvisoPT2.getStage().close();
        new GerarAvisoExtraPT1().start(new Stage());
    }

    /**
     * Método que permite gerar aviso débito
     *
     * @param event evento
     */
    @FXML
    private void btGerarFired(ActionEvent event) {
        lvLog.getItems().clear();
        int day;

        try {
            day = Integer.parseInt(txtDiaLimite.getText().trim());
        } catch (NumberFormatException ex) {
            day = 0;
        }

        if (!mesSel.isEmpty() && !fraccaoSel.isEmpty() && !txtDiaLimite.getText().trim().equals("")) {
            if (day > 0 && day <= 31) {
                AvisoDebitoDAO dao = new AvisoDebitoDAO();
                AvisoDebito aviso = new AvisoDebito();

                for (String s1 : fraccaoSel) {
                    for (String s : mesSel) {
                        ReciboDAO rDAO = new ReciboDAO();
                        aviso.setIdOrcamento(idOrc);
                        aviso.setCodFraccao(s1);
                        aviso.setData(dateFormat.format(cal.getTime()));
                        aviso.setMes(getMonthNumber(s));
                        aviso.setDescricao("Aviso de Débito relativo a " + s + " do Orcamento: " + getOrcamentoInfo(idOrc).getAno());
                        String dataL = txtDiaLimite.getText() + "/" + aviso.getMes() + "/" + getOrcamentoInfo(idOrc).getAno();
                        aviso.setDataLimite(dataL);

                        if (s.equals("Dezembro")) { // fazer o acerto das mensalidades em dezembro (pagar mais ou até menos conforme)
                            FraccaoQuotasOrcamentoDAO fqoDAO = new FraccaoQuotasOrcamentoDAO();
                            int mensalidade = fqoDAO.getMensalidade(s1, idOrc);
                            int valorAnual = fqoDAO.getValorAnual(s1, idOrc);
                            aviso.setValorPagar(valorAnual - (mensalidade * 11));
                            fqoDAO.close();
                        } else {
                            FraccaoQuotasOrcamentoDAO fqoDAO = new FraccaoQuotasOrcamentoDAO();
                            aviso.setValorPagar(fqoDAO.getMensalidade(s1, idOrc));
                            fqoDAO.close();
                        }

                        aviso.setTotalEmDivida(aviso.getValorPagar() + dao.somaValorPagarAteMomentoAvisos(aviso.getCodFraccao(), idOrc) - rDAO.somaPagoAteMomento(aviso.getCodFraccao(), idOrc));
                        aviso.setResolvido(0);
                        //aviso.setIdSubOrcamento(0);
                        rDAO.close();

                        if (!dao.exist(aviso)) {
                            dao.adicionarAvisoDebito(aviso);
                            lvLog.getItems().add(aviso.getDescricao() + " da fração " + aviso.getCodFraccao() + " [OK - Aviso Gerado Com SUCESSO]");
                        } else {
                            lvLog.getItems().add(aviso.getDescricao() + " da fração " + aviso.getCodFraccao() + " [IGNORADO - Aviso já existente]");
                        }

                    }

                }
                lvFraccoes.getSelectionModel().clearSelection();

                lvMeses.getSelectionModel().clearSelection();
                dao.close();
            } else {
                lvLog.getItems().add("Dia Inválido !!!");
            }
        } else {
            lvLog.getItems().add("Selecione pelo menos uma fração e um mês e um dia limite para o pagamento !!!");
        }
        txtDiaLimite.setText("");
    }

    /**
     * Método listener que detecta os meses seleccionados pelo utilizador
     */
    private final ListChangeListener<String> mesesSelecionados
            = new ListChangeListener<String>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends String> c) {
                    mesSel = lvMeses.getSelectionModel().getSelectedItems();
                }
            };

    /**
     * Método listener que detecta as fracções seleccionados pelo utilizador
     */
    private final ListChangeListener<String> fraccoesSelecionadas
            = new ListChangeListener<String>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends String> c) {
                    fraccaoSel = lvFraccoes.getSelectionModel().getSelectedItems();
                }
            };

    /**
     * Método que inicia a classe GerarAvisoControllerPT2
     *
     * @param url url
     * @param rb resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lvMeses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvFraccoes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        fraccaoSel = FXCollections.observableArrayList();
        mesSel = FXCollections.observableArrayList();

        or = new Orcamento();
        try {
            or = getOrcamentoInfo(idOrc);

            lbOrcamentoInfo.setText("Orcamento : " + or.getAno());
            initListBoxMeses();
            initFraccoesOrcamento();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(GerarAvisoControllerPT2.class.getName()).log(Level.SEVERE, null, ex);
        }

        //listener para atualizar meses selecionados
        lvMeses.getSelectionModel().getSelectedItems().addListener(mesesSelecionados);

        //listener para atualizar fraccoes selecionadas
        lvFraccoes.getSelectionModel().getSelectedItems().addListener(fraccoesSelecionadas);

    }

    /**
     * Método que recebe o id do Orçamento a gerar avisos
     *
     * @param id identificação de um orçamento
     */
    public void setIdOrc(int id) {
        GerarAvisoControllerPT2.idOrc = id;
    }

    /**
     * Método que permite obter toda a informação de um Orçamento através do seu
     * id
     *
     * @param idOrcamento identificador de um orçamento
     * @return Orcamento
     */
    private Orcamento getOrcamentoInfo(int idOrcamento) {

        OrcamentoDAO dao = new OrcamentoDAO();
        Orcamento orc = new Orcamento();
        orc = dao.getOrcamento(idOrcamento);
        return orc;
    }

    /**
     * Método que preenche a list view com todos os mesês
     */
    private void initListBoxMeses() {
        meses = FXCollections.observableArrayList();

        meses.add("Janeiro");
        meses.add("Fevereiro");
        meses.add("Março");
        meses.add("Abril");
        meses.add("Maio");
        meses.add("Junho");
        meses.add("Julho");
        meses.add("Agosto");
        meses.add("Setembro");
        meses.add("Outubro");
        meses.add("Novembro");
        meses.add("Dezembro");
        lvMeses.setItems(meses);
    }

    /**
     * Método que preenche a list view com todas as fracções pagantes daquele
     * orçamento
     *
     * @throws ClassNotFoundException erro de classe
     * @throws SQLException erro de sql
     */
    private void initFraccoesOrcamento() throws ClassNotFoundException, SQLException {
        FraccaoOrcamentoDAO dao = new FraccaoOrcamentoDAO();
        fraccoes = FXCollections.observableArrayList();

        for (FraccaoOrcamento fr : dao.getAllFraccoesOrcamento(idOrc)) {
            fraccoes.add(fr.getFraccao());
        }
        lvFraccoes.setItems(fraccoes);
    }

    /**
     * Método que retorna o número de um determindo mês
     *
     * @param month mês
     * @return numero daquele mês
     */
    private int getMonthNumber(String month) {
        int number;

        switch (month) {
            case "Janeiro":
                number = 1;
                break;
            case "Fevereiro":
                number = 2;
                break;
            case "Março":
                number = 3;
                break;
            case "Abril":
                number = 4;
                break;
            case "Maio":
                number = 5;
                break;
            case "Junho":
                number = 6;
                break;
            case "Julho":
                number = 7;
                break;
            case "Agosto":
                number = 8;
                break;
            case "Setembro":
                number = 9;
                break;
            case "Outubro":
                number = 10;
                break;
            case "Novembro":
                number = 11;
                break;
            case "Dezembro":
                number = 12;
                break;
            default:
                number = 0;
                break;

        }
        return number;
    }

}
