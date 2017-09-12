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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Condominio;

/**
 * Esta classe permite efectuar operações na BD relativamente a um Condomínio.
 *
 * @author Luís Sousa - 8090228
 */
public class CondominioDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe Condominio DAO aqui é feita a ligação com a BD.
     *
     */
    public CondominioDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar dados de um condominio na BD
     *
     * @param condominio objeto com os dados de um condominio
     * @return se a operação foi efetuada com sucesso
     */
    public boolean adicionarCondominio(Condominio condominio) {
        boolean result = false;
        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO Condominio (nome, morada, codPostal, localidade, "
                    + "telefone, telemovel, email, contribuinte)"
                    + "VALUES (?,?,?,?,?,?,?,?)";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, condominio.getNome());
            this.stmt.setString(2, condominio.getMorada());
            this.stmt.setString(3, condominio.getCodPostal());
            this.stmt.setString(4, condominio.getLocalidade());
            this.stmt.setInt(5, condominio.getTelefone());
            this.stmt.setInt(6, condominio.getTelemovel());
            this.stmt.setString(7, condominio.getMail());
            this.stmt.setInt(8, condominio.getContribuinte());

            this.stmt.execute();
            result = true;
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * Método para editar dados de um condominio na BD
     *
     * @param condominio objeto com os dados de um condominio
     * @return se a operação foi efetuada com sucesso
     */
    public boolean editarCondominio(Condominio condominio) {
        boolean result = false;
        try {
            // Sql para adicionar condominio
            String sql = "UPDATE Condominio set nome=?, morada=?, codPostal=?, localidade=?, telefone=?, telemovel=?, email=?, contribuinte=?"
                    + "WHERE id=?";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, condominio.getNome());
            this.stmt.setString(2, condominio.getMorada());
            this.stmt.setString(3, condominio.getCodPostal());
            this.stmt.setString(4, condominio.getLocalidade());
            this.stmt.setInt(5, condominio.getTelefone());
            this.stmt.setInt(6, condominio.getTelemovel());
            this.stmt.setString(7, condominio.getMail());
            this.stmt.setInt(8, condominio.getContribuinte());
            this.stmt.setInt(9, condominio.getId());
            this.stmt.execute();
            result = true;
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * Método que permite fechar a connection
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

    /**
     * Método que devolve um resultset com os dados do condominio
     *
     * @return resultset com dados de um condomínio
     * @throws java.sql.SQLException
     */
   public ResultSet getCondominio() throws SQLException {
        ResultSet rs = null;
        if (this.haveData()) {

            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            rs = statement.executeQuery("SELECT * FROM Condominio");

        }
        return rs;
    }


    /**
     * Método que devolve os dados relativamente ao Condomínio
     *
     * @return Condominio
     */
    public Condominio getCondominioObject() {
        Condominio condominio = new Condominio();
        try {
            ResultSet rs = this.getCondominio();
            while (rs.next()) {
                condominio.setId(rs.getInt("id"));
                condominio.setNome(rs.getString("nome"));
                condominio.setMorada(rs.getString("morada"));
                condominio.setCodPostal(rs.getString("codPostal"));
                condominio.setLocalidade(rs.getString("localidade"));
                condominio.setTelefone(rs.getInt("telefone"));
                condominio.setTelemovel(rs.getInt("telemovel"));
                condominio.setMail(rs.getString("email"));
                condominio.setContribuinte(rs.getInt("contribuinte"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CondominioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return condominio;
    }

    /**
     * Método que permite verificar se já existem ou não dados relativamente ao
     * Condomínio
     *
     * @return se existe dados na BD relativamente ao Condomínio
     */
    public boolean haveData() {
        int rows = 0;
        try (Statement statement = this.connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("SELECT * FROM Condominio")) {
                while (rs.next()) {
                    rows++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CondominioDAO.class.getName()).log(Level.SEVERE, null, ex);
        } // set timeout to 30 sec.
        return rows > 0;
    }
}
