package sriracha.frontend.android.results;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 25/03/12
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Axis extends FrameLayout
{



    protected boolean logscale;
    
    protected int logbase;
    
    protected double minValue;
    
    protected double maxValue;
    
    protected int labelCount;
    
    protected Paint linePaint;



    protected List<TextView> labelViews;
    //protected List<String> labels;


    protected Axis(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected Axis(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Axis(Context context) {
        super(context);
        init();
      
    }
    
    private void init(){
        setWillNotDraw(false);
        labelViews = new ArrayList<TextView>();
        labelCount = 8;//default value
        linePaint = new Paint();
        linePaint.setARGB(255, 255, 255, 255);
        linePaint.setStrokeWidth(2);
        minValue = -1000;
        maxValue = 1000;
        logscale = false;
        logbase = 10;
        initLabels();

    }
    
    protected void initLabels(){
        double interval = (maxValue - minValue) / (labelCount - 1);
        for(int i = 0; i< labelCount; i++){
            TextView v = new TextView(getContext());
            v.setText(axisNumFormat(minValue + interval * i));
            labelViews.add(v);
            addView(v);
        }
    }

    protected static String axisNumFormat(double val){
        if(val <= 1000 && val >= -1000){
            DecimalFormat format = new DecimalFormat("###.##");
            return format.format(val);
        }else return null;
    }

}
