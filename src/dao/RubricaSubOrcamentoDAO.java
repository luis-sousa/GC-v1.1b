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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.RubricaSubOrcamento;
import util.MoneyConverter;

/**
 * Esta classe permite efectuar operações na BD sobre uma Rubrica associada a um
 * Orçamento.
 *
 * @author Luís Sousa - 8090228
 */
public class RubricaSubOrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe Rubrica DAO Aqui é feita a ligação com a BD.
     *
     */
    public RubricaSubOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar rubricas associadas a um Sub Orçamento na BD
     *
     * @param rubrica objecto com toda a informação de uma rubrica e o seu
     * Orçamento
     */
    public void adicionar(RubricaSubOrcamento rubrica) {
        try {
            String sql = "INSERT INTO RubricaSubOrcamento (rubrica,descricao,valor,idSubOrcamento)"
                    + "VALUES (?,?,?,?)";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, rubrica.getRubrica());
            this.stmt.setString(2, rubrica.getDescricao());
            this.stmt.setInt(3, rubrica.getValorCentimos());
            this.stmt.setInt(4, rubrica.getIdSubOrcamento());
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
     * @param idSubOrcamento identificador de um orçamento
     */
    public void remover(String nome, int idSubOrcamento) {
        try {
            String sql = "delete from RubricaSubOrcamento where rubrica=? and idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, nome);
            this.stmt.setInt(2, idSubOrcamento);
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
    public void editar(RubricaSubOrcamento rubrica) {
        try {
            String sql = "update RubricaSubOrcamento set valor=? where rubrica=? and idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, rubrica.getValorCentimos());
            this.stmt.setString(2, rubrica.getRubrica());
            this.stmt.setInt(3, rubrica.getIdSubOrcamento());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todas as rubricas associadas a um determinado Sub
     * Orçamento
     *
     * @param id
     * @return lista com todas as rubricas associadas a um orcamento
     */
    public ArrayList<RubricaSubOrcamento> getAllRubricasOrcamento(int id) {
        ArrayList<RubricaSubOrcamento> lista = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM RubricaSubOrcamento WHERE idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                RubricaSubOrcamento rubricaOrcamento = new RubricaSubOrcamento();
                rubricaOrcamento.setRubrica(rs.getString("rubrica"));
                rubricaOrcamento.setDescricao(rs.getString("descricao"));
                rubricaOrcamento.setValorEuros((MoneyConverter.getEuros(rs.getInt("valor"))));
                rubricaOrcamento.setValorCentimos(rs.getInt("valor"));
                rubricaOrcamento.setIdSubOrcamento(rs.getInt("idSubOrcamento"));
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
