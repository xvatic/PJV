package thedrake;

public class Troop {

    private final Offset2D aversPivot;
    private final Offset2D reversPivot;
    private final String name;

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot) {
        this.name = name;
        this.aversPivot = new Offset2D(aversPivot.x, aversPivot.y);
        this.reversPivot = new Offset2D(reversPivot.x, reversPivot.y);
    }

    public Troop(String name, Offset2D pivot) {
        this.name = name;
        this.aversPivot = new Offset2D(pivot.x, pivot.y);
        this.reversPivot = new Offset2D(pivot.x, pivot.y);
    }

    public Troop(String name) {
        this.name = name;
        this.aversPivot = new Offset2D(1, 1);
        this.reversPivot = new Offset2D(1, 1);
    }

    public String name() {
        return name;
    }

    public Offset2D pivot(boolean face) {
        if (face) {
            return aversPivot;
        } else {
            return  reversPivot;
        }
    }
}
