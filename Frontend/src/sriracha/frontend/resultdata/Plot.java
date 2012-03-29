package sriracha.frontend.resultdata;

import java.util.ArrayList;

public class Plot
{
    ArrayList<Point> points;
    
    public Plot(){
        points = new ArrayList<Point>();
    }
    
    public void addPoint(Point p){
        points.add(p);
    }

    public void addPoints(ArrayList<? extends Point> p){
        points.addAll(p);
    }
    
    public Point getPoint(int i){
        return points.get(i);
    }

    public int size() {
        return points.size();
    }
}
