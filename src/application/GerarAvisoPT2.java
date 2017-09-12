package application;

/**
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras 
 * IPP - Instituto Politécnico do Porto 
 * LEI - Licenciatura em Engenharia Informática 
 * Projeto Final 2013/2014
 */

import java.io.IOException;
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
public class GerarAvisoPT2 extends Application {

    // Atributo do tipo Stage 
    private static Stage stage;

    @Override
    /**
     * Este método tem como função abrir uma nova cena para gerar Avisos de Débitos relativamente a um Orcamento
     *
     * @param Stage
     * @return void
     */
    public void start(final Stage stage) throws IOException  {
        Parent root = FXMLLoader.load(getClass().getResource("/application/GerarAvisoPT2.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Gerar Avisos para este orçamento");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.centerOnScreen();
        stage.show();

        //definir stage
        GerarAvisoPT2.stage = stage;
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
