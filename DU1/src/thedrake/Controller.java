package thedrake;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Controller {
    @FXML
    public Button exitButton;
    public Button duelButton;

    @FXML
    private ImageView logo;



    public void runGame(ActionEvent event) throws IOException {
        BoardView boardView = new BoardView(createSampleGameState());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gameui.fxml"));
        AnchorPane gameForm = loader.load();
        ((Pane) gameForm.getChildren().get(0)).getChildren().add(boardView);
        GameController controller = loader.getController();
        boardView.setContext(controller);
        controller.fillBlueStackAndCaptured();
        controller.fillOrangeStackAndCaptured();
        Scene game = new Scene(gameForm);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(game);
        window.show();
    }

    private static GameState createSampleGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board);
    }

    public void exit() {
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        duelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    runGame(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void initialize() throws FileNotFoundException {

        exit();

    }

}
