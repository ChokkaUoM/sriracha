package sriracha.frontend.android.results;

import android.content.Context;
import android.view.View;

public class Graph extends View {

    private double ymin, ymax, xmin, xmax;


    public Graph(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();    
        //get items from xml
    }

    public double getYmin() {
        return ymin;
    }

    public void setYmin(double ymin) {
        this.ymin = ymin;
    }

    public double getYmax() {
        return ymax;
    }

    public void setYmax(double ymax) {
        this.ymax = ymax;
    }

    public double getXmin() {
        return xmin;
    }

    public void setXmin(double xmin) {
        this.xmin = xmin;
    }

    public double getXmax() {
        return xmax;
    }

    public void setXmax(double xmax) {
        this.xmax = xmax;
    }
}
