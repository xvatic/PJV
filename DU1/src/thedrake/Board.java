package thedrake;

import java.io.PrintWriter;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Board implements JSONSerializable{
    BoardTile mapTiles[][];

    // Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
    public Board(int dimension) {
        mapTiles = new BoardTile[dimension][dimension];
        for (int i = 0; i < mapTiles.length; i++) {
            for (int j = 0; j < mapTiles[i].length; j++) {
                mapTiles[i][j] = BoardTile.EMPTY;
            }
        }


    }

    // Rozměr hrací desky
    public int dimension() {
        return mapTiles.length;
    }

    // Vrací dlaždici na zvolené pozici.
    public BoardTile at(TilePos pos) {
        return mapTiles[pos.i()][pos.j()];
    }

    // Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
    public Board withTiles(TileAt ...ats) {
        // Místo pro váš kód
        Board newBoard = new Board(mapTiles.length);
        newBoard.mapTiles = new BoardTile[mapTiles.length][mapTiles.length];
        for (int i = 0; i<mapTiles.length;i++){
            newBoard.mapTiles[i] = mapTiles[i].clone();
        }
        for (TileAt at:ats) {
            newBoard.mapTiles[at.pos.i()][at.pos.j()] = at.tile;
        }

        return newBoard;

    }

    // Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
    public PositionFactory positionFactory() {
        // Místo pro váš kód
        return new PositionFactory(mapTiles.length);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("{\"dimension\":" + (char)(this.dimension()+'0'));
        writer.print(",\"tiles\":[");
        int count = 0;
        for (int i = 0; i < this.dimension(); i++) {
            for (int j = 0; j < this.dimension(); j++) {
                this.mapTiles[j][i].toJSON(writer);
                if (count != (this.dimension()*this.dimension()) - 1){
                    writer.print(",");
                    count++;
                }
            }
        }

        writer.print("]}");
    }

    public static class TileAt {
        public final BoardPos pos;
        public final BoardTile tile;

        public TileAt(BoardPos pos, BoardTile tile) {
            this.pos = pos;
            this.tile = tile;
        }
    }
}
