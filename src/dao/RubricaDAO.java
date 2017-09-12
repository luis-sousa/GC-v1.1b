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
import model.Rubrica;

/**
 * Esta classe permite efectuar operações na BD sobre uma Rubrica.
 *
 * @author Luís Sousa - 8090228
 */
public class RubricaDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe RubricaDAO aqui é feita a ligação com a BD.
     *
     */
    public RubricaDAO(){
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar Rubricas na BD
     *
     * @param rubrica objeto com os dados relativos a uma rubrica
     * @return 
     */
    public boolean adicionarRubrica(Rubrica rubrica) {
        boolean result = false;
        try {
            String sql = "INSERT INTO Rubrica (nome)"
                    + "VALUES (?)";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, rubrica.getNome());
            this.stmt.execute();
            result = true;
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * Metodo para apagar rubricas na BD
     *
     * @param rubrica objeto com os dados relativos a uma rubrica
     * @return 
     */
    public boolean removerRubrica(Rubrica rubrica) {
        boolean result = false;
        try {
            String sql = "delete from Rubrica where nome=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, rubrica.getNome());
            this.stmt.execute();
            result = true;
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * Método que retorna todas as rubricas existente na BD
     *
     * @return lista com rubricas
     */
    public ArrayList<Rubrica> getAllRubricas() {
        ArrayList<Rubrica> lista = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM Rubrica");
            while (rs.next()) {
                Rubrica rubrica = new Rubrica();
                rubrica.setNome(rs.getString("nome"));
                lista.add(rubrica);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(RubricaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    
    /**
     * Método que verifica se pode remover uma rubrica. Só pode remover a rubrica
     * se ele não existir em nenhum Orçamento, ou seja na tabela RubricaOrcamento
     *
     * @param rubrica rubrica
     * @return se pode remover rubrica
     */
    public boolean verifyRemove(String rubrica) {
        RubricaOrcamentoDAO dao = new RubricaOrcamentoDAO();
        return !dao.existRubrica(rubrica);
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
