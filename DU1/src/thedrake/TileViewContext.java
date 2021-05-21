package thedrake;

import thedrake.Move;

import java.io.IOException;

public interface TileViewContext {

    void tileViewSelected(TileView tileView);

    void executeMove(Move move) throws IOException;

}
