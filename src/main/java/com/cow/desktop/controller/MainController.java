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

import java.io.IOException;
import java.util.ArrayList;

public class MainController {
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
    boolean isSelectingTask = false;


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

    public void btnStartHandler(ActionEvent actionEvent) {
        if (timeline == null) {
            timeline = new Timeline(
                    new KeyFrame(
                            Duration.millis(100),
                            ae -> {
                                time[0]++;
                                String hours = String.valueOf(time[0] / 36000);
                                String minutes = String.valueOf((time[0] / 600)-(time[0] / 36000)*60);
                                //String seconds = String.valueOf(time[0] % 6000);
                                String seconds = String.valueOf(time[0] / 10 - (time[0] / 600)*60);
                                if (hours.length() == 1)
                                    hours = "0" + hours;
                                if (minutes.length() == 1)
                                    minutes = "0" + minutes;
                                if (seconds.length() == 1)
                                    seconds = "0" + seconds;

                                lblTimer.setText(hours + ":" + minutes + ":" + seconds);
                            }
                    )
            );

            timeline.setCycleCount(Animation.INDEFINITE); //Animation.INDEFINITE - бесконечное повторение
        }
        timeline.play();
    }

    public void btnPauseHandler(ActionEvent actionEvent) {
        if (timeline != null)
            timeline.pause();
    }

    public void btnStopHandler(ActionEvent actionEvent) {
        if (timeline != null){
            timeline.stop();
            timeline = null;
            time[0] = 0;
            lblTimer.setText("00:00:00");
        }
    }

    public void btnCompleteClicked(ActionEvent actionEvent) {
        lblSelectedTask.setText("");
        selectedTask = "";
        btnStopHandler(actionEvent);
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
        mainListView.getItems().clear();
        teams.add("Team1");
        teams.add("Team2");
        teams.add("Team3");
        teams.add("Team4");
        teams.add("Team5");
        teams.add("Team6");

        ObservableList<String> langs = FXCollections.observableArrayList(teams);
        mainListView.setItems(langs);
    }

    public void btnSelectTaskClicked(ActionEvent actionEvent) {
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
        tasks.add("Task1");
        tasks.add("Task2");
        tasks.add("Task3");
        tasks.add("Task4");
        tasks.add("Task5");
        tasks.add("Task6");
        tasks.add("Task7");
        tasks.add("Task8");
        tasks.add("Task9");
        tasks.add("Task10");
        tasks.add("Task11");
        tasks.add("Task12");
        tasks.add("Task13");
        tasks.add("Task14");
        tasks.add("Task15");
        tasks.add("Task16");
        tasks.add("Task17");
        tasks.add("Task18");
        tasks.add("Task19");
        tasks.add("Task20");

        ObservableList<String> langs = FXCollections.observableArrayList(tasks);
        mainListView.setItems(langs);
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
            }
        } else {
            if (mainListView.getSelectionModel().getSelectedItem() != null) {
                getSelectedTeam = mainListView.getSelectionModel().getSelectedItem().toString();
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
}
