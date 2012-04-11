package sriracha.frontend.resultdata;

import java.util.ArrayList;

public class Plot
{
    ArrayList<Point> points;

    public Plot()
    {
        points = new ArrayList<Point>();
    }

    public void addPoint(Point p)
    {
        points.add(p);
    }

    public void addPoints(ArrayList<? extends Point> p)
    {
        points.addAll(p);
    }

    public Point getPoint(int i)
    {
        return points.get(i);
    }

    /**
     * range of the data in this plot
     *
     * @return []{xmin, xmax, ymin, ymax}
     */
    public double[] getRange()
    {
        double xmin = Double.POSITIVE_INFINITY, xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY, ymax = Double.NEGATIVE_INFINITY;
        for (Point p : points)
        {
            if (p.getX() > xmax)
            {
                xmax = p.getX();
            }
            if (p.getX() < xmin)
            {
                xmin = p.getX();
            }

            if (p.getY() > ymax)
            {
                ymax = p.getY();
            }
            if (p.getY() < ymin)
            {
                ymin = p.getY();
            }
        }

        return new double[]{xmin, xmax, ymin, ymax};
    }


    public int size()
    {
        return points.size();
    }
}
