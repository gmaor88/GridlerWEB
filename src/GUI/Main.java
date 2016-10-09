package GUI;

/**
 * Created by dan on 9/5/2016.
 */

        import javafx.application.Application;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/MainView.fxml"));
        Parent root = fxmlLoader.load();
        MainViewController controller = (MainViewController) fxmlLoader.getController();
        controller.init(primaryStage);
        primaryStage.setTitle("Gridler 2.0");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        scene.getStylesheets().addAll(getClass().getResource("CSS/defaultSkin.css").toExternalForm());
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
