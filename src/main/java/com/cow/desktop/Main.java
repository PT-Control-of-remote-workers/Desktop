package com.cow.desktop;

import com.cow.desktop.controller.SignUpController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        FXMLLoader loader= new FXMLLoader(getClass().getResource("/fxml/signUpStyle.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("COW");
        Scene scene = new Scene(root, Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

        SignUpController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
