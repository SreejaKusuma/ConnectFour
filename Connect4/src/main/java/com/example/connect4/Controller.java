package com.example.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
    private static final int columns = 7;
    private static final int rows = 6;
    private static final int circleDiameter = 80;
    private static final String discColor1 = "#1aa3ff";
    private static final String discColor2 = "#002e4d";
    private static String playerOne = "";
    private static String playerTwo = "";
    private boolean isPlayerOneTurn = true;
    private boolean isAllowedToInsert = true;
    private boolean isStart = true;
    private boolean isClicked = false;
    private Disc[][] insertedDiscsArray = new Disc[rows][columns];


    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertedDiscsPane;
    @FXML
    public Label playerNameLabel;
    @FXML
    public TextField playerOneTextField, playerTwoTextField;
    @FXML
    public Button setNamesButton;


    public void createPlayGround(){
        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles, 0, 1);
        List<Rectangle> rectangleList = createClickableColumns();
        for(Rectangle rectangle: rectangleList){
            rootGridPane.add(rectangle, 0, 1);
        }

        setNamesButton.setOnAction(actionEvent -> {
            isClicked=true;
            while(isStart){
                playerOne = playerOneTextField.getText();
                playerTwo = playerTwoTextField.getText();
                playerNameLabel.setText(playerOne);
                isStart = false;
            }
        });
    }

    private Shape createGameStructuralGrid()
    {
        Shape rectangleWithHoles = new Rectangle((columns+1)*circleDiameter, (rows+1)*circleDiameter);
        for(int row = 0; row<rows; row++){
            for(int col = 0; col<columns; col++){
                Circle circle = new Circle();
                circle.setRadius(circleDiameter/2);
                circle.setCenterX(circleDiameter/2);
                circle.setCenterY(circleDiameter/2);
                circle.setSmooth(true);
                circle.setTranslateX(col*(circleDiameter +5)+(circleDiameter/4));
                circle.setTranslateY(row*(circleDiameter+5)+(circleDiameter/4));
                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;
    }
    private List<Rectangle> createClickableColumns(){
        List<Rectangle> rectangleList = new ArrayList();
        for(int col = 0; col<columns; col++){
            Rectangle rectangle = new Rectangle(circleDiameter, (rows+1)*circleDiameter);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX((col*(circleDiameter+5)) + circleDiameter/4);
            rectangle.setOnMouseEntered(actionEvent-> rectangle.setFill(Color.valueOf("#00000008")));
            rectangle.setOnMouseExited(actionEvent-> rectangle.setFill(Color.TRANSPARENT));
            final int column = col;

            rectangle.setOnMouseClicked(actionEvent-> {
                if(isAllowedToInsert)
                {
                    isAllowedToInsert=false;
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }
            });
            rectangleList.add(rectangle);
        }
        return rectangleList;
    }

    private void insertDisc(Disc disc, int column) {
        int row = rows-1;
        while(row >= 0){
            if(getDiscIfPresent(row, column)==null){
                break;
            }
            row--;
        }
        if(row<0){
            return;
        }
        insertedDiscsArray[row][column] = disc;
        int currentRow = row;
        disc.setTranslateX(column*(circleDiameter +5)+(circleDiameter/4));
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.3), disc);
        translateTransition.setToY((row*(circleDiameter+5))+(circleDiameter/4));
        translateTransition.setOnFinished(actionEvent ->{
            isAllowedToInsert = true;
            if(gameEnded(currentRow, column)){
                gameOver();
                return;
            }
            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? playerOne: playerTwo);
        });
        translateTransition.play();
        insertedDiscsPane.getChildren().add(disc);
    }

    private void gameOver() {
        String winner = isPlayerOneTurn? playerOne: playerTwo;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Connect 4");
        alert.setHeaderText("The winner is "+ winner);
        isStart = true;
        alert.setContentText("Want to play again?");
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No, exit");
        alert.getButtonTypes().setAll(yesButton, noButton);
        Platform.runLater(()->{
            Optional<ButtonType> clickedButton = alert.showAndWait();
            if(clickedButton.get() == yesButton){
                resetGame();
            }else{
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public void resetGame() {
        insertedDiscsPane.getChildren().clear();
        for (int row = 0; row < insertedDiscsArray.length; row++) {
            for (int column = 0; column < insertedDiscsArray[row].length; column++) {
                insertedDiscsArray[row][column]=null;
            }
        }
        isPlayerOneTurn=true;
        playerNameLabel.setText(playerOne);
        createPlayGround();
    }

    private boolean gameEnded(int row, int column) {
        List<Point2D> verticalPoints = IntStream.rangeClosed(row-3, row+3).mapToObj(r->new Point2D(r, column)).collect(Collectors.toList());
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3, column+3).mapToObj(c-> new Point2D(row, c)).collect(Collectors.toList());
        Point2D start1Point = new Point2D(row-3, column+3);
        List<Point2D> diagonal1points = IntStream.rangeClosed(0, 6).mapToObj(i-> start1Point.add(i, -i)).collect(Collectors.toList());
        Point2D start2Point = new Point2D(row-3, column-3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6).mapToObj(i-> start2Point.add(i, i)).collect(Collectors.toList());
        boolean isEnded = checkCombinations(verticalPoints)||checkCombinations(horizontalPoints)||checkCombinations(diagonal1points)||checkCombinations(diagonal2Points);
        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain = 0;
        for(Point2D point : points){
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();
            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);
            if(disc!=null&&disc.isPlayerOneMove==isPlayerOneTurn){
                chain++;
                if(chain==4){
                    return true;
                }
            }else{
                chain = 0;
            }
        }
        return false;
    }

    private Disc getDiscIfPresent(int row, int column) {
        if(row>=rows||row<0||column>=columns||column<0){
            return null;
        } else {
            return  insertedDiscsArray[row][column];
        }
    }

    private static class Disc extends Circle{
        private boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(circleDiameter/2);
            setFill(isPlayerOneMove? Color.valueOf(discColor1):Color.valueOf(discColor2));
            setCenterX(circleDiameter/2);
            setCenterY(circleDiameter/2);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}