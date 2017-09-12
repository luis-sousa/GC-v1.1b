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
import model.FraccaoRubricaOrcamento;
import model.Orcamento;
import model.RubricaOrcamento;
import util.MoneyConverter;

/**
 * Esta classe permite efectuar operações na BD sobre as Rubricas associadas a
 * uma Fracção relativamente a um determinado Orçamento .
 *
 * @author Luís Sousa - 8090228
 */
public class FraccaoRubricaOrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe FraccaoRubricaOrcamentoDAO aqui é feita a ligação
     * com a BD.
     *
     */
    public FraccaoRubricaOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar(associar) Rubricas a uma Fraccao na BD
     *
     * @param fro objeto FraccaoRubricaOrcamento
     */
    public void adicionar(FraccaoRubricaOrcamento fro) {
        try {
            String sql = "INSERT INTO FraccaoRubricaOrcamento (fraccao,rubrica,idOrcamento)"
                    + "VALUES (?,?,?)";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, fro.getFraccao());
            this.stmt.setString(2, fro.getRubrica());
            this.stmt.setInt(3, fro.getIdOrcamento());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Metodo para apagar rubricas associadas a uma Fraccao na BD
     *
     * @param fro objeto FraccaoRubricaOrcamento
     */
    public void remover(FraccaoRubricaOrcamento fro) {
        try {
            // Sql para adicionar usuario
            String sql = "delete from FraccaoRubricaOrcamento where fraccao=? and rubrica=? and idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, fro.getFraccao());
            this.stmt.setString(2, fro.getRubrica());
            this.stmt.setInt(3, fro.getIdOrcamento());
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
    public ArrayList<FraccaoRubricaOrcamento> getAllFraccoesRubricasOrcamento() {
        ArrayList<FraccaoRubricaOrcamento> lista = new ArrayList<>();
        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM FraccaoRubricaOrcamento");
            while (rs.next()) {
                FraccaoRubricaOrcamento fraccaoRubricaOrcamento = new FraccaoRubricaOrcamento();
                fraccaoRubricaOrcamento.setFraccao(rs.getString("fraccao"));
                fraccaoRubricaOrcamento.setRubrica(rs.getString("rubrica"));
                fraccaoRubricaOrcamento.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(fraccaoRubricaOrcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoRubricaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todas as rubricas e a sua (respetiva fracção)
     * associadas a um Orçamento
     *
     * @param id identificador de um Orçamento
     * @return lista com todos as rubricas independentemente do orçamento
     */
    public ArrayList<FraccaoRubricaOrcamento> getAllFraccoesRubricasOrcamento(int id) {
        ArrayList<FraccaoRubricaOrcamento> lista = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM FraccaoRubricaOrcamento WHERE idOrcamento=? ORDER BY fraccao ASC";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                FraccaoRubricaOrcamento fraccaoRubricaOrcamento = new FraccaoRubricaOrcamento();
                fraccaoRubricaOrcamento.setFraccao(rs.getString("fraccao"));
                fraccaoRubricaOrcamento.setRubrica(rs.getString("rubrica"));
                fraccaoRubricaOrcamento.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(fraccaoRubricaOrcamento);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(FraccaoRubricaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todas as frações que têm uma determinada rubrica num
     * determinado orçamento
     *
     *
     * @param nomeRubrica rubrica em causa
     * @param id identificação de um Orçamento
     * @return lista com FraccoesOrcamento
     */
    public ArrayList<Fraccao> getFracoesRubricaOrcamento(String nomeRubrica, int id) {
        ArrayList<Fraccao> lista = new ArrayList<>();
        try {
            String sql = "SELECT f.* FROM Fraccao f, FraccaoRubricaOrcamento fRO WHERE fRO.idOrcamento=? and fRO.rubrica =? and fRO.fraccao = f.codFracao";
            ResultSet rs;
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            this.stmt.setString(2, nomeRubrica);
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
     * Método que devolve todas as rubricas associadas uma determinada fracao
     * num determinado orçamento
     *
     *
     * @param codFraccao rubrica em causa
     * @param id identificação de um Orçamento
     * @return lista com FraccoesOrcamento
     */
    public ArrayList<RubricaOrcamento> getRubricaFracaoOrcamento(String codFraccao, int id) {
        ArrayList<RubricaOrcamento> lista = new ArrayList<>();
        try {
            String sql = "SELECT rO.* FROM RubricaOrcamento rO, FraccaoRubricaOrcamento fRO WHERE fRO.idOrcamento=? and fRO.fraccao =? and fRO.rubrica = rO.nome and rO.idOrcamento=fRO.idOrcamento";
            ResultSet rs;
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            this.stmt.setString(2, codFraccao);
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
            Logger.getLogger(FraccaoOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
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
     * Método para remover da BD todas rubricas relativamente a um Orcamento
     *
     * @param idOrc identificador de um Orçamento
     */
    public void removerAll(int idOrc) {
        try {
            String sql = "delete from FraccaoRubricaOrcamento where idOrcamento=?";
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
     * determinado orçamento na tabela FraccaoRubricaOrcamento
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existOrcamento(int id) {
        int rows = 0;
        try {
            String sql = "SELECT * FROM FraccaoRubricaOrcamento WHERE idOrcamento=?";
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
