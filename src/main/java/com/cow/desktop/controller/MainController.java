package com.cow.desktop.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainController {
    public Label lblUserName;
    String workerid = "";
    SignUpController signUpController;
    
    public Button btnComplete;
    public Button btnSelectTeam;
    public Button btnSelectTask;
    public Button btnClose;
    public Button btnRollUp;
    public ListView mainListView;
    public Button selectButton;
    public Button btnStart;
    public Button btnPause;
    public Button btnStop;
    public Label lblSelectedTask;
    public Button btnSettings;
    public AnchorPane settingsPane;
    public Button btnSaveSettings;
    public VBox mainVbox;
    public AnchorPane mainMiddlePane;
    public AnchorPane mainBottomPane;
    Timeline timeline;
    public Label lblTimer;
    int[] time = {0};
    private Stage mainStage;
    private double xOffset = 0;
    private double yOffset = 0;
    ArrayList<String> tasks = new ArrayList<String>();
    ArrayList<String> teams = new ArrayList<String>();
    String selectedTask;
    String getSelectedTeam;
    int selectedTeamId = -1;
    int selectedTaskId = -1;
    boolean isSelectingTask = false;
    JSONArray jsonTeams = null;
    JSONArray jsonTasks = null;

    private static final String URL = "http://localhost:8081";
    private static final String getTeamsByWorkerURL = URL + "/api/v1/teams/workers";
    private static final String getTasksByWorkerAndTeamURL = URL + "/api/v1/task/getByWorkerAndTeam";
    private static final String taskCompleteURL = URL + "/api/v1/tasks";
    private static final String callURL = "http://localhost:8080/api/v1/call";

    String token = "";
    


    public void setPrimaryStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void paneSignUpDragged(MouseEvent event) {
        try {
            mainStage.setX(event.getScreenX() + xOffset);
            mainStage.setY(event.getScreenY() + yOffset);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void paneSignUpPressed(MouseEvent event) {
        try {
            xOffset = mainStage.getX() - event.getScreenX();
            yOffset = mainStage.getY() - event.getScreenY();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void sendCall(String status){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("process", "none");
            jsonObject.put("taskId", selectedTaskId);
            jsonObject.put("teamId", selectedTeamId);
            jsonObject.put("type", status);
            jsonObject.put("workerId", workerid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(callURL)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnStartHandler(ActionEvent actionEvent) {
        if (selectedTaskId != -1) {
            sendCall("WORKING");
            if (timeline == null) {
                timeline = new Timeline(
                        new KeyFrame(
                                Duration.millis(100),
                                ae -> {
                                    time[0]++;
                                    String hours = String.valueOf(time[0] / 36000);
                                    String minutes = String.valueOf((time[0] / 600) - (time[0] / 36000) * 60);
                                    //String seconds = String.valueOf(time[0] % 6000);
                                    String seconds = String.valueOf(time[0] / 10 - (time[0] / 600) * 60);
                                    if (hours.length() == 1)
                                        hours = "0" + hours;
                                    if (minutes.length() == 1)
                                        minutes = "0" + minutes;
                                    if (seconds.length() == 1)
                                        seconds = "0" + seconds;

                                    lblTimer.setText(hours + ":" + minutes + ":" + seconds);

                                    if (time[0]%3000==0){
                                        sendCall("WORKING");
                                    }
                                }
                        )
                );
                timeline.setCycleCount(Animation.INDEFINITE); //Animation.INDEFINITE - бесконечное повторение
            }
            timeline.play();
        }
    }

    public void btnPauseHandler(ActionEvent actionEvent) {
        if (timeline != null) {
            sendCall("PAUSE");
            timeline.pause();
        }
    }

    public void btnStopHandler(ActionEvent actionEvent) {
        if (timeline != null){
            timeline.stop();
            timeline = null;
            time[0] = 0;
            lblTimer.setText("00:00:00");
            selectedTaskId = -1;
        }
    }

    public void btnCompleteClicked(ActionEvent actionEvent) {
        if (selectedTaskId != -1) {

            JSONObject jsonObject = new JSONObject();

            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(taskCompleteURL + "/" + workerid + "/complete")
                        .addHeader("Authorization", "Bearer " + token)
                        .put(body)
                        .build();
                Response responses = null;

                try {
                    responses = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            lblSelectedTask.setText("");
            selectedTask = "";
            btnStopHandler(actionEvent);
        }
    }

    public void btnSelectTeamClicked(ActionEvent actionEvent) {
        isSelectingTask = false;
        btnComplete.setVisible(false);
        btnSelectTask.setVisible(false);
        btnSelectTeam.setVisible(false);
        mainListView.setVisible(true);
        selectButton.setVisible(true);
        btnStart.setVisible(false);
        btnPause.setVisible(false);
        btnStop.setVisible(false);

        teams.clear();

        JSONObject jsonObject = new JSONObject();

        try {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(getTeamsByWorkerURL + "/" + workerid)
                    .addHeader("Authorization", "Bearer " + token)
                    .post(body)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonData = responses.body().string();

            jsonData = "{\"teams\":" + jsonData;
            jsonData = jsonData + "}";
            JSONObject Jobject = new JSONObject(jsonData);
            jsonTeams = Jobject.getJSONArray("teams");

            for (int i = 0; i < jsonTeams.length(); i++) {
                JSONObject object = jsonTeams.getJSONObject(i);
                teams.add(object.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainListView.getItems().clear();
        ObservableList<String> langs = FXCollections.observableArrayList(teams);
        mainListView.setItems(langs);
    }

    public void btnSelectTaskClicked(ActionEvent actionEvent) {
        if (selectedTeamId != -1) {
            isSelectingTask = true;
            btnComplete.setVisible(false);
            btnSelectTask.setVisible(false);
            btnSelectTeam.setVisible(false);
            btnStart.setVisible(false);
            btnPause.setVisible(false);
            btnStop.setVisible(false);

            mainListView.setVisible(true);
            selectButton.setVisible(true);

            tasks.clear();
            mainListView.getItems().clear();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", selectedTeamId);
                jsonObject.put("userId", workerid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(getTasksByWorkerAndTeamURL)
                        .addHeader("Authorization", "Bearer " + token)
                        .post(body)
                        .build();
                Response responses = null;

                try {
                    responses = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonData = responses.body().string();
                jsonData = "{\"tasks\":" + jsonData;
                jsonData = jsonData + "}";

                JSONObject Jobject = new JSONObject(jsonData);
                //System.out.println(jsonData);
                jsonTasks = Jobject.getJSONArray("tasks");

                for (int i = 0; i < jsonTasks.length(); i++) {
                    JSONObject object = jsonTasks.getJSONObject(i);
                    tasks.add(object.getString("name"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ObservableList<String> langs = FXCollections.observableArrayList(tasks);
            mainListView.setItems(langs);
        }
    }

    public void btnCloseClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void btnRollUpClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) btnRollUp.getScene().getWindow();
        stage.setIconified(true);
    }

    public void selectButtonClicked(ActionEvent actionEvent) {
        selectButton.setVisible(false);
        mainListView.setVisible(false);
        btnComplete.setVisible(true);
        btnSelectTask.setVisible(true);
        btnSelectTeam.setVisible(true);

        btnStart.setVisible(true);
        btnPause.setVisible(true);
        btnStop.setVisible(true);

        if (isSelectingTask) {
            if (mainListView.getSelectionModel().getSelectedItem() != null) {
                selectedTask = mainListView.getSelectionModel().getSelectedItem().toString();
                lblSelectedTask.setText(mainListView.getSelectionModel().getSelectedItem().toString());
                selectedTaskId = jsonTasks.getJSONObject(mainListView.getSelectionModel().getSelectedIndex()).getInt("id");
            }
        } else {
            if (mainListView.getSelectionModel().getSelectedItem() != null) {
                getSelectedTeam = mainListView.getSelectionModel().getSelectedItem().toString();
                selectedTeamId = jsonTeams.getJSONObject(mainListView.getSelectionModel().getSelectedIndex()).getInt("id");
            }
        }
    }

    public void btnSettingsClicked(ActionEvent actionEvent) throws IOException {
        mainVbox.setVisible(false);
        mainMiddlePane.setVisible(false);
        settingsPane.setVisible(true);
    }

    public void btnSaveSettingsClicked(ActionEvent actionEvent) {
        mainVbox.setVisible(true);
        mainMiddlePane.setVisible(true);
        settingsPane.setVisible(false);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void btnLogoutClicked(ActionEvent actionEvent) throws IOException {
        signUpController.getSignUpStage().setX(mainStage.getX());
        signUpController.getSignUpStage().setY(mainStage.getY());

        signUpController.getSignUpStage().show();

        mainStage.hide();
    }

    public SignUpController getSignUpController() {
        return signUpController;
    }

    public void setSignUpController(SignUpController signUpController) {
        this.signUpController = signUpController;
    }
}
