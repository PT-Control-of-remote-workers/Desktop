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
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

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

    private final String AUTH_URL = "http://localhost:9090/api/v1/auth/login";
    private final String REFRESH_TOKEN_URL = "http://localhost:9090/api/v1/auth/refresh-token";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void buttonLogInClicked(ActionEvent actionEvent) throws IOException {
        /*
        * производим авторизацию
        * */


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", textFieldPassword.getText());
            jsonObject.put("username", textFieldLogin.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(AUTH_URL)
                    .post(body)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonData = responses.body().string();

            JSONObject Jobject = new JSONObject(jsonData);

            if(responses.isSuccessful()) {
                final String[] token = {Jobject.getString("accessToken")};
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
                controller.setToken(token[0]);
                controller.setPrimaryStage(mainStage);

                Runnable task = new Runnable() {
                    public void run() {
                        while(true) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(REFRESH_TOKEN_URL)
                                        .addHeader("Cookie", "REFRESH_TOKEN=" + token[0])
                                        .get()
                                        .build();
                                Response responses = null;

                                try {
                                    responses = client.newCall(request).execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (responses != null && responses.isSuccessful()) {
                                    String jsonData = responses.body().string();
                                    JSONObject Jobject = new JSONObject(jsonData);

                                    token[0] = Jobject.getString("accessToken");
                                    controller.setToken(token[0]);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                Thread.sleep(3600000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Thread thread = new Thread(task);
                thread.start();

                controller.lblUserName.setText(textFieldLogin.getText());
                controller.workerid = textFieldLogin.getText();
                controller.setSignUpController(this);

                signUpStage.hide();
            } else {
                System.out.println("Not correct login or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Stage getSignUpStage() {
        return signUpStage;
    }
}
