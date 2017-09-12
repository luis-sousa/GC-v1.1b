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
import model.RubricaOrcamento;
import util.MoneyConverter;

/**
 * Esta classe permite efectuar operações na BD sobre uma Rubrica associada a um
 * Orçamento.
 *
 * @author Luís Sousa - 8090228
 */
public class RubricaOrcamentoDAO {
    
    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe Rubrica DAO Aqui é feita a ligação com a BD.
     *
     */
    public RubricaOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar rubricas associadas a um Orçamento na BD
     *
     * @param rubrica objecto com toda a informação de uma rubrica e o seu
     * Orçamento
     */
    public void adicionar(RubricaOrcamento rubrica) {
        try {
            String sql = "INSERT INTO RubricaOrcamento (nome,valor,idOrcamento)"
                    + "VALUES (?,?,?)";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, rubrica.getRubrica());
            this.stmt.setInt(2, rubrica.getValorCentimos());
            this.stmt.setInt(3, rubrica.getIdOrcamento());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Metodo para apagar rubricas referentes a um Orçamento na BD
     *
     * @param nome nome de uma rubrica
     * @param idOrcamento identificador de um orçamento
     */
    public void remover(String nome, int idOrcamento) {
        try {
            String sql = "delete from RubricaOrcamento where nome=? and idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, nome);
            this.stmt.setInt(2, idOrcamento);
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método para editar dados de uma rubrica referente a um Orçamento na BD
     *
     * @param rubrica objecto com toda a informação de uma rubrica e o seu
     * Orçamento
     */
    public void editar(RubricaOrcamento rubrica) {
        try {
            String sql = "update RubricaOrcamento set valor=? where nome=? and idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, rubrica.getValorCentimos());
            this.stmt.setString(2, rubrica.getRubrica());
            this.stmt.setInt(3, rubrica.getIdOrcamento());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todas as rubricas
     *
     * @return lista com todas as rubricas
     */
    public ArrayList<RubricaOrcamento> getAllRubricasOrcamento() {
        ArrayList<RubricaOrcamento> lista = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM RubricaOrcamento");
            while (rs.next()) {
                RubricaOrcamento rubricaOrcamento = new RubricaOrcamento();
                rubricaOrcamento.setRubrica(rs.getString("nome"));
                rubricaOrcamento.setValorEuros(MoneyConverter.getEuros((rs.getInt("valor"))));
                rubricaOrcamento.setValorCentimos(rs.getInt("valor"));
                lista.add(rubricaOrcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(RubricaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todas as rubricas associadas a um determinado
     * Orçamento
     *
     * @param id
     * @return lista com todas as rubricas associadas a um orcamento
     */
    public ArrayList<RubricaOrcamento> getAllRubricasOrcamento(int id) {
        ArrayList<RubricaOrcamento> lista = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM RubricaOrcamento WHERE idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                RubricaOrcamento rubricaOrcamento = new RubricaOrcamento();
                rubricaOrcamento.setRubrica(rs.getString("nome"));
                rubricaOrcamento.setValorEuros(MoneyConverter.getEuros((rs.getInt("valor"))));
                rubricaOrcamento.setValorCentimos(rs.getInt("valor"));
                rubricaOrcamento.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(rubricaOrcamento);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RubricaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
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
     * Método que permite verificar se existe uma determinada rubrica na tabela
     * RubricaOrcamento
     *
     * @param rubrica nome da rubrica
     * @return existe rubrica
     */
    public boolean existRubrica(String rubrica) {
        int rows = 0;
        try {
            String sql = "SELECT Distinct * FROM RubricaOrcamento WHERE nome=?";
            ResultSet rs;
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, rubrica);
            rs = this.stmt.executeQuery();
            
            while (rs.next()) {
                rows++;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CondominioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rows > 0;
    }

    /**
     * Método que permite verificar se existe algum registo com o id de um
     * determinado orçamento na tabela RubricaOrcamento
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existOrcamento(int id) {
        int rows = 0;
        try {
            String sql = "SELECT * FROM RubricaOrcamento WHERE idOrcamento=?";
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
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignore) {
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignore) {
            }
        }
    }
    
}
