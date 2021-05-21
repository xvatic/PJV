package thedrake;

import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class TroopTile implements Tile, JSONSerializable{

    @Override
    public boolean canStepOn() {
        return false;
    }

    @Override
    public boolean hasTroop() {
        return true;
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {
        List<Move> result = new ArrayList<>();
        for (TroopAction action :this.troop.actions(this.face)) {
            result.addAll(action.movesFrom(pos, this.side, state));
        }
        return result;
    }

    private Troop troop;
    private PlayingSide side;
    private TroopFace face;

    public TroopTile(Troop troop, PlayingSide side, TroopFace face) {
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    public PlayingSide side() {
        return side;
    }

    public TroopFace face() {
        return face;
    }

    public Troop troop() {
        return troop;
    }


    public TroopTile flipped() {
        TroopFace faceFlipped;
        if (face == TroopFace.AVERS) {
            faceFlipped = TroopFace.REVERS;
        } else {
            faceFlipped = TroopFace.AVERS;
        }
        return new TroopTile(troop, side, faceFlipped);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("{\"troop\":");
        this.troop.toJSON(writer);
        writer.print(",\"side\":");
        this.side.toJSON(writer);
        writer.print(",\"face\":");
        this.face.toJSON(writer);
        writer.print("}");
    }
}
