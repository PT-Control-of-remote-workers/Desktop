package com.cow.desktop.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    public TextField textFieldLogin;
    public PasswordField textFieldPassword;
    public Button buttonLogIn;
    public Button btnClose;
    public Button btnRollUp;
    private Stage signUpStage;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void buttonLogInClicked(ActionEvent actionEvent) throws IOException {
        String trueLogin = "denis"; //заглушка
        String truePass = "qwerty123"; //заглушка
        /*
        * производим авторизацию
        * */
        //if(textFieldLogin.getText().equals(trueLogin) && textFieldPassword.getText().equals(truePass)) {
            Stage mainStage = new Stage();
            mainStage.setX(signUpStage.getX());
            mainStage.setY(signUpStage.getY());

            mainStage.initStyle(StageStyle.TRANSPARENT);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainStyle.fxml"));
            Parent root = loader.load();
            mainStage.setTitle("COW");
            Scene scene = new Scene(root, Color.TRANSPARENT);
            mainStage.setScene(scene);
            mainStage.show();

            MainController controller = loader.getController();
            controller.setPrimaryStage(mainStage);

            signUpStage.hide();
        //}
    }

    public void paneSignUpDragged(MouseEvent event) {
        try {
            signUpStage.setX(event.getScreenX() + xOffset);
            signUpStage.setY(event.getScreenY() + yOffset);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void paneSignUpPressed(MouseEvent event) {
        try {
        xOffset = signUpStage.getX() - event.getScreenX();
        yOffset = signUpStage.getY() - event.getScreenY();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void setPrimaryStage(Stage signUpStage) {
        this.signUpStage = signUpStage;
    }

    public void btnCloseClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void btnRollUpClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) btnRollUp.getScene().getWindow();
        stage.setIconified(true);
    }
}
