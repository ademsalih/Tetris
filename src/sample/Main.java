package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {


    private static boolean pressed = false;
    private static boolean pressed2 = false;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("View/ny.fxml"));
        Parent root = loader.load();

        root.getStylesheets().add(getClass().getResource("View/startStylesheet.css").toString());

        primaryStage.setTitle("Tetris");

        Scene scene = new Scene(root, 306,450);

        root.requestFocus();

        Button button = (Button) loader.getNamespace().get("newGameButton");

        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                root.requestFocus();
            }
        });

        scene.setOnKeyPressed(event -> {

            if (!Controller.instance.gameOver) {
                if (event.getCode() == KeyCode.RIGHT) {
                    Controller.instance.goRight();
                } else if (event.getCode() == KeyCode.LEFT) {
                    Controller.instance.goLeft();
                } else if ((event.getCode() == KeyCode.X || event.getCode() == KeyCode.UP) && !pressed) {

                    pressed = true;
                    Controller.instance.rotateShape(Direction.ClockWise);

                } else if (event.getCode() == KeyCode.Z && !pressed) {

                    pressed = true;
                    Controller.instance.rotateShape(Direction.CounterClockWise);
                } else if (event.getCode() == KeyCode.DOWN) {
                    Controller.instance.speedUp();
                } else if (event.getCode() == KeyCode.SPACE && !pressed2) {
                    pressed2 = true;
                    Controller.instance.instantDrop();
                } else if (event.getCode() == KeyCode.P) {
                    Controller.instance.pause();
                } else if (event.getCode() == KeyCode.N) {
                    Controller.instance.newGame();
                }
            }

        });

        scene.setOnKeyReleased(event -> {
            if (!Controller.instance.gameOver) {
                pressed = false;
                pressed2 = false;
                if (event.getCode() == KeyCode.DOWN) {
                    Controller.instance.speedDown();
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
