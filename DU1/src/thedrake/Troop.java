package thedrake;

import java.io.PrintWriter;
import java.util.List;

public class Troop implements JSONSerializable{

    private final Offset2D aversPivot;
    private final Offset2D reversPivot;
    private final String name;
    private List<TroopAction> aversActions;
    private List<TroopAction> reversActions;

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = new Offset2D(aversPivot.x, aversPivot.y);
        this.reversPivot = new Offset2D(reversPivot.x, reversPivot.y);
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public Troop(String name, Offset2D pivot,List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = new Offset2D(pivot.x, pivot.y);
        this.reversPivot = new Offset2D(pivot.x, pivot.y);
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public Troop(String name, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = new Offset2D(1, 1);
        this.reversPivot = new Offset2D(1, 1);
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public String name() {
        return name;
    }

    public List<TroopAction> actions(TroopFace face) {
        if (face == TroopFace.AVERS){
            return this.aversActions;
        } else {
            return this.reversActions;
        }
    }

    public Offset2D pivot(TroopFace face) {
        if (face == TroopFace.AVERS) {
            return aversPivot;
        } else {
            return  reversPivot;
        }
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("\"" + this.name() + "\"");
    }
}
