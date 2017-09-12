package connection;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import util.Ficheiro;

/**
 * Esta classe permite fazer conexão á base de dados.
 *
 * @author Luís Sousa - 8090228
 */
public class DB {

    private final String driverClassName = "org.sqlite.JDBC";
    private final String folderDB = "db/";
    private final String host = "jdbc:sqlite:" + folderDB;
    private final String file = "db/lastdb.txt";
    private Connection con;

    /**
     * Método construtor para instanciar a classe DB
     */
    public DB() {
    }

    /**
     * Método que muda a ligação à base de dados
     *
     * @param newdb nome da bd a gerir
     */
    public void setConnection(String newdb) {
        try {
            Ficheiro f = new Ficheiro(file);
            f.write(newdb);
        } catch (IOException ex) {
        }
    }

    /**
     * Método que faz a ligação à base de dados
     *
     * @return a conexão á base de dados
     */
    public Connection getConnection() {
        Connection connection = null;
        try {
            String db = getName();
            //load sqlite-JDBC driver usando a current class loader 
            Class.forName(driverClassName);
            //criar a conexão
            connection = DriverManager.getConnection(host + db);
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
        this.con = connection;
        return connection;
    }

    /**
     * Método que cria nova base de dados
     *
     * @param name nome da nova DB
     */
    public void create(String name) {
        try {
            Connection connection;
            // load sqlite-JDBC driver usando a current class loader 
            Class.forName("org.sqlite.JDBC");

            // criar a database conection
            connection = DriverManager.getConnection(host + name);

            this.con = connection;
            createTables();

        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Método que permite fechar conexão
     */
    public void close() {
        try {
            if (this.con != null) {
                con.close();
            }
        } catch (SQLException ex) {
        }
    }

    /**
     * Método que retorna o nome da última Base de Dados aberta
     *
     * @return Nome da última base de dados aberta
     */
    public String getName() {
        Ficheiro f = new Ficheiro(file);
        String db = f.read();
        return db;
    }

    /**
     * Método que cria as tabelas necessárias
     *
     * @throws java.sql.SQLException
     */
    public void createTables() throws SQLException {
        try (Statement statement = this.con.createStatement()) {
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("create table if not exists Condominio (id INTEGER PRIMARY KEY   AUTOINCREMENT,nome varchar(50), morada varchar(75),codPostal char(8),localidade varchar(25),telefone decimal(9,0),telemovel decimal(9,0),email varchar(50),contribuinte decimal(9,0))");
            statement.executeUpdate("create table if not exists Fraccao (codFracao char(1) PRIMARY KEY,nome varchar(50), morada varchar(75),codPostal char(8),localidade varchar(25),telefone decima(9,0),telemovel decimal(9,0),email varchar(50),contribuinte decimal(9,0),permilagem decimal(4,0),tipo varchar(15))");
            statement.executeUpdate("create table if not exists Rubrica (nome varchar(25) PRIMARY KEY)");
            String insertFCR = "INSERT INTO Rubrica" + "(nome) " + "VALUES" + "('Fundo Comum de Reserva')";
            statement.executeUpdate(insertFCR);
            statement.executeUpdate("create table if not exists Orcamento (id INTEGER PRIMARY KEY   AUTOINCREMENT,versao varchar(25),ano decimal(4,0),data char(10),estado varchar(15))");
            statement.executeUpdate("create table if not exists RubricaOrcamento (nome varchar(25),valor integer,idOrcamento integer,PRIMARY KEY (nome,idOrcamento))");
            statement.executeUpdate("create table if not exists FraccaoOrcamento (fraccao varchar(25),idOrcamento integer,PRIMARY KEY (fraccao,idOrcamento))");
            statement.executeUpdate("create table if not exists FraccaoRubricaOrcamento (fraccao varchar(25),rubrica varchar(25),idOrcamento integer,PRIMARY KEY ( fraccao, rubrica,idOrcamento))");
            statement.executeUpdate("create table if not exists FraccaoQuotasOrcamento (fraccao varchar(25),qMensal integer,qAnual integer,idOrcamento integer,PRIMARY KEY (fraccao,idOrcamento))");
            statement.executeUpdate("create table if not exists ObservacaoOrcamento (fraccao varchar(25),descricao varchar(100),idOrcamento integer,PRIMARY KEY (fraccao,idOrcamento))");
            statement.executeUpdate("create table if not exists AvisoDebito (idAviso INTEGER PRIMARY KEY AUTOINCREMENT,idOrcamento INTEGER,codFraccao char(1),mes INTEGER,data char(10),dataLimite char(10),descricao varchar(100),valorPagar integer,totalEmDivida integer,resolvido bit,idSubOrcamento Integer)");
            statement.executeUpdate("create table if not exists Recibo (idRecibo INTEGER PRIMARY KEY AUTOINCREMENT,data char(10),totalRecibo integer)");
            statement.executeUpdate("create table if not exists ReciboLinha (idReciboLinha INTEGER PRIMARY KEY AUTOINCREMENT,idRecibo integer,idAvisoDebito integer,valorPago integer, descricao varchar(200))");
            statement.executeUpdate("create table if not exists DespesaOrcamento (idDespesa INTEGER PRIMARY KEY AUTOINCREMENT,despesa varchar(50),rubrica varchar(25),data char(10),montante integer,idOrcamento integer)");
            statement.executeUpdate("create table if not exists SubOrcamento (idSubOrcamento INTEGER PRIMARY KEY AUTOINCREMENT,nome varchar(25),idOrcamento integer,data char(10))");
            statement.executeUpdate("create table if not exists RubricaSubOrcamento (rubrica varchar(25),descricao varchar(100),valor integer,idSubOrcamento integer,PRIMARY KEY (rubrica,idSubOrcamento))");
            statement.executeUpdate("create table if not exists FracaoSubOrcamento (fracao varchar(25),idSubOrcamento integer,PRIMARY KEY (fracao,idSubOrcamento))");
            statement.executeUpdate("create table if not exists FracaoRubricaSubOrcamento (fracao varchar(25),rubrica varchar(25),idSubOrcamento integer,PRIMARY KEY ( fracao, rubrica,idSubOrcamento))");
            statement.executeUpdate("create table if not exists MensalidadesSubOrcamento (fracao varchar(25),valorMensal integer,valorAnual integer,idSubOrcamento integer,PRIMARY KEY (fracao,idSubOrcamento))");
        } // set timeout to 30 sec.
    }
}
