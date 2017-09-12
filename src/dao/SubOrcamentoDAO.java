/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import connection.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.SubOrcamento;

/**
 *
 * @author luis__000
 */
public class SubOrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe SubOrcamentoDAO aqui é feita a ligação com a BD.
     *
     */
    public SubOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar dados de um Sub Orçamento na BD
     *
     * @param orcamento objeto com os dados de um orcamento
     */
    public void adicionarOrcamento(SubOrcamento orcamento) {
        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO SubOrcamento (nome, idOrcamento, data)"
                    + "VALUES (?,?,?)";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, orcamento.getNome());
            this.stmt.setInt(2, orcamento.getIdOrcamento());
            this.stmt.setString(3, orcamento.getData());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método para adicionar dados do da Fraccao na BD
     *
     * @param newOrcamento objeto com os dados de um orcamento
     * @param idSubOrcamento identificador de um orcamento
     */
    public void editarOrcamento(SubOrcamento newOrcamento, int idSubOrcamento) {
        try {
            String sql = "update SubOrcamento set nome=?, data=?, idOrcamento=? where idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, newOrcamento.getNome());
            this.stmt.setString(2, newOrcamento.getData());
            this.stmt.setInt(3, newOrcamento.getIdOrcamento());
            this.stmt.setInt(4, idSubOrcamento);
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Metodo para apagar um Sub Orçamento da BD
     *
     * @param idSubOrcamento identificador de um orcamento
     */
    public void removerOrcamento(int idSubOrcamento) {
        try {

            String sql = "delete from SubOrcamento where idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setInt(1, idSubOrcamento);

            this.stmt.execute();
            this.stmt.close();

            String sql2 = "delete from RubricaSubOrcamento where idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql2);

            this.stmt.setInt(1, idSubOrcamento);

            this.stmt.execute();
            this.stmt.close();

            String sql3 = "delete from FracaoSubOrcamento where idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql3);

            this.stmt.setInt(1, idSubOrcamento);

            this.stmt.execute();
            this.stmt.close();

            String sql4 = "delete from FracaoRubricaSubOrcamento where idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql4);

            this.stmt.setInt(1, idSubOrcamento);

            this.stmt.execute();
            this.stmt.close();

            String sql5 = "delete from MensalidadesSubOrcamento where idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql5);

            this.stmt.setInt(1, idSubOrcamento);

            this.stmt.execute();
            this.stmt.close();

//            String sql6 = "delete from ObservacaoOrcamento where idOrcamento=?";
//            this.stmt = this.connection.prepareStatement(sql6);
//
//            this.stmt.setInt(1, id);
//
//            this.stmt.execute();
//            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todos Sub Orçamentos
     *
     * @return uma lista de Orçamentos
     */
    public ArrayList<SubOrcamento> getAllSubOrcamentos() {
        ArrayList<SubOrcamento> lista = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            rs = statement.executeQuery("SELECT * FROM SubOrcamento");
            while (rs.next()) {
                SubOrcamento orcamento = new SubOrcamento();
                orcamento.setId(rs.getInt("idSubOrcamento"));
                orcamento.setNome(rs.getString("nome"));
                orcamento.setIdOrcamento(rs.getInt("idOrcamento"));
                orcamento.setData(rs.getString("data"));
                lista.add(orcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todos os sub orçamentos de um Orçamento ID
     *
     * @param id ID Orçamento
     * @return Orcamento
     */
    public ArrayList<SubOrcamento> getAllSubOrcamentos(int id) {
        ArrayList<SubOrcamento> lista = new ArrayList<>();

        try {
            ResultSet rs;
            String sql = "SELECT * FROM SubOrcamento WHERE idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                SubOrcamento orcamento2 = new SubOrcamento();
                orcamento2.setId(rs.getInt("idSubOrcamento"));
                orcamento2.setNome(rs.getString("nome"));
                orcamento2.setIdOrcamento(rs.getInt("idOrcamento"));
                orcamento2.setData(rs.getString("data"));
                lista.add(orcamento2);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve um id de um Sub Orçamento através do nome , orçamento
     * e data
     *
     * @param nome nome do Sub Orçamento
     * @param idOrcamento id Orçamento
     * @param data data
     * @return id do Sub Orçamento
     */
    public int getSubOrcamentoId(String nome, int idOrcamento, String data) {
        int result = -1;
        try {
            ResultSet rs;
            String sql = "SELECT DISTINCT idSubOrcamento FROM SubOrcamento WHERE nome=? and idOrcamento=? and data=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, nome);
            this.stmt.setInt(2, idOrcamento);
            this.stmt.setString(3, data);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                SubOrcamento orcamento2 = new SubOrcamento();
                orcamento2.setId(rs.getInt("idSubOrcamento"));
                result = orcamento2.getId();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Método que devolve toda a informação de um Sub Orcamento através do seu
     * ID
     *
     * @param id identificação de um Sub Orcamento ID
     * @return Orcamento
     */
    public SubOrcamento getSubOrcamento(int id) {
        SubOrcamento orcamento2 = new SubOrcamento();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM SubOrcamento WHERE idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                orcamento2.setId(rs.getInt("idSubOrcamento"));
                orcamento2.setNome(rs.getString("nome"));
                orcamento2.setIdOrcamento(rs.getInt("idOrcamento"));
                orcamento2.setData(rs.getString("data"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orcamento2;
    }

    public int getOrcamentoID(int idSubOrcamento) {
        int id = -1;
        try {
            ResultSet rs;
            String sql = "SELECT Distinct idOrcamento FROM SubOrcamento WHERE idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idSubOrcamento);
            rs = this.stmt.executeQuery();
            id = rs.getInt("idOrcamento");
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(OrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

//     /**
//     * Método que permite verficar se ja existe este nome de sub orçamento
//     *
//     * @param nome nome do Sub Orçamento
//     * @param idOrcamento ID
//     * @return existe nome
//     */
//    public boolean existSubOrcamentoNome(String nome, int idOrcamento) {
//          int rows = 0;
//        try {
//            ResultSet rs;
//            String sql = "SELECT * FROM SubOrcamento WHERE nome=? and idOrcamento=?";
//            this.stmt = this.connection.prepareStatement(sql);
//            this.stmt.setString(1, nome);
//            this.stmt.setInt(2, idOrcamento);
//            rs = this.stmt.executeQuery();
//            while (rs.next()) {
//               rows++;
//            }
//            rs.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(SubOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return rows>0;
//    }
    /**
     * Método que permite verificar se existe um determinado orçamento associado
     * a algum sub Orçamento
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existOrcamento(int id) {
        int rows = 0;
        try {
            String sql = "SELECT Distinct * FROM SubOrcamento WHERE idOrcamento=?";
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
