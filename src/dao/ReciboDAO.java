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
import model.Recibo;

/**
 * Esta classe permite efectuar operações na BD sobre um Recibo.
 *
 * @author Luís Sousa - 8090228
 */
public class ReciboDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe ReciboDAO qqui é feita a ligação com a BD.
     *
     */
    public ReciboDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar recibos na BD
     *
     * @param rb objecto com os dados de um Recibo
     */
    public void adicionarRecibo(Recibo rb) {
        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO Recibo (data,totalRecibo)"
                    + "VALUES (?,?)";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, rb.getData());
            this.stmt.setInt(2, rb.getTotalRecibo());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve o id de um determinado recibo
     *
     * @param recibo objecto com os dados de um Recibo
     * @return id de um Recibo
     */
    public int getIDRecibo(Recibo recibo) {
        int id = -1;
        try {
            ResultSet rs;
            String sql = "SELECT idRecibo FROM Recibo WHERE data=? and totalRecibo=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, recibo.getData());
            this.stmt.setFloat(2, recibo.getTotalRecibo());
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                id = rs.getInt("idRecibo");
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReciboDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    /**
     * Método que edita o total de um determinado Recibo
     *
     * @param valor objecto com os dados de um Recibo
     * @param idRecibo identificador de um Recibo
     */
    public void editTotalRecibo(float valor, int idRecibo) {
        try {
            String sql = "update Recibo set totalRecibo=? where idRecibo=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setFloat(1, valor);
            this.stmt.setInt(2, idRecibo);
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método que devolve todos os Recibos existentes
     *
     * @return Recibo
     */
    public ArrayList<Recibo> getAllRecibos() {
        ArrayList<Recibo> lista = new ArrayList<>();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM Recibo";
            this.stmt = this.connection.prepareStatement(sql);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                Recibo aviso = new Recibo();
                aviso.setIdRecibo(rs.getInt("idRecibo"));
                aviso.setData(rs.getString("data"));
                aviso.setTotalRecibo(rs.getInt("totalRecibo"));
                lista.add(aviso);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReciboDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todos os Recibos relativamente a um orçamento
     *
     * @param idOrc identificador de um Orçamento
     * @return Recibo
     */
    public ArrayList<Recibo> getAllRecibos(int idOrc) {
        ArrayList<Recibo> lista = new ArrayList<Recibo>();
        try {
            ResultSet rs;
            String sql = "SELECT DISTINCT r.* FROM Recibo r, ReciboLinha rL, AvisoDebito aD WHERE r.idRecibo=rL.idRecibo and rL.idAvisoDebito=aD.idAviso and aD.idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                Recibo aviso = new Recibo();
                aviso.setIdRecibo(rs.getInt("idRecibo"));
                aviso.setData(rs.getString("data"));
                aviso.setTotalRecibo(rs.getInt("totalRecibo"));
                lista.add(aviso);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReciboDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve todos os Recibos de uma fracção relativamente a um
     * determinado Orçamento
     *
     * @param idOrc
     * @param codFraccao
     * @return Recibo
     */
    public ArrayList<Recibo> getAllRecibos(int idOrc, String codFraccao) {
        ArrayList<Recibo> lista = new ArrayList<Recibo>();
        try {
            ResultSet rs;
            String sql = "SELECT DISTINCT r.* FROM Recibo r, ReciboLinha rL, AvisoDebito aD WHERE r.idRecibo=rL.idRecibo and rL.idAvisoDebito=aD.idAviso and aD.idOrcamento=? and aD.codFraccao LIKE ?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            this.stmt.setString(2, codFraccao);
            rs = this.stmt.executeQuery();
            while (rs.next()) {
                Recibo rb = new Recibo();
                rb.setIdRecibo(rs.getInt("idRecibo"));
                rb.setData(rs.getString("data"));
                rb.setTotalRecibo(rs.getInt("totalRecibo"));
                lista.add(rb);
            }
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReciboDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que devolve o código da fracção daquele recibo
     *
     * @param idRecibo identificador de um recibo
     * @return codigo de uma Fracção
     */
    public String getCodFraccao(int idRecibo) {
        String cod = null;
        try {
            ResultSet rs;
            String sql = "SELECT DISTINCT f.codFracao FROM Fraccao f, AvisoDebito aD, ReciboLinha rL WHERE f.codFracao = aD.codFraccao and  aD.idAviso= rL.idAvisoDebito and rL.idRecibo=? ";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idRecibo);
            rs = this.stmt.executeQuery();
            cod = rs.getString("codFracao");
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReciboDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cod;
    }

    /**
     * Método que devolve o que foi pago até ao momento para um fracção
     * relativamente a um Orçamento
     *
     * @param codFraccao identificador de um Fracção
     * @param idOrc identificador de um Orçamento
     * @return soma pago até ao momento
     */
    public int somaPagoAteMomento(String codFraccao, int idOrc) {
        int soma = 0;
        try {
            ResultSet rs;
            String sql = "SELECT SUM(valorPago) As soma FROM ReciboLinha rL, AvisoDebito aD WHERE rL.idAvisoDebito=aD.idAviso and aD.idOrcamento=? and aD.codFraccao=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            this.stmt.setString(2, codFraccao);
            rs = this.stmt.executeQuery();
            soma = rs.getInt("soma");
        } catch (SQLException ex) {
            Logger.getLogger(ReciboDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return soma;
    }
    
    
    /**
     * Método que devolve o que foi pago até ao momento para um fracção
     * independentemente Orçamento
     *
     * @param codFraccao identificador de um Fracção
     * @return soma pago até ao momento
     */
    public int somaPagoAteMomento(String codFraccao) {
        int soma = 0;
        try {
            ResultSet rs;
            String sql = "SELECT SUM(valorPago) As soma FROM ReciboLinha rL, AvisoDebito aD WHERE rL.idAvisoDebito=aD.idAviso and aD.codFraccao=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, codFraccao);
            rs = this.stmt.executeQuery();
            soma = rs.getInt("soma");
        } catch (SQLException ex) {
            Logger.getLogger(ReciboDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return soma;
    }

    /**
     * Método que devolve o valor de receitas para um determinado Ano (orçamentos e subOrçamentos incluidos)
     *
     * @param idOrc identificador de um Orçamento
     * @return receitas para um orçamento
     */
    public int getTotalReceitasComSubOrcamentos(int idOrc) {
        int soma = 0;
        try {
            ResultSet rs;
            String sql = "SELECT SUM(valorPago) As soma FROM ReciboLinha rL, AvisoDebito aD WHERE rL.idAvisoDebito=aD.idAviso and aD.idOrcamento=?";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            rs = this.stmt.executeQuery();
            soma = rs.getInt("soma");
        } catch (SQLException ex) {
        }
        return soma;
    }
    
    
    
        /**
     * Método que devolve o valor de receitas para um determinado Orçamento
     *
     * @param idOrc identificador de um Orçamento
     * @return receitas para um orçamento
     */
    public int getTotalReceitas(int idOrc) {
        int soma = 0;
        try {
            ResultSet rs;
            String sql = "SELECT SUM(valorPago) As soma FROM ReciboLinha rL, AvisoDebito aD, FraccaoOrcamento fO WHERE rL.idAvisoDebito=aD.idAviso and aD.idOrcamento=fO.idOrcamento and fO.idOrcamento=? and aD.codFraccao = fO.fraccao";
            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            rs = this.stmt.executeQuery();
            soma = rs.getInt("soma");
        } catch (SQLException ex) {
        }
        return soma;
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
