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
import model.AvisoDebito;

/**
 * Esta classe permite efectuar operações na BD relativamente a um Aviso de
 * Débito.
 *
 * @author Luís Sousa - 8090228
 */
public class AvisoDebitoDAO {

    private final Connection connection;
    private PreparedStatement stmt;

    /**
     * Construtor da classe Condominio DAO Aqui é feita a ligação com a BD.
     *
     */
    public AvisoDebitoDAO() {
        DB db = new DB();
        this.connection = db.getConnection();
    }

    /**
     * Método para adicionar um Aviso de Débito na BD
     *
     * @param aviso aviso de débito relativo a um orçamento
     */
    public void adicionarAvisoDebito(AvisoDebito aviso) {
        try {
            // Sql para adicionar condominio
            String sql = "INSERT INTO AvisoDebito (idOrcamento,codFraccao,mes,data,dataLimite,descricao,valorPagar,totalEmDivida,resolvido,idSubOrcamento)"
                    + "VALUES (?,?,?,?,?,?,?,?,?,?)";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setInt(1, aviso.getIdOrcamento());
            this.stmt.setString(2, aviso.getCodFraccao());
            this.stmt.setInt(3, aviso.getMes());
            this.stmt.setString(4, aviso.getData());
            this.stmt.setString(5, aviso.getDataLimite());
            this.stmt.setString(6, aviso.getDescricao());
            this.stmt.setInt(7, aviso.getValorPagar());
            this.stmt.setInt(8, aviso.getTotalEmDivida());
            this.stmt.setInt(9, aviso.getResolvido());
            this.stmt.setInt(10, aviso.getIdSubOrcamento());
            this.stmt.execute();
            this.stmt.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
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
     * Método que verifica se um aviso existe
     *
     * @param aviso aviso de débito relativo a um orçamento
     * @return boolean existe ou não
     */
    public boolean exist(AvisoDebito aviso) {
        int rows = 0;
        try {
            ResultSet rs;
            String sql = "SELECT * FROM AvisoDebito WHERE idOrcamento=? and codFraccao=? and mes=? and idSubOrcamento=?";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, aviso.getIdOrcamento());
            this.stmt.setString(2, aviso.getCodFraccao());
            this.stmt.setInt(3, aviso.getMes());
            this.stmt.setInt(4, aviso.getIdSubOrcamento());
            rs = this.stmt.executeQuery();

            while (rs.next()) {
                rows++;
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return rows > 0;
    }

    /**
     *
     * @param idOrc id de um orçamento
     * @return
     */
    public ArrayList<AvisoDebito> getAllAvisosDebito(int idOrc) {
        ArrayList<AvisoDebito> lista = new ArrayList<>();

        try {
            ResultSet rs;
            String sql = "SELECT * FROM AvisoDebito WHERE idOrcamento=? ORDER BY mes DESC, codFraccao ASC";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setInt(1, idOrc);

            rs = this.stmt.executeQuery();

            while (rs.next()) {
                AvisoDebito aviso = new AvisoDebito();
                aviso.setIdAviso(rs.getInt("idAviso"));
                aviso.setIdOrcamento(rs.getInt("idOrcamento"));
                aviso.setCodFraccao(rs.getString("codFraccao"));
                aviso.setMes(rs.getInt("mes"));
                aviso.setData(rs.getString("data"));
                aviso.setDataLimite(rs.getString("dataLimite"));
                aviso.setDescricao(rs.getString("descricao"));
                aviso.setValorPagar(rs.getInt("valorPagar"));
                aviso.setTotalEmDivida(rs.getInt("totalEmDivida"));
                aviso.setResolvido(rs.getInt("resolvido"));
                aviso.setIdSubOrcamento(rs.getInt("idSubOrcamento"));

                lista.add(aviso);
            }
            // Fechamos a conexao
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            System.err.println(ex);
        }
        return lista;
    }

    /**
     * Método que permite obter todos os avisos de débito para uma fracção de um
     * determinado orçamento
     *
     * @param idOrc id relativo a um orçamento
     * @param codFraccao código identificativo de uma fracção
     * @return lista com Avisos de Débito
     */
    public ArrayList<AvisoDebito> getAllAvisosDebitoFraccao(int idOrc, String codFraccao) {
        ArrayList<AvisoDebito> lista = new ArrayList<>();

        try {
            ResultSet rs;
            String sql = "SELECT * FROM AvisoDebito WHERE idOrcamento=? and codFraccao LIKE ?";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            this.stmt.setString(2, "%" + codFraccao + "%");
            rs = this.stmt.executeQuery();

            while (rs.next()) {
                AvisoDebito aviso = new AvisoDebito();
                aviso.setIdAviso(rs.getInt("idAviso"));
                aviso.setIdOrcamento(rs.getInt("idOrcamento"));
                aviso.setCodFraccao(rs.getString("codFraccao"));
                aviso.setMes(rs.getInt("mes"));
                aviso.setData(rs.getString("data"));
                aviso.setDataLimite(rs.getString("dataLimite"));
                aviso.setDescricao(rs.getString("descricao"));
                aviso.setValorPagar(rs.getInt("valorPagar"));
                aviso.setTotalEmDivida(rs.getInt("totalEmDivida"));
                aviso.setResolvido(rs.getInt("resolvido"));
                aviso.setIdSubOrcamento(rs.getInt("idSubOrcamento"));

                lista.add(aviso);
            }
            // Fechamos a conexao
            rs.close();
            this.stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(AvisoDebitoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    /**
     * Método que permite obter um determinado aviso de débito
     *
     * @param idAviso identificação do Aviso de Débito
     * @return um aviso de débito
     */
    public AvisoDebito getAvisoDebito(int idAviso) {
        AvisoDebito aviso = new AvisoDebito();
        try {
            ResultSet rs;
            String sql = "SELECT * FROM AvisoDebito WHERE idAviso=?";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idAviso);
            rs = this.stmt.executeQuery();

            aviso.setIdAviso(rs.getInt("idAviso"));
            aviso.setIdOrcamento(rs.getInt("idOrcamento"));
            aviso.setCodFraccao(rs.getString("codFraccao"));
            aviso.setMes(rs.getInt("mes"));
            aviso.setData(rs.getString("data"));
            aviso.setDataLimite(rs.getString("dataLimite"));
            aviso.setDescricao(rs.getString("descricao"));
            aviso.setValorPagar(rs.getInt("valorPagar"));
            aviso.setTotalEmDivida(rs.getInt("totalEmDivida"));
            aviso.setResolvido(rs.getInt("resolvido"));
            aviso.setIdSubOrcamento(rs.getInt("idSubOrcamento"));

            // Fechamos a conexao
            rs.close();
            this.stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(AvisoDebitoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aviso;
    }

    /**
     * Método que permite mudar o estado de um aviso de débito (resolvido ou não
     * resolvido)
     *
     * @param idAviso identificativo de um aviso de débito
     * @param estado estado de um aviso de débito (1-resolvido 0-não resolvido)
     */
    public void setEstadoAviso(int idAviso, int estado) {
        try {
            // Sql para adicionar condominio
            String sql = "UPDATE AvisoDebito set resolvido=?"
                    + "WHERE idAviso=?";

            this.stmt = this.connection.prepareStatement(sql);

            this.stmt.setInt(1, estado);
            this.stmt.setInt(2, idAviso);

            this.stmt.execute();
            this.stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(AvisoDebitoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo que permite calcular o valor total a pagar de todos os avisos
     * relativamente a uma fracção para um determinado orçamento
     *
     * @param codFraccao
     * @param idOrc
     * @return soma valor a pagar de todos os avisos de uma fraccao para um
     * determinado orcamento
     */
    public int somaValorPagarAteMomentoAvisos(String codFraccao, int idOrc) {
        int soma = 0;
        try {
            ResultSet rs;
            String sql = "SELECT SUM(valorPagar) As soma FROM AvisoDebito aD WHERE aD.idOrcamento=? and aD.codFraccao=?";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setInt(1, idOrc);
            this.stmt.setString(2, codFraccao);
            rs = this.stmt.executeQuery();
            soma = rs.getInt("soma");
        } catch (SQLException ex) {
            Logger.getLogger(AvisoDebitoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return soma;
    }
    
    
    
        /**
     * Metodo que permite calcular o valor total a pagar de todos os avisos
     * relativamente a uma fracção para um determinado orçamento
     *
     * @param codFraccao
     * @return soma valor a pagar de todos os avisos de uma fraccao independentemente do orcamento
     */
    public int somaValorPagarAteMomentoAvisos(String codFraccao) {
        int soma = 0;
        try {
            ResultSet rs;
            String sql = "SELECT SUM(valorPagar) As soma FROM AvisoDebito aD WHERE aD.codFraccao=?";

            this.stmt = this.connection.prepareStatement(sql);
            this.stmt.setString(1, codFraccao);
            rs = this.stmt.executeQuery();
            soma = rs.getInt("soma");
        } catch (SQLException ex) {
            Logger.getLogger(AvisoDebitoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return soma;
    }

    /**
     * Método que permite verificar se existe algum registo com o id de um
     * determinado orçamento na tabela AvisoDebito
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existOrcamento(int id) {
        int rows = 0;
        try {
            String sql = "SELECT Distinct * FROM AvisoDebito WHERE idOrcamento=?";
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
     * Método que permite verificar se existe algum registo com o id de um
     * determinado sub orçamento na tabela AvisoDebito
     *
     * @param id identificador orçamento
     * @return existe orçamento
     */
    public boolean existSubOrcamento(int id) {
        int rows = 0;
        try {
            String sql = "SELECT Distinct * FROM AvisoDebito WHERE idSubOrcamento=?";
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
}
