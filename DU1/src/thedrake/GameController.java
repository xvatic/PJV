package thedrake;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GameController implements BoardViewContext{
    @FXML
    public Button orangeStackButton;

    @FXML
    public Button blueStackButton;
    public GridPane orangeCaptured;
    public GridPane blueCaptured;
    PositionFactory positionFactory;

    public GridPane orangeStack;
    public GridPane blueStack;
    public AnchorPane gameBoard;


    public void fillBlueStackAndCaptured() {
        blueStack.getChildren().clear();
        blueCaptured.getChildren().clear();

        List<Troop> stack = ((BoardView) gameBoard.getChildren().get(0)).gameState.getBlueStack();
        List<Troop> captured = ((BoardView) gameBoard.getChildren().get(0)).gameState.getBlueCaptured();

        int row = 0;

        for (int i = 0; i < stack.size(); i++) {
            blueStack.add(new TileView(new BoardPos(4,-1,-1), new TroopTile(stack.get(i), PlayingSide.BLUE, TroopFace.AVERS), null), i, row);
        }
        for (int i = 0; i < captured.size(); i++) {
            blueCaptured.add(new TileView(new BoardPos(4,-1,-1), new TroopTile(captured.get(i), PlayingSide.ORANGE, TroopFace.AVERS), null), i, row);
        }

    }

    public void fillOrangeStackAndCaptured() {
        orangeStack.getChildren().clear();
        orangeCaptured.getChildren().clear();
        int row = 0;
        List<Troop> stack = ((BoardView) gameBoard.getChildren().get(0)).gameState.getOrangeStack();
        List<Troop> captured = ((BoardView) gameBoard.getChildren().get(0)).gameState.getOrangeCaptured();
        for (int i = 0; i < stack.size(); i++) {
            orangeStack.add(new TileView(new BoardPos(4,-1,-1), new TroopTile(stack.get(i), PlayingSide.ORANGE, TroopFace.AVERS), null), i, row);
        }
        for (int i = 0; i < captured.size(); i++) {
            orangeCaptured.add(new TileView(new BoardPos(4,-1,-1), new TroopTile(captured.get(i), PlayingSide.BLUE, TroopFace.AVERS), null), i, row);
        }
    }

    public void bluePlaceFromStack() {
        if (((BoardView) gameBoard.getChildren().get(0)).gameState.armyOnTurn().side() != PlayingSide.BLUE) {
            return;
        }
        List<Troop> stack = ((BoardView) gameBoard.getChildren().get(0)).gameState.getBlueStack();
        if (stack.size() == 0)
            return;
        ((BoardView) gameBoard.getChildren().get(0)).tileViewSelectedStack(new TileView(new BoardPos(4,-1,-1), new TroopTile(stack.get(0), PlayingSide.BLUE, TroopFace.AVERS), (BoardView) gameBoard.getChildren().get(0)));
    }

    public void orangePlaceFromStack() {
        if (((BoardView) gameBoard.getChildren().get(0)).gameState.armyOnTurn().side() != PlayingSide.ORANGE) {
            return;
        }
        List<Troop> stack = ((BoardView) gameBoard.getChildren().get(0)).gameState.getOrangeStack();
        if (stack.size() == 0)
            return;
        ((BoardView) gameBoard.getChildren().get(0)).tileViewSelectedStack(new TileView(new BoardPos(4,-1,-1), new TroopTile(stack.get(0), PlayingSide.ORANGE, TroopFace.AVERS), (BoardView) gameBoard.getChildren().get(0)));
    }


    public void track() {
        orangeStackButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                orangePlaceFromStack();
            }
        });
        blueStackButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                bluePlaceFromStack();
            }
        });
    }

    @FXML
    void initialize() throws FileNotFoundException {

        track();
    }

    @Override
    public void refresh(){

        if (((BoardView) gameBoard.getChildren().get(0)).gameState.result() == GameResult.IN_PLAY) {
            fillOrangeStackAndCaptured();
            fillBlueStackAndCaptured();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("The End");
        if (((BoardView) gameBoard.getChildren().get(0)).gameState.sideOnTurn() == PlayingSide.BLUE && ((BoardView) gameBoard.getChildren().get(0)).gameState.result() == GameResult.VICTORY) {
            alert.setHeaderText("ORANGES WON!");
        } else if (((BoardView) gameBoard.getChildren().get(0)).gameState.sideOnTurn() == PlayingSide.ORANGE && ((BoardView) gameBoard.getChildren().get(0)).gameState.result() == GameResult.VICTORY){
            alert.setHeaderText("BLUES WON!");
        } else if (((BoardView) gameBoard.getChildren().get(0)).gameState.result() == GameResult.DRAW) {
            alert.setHeaderText("DRAW!");
        }

        alert.setContentText("Play again?");
        ButtonType buttonTypeOne = new ButtonType("YES");
        ButtonType buttonTypeTwo = new ButtonType("NO");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
        Optional<ButtonType> result = alert.showAndWait();


        if (result.get() == buttonTypeOne){
            gameBoard.getChildren().clear();
            BoardView boardView = new BoardView(createSampleGameState());
            boardView.setContext(this);
            gameBoard.getChildren().add(boardView);
            fillOrangeStackAndCaptured();
            fillBlueStackAndCaptured();
            return;
        } else {
            Stage window = (Stage) gameBoard.getScene().getWindow();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("mainui.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }


            String css = this.getClass().getResource("style.css").toExternalForm();

            Scene scene = new Scene(root,1200, 800);
            scene.getStylesheets().add(css);
            window.setScene(scene);
            window.show();
        }

    }

    private static GameState createSampleGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board);
    }
}
