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

/**
 * Esta classe permite efectuar operações na BD relativamente a uma Fraccão.
 *
 * @author Luís Sousa - 8090228
 */
public class FraccaoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe Fraccao DAO aqui é feita a ligação com a BD.
     *
     */
    public FraccaoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método que permite adicionar dados de uma Fraccao na BD
     *
     * @param fraccao objecto com os dados de uma fracção
     * @return se a operação foi bem sucedida
     */
    public boolean adicionarFraccao(Fraccao fraccao) {
        boolean result = false;
        try {
            String sql = "INSERT INTO Fraccao (codFracao,nome, morada, codPostal, localidade, "
                    + "telefone, telemovel, email, contribuinte,permilagem,tipo)"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, fraccao.getCod());
            this.stmt.setString(2, fraccao.getNome());
            this.stmt.setString(3, fraccao.getMorada());
            this.stmt.setString(4, fraccao.getCodPostal());
            this.stmt.setString(5, fraccao.getLocalidade());
            this.stmt.setInt(6, fraccao.getTelefone());
            this.stmt.setInt(7, fraccao.getTelemovel());
            this.stmt.setString(8, fraccao.getMail());
            this.stmt.setInt(9, fraccao.getContribuinte());
            this.stmt.setFloat(10, fraccao.getPermilagem());
            this.stmt.setString(11, fraccao.getTipo());

            this.stmt.execute();
            result = true;
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * Método que permite editar dados de uma Fraccao na BD
     *
     * @param fraccao objeto com os dados de uma fracção
     * @return se a operação foi bem sucedida
     */
    public boolean editarFraccao(Fraccao fraccao) {
        boolean result = false;
        try {
            String sql = "update Fraccao set nome=?, morada=?, codPostal=?, localidade=?,telefone=?,telemovel=?,email=?,contribuinte=?,permilagem=?, tipo=? where codFracao=?";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, fraccao.getNome());
            this.stmt.setString(2, fraccao.getMorada());
            this.stmt.setString(3, fraccao.getCodPostal());
            this.stmt.setString(4, fraccao.getLocalidade());
            this.stmt.setInt(5, fraccao.getTelefone());
            this.stmt.setInt(6, fraccao.getTelemovel());
            this.stmt.setString(7, fraccao.getMail());
            this.stmt.setInt(8, fraccao.getContribuinte());
            this.stmt.setFloat(9, fraccao.getPermilagem());
            this.stmt.setString(10, fraccao.getTipo());
            this.stmt.setString(11, fraccao.getCod());

            this.stmt.execute();
            result = true;
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * Metodo que permite apagar uma fraccao da BD
     *
     * @param fraccao
     * @return se a operação foi bem sucedida
     */
    public boolean removerFraccao(Fraccao fraccao) {
        boolean result = false;
        try {
            String sql = "delete from Fraccao where codFracao=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, fraccao.getCod());
            this.stmt.execute();
            result = true;
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
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
     * Método que permite obter um email de uma Fracção
     *
     * @param codFraccao identificador de uma Fracção
     * @return email de um determinada fracção
     */
    public String getMail(String codFraccao) {
        String email = null;
        try {
            ResultSet rs;
            String sql = "SELECT email FROM Fraccao WHERE codFracao=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, codFraccao);
            rs = this.stmt.executeQuery();
            email = rs.getString("email");
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return email;
    }

    /**
     * Método que verifica se pode remover uma fração. Só pode remover a fração
     * se ela não existir em nenhum Orçamento, ou seja na tabela
     * FraccaoOrcamento
     *
     * @param codFraccao código da fração
     * @return se pode remover fracao
     */
    public boolean verifyRemove(String codFraccao) {
        boolean remove;
        FraccaoOrcamentoDAO dao = new FraccaoOrcamentoDAO();
        remove = !dao.existFraccao(codFraccao);
        dao.close();
        return remove;
    }

    /**
     * Método que verifica se pode editar a permilagem de uma fração. Só pode
     * editar a permilagem de uma fração se a fração não existir em nenhum
     * Orçamento, ou seja na tabela FraccaoOrcamento
     *
     * @param codFraccao identificador da fração
     * @return se pode editar fracao
     */
    public boolean verifyEditPerm(String codFraccao) {
        boolean edit;
        FraccaoOrcamentoDAO dao = new FraccaoOrcamentoDAO();
        edit = !dao.existFraccao(codFraccao);
        dao.close();
        return edit;
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
