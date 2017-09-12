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
import model.Orcamento;

/**
 * Esta classe permite efectuar operações na BD sobre um Orçamento.
 *
 * @author Luís Sousa - 8090228
 */
public class OrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe OrcamentoDAO aqui é feita a ligação com a BD.
     *
     */
    public OrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar dados do orcamento na BD
     *
     * @param orcamento objeto com os dados de um orcamento
     */
    public void adicionarOrcamento(Orcamento orcamento) {
        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO Orcamento (versao,ano, data, estado)"
                    + "VALUES (?,?,?,?)";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, orcamento.getVersao());
            this.stmt.setInt(2, orcamento.getAno());
            this.stmt.setString(3, orcamento.getData());
            this.stmt.setString(4, orcamento.getEstado());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método para adicionar dados de um Orçamento na BD
     *
     * @param newOrcamento objeto com os dados de um orcamento
     * @param id identificador de um orcamento
     */
    public void editarOrcamento(Orcamento newOrcamento, int id) {
        try {
            String sql = "update Orcamento set versao=?, ano=?, data=?, estado=? where id=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, newOrcamento.getVersao());
            this.stmt.setInt(2, newOrcamento.getAno());
            this.stmt.setString(3, newOrcamento.getData());
            this.stmt.setString(4, newOrcamento.getEstado());
            this.stmt.setInt(5, id);
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Metodo para apagar um orcamento da BD
     *
     * @param id identificador de um orcamento
     */
    public void removerOrcamento(int id) {
        try {

            String sql = "delete from Orcamento where id=?";
            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setInt(1, id);

            this.stmt.execute();
            this.stmt.close();

            String sql2 = "delete from RubricaOrcamento where idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql2);

            this.stmt.setInt(1, id);

            this.stmt.execute();
            this.stmt.close();

            String sql3 = "delete from FraccaoOrcamento where idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql3);

            this.stmt.setInt(1, id);

            this.stmt.execute();
            this.stmt.close();

            String sql4 = "delete from FraccaoRubricaOrcamento where idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql4);

            this.stmt.setInt(1, id);

            this.stmt.execute();
            this.stmt.close();

            String sql5 = "delete from FraccaoQuotasOrcamento where idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql5);

            this.stmt.setInt(1, id);

            this.stmt.execute();
            this.stmt.close();

            String sql6 = "delete from ObservacaoOrcamento where idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql6);

            this.stmt.setInt(1, id);

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todos um orçamentos/propostas existem para um ano
     *
     * @param ano ano de um Orçamento
     * @return uma lista de Orçamentos
     */
    public ArrayList<Orcamento> getAllOrcamentos(int ano) {
        ArrayList<Orcamento> lista = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM Orcamento where ano=" + ano);
            while (rs.next()) {
                Orcamento orcamento = new Orcamento();
                orcamento.setId(rs.getInt("id"));
                orcamento.setVersao(rs.getString("versao"));
                orcamento.setAno(rs.getInt("ano"));
                orcamento.setData(rs.getString("data"));
                orcamento.setEstado(rs.getString("estado"));
                lista.add(orcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todos orçamentos/propostas
     *
     * @return uma lista de Orçamentos
     */
    public ArrayList<Orcamento> getAllOrcamentos() {
        ArrayList<Orcamento> lista = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM Orcamento");
            while (rs.next()) {
                Orcamento orcamento = new Orcamento();
                orcamento.setId(rs.getInt("id"));
                orcamento.setVersao(rs.getString("versao"));
                orcamento.setAno(rs.getInt("ano"));
                orcamento.setData(rs.getString("data"));
                orcamento.setEstado(rs.getString("estado"));
                lista.add(orcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todos orçamentos aprovados (exclui propostas - estado
     * igual a Proposta)
     *
     * @return uma lista de Orçamentos
     */
    public ArrayList<Orcamento> getAllOrcamentosAprovados() {
        ArrayList<Orcamento> lista = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM Orcamento WHERE estado =?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, "Definitivo");
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                Orcamento orcamento = new Orcamento();
                orcamento.setId(rs.getInt("id"));
                orcamento.setVersao(rs.getString("versao"));
                orcamento.setAno(rs.getInt("ano"));
                orcamento.setData(rs.getString("data"));
                orcamento.setEstado(rs.getString("estado"));
                lista.add(orcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve um id de um Orçamento através da sua versão e do ano
     *
     * @param versao versão de um Orçamento
     * @param ano ano
     * @return id de um Orçamento
     */
    public int getOrcamentoId(String versao, int ano) {
        int result = -1;
        try {
            ResultSet rs;
            String sql = "SELECT id FROM Orcamento WHERE versao=? and ano=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, versao);
            this.stmt.setInt(2, ano);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                Orcamento orcamento2 = new Orcamento();
                orcamento2.setId(rs.getInt("id"));
                result = orcamento2.getId();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    
     /**
     * Método que devolve um id de um Orçamento através do ano e do seu Estado
     * @param ano ano
     * @param estado estado do Orçamento . Defenitivo ou Proposta
     * @return id de um Orçamento
     */
    public int getOrcamentoId(int ano, String estado) {
        int result = -1;
        try {
            ResultSet rs;
            String sql = "SELECT id FROM Orcamento WHERE estado=? and ano=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, estado);
            this.stmt.setInt(2, ano);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                Orcamento orcamento2 = new Orcamento();
                orcamento2.setId(rs.getInt("id"));
                result = orcamento2.getId();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    
         /**
     * Método que devolve um id de um Orçamento através do seu Estado
     * @param estado estado do Orçamento . Defenitivo ou Proposta
     * @return id de um Orçamento
     */
    public int getOrcamentoId(String estado) {
        int result = -1;
        try {
            ResultSet rs;
            String sql = "SELECT id FROM Orcamento WHERE estado=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, estado);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                Orcamento orcamento2 = new Orcamento();
                orcamento2.setId(rs.getInt("id"));
                result = orcamento2.getId();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Método que devolve toda a informação de um Orcamento através do seu ID
     *
     * @param id identificação de um Orcamento ID
     * @return Orcamento
     */
    public Orcamento getOrcamento(int id) {
        Orcamento orcamento2 = new Orcamento();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM Orcamento WHERE id=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                orcamento2.setId(rs.getInt("id"));
                orcamento2.setVersao(rs.getString("versao"));
                orcamento2.setAno(rs.getInt("ano"));
                orcamento2.setData(rs.getString("data"));
                orcamento2.setEstado(rs.getString("estado"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orcamento2;
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
