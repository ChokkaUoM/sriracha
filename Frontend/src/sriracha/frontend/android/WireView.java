package sriracha.frontend.android;

import android.content.*;
import android.graphics.*;
import android.view.*;
import sriracha.frontend.android.model.*;

import java.util.*;

public class WireView extends View
{
    private CircuitElementPortView start;
    private CircuitElementPortView end;
    private ArrayList<WireNode> vertices = new ArrayList<WireNode>();

    private OnAddVertex onAddVertexListener;

    private final Paint paint = new Paint();

    public WireView(Context context, CircuitElementPortView start)
    {
        super(context);

        this.start = start;
        addVertex(getPortPositionAsNode(start));

        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(4);
    }

    public CircuitElementPortView getStart() { return start; }
    public CircuitElementPortView getEnd() { return end; }
    public void setEnd(CircuitElementPortView end)
    {
        connectToPoint(getPortPositionAsPoint(end));
        this.end = end;
    }

    public ArrayList<WireNode> getVertices()
    {
        return vertices;
    }

    public void setOnAddVertexListener(OnAddVertex onAddVertexListener)
    {
        this.onAddVertexListener = onAddVertexListener;
    }

    public WireNode connectToPoint(Point vertex)
    {
        addIntermediateVertices(vertex);
        WireNode newNode = new WireNode(vertex);
        addVertex(newNode);
        invalidate();
        return newNode;
    }

    private void addIntermediateVertices(Point vertex)
    {
        WireNode lastVertex = vertices.get(vertices.size() - 1);

        if (lastVertex.x != vertex.x && lastVertex.y != vertex.y)
            addVertex(new WireNode(lastVertex.x, vertex.y));
    }

    public void addVertex(WireNode vertex)
    {
        if (vertices.size() > 0)
            vertex.addNeighbour(vertices.get(vertices.size() - 1));
        vertices.add(vertex);
        triggerOnAddVertex();
        invalidate();
    }

    public void insertBefore(WireNode toInsert, WireNode beforeThis)
    {
        int beforeIndex = vertices.indexOf(beforeThis);
        if (beforeIndex == -1)
            throw new IllegalArgumentException("beforeThis");

        if (beforeIndex > 0)
            toInsert.addNeighbour(vertices.get(beforeIndex - 1));
        
        toInsert.addNeighbour(beforeThis);
        vertices.add(beforeIndex, toInsert);
        triggerOnAddVertex();
        invalidate();
    }

    public void insertAfter(WireNode toInsert, WireNode afterThis)
    {
        int afterIndex = vertices.indexOf(afterThis);
        if (afterIndex == -1)
            throw new IllegalArgumentException("afterThis");

        toInsert.addNeighbour(afterThis);
        if (afterIndex < vertices.size() - 1)
            toInsert.addNeighbour(vertices.get(afterIndex + 1));

        vertices.add(afterIndex + 1, toInsert);
        triggerOnAddVertex();
        invalidate();
    }
    
    private void triggerOnAddVertex()
    {
        if (onAddVertexListener != null)
            onAddVertexListener.onAddVertex(this);
    }

    private WireNode getPortPositionAsNode(CircuitElementPortView port)
    {
        CircuitElementView element = port.getElement();
        float[] position = port.getTransformedPosition();
        return new WireNode((int) (element.getPositionX() + element.getWidth() * position[0]),
                (int) (element.getPositionY() + element.getHeight() * position[1]));
    }

    private Point getPortPositionAsPoint(CircuitElementPortView port)
    {
        CircuitElementView element = port.getElement();
        float[] position = port.getTransformedPosition();
        return new Point((int) (element.getPositionX() + element.getWidth() * position[0]),
                (int) (element.getPositionY() + element.getHeight() * position[1]));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        ArrayList<WireNode> allVertices = new ArrayList<WireNode>();
        allVertices.add(getPortPositionAsNode(start));
        allVertices.addAll(vertices);
        if (end != null)
            allVertices.add(getPortPositionAsNode(end));

        for (int i = 1; i < allVertices.size(); i++)
        {
            WireNode from = allVertices.get(i - 1);
            WireNode to = allVertices.get(i);
            canvas.drawLine(from.x, from.y, to.x, to.y, paint);

            if (i < allVertices.size() - 1)
                canvas.drawCircle(to.x, to.y, 4, paint);
        }
    }

    public interface OnAddVertex
    {
        public void onAddVertex(WireView wireView);
    }
}
