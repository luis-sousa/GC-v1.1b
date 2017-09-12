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
import model.ObsOrcamento;

/**
 * Esta classe permite efectuar operações na BD sobre as Observações associadas
 * a um Orçamento .
 *
 * @author Luís Sousa - 8090228
 */
public class ObsOrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe ObsOrcamentoDAO aqui é feita a ligação com a BD.
     *
     */
    public ObsOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método que permite adicionar observações na BD
     *
     * @param obs objecto com os dados de uma Observação
     */
    public void adicionarObs(ObsOrcamento obs) {
        try {
            String sql = "INSERT INTO ObservacaoOrcamento (fraccao,descricao,idOrcamento)"
                    + "VALUES (?,?,?)";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, obs.getFraccao());
            this.stmt.setString(2, obs.getObs());
            this.stmt.setInt(3, obs.getIdOrc());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que permite editar dados de uma observação relativamente a um
     * orcamento na BD
     *
     * @param obs objecto com os dados de uma Observação
     */
    public void editarObs(ObsOrcamento obs) {
        try {
            String sql = "update ObservacaoOrcamento set descricao=? where idOrcamento=? and fraccao=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, obs.getObs());
            this.stmt.setInt(2, obs.getIdOrc());
            this.stmt.setString(3, obs.getFraccao());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Metodo que permite apagar uma observação de um orcamento na BD
     *
     * @param obs objecto com os dados de uma Observaçã
     */
    public void removerOrcamento(ObsOrcamento obs) {
        try {
            String sql = "delete from ObservacaoOrcamento where idOrcamento=? and fraccao=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, obs.getIdOrc());
            this.stmt.setString(2, obs.getFraccao());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que pdevolve todas as observações relativamente a um Orçamento
     *
     * @param idOrcamento
     * @return lista com todas as observações para um Orçamento
     */
    public ArrayList<ObsOrcamento> getObservacoes(int idOrcamento) {
        ArrayList<ObsOrcamento> lista = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM ObservacaoOrcamento WHERE idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrcamento);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                ObsOrcamento obs = new ObsOrcamento();
                obs.setFraccao(rs.getString("fraccao"));
                obs.setObs(rs.getString("descricao"));
                obs.setIdOrc(rs.getInt("idOrcamento"));
                lista.add(obs);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(ObsOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve se existe ou não observações sobre um determinado
     * Orçamento
     *
     * @param idOrcamento identificador do Orçamento
     * @return se existe dados ou não
     */
    public boolean haveData(int idOrcamento) {
        int rows = 0;
        try {
            ResultSet rs;
            String sql = "SELECT * FROM ObservacaoOrcamento WHERE idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrcamento);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                rows++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ObsOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
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
