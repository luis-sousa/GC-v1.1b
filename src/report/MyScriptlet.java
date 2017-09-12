package report;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */

/**
 * Esta classe permite efectuar operações na BD relativamente a um Aviso de
 * Débito.
 *
 * @author Luís Sousa - 8090228
 */
import dao.DespesaOrcamentoDAO;
import dao.ReciboDAO;
import java.math.BigDecimal;
import net.sf.jasperreports.engine.JRDefaultScriptlet;

/**
 * Esta classe é um scriptlet que vai servir de apoio para gerar alguns reports
 * necessários para a aplicação.
 *
 * @author Luís Sousa - 8090228
 */
public class MyScriptlet extends JRDefaultScriptlet {

    //http://community.jaspersoft.com/blog/all-you-want-know-about-scriptlets
    /**
     * Método que converte um número em extenso
     *
     * @param value numero a converter
     * @return uma string com o numero por extenso
     */
    public static String convertNumberToWords(float value) {
        NumeroPorExtenso teste = new NumeroPorExtenso(new BigDecimal(value));
        return teste.toString();
    }

    /**
     * Método responsável por calcular as receitas obtidas pelo condominio
     * relativamente a uma fracção para um determinado Orçamento
     *
     * @param idOrc identificador de um orçamento
     * @param codFraccao identificador de uma fracção
     * @return valor
     */
    public static int getReceitas(int idOrc, String codFraccao) {
        int valor = -1;
        ReciboDAO dao = new ReciboDAO();
        valor = dao.somaPagoAteMomento(codFraccao, idOrc);
        dao.close();
        return valor;
    }

    /**
     * Método que devolve a soma de despesas relativamente a uma rubrica para um
     * determinado Orçamento
     *
     * @param idOrc identificador de um orçamento
     * @param rubrica identificador de uma rubrica
     * @return valor
     */
    public static int getDespesas(int idOrc, String rubrica) {
        int valor = -1;
        DespesaOrcamentoDAO dao = new DespesaOrcamentoDAO();
        valor = dao.somaDespesas(rubrica, idOrc);
        dao.close();
        return valor;
    }

    /**
     * Método que devolve o total de receitas para um determinado Ano (orçamentos e subOrçamentos incluidos)
     *
     * @param idOrc identificador de um orçamento
     * @return valor
     */
    public static int totalReceitasComSubOrcamentos(int idOrc) {
        int valor = -1;
        ReciboDAO dao = new ReciboDAO();
        valor = dao.getTotalReceitas(idOrc);
        dao.close();
        return valor;
    }
    
    
    /**
     * Método que devolve o total de receitas para um orçamento
     *
     * @param idOrc identificador de um orçamento
     * @return valor
     */
    public static int totalReceitas(int idOrc) {
        int valor = -1;
        ReciboDAO dao = new ReciboDAO();
        valor = dao.getTotalReceitas(idOrc);
        dao.close();
        return valor;
    }

    /**
     * Método que devolve o total de despesas para um orçamento
     *
     * @param idOrc identificador de um orçamento
     * @return valor
     */
    public static int totalDespesas(int idOrc) {
        int valor = -1;
        DespesaOrcamentoDAO dao = new DespesaOrcamentoDAO();
        valor = dao.getTotalDespesas(idOrc);
        dao.close();
        return valor;

    }

}
