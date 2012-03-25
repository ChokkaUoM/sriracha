package sriracha.frontend.android;

import android.graphics.*;

import java.util.*;

public class WireNode
{
    private ArrayList<WireNode> neighbours = new ArrayList<WireNode>(4); // Sticking to a 90deg grid.

    public int x;
    public int y;

    public WireNode(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public WireNode(Point point)
    {
        x = point.x;
        y = point.y;
    }
    public WireNode(WireNode node)
    {
        this(node.x, node.y);
    }

    public ArrayList<WireNode> getNeighbours() { return neighbours; }
    public void addNeighbour(WireNode neighbour)
    {
        neighbours.add(neighbour);
    }
    
    public boolean hasNeighbourOpposite(WireNode neighbour)
    {
        for (WireNode possibleOpposite : neighbours)
        {
            if (possibleOpposite != neighbour && (possibleOpposite.x == neighbour.x || possibleOpposite.y == neighbour.y))
                return true;
        }
        return false;
    }
}
