package application;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Esta classe tem como objetivo definir a Stage Principal e chamar o respectivo fxml.
 *
 * @author Luís Sousa - 8090228
 */
public class GestCondominio extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        new Menu().start(new Stage());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
