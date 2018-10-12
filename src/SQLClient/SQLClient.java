/**
 * Rolando Murillo
 * COP 4330, Fall 2018
 * SQL Client Main
 */

package SQLClient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SQLClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("SQLClient.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setTitle("MySQL Client");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
