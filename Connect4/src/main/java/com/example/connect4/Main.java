package com.example.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayGround();
        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        Scene scene = new Scene(rootGridPane);
        stage.setTitle("Connect 4 Game");
        stage.setScene(scene);
        stage.show();
    }

    private MenuBar createMenu() {
        Menu fileMenu = new Menu("File");
        MenuItem newMenuItem = new MenuItem("New Game");
        newMenuItem.setOnAction(actionEvent -> resetGame());
        MenuItem resetMenuItem = new MenuItem("Reset Game");
        resetMenuItem.setOnAction(actionEvent -> resetGame());
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        MenuItem exitMenuItem = new MenuItem("Exit Game");
        exitMenuItem.setOnAction(actionEvent -> exitGame());
        fileMenu.getItems().addAll(newMenuItem, resetMenuItem, separator1, exitMenuItem);
        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Game");
        aboutGame.setOnAction(actionEvent -> aboutConnect4());
        SeparatorMenuItem separator2 = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutDeveloper());
        helpMenu.getItems().addAll(aboutGame, separator2, aboutMe);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private void aboutDeveloper() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText("Sreeja");
        alert.setContentText("Hello! This is my first game made with Java.");
        alert.setResizable(true);
        alert.show();
    }

    private void aboutConnect4() {
        String gameDescription = "Connect Four is a two-player connection game in which the players first choose a color"+
                " and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended"+
                " grid. The pieces fall straight down, occupying the next available space within the column. The objective "+
                "of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. "+
                "Connect Four is a solved game. The first player can always win by playing the right moves.";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Game");
        alert.setHeaderText("How to play ?");
        alert.setContentText(gameDescription);
        alert.setResizable(true);
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
        controller.resetGame();
    }
}