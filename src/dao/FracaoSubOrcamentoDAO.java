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
import model.FracaoSubOrcamento;
import model.Orcamento;

/**
 * Esta classe permite efectuar operações na BD sobre uma Fraccão (Fracção
 * Pagante) relativamente a um determinado Orçamento .
 *
 * @author Luís Sousa - 8090228
 */
public class FracaoSubOrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe FraccaoOrcamentoDAO aqui é feita a ligação com a BD.
     *
     */
    public FracaoSubOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar fracoes para um determinado sub orçamento na BD
     * (Fracções Pagantes)
     *
     * @param fracao objeto com os dados de uma fracção
     */
    public void adicionar(FracaoSubOrcamento fracao) {

        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO FracaoSubOrcamento (fracao,idSubOrcamento)"
                    + "VALUES (?,?)";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, fracao.getFracao());
            this.stmt.setInt(2, fracao.getIdSubOrcamento());

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que permite para desassociar uma fracção de um Sub Orçamento.
     *
     * @param fracao objeto com os dados de uma fracção
     */
    public void remover(FracaoSubOrcamento fracao) {
        try {
            // Sql para adicionar usuario
            String sql = "delete from FracaoSubOrcamento where fracao=? and idSubOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, fracao.getFracao());
            this.stmt.setInt(2, fracao.getIdSubOrcamento());

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todas as frações pagantes independentemente do Sub
     * Orçamento
     *
     * @return lista com FraccoesOrcamento
     */
    public ArrayList<FracaoSubOrcamento> getAllFracoesOrcamento() {
        ArrayList<FracaoSubOrcamento> lista = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            rs = statement.executeQuery("SELECT * FROM FracaoSubOrcamento");
            while (rs.next()) {
                FracaoSubOrcamento fracaoOrcamento = new FracaoSubOrcamento();
                fracaoOrcamento.setFracao(rs.getString("fracao"));
                fracaoOrcamento.setIdSubOrcamento(rs.getInt("idSubOrcamento"));
                lista.add(fracaoOrcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todas as frações pagantes relativamente a um Sub
     * Orçamento
     *
     * @param id identificação de um Orçamento
     * @return lista com Fracoes
     */
    public ArrayList<FracaoSubOrcamento> getAllFracoesSubOrcamento(int id) {
        ArrayList<FracaoSubOrcamento> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM FracaoSubOrcamento WHERE idSubOrcamento=?";
            ResultSet rs;
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                FracaoSubOrcamento fracaoOrcamento = new FracaoSubOrcamento();
                fracaoOrcamento.setFracao(rs.getString("fracao"));
                fracaoOrcamento.setIdSubOrcamento(rs.getInt("idSubOrcamento"));
                lista.add(fracaoOrcamento);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

//    /**
//     * Método que permite verificar se existe uma fracao na tabela
//     * FracaoSubOrcamento
//     *
//     * @param codFracao código da fração
//     * @return existe fracao
//     */
//    public boolean existFracao(String codFracao) {
//        int rows = 0;
//        try {
//            String sql = "SELECT Distinct * FROM FracaoSubOrcamento WHERE fracao=?";
//            ResultSet rs;
//            this.stmt = this.connection.prepareStatement(sql);
//            this.stmt.setString(1, codFracao);
//            rs = this.stmt.executeQuery();
//
//            while (rs.next()) {
//                rows++;
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(CondominioDAO.class.getName()).log(Level.SEVERE, null, ex);
//        } // set timeout to 30 sec.
//        return rows > 0;
//    }
    
    

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
            String sql = "SELECT * FROM FracaoSubOrcamento WHERE idSubOrcamento=?";
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
