package application;

/**
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras 
 * IPP - Instituto Politécnico do Porto 
 * LEI - Licenciatura em Engenharia Informática 
 * Projeto Final 2013/2014
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Esta classe tem como objetivo definir uma stage e chamar o respectivo fxml.
 *
 * @author Luís Sousa - 8090228
 */
public class GerarReciboPT2 extends Application {

    // Atributo do tipo Stage 
    private static Stage stage;

    @Override
    /**
     * Este método tem como função abrir uma nova cena gerar Recibos relativamente a um Orcamento.
     *
     * @param Stage
     * @return void
     */
    public void start(final Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/GerarReciboPT2.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Escolha o Orçamento");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.centerOnScreen();
        stage.show();

        //definir stage
        GerarReciboPT2.stage = stage;
    }

    /**
     * Método estatico para returnar a stage
     *
     * @return Stage
     */
    public static Stage getStage() {
        return stage;
    }
}
