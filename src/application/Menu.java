package application;

/**
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras IPP - Instituto
 * Politécnico do Porto LEI - Licenciatura em Engenharia Informática Projeto
 * Final 2013/2014
 */
import dao.CondominioDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Esta classe tem como objetivo definir a Stage Principal e chamar o respectivo
 * fxml.
 *
 * @author Luís Sousa - 8090228
 */
public class Menu extends Application {

    // Atributo do tipo Stage 
    private static Stage stage;

    @Override
    /**
     * Este método tem como função abrir a Stage Principal (Main)
     *
     * @param Stage
     * @return void
     */
    public void start(final Stage stage) throws Exception {

        CondominioDAO dao = new CondominioDAO();

        if (dao.haveData()) {
            stage.setTitle("Gestão Condominio - " + dao.getCondominioObject().getNome());
        } else {
            stage.setTitle("Gestão Condominio - Sem Nome");
        }

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/house.png")));
        Parent root = FXMLLoader.load(getClass().getResource("Menu.fxml"));

        Scene scene = new Scene(root);
        //stage.setResizable(false);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        //definir stage
        Menu.stage = stage;
    }

    /**
     * Método estatico para returnar a stage
     *
     * @return Stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     *
     * @param title definir o titulo da Stage Principal
     */
    public static void setStageTitle(String title) {
        Menu.getStage().setTitle(title);
    }

}
