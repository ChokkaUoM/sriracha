package sriracha.frontend.util;

public class Vector2
{
    public static final Vector2 Zero = new Vector2(0, 0);

    private float x;
    private float y;

    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
}
