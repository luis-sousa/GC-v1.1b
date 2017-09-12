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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.AvisoDebito;
import model.ReciboLinha;

/**
 * Esta classe permite efectuar operações na BD sobre uma Linha de um Recibo (um
 * recibo pode estar associado a vários avisos de débitos por isso usei esta
 * abordagem).
 *
 * @author Luís Sousa - 8090228
 */
public class ReciboLinhaDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe ReciboLinhaDAO aqui é feita a ligação com a BD.
     *
     */
    public ReciboLinhaDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar dados de uma Linha de um recibo na BD
     *
     * @param rL objeto que contém imformação sobre uma linha do recibo
     */
    public void adicionarLinha(ReciboLinha rL) {
        try {
            String sql = "INSERT INTO ReciboLinha (idRecibo,idAvisoDebito,valorPago, descricao)"
                    + "VALUES (?,?,?,?)";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, rL.getIdRecibo());
            this.stmt.setInt(2, rL.getIdAviso());
            this.stmt.setInt(3, rL.getValorPago());
            this.stmt.setString(4, rL.getDescricao());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve o valor pago até ao momento relativamente a um aviso
     * de débito
     *
     * @param idAviso identificador de um aviso
     * @return valor pago
     */
    public int somaPagoAteMomento(int idAviso) {
        int soma = 0;
        try {
            ResultSet rs;
            String sql = "SELECT SUM(valorPago) As soma FROM ReciboLinha WHERE idAvisoDebito=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idAviso);
            rs = this.stmt.executeQuery();
            soma = rs.getInt("soma");
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReciboLinhaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return soma;
    }

    /**
     * Método que calcula o valor por pagar para um aviso
     *
     * @param idAviso
     * @return valor por pagar
     */
    public int calcValorPorPagar(int idAviso) {
        int result;
        AvisoDebitoDAO dao = new AvisoDebitoDAO();
        AvisoDebito ad;
        ad = dao.getAvisoDebito(idAviso);
        int valorMensalidade = ad.getValorPagar();
        dao.close();
        result = valorMensalidade - this.somaPagoAteMomento(idAviso);
        return result;
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
