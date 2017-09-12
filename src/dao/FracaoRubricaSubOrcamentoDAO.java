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
import model.FracaoRubricaSubOrcamento;
import model.Orcamento;

/**
 * Esta classe permite efectuar operações na BD sobre as Rubricas associadas a
 * uma Fração relativamente a um determinado Sub Orçamento .
 *
 * @author Luís Sousa - 8090228
 */
public class FracaoRubricaSubOrcamentoDAO {
    
     private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe FraccaoRubricaOrcamentoDAO aqui é feita a ligação
     * com a BD.
     *
     */
    public FracaoRubricaSubOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }
    
    
      /**
     * Método para adicionar(associar) Rubricas a uma Fraccao na BD
     *
     * @param fro objeto FraccaoRubricaOrcamento
     */
    public void adicionar(FracaoRubricaSubOrcamento fro) {
        try {
            String sql = "INSERT INTO FracaoRubricaSubOrcamento (fracao,rubrica,idSubOrcamento)"
                    + "VALUES (?,?,?)";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, fro.getFracao());
            this.stmt.setString(2, fro.getRubrica());
            this.stmt.setInt(3, fro.getIdSubOrcamento());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
     /**
     * Metodo para apagar rubricas associadas a uma Fracao na BD
     *
     * @param fro objeto FracaoRubricaSubOrcamento
     */
    public void remover(FracaoRubricaSubOrcamento fro) {
        try {
            // Sql para adicionar usuario
            String sql = "delete from FracaoRubricaSubOrcamento where fracao=? and rubrica=? and idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, fro.getFracao());
            this.stmt.setString(2, fro.getRubrica());
            this.stmt.setInt(3, fro.getIdSubOrcamento());
            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todas as rubricas independentemente do Orçamento
     *
     * @return lista com todos as rubricas independentemente do orçamento
     */
    public ArrayList<FracaoRubricaSubOrcamento> getAllFracoesRubricasSubOrcamento() {
        ArrayList<FracaoRubricaSubOrcamento> lista = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM FracaoRubricaSubOrcamento");
            while (rs.next()) {
                FracaoRubricaSubOrcamento fracaoRubricaOrcamento = new FracaoRubricaSubOrcamento();
                fracaoRubricaOrcamento.setFracao(rs.getString("fracao"));
                fracaoRubricaOrcamento.setRubrica(rs.getString("rubrica"));
                fracaoRubricaOrcamento.setIdSubOrcamento(rs.getInt("idSubOrcamento"));
                lista.add(fracaoRubricaOrcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoRubricaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todas as rubricas e a sua (respetiva fração)
     * associadas a um Sub Orçamento
     *
     * @param id identificador de um Orçamento
     * @return lista com todos as rubricas independentemente do orçamento
     */
    public ArrayList<FracaoRubricaSubOrcamento> getAllFraccoesRubricasOrcamento(int id) {
        ArrayList<FracaoRubricaSubOrcamento> lista = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM FracaoRubricaSubOrcamento WHERE idSubOrcamento=? ORDER BY fracao ASC";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
               FracaoRubricaSubOrcamento fracaoRubricaOrcamento = new FracaoRubricaSubOrcamento();
                fracaoRubricaOrcamento.setFracao(rs.getString("fracao"));
                fracaoRubricaOrcamento.setRubrica(rs.getString("rubrica"));
                fracaoRubricaOrcamento.setIdSubOrcamento(rs.getInt("idSubOrcamento"));
                lista.add(fracaoRubricaOrcamento);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoRubricaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
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
     * Método para remover da BD todas rubricas relativamente a um Sub Orcamento
     *
     * @param idOrc identificador de um Orçamento
     */
    public void removerAll(int idOrc) {
        try {
            String sql = "delete from FracaoRubricaSubOrcamento where idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
      /**
     * Método que permite verificar se existe algum registo com o id de um
     * determinado orçamento na tabela FracaoRubricaSubOrcamento
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existOrcamento(int id) {
        int rows = 0;
        try  {
            String sql = "SELECT * FROM FracaoRubricaSubOrcamento WHERE idSubOrcamento=?";
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
