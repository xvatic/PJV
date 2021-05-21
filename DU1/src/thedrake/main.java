package thedrake;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application{


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainui.fxml"));
        String css = this.getClass().getResource("style.css").toExternalForm();

        primaryStage.setTitle("The Drake");
        Scene scene = new Scene(root,1200, 800);
        scene.getStylesheets().add(css);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();


    }
    public static void main(String[] args) {
        launch(args);
    }
}