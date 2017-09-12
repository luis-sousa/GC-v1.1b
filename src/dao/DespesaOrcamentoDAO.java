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
import model.DespesaOrcamento;
import util.MoneyConverter;

/**
 * Esta classe permite efectuar operações na BD relativamente a uma Despesa.
 *
 * @author Luís Sousa - 8090228
 */
public class DespesaOrcamentoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe DespesaOrcamentoDAO que faz a ligação com a BD.
     *
     */
    public DespesaOrcamentoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar uma despesa BD
     *
     * @param despesa objeto com os dados de uma despesa
     */
    public void adicionarDespesa(DespesaOrcamento despesa) {

        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO DespesaOrcamento (despesa,rubrica,data,montante,idOrcamento)"
                    + "VALUES (?,?,?,?,?)";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setString(1, despesa.getDespesa());
            this.stmt.setString(2, despesa.getRubrica());
            this.stmt.setString(3, despesa.getData());
            this.stmt.setInt(4, despesa.getMontanteCentimos());
            this.stmt.setInt(5, despesa.getIdOrcamento());

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que permite editar os dados de uma Despesa na BD
     *
     * @param newDespesaOrc nova despesa
     * @param idOrc identificação de um orçamento
     * @param idDespesa objeto com os dados de uma despesa
     */
    public void editarDespesa(DespesaOrcamento newDespesaOrc, int idOrc, int idDespesa) {
        try {
            // Sql para editar orcamento
            String sql = "update DespesaOrcamento set despesa=?, rubrica=?, data=?, montante=? where idOrcamento=? and idDespesa=?";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, newDespesaOrc.getDespesa());
            this.stmt.setString(2, newDespesaOrc.getRubrica());
            this.stmt.setString(3, newDespesaOrc.getData());
            this.stmt.setInt(4, newDespesaOrc.getMontanteCentimos());
            this.stmt.setInt(5, idOrc);
            this.stmt.setInt(6, idDespesa);

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Metodo que permite remover uma despesa da BD
     *
     * @param id identificação da despesa
     */
    public void removerDespesa(int id) {
        try {
            String sql = "delete from DespesaOrcamento where idDespesa=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que permite obter todas as despesas relativamente a um Orçamento
     *
     * @param idOrc id de um Orçamento
     * @return lista com todas as depesas de um Orçamento
     */
    public ArrayList<DespesaOrcamento> getAllDespesas(int idOrc) {
        ArrayList<DespesaOrcamento> lista = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            rs = statement.executeQuery("SELECT * FROM DespesaOrcamento where idOrcamento=" + idOrc);

            while (rs.next()) {
                DespesaOrcamento orcamento = new DespesaOrcamento();
                orcamento.setDespesa(rs.getString("despesa"));
                orcamento.setRubrica(rs.getString("rubrica"));
                orcamento.setData(rs.getString("data"));
                orcamento.setMontanteCentimos(rs.getInt("montante"));
                orcamento.setMontanteEuros(MoneyConverter.getEuros(rs.getInt("montante")));
                orcamento.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(orcamento);
            }
            // Fechamos a conexao
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DespesaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que permite obter a soma de todas as depesas efetuadas sobre uma
     * rubrica para um determinado Orçamento
     *
     * @param rubrica nome da rubrica
     * @param idOrc id do Orçamento
     * @return soma de todas as despesas de uma rubrica para um orçamento
     */
    public int somaDespesas(String rubrica, int idOrc) {
        int soma = 0;
        try {
            ResultSet rs;
            String sql = "SELECT SUM(montante) As soma FROM DespesaOrcamento dO WHERE dO.idOrcamento=? and dO.rubrica=?";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            this.stmt.setString(2, rubrica);
            rs = this.stmt.executeQuery();

            soma = rs.getInt("soma");
        } catch (SQLException ex) {
            Logger.getLogger(DespesaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return soma;
    }

    /**
     * Método que devolve todas as despesas existentes na BD
     *
     * @return lista com todas as despesas
     */
    public ArrayList<DespesaOrcamento> getAllDespesas() {
        ArrayList<DespesaOrcamento> lista = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {
            ResultSet rs;
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            rs = statement.executeQuery("SELECT * FROM DespesaOrcamento");

            while (rs.next()) {
                DespesaOrcamento orcamento = new DespesaOrcamento();
                orcamento.setDespesa(rs.getString("despesa"));
                orcamento.setRubrica(rs.getString("rubrica"));
                orcamento.setData(rs.getString("data"));
                orcamento.setMontanteCentimos(rs.getInt("montante"));
                orcamento.setMontanteEuros(MoneyConverter.getEuros(rs.getInt("montante")));
                orcamento.setIdOrcamento(rs.getInt("idOrcamento"));
                lista.add(orcamento);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DespesaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } // set timeout to 30 sec.
        return lista;
    }

    /**
     * Método que devolve o ID de uma Despesa
     *
     * @param despesa Despesa de um Orçamento
     * @return o id de um determinada Despesa
     */
    public int getDespesaId(DespesaOrcamento despesa) {
        int result = -1;
        try {
            ResultSet rs;
            String sql = "SELECT idDespesa FROM DespesaOrcamento WHERE despesa=? and rubrica=? and data=? and montante=? and idOrcamento=?";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, despesa.getDespesa());
            this.stmt.setString(2, despesa.getRubrica());
            this.stmt.setString(3, despesa.getData());
            this.stmt.setInt(4, despesa.getMontanteCentimos());
            this.stmt.setInt(5, despesa.getIdOrcamento());
            rs = this.stmt.executeQuery();

            while (rs.next()) {
                result = rs.getInt("idDespesa");
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DespesaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Método que devolve uma Despesa através de um ID
     *
     * @param id identicação de uma Despesa
     * @return uma Despesa
     */
    public DespesaOrcamento getDespesa(int id) {
        DespesaOrcamento despesa = new DespesaOrcamento();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM DespesaOrcamento WHERE idDespesa=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, id);
            rs = this.stmt.executeQuery();

            while (rs.next()) {
                despesa.setDespesa(rs.getString("despesa"));
                despesa.setRubrica(rs.getString("rubrica"));
                despesa.setData(rs.getString("data"));
                despesa.setMontanteCentimos(rs.getInt("montante"));
                despesa.setMontanteEuros(MoneyConverter.getEuros(rs.getInt("montante")));
                despesa.setIdOrcamento(rs.getInt("idOrcamento"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DespesaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return despesa;
    }

    /**
     * Método que devolve o total gasto relativamente a um orçamento
     *
     * @param idOrc
     * @return total Despesas
     */
    public int getTotalDespesas(int idOrc) {
        int soma = 0;
        try {

            ResultSet rs;
            String sql = "SELECT SUM(montante) As soma FROM DespesaOrcamento dO WHERE dO.idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            rs = this.stmt.executeQuery();

            soma = rs.getInt("soma");
        } catch (SQLException ex) {
            Logger.getLogger(DespesaOrcamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return soma;
    }

    /**
     * Método que permite verificar se existe algum registo com o id de um
     * determinado orçamento na tabela DespesaOrcamento
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existOrcamento(int id) {
        int rows = 0;
        try {

            String sql = "SELECT Distinct * FROM DespesaOrcamento WHERE idOrcamento=?";
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
