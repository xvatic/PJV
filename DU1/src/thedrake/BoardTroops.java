package thedrake;

import java.io.PrintWriter;
import java.util.*;

public class BoardTroops implements JSONSerializable{
    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private final TilePos leaderPosition;
    private final int guards;
    private boolean midGame = false;
    //private Object IllegalArgumentException;

    public BoardTroops(PlayingSide playingSide) {
        this.guards = 0;
        this.leaderPosition = TilePos.OFF_BOARD;
        this.troopMap = Collections.emptyMap();
        this.playingSide = playingSide;
    }

    public BoardTroops(
            PlayingSide playingSide,
            Map<BoardPos, TroopTile> troopMap,
            TilePos leaderPosition,
            int guards) {

            this.guards = guards;
            this.leaderPosition = leaderPosition;
            this.troopMap = troopMap;
            this.playingSide = playingSide;

    }

    public Optional<TroopTile> at(TilePos pos) {
        return Optional.ofNullable( troopMap.get(pos));
    }

    public PlayingSide playingSide() {
        return playingSide;
    }

    public TilePos leaderPosition() {
        return leaderPosition;
    }

    public int guards() {
        return guards;
    }

    public boolean isLeaderPlaced() {
        return leaderPosition != TilePos.OFF_BOARD;
    }

    public boolean isPlacingGuards() {
        if (midGame) {
            return false;
        }
        return this.leaderPosition != TilePos.OFF_BOARD && this.guards < 2;
    }

    public Set<BoardPos> troopPositions() {
        Set<BoardPos> pos = new HashSet<BoardPos>();
        for (Map.Entry<BoardPos, TroopTile> entry : this.troopMap.entrySet()){
            if (entry.getValue().hasTroop())
                pos.add(entry.getKey());
        }
        return pos;
    }

    public BoardTroops placeTroop(Troop troop, BoardPos target) throws  IllegalArgumentException {
        if (this.troopMap.containsKey(target)) {
            throw new IllegalArgumentException();
        }
        TroopTile tile = new TroopTile(troop, this.playingSide, TroopFace.AVERS);
        Map<BoardPos, TroopTile> newMap = new HashMap<BoardPos, TroopTile>();
        for (Map.Entry<BoardPos, TroopTile> entry : this.troopMap.entrySet()){
            newMap.put(entry.getKey(), entry.getValue());
        }
        newMap.put(target, tile);
        int guards = 0;
        TilePos leaderPosition = this.leaderPosition;
        if (!this.isLeaderPlaced()) {
            guards+= this.guards;
            leaderPosition = target;
        } else if (this.isPlacingGuards()){
            guards+= this.guards + 1;
        } else {
            guards+= this.guards;
        }
        if (guards >= 2) {
            this.midGame = true;
        }
        return new BoardTroops(this.playingSide, newMap, leaderPosition, guards);
    }

    public BoardTroops troopStep(BoardPos origin, BoardPos target) throws IllegalArgumentException, IllegalStateException{
        if(!this.isLeaderPlaced() ) {
            throw new IllegalStateException();
        }
        if (this.troopMap.containsKey(target) || !this.troopMap.containsKey(origin)) {
            throw new IllegalArgumentException();
        }
        Map<BoardPos, TroopTile> newMap = new HashMap<BoardPos, TroopTile>();
        for (Map.Entry<BoardPos, TroopTile> entry : this.troopMap.entrySet()){
            if(origin.equalsTo(entry.getKey().i(), entry.getKey().j())) {
                newMap.put(target, entry.getValue().flipped());

            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (origin.equalsTo(leaderPosition().i(), leaderPosition().j())) {
            return new BoardTroops(this.playingSide, newMap, target, this.guards);
        } else return new BoardTroops(this.playingSide, newMap, this.leaderPosition , this.guards);
    }

    public BoardTroops troopFlip(BoardPos origin) {
        if(!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if(isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if(!at(origin).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    public BoardTroops removeTroop(BoardPos target) throws IllegalArgumentException, IllegalStateException {
        if(!this.isLeaderPlaced() || this.isPlacingGuards()) {
            throw new IllegalStateException();
        }
        if (!this.troopMap.containsKey(target)) {
            throw new IllegalArgumentException();
        }
        int guards = this.guards;
        Map<BoardPos, TroopTile> newMap = new HashMap<BoardPos, TroopTile>();
        for (Map.Entry<BoardPos, TroopTile> entry : this.troopMap.entrySet()){
            if(target.equalsTo(entry.getKey().i(), entry.getKey().j())) {
                guards--;
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (target.equalsTo(leaderPosition().i(), leaderPosition().j())) {
            return new BoardTroops(this.playingSide, newMap, TilePos.OFF_BOARD, this.guards);
        } else if (this.troopMap.size() >= 3) {
            return new BoardTroops(this.playingSide, newMap, this.leaderPosition , this.guards);
        }
         else    return new BoardTroops(this.playingSide, newMap, this.leaderPosition , guards);

    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("{\"side\":");
        this.playingSide().toJSON(writer);
        writer.print(",\"leaderPosition\":");
        this.leaderPosition.toJSON(writer);
        writer.print(",\"guards\":" + (char)(this.guards+'0'));
        writer.print(",\"troopMap\":{");
        SortedSet<BoardPos> coords = new TreeSet<>(this.troopMap.keySet());
        int count = 0;
        for(BoardPos value : coords) {
            value.toJSON(writer);
            writer.print(":");
            this.troopMap.get(value).toJSON(writer);
            if (count != coords.size()-1) {
                writer.print(",");
                count++;
            }
        }
        writer.print("}}");
    }
}

