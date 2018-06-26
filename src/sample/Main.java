package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Main extends Application {

    static boolean pressed = false;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("Tetris");

        Scene scene = new Scene(root, 220,420);



        scene.setOnKeyPressed(event -> {

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
            }
        });

        scene.setOnKeyReleased(event -> pressed = false);


        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
