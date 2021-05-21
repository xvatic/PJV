package thedrake;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

public class GameState implements JSONSerializable{
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if(side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if(sideOnTurn == PlayingSide.BLUE)
            return orangeArmy;

        return blueArmy;
    }

    public Tile tileAt(TilePos pos) {
        if (this.blueArmy.boardTroops().at(pos).isPresent()) {
            return this.blueArmy.boardTroops().at(pos).get();
        }
        if (this.orangeArmy.boardTroops().at(pos).isPresent()) {
            return this.orangeArmy.boardTroops().at(pos).get();
        }
        return board.at(pos);
    }

    private boolean canStepFrom(TilePos origin) {
        if (this.result != GameResult.IN_PLAY) {
            return false;
        }

        if (this.sideOnTurn == PlayingSide.BLUE) {
            if (this.blueArmy.boardTroops().isPlacingGuards()) {
                return false;
            }
            return this.blueArmy.boardTroops().at(origin).isPresent();

        } else {
            if (this.orangeArmy.boardTroops().isPlacingGuards()) {
                return false;
            }
            return this.orangeArmy.boardTroops().at(origin).isPresent();
        }


    }

    private boolean canStepTo(TilePos target) {
        if (this.result != GameResult.IN_PLAY || target == TilePos.OFF_BOARD) {
            return false;
        }

        if (this.orangeArmy.boardTroops().at(target).isPresent() || this.blueArmy.boardTroops().at(target).isPresent()) {
           return false;
        }

        return this.board.at(target).canStepOn();
    }

    private boolean canCaptureOn(TilePos target) {
        if (this.result != GameResult.IN_PLAY) {
            return false;
        }
        if (this.sideOnTurn == PlayingSide.BLUE) {
            if (this.orangeArmy.boardTroops().at(target).isPresent())
                return true;

        } else {
            if (this.blueArmy.boardTroops().at(target).isPresent())
                return true;

        }
       return false;
    }

    public boolean canStep(TilePos origin, TilePos target)  {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target)  {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        if (this.result != GameResult.IN_PLAY || target == TilePos.OFF_BOARD) {
            return false;
        }


        if (this.sideOnTurn == PlayingSide.BLUE) {
            if (this.blueArmy.stack().isEmpty()){
                return false;
            }
            if (!this.blueArmy.boardTroops().isLeaderPlaced()) {
                return target.j() == 0 && !this.orangeArmy.boardTroops().at(target).isPresent() && !this.blueArmy.boardTroops().at(target).isPresent() && this.board.at(target) != BoardTile.MOUNTAIN;
            } if (this.blueArmy.boardTroops().isPlacingGuards()){
                for (TilePos pos:this.blueArmy.boardTroops().leaderPosition().neighbours()) {
                    if (pos.equalsTo(target.i(), target.j()) && !this.orangeArmy.boardTroops().at(target).isPresent() && !this.blueArmy.boardTroops().at(target).isPresent() && this.board.at(target) != BoardTile.MOUNTAIN) {
                        return true;
                    }
                }
                return false;
            } else {
                for (TilePos pos : target.neighbours()) {
                    if (this.blueArmy.boardTroops().at(pos).isPresent() && !this.orangeArmy.boardTroops().at(target).isPresent() && !this.blueArmy.boardTroops().at(target).isPresent()&& this.board.at(target) != BoardTile.MOUNTAIN)
                        return true;
                }
                return false;
            }

        } else {
            if (this.orangeArmy.stack().isEmpty()){
                return false;
            }
            if (!this.orangeArmy.boardTroops().isLeaderPlaced()) {
                return (this.board.dimension() -target.j() == 1)  && !this.blueArmy.boardTroops().at(target).isPresent() && this.board.at(target) != BoardTile.MOUNTAIN;
            }
            if (this.orangeArmy.boardTroops().isPlacingGuards()) {
                for (TilePos pos:this.orangeArmy.boardTroops().leaderPosition().neighbours()) {
                    if (pos.equalsTo(target.i(), target.j()) && !this.blueArmy.boardTroops().at(target).isPresent() && !this.orangeArmy.boardTroops().at(target).isPresent() && this.board.at(target) != BoardTile.MOUNTAIN) {
                        return true;
                    }
                }
                return false;
            } else {
                for (TilePos pos : target.neighbours()) {
                    if (this.orangeArmy.boardTroops().at(pos).isPresent() && !this.blueArmy.boardTroops().at(target).isPresent() && !this.orangeArmy.boardTroops().at(target).isPresent()&& this.board.at(target) != BoardTile.MOUNTAIN)
                        return true;
                }
                return false;
            }
        }
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if(canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if(canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if(canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }
    public List<Troop>  getBlueStack() {
        return blueArmy.stack();
    }
    public List<Troop>  getBlueCaptured() {
        return blueArmy.captured();
    }

    public List<Troop>  getOrangeStack() {
        return orangeArmy.stack();
    }
    public List<Troop>  getOrangeCaptured() {
        return orangeArmy.captured();
    }

    public GameState placeFromStack(BoardPos target) {
        if(canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if(armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("{\"result\":");
        this.result.toJSON(writer);
        writer.print(",\"board\":");
        this.board().toJSON(writer);
        writer.print(",\"blueArmy\":");
        this.blueArmy.toJSON(writer);
        writer.print(",\"orangeArmy\":");
        this.orangeArmy.toJSON(writer);
        writer.print("}");
    }
}
