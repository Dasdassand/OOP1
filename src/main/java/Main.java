import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Точка входа
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    /**
     *Загрузка FXML страницы
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Карточная игра Дурак");
        root.setStyle("-fx-background-color: rgba(37, 161, 64, 1);");
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}