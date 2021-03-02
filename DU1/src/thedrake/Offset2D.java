package thedrake;

public class Offset2D {
    public final int x;
    public final int y;

    public Offset2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equalsTo(int x, int y) {
        if(this.x == x && this.y == y) {
            return true;
        } else {
            return false;
        }
    }

    public Offset2D yFlipped() {
        Offset2D flippedOffset = new Offset2D(this.x, -this.y);
        return flippedOffset;
    }



}
