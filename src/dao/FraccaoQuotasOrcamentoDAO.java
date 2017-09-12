package dao;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import connection.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.FraccaoQuotasOrcamento;
import model.Orcamento;
import util.MoneyConverter;

/**
 * Esta classe permite efectuar operações na BD sobre as Quotas de uma Fracção
 * relativamente a um determinado Orçamento .
 *
 * @author Luís Sousa - 8090228
 */
public class FraccaoQuotasOrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe FraccaoQuotasorcamentoDAO Aqui é feita a ligação com
     * a BD.
     *
     */
    public FraccaoQuotasOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar as quotas de uma fracção para um Orcamento na BD
     *
     * @param fqo objeto FraccaoQuotasOrcamento
     */
    public void adicionar(FraccaoQuotasOrcamento fqo) {

        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO FraccaoQuotasOrcamento (fraccao,qMensal,qAnual,idOrcamento)"
                    + "VALUES (?,?,?,?)";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, fqo.getFraccao());
            this.stmt.setInt(2, fqo.getMensalCentimos());
            this.stmt.setInt(3, fqo.getAnualCentimos());
            this.stmt.setInt(4, fqo.getIdOrcamento());

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método para remover da BD todas as quotas relativamente a um Orcamento
     *
     * @param idOrcamento identificador de um Orçamento
     */
    public void removerAll(int idOrcamento) {
        try {
            // Sql para adicionar usuario
            String sql = "delete from FraccaoQuotasOrcamento where idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrcamento);
            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todas as quotas independentemente do Orçamento
     *
     * @return todas as quotas independentemente do Orçamento
     */
    public ArrayList<FraccaoQuotasOrcamento> getAllFraccoesQuotasOrcamento() {
        ArrayList<FraccaoQuotasOrcamento> lista = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM FraccaoQuotasOrcamento");
            while (rs.next()) {
                FraccaoQuotasOrcamento fqo = new FraccaoQuotasOrcamento();
                fqo.setFraccao(rs.getString("fraccao"));
                fqo.setMensalCentimos(rs.getInt("qMensal"));
                fqo.setAnualCentimos(rs.getInt("qAnual"));
                fqo.setMensalEuros(MoneyConverter.getEuros(rs.getInt("qMensal")));
                fqo.setAnualEuros(MoneyConverter.getEuros(rs.getInt("qAnual")));
                fqo.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(fqo);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoQuotasOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todas as quotas relativamente a um Orçamento
     *
     * @param idOrc identificador do Orçamento
     * @return todas as quotas relativamente a um Orçamento
     */
    public ArrayList<FraccaoQuotasOrcamento> getAllFraccoesQuotasOrcamento(int idOrc) {
        ArrayList<FraccaoQuotasOrcamento> lista = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM FraccaoQuotasOrcamento WHERE idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                FraccaoQuotasOrcamento fqo = new FraccaoQuotasOrcamento();
                fqo.setFraccao(rs.getString("fraccao"));
                fqo.setMensalCentimos(rs.getInt("qMensal"));
                fqo.setAnualCentimos(rs.getInt("qAnual"));
                fqo.setMensalEuros(MoneyConverter.getEuros(rs.getInt("qMensal")));
                fqo.setAnualEuros(MoneyConverter.getEuros(rs.getInt("qAnual")));
                fqo.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(fqo);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoQuotasOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve toda a informação de um Orcamento através do seu ID
     *
     * @param id identificação de um Orcamento ID
     * @return Orcamento
     */
    public Orcamento getOrcamento(int id) {
        Orcamento orcamento = new Orcamento();
        OrcamentoDAO dao = new OrcamentoDAO();
        orcamento = dao.getOrcamento(id);
        dao.close();
        return orcamento;
    }

    /**
     * Método que devolve a mensalidade/quota a pagar por uma fracção para um
     * determinado Orçamento
     *
     * @param codFraccao código da Fracção
     * @param idOrc identificador do Orçamento
     * @return quota
     */
    public int getMensalidade(String codFraccao, int idOrc) {
        int result = 0;
        try {
            ResultSet rs;
            String sql = "SELECT qMensal FROM FraccaoQuotasOrcamento WHERE idOrcamento=? and fraccao=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            this.stmt.setString(2, codFraccao);
            rs = this.stmt.executeQuery();
            result = rs.getInt("qMensal");
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoQuotasOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    
     /**
     * Método que devolve o valor anual  a pagar por uma fracção para um
     * determinado Orçamento
     *
     * @param codFraccao código da Fracção
     * @param idOrc identificador do Orçamento
     * @return quota
     */
    public int getValorAnual(String codFraccao, int idOrc) {
        int result = 0;
        try {
            ResultSet rs;
            String sql = "SELECT qAnual FROM FraccaoQuotasOrcamento WHERE idOrcamento=? and fraccao=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            this.stmt.setString(2, codFraccao);
            rs = this.stmt.executeQuery();
            result = rs.getInt("qAnual");
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoQuotasOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Método que verifica se eixtem dados relativamente a um Orçamento
     *
     * @param idOrcamento
     * @return se existe dados na BD em relação a um determinado Orçamento
     */
    public boolean haveData(int idOrcamento) {
        int rows = 0;
        try {
            ResultSet rs;
            String sql = "SELECT * FROM FraccaoQuotasOrcamento WHERE idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrcamento);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                rows++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoQuotasOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rows > 0;
    }

    /**
     * Método que permite verificar se existe algum registo com o id de um
     * determinado orçamento na tabela FraccaoRubricaOrcamento
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existOrcamento(int id) {
        int rows = 0;
        try {
            String sql = "SELECT * FROM FraccaoQuotasOrcamento WHERE idOrcamento=?";
            ResultSet rs;
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();

            while (rs.next()) {
                rows++;
            }

        } catch (SQLException ex) {
            Logger.getLogger(CondominioDAO.class.getName()).log(Level.SEVERE, null, ex);
        } // set timeout to 30 sec.
        return rows > 0;
    }

    /**
     * Fechar a ligação á BD
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // connection close failed.
            System.err.println(e);
        }
    }

}
