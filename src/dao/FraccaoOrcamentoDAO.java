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
import model.Fraccao;
import model.FraccaoOrcamento;
import model.Orcamento;

/**
 * Esta classe permite efectuar operações na BD sobre uma Fraccão (Fracção
 * Pagante) relativamente a um determinado Orçamento .
 *
 * @author Luís Sousa - 8090228
 */
public class FraccaoOrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe FraccaoOrcamentoDAO aqui é feita a ligação com a BD.
     *
     */
    public FraccaoOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar fraccoes para um determinado orçamento na BD
     * (Fracções Pagantes)
     *
     * @param fraccao objeto com os dados de uma fracção
     */
    public void adicionar(FraccaoOrcamento fraccao) {

        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO FraccaoOrcamento (fraccao,idOrcamento)"
                    + "VALUES (?,?)";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, fraccao.getFraccao());
            this.stmt.setInt(2, fraccao.getIdOrcamento());

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que permite para desassociar uma fracção de um Orçamento.
     *
     * @param fraccao objeto com os dados de uma fracção
     */
    public void remover(FraccaoOrcamento fraccao) {
        try {
            // Sql para adicionar usuario
            String sql = "delete from FraccaoOrcamento where fraccao=? and idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, fraccao.getFraccao());
            this.stmt.setInt(2, fraccao.getIdOrcamento());

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todas as fracções pagantes independentemente do
     * Orçamento
     *
     * @return lista com FraccoesOrcamento
     */
    public ArrayList<FraccaoOrcamento> getAllFraccoesOrcamento() {
        ArrayList<FraccaoOrcamento> lista = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM FraccaoOrcamento");
            while (rs.next()) {
                FraccaoOrcamento fraccaoOrcamento = new FraccaoOrcamento();
                fraccaoOrcamento.setFraccao(rs.getString("fraccao"));
                fraccaoOrcamento.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(fraccaoOrcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todas as fracções pagantes relativamente a um
     * Orçamento
     *
     * @param id identificação de um Orçamento
     * @return lista com FraccoesOrcamento
     */
    public ArrayList<FraccaoOrcamento> getAllFraccoesOrcamento(int id) {
        ArrayList<FraccaoOrcamento> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM FraccaoOrcamento WHERE idOrcamento=?";
            ResultSet rs;
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                FraccaoOrcamento fraccaoOrcamento = new FraccaoOrcamento();
                fraccaoOrcamento.setFraccao(rs.getString("fraccao"));
                fraccaoOrcamento.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(fraccaoOrcamento);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
 /**
     * Método que devolve todas as fracções existente na BD
     *
     * @return lista com todas as fracções existente na BD
     */
    public ArrayList<Fraccao> getAllFraccoes() {
        ArrayList<Fraccao> lista = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM Fraccao");

            while (rs.next()) {
                Fraccao fraccao = new Fraccao();
                fraccao.cod.set(rs.getString("codFracao"));
                fraccao.nome.set(rs.getString("nome"));
                fraccao.morada.set(rs.getString("morada"));
                fraccao.codPostal.set(rs.getString("codPostal"));
                fraccao.localidade.set(rs.getString("localidade"));
                fraccao.telefone.set(rs.getInt("telefone"));
                fraccao.telemovel.set(rs.getInt("telemovel"));
                fraccao.mail.set(rs.getString("email"));
                fraccao.contribuinte.set(rs.getInt("contribuinte"));
                fraccao.permilagem.set(rs.getFloat("permilagem"));
                fraccao.tipo.set(rs.getString("tipo"));
                lista.add(fraccao);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    
     /**
     * Método que devolve todas as fracções pagantes relativamente a um
     * Orçamento
     *
     * @param id identificação de um Orçamento
     * @return lista com FraccoesOrcamento
     */
    public ArrayList<Fraccao> getFracoesPagantesOrcamento(int id) {
        ArrayList<Fraccao> lista = new ArrayList<>();
        try {
            String sql = "SELECT f.* FROM Fraccao f, FraccaoOrcamento fO WHERE fO.idOrcamento=? and fO.fraccao = f.codFracao";
            ResultSet rs;
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
               Fraccao fraccao = new Fraccao();
                fraccao.cod.set(rs.getString("codFracao"));
                fraccao.nome.set(rs.getString("nome"));
                fraccao.morada.set(rs.getString("morada"));
                fraccao.codPostal.set(rs.getString("codPostal"));
                fraccao.localidade.set(rs.getString("localidade"));
                fraccao.telefone.set(rs.getInt("telefone"));
                fraccao.telemovel.set(rs.getInt("telemovel"));
                fraccao.mail.set(rs.getString("email"));
                fraccao.contribuinte.set(rs.getInt("contribuinte"));
                fraccao.permilagem.set(rs.getFloat("permilagem"));
                fraccao.tipo.set(rs.getString("tipo"));
                lista.add(fraccao);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que permite verificar se existe uma fraccao na tabela
     *
     * @param codFraccao código da fração
     * @return existe fracao
     */
    public boolean existFraccao(String codFraccao) {
        int rows = 0;
        try {
            String sql = "SELECT Distinct * FROM FraccaoOrcamento WHERE fraccao=?";
            ResultSet rs;
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, codFraccao);
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
     * Método que permite verificar se existe algum registo com o id de um
     * determinado orçamento na tabela FraccaoOrcamento
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existOrcamento(int id) {
        int rows = 0;
        try {
            String sql = "SELECT * FROM FraccaoOrcamento WHERE idOrcamento=?";
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
