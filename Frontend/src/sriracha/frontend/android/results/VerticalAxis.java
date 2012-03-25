package sriracha.frontend.android.results;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 25/03/12
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class VerticalAxis extends Axis{

    private int width = 60;

    public VerticalAxis(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalAxis(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(!changed) return;
        float spacing = (t - b) / (labelCount - 1);
        int i =0;
        for(TextView tv : labelViews)
        {
            tv.setLeft(l);
            tv.setTop((int) ((i++)*spacing));
        }
    }

 
    public VerticalAxis(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, heightMeasureSpec);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();    //To change body of overridden methods use File | Settings | File Templates.

        initLabels();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float lineOffset = getWidth() * 0.9f;

        int h = getHeight();

        canvas.drawLine(lineOffset, 0 ,lineOffset, h, linePaint);

        float spacing = getHeight() / (labelCount - 1);
        for(int i =0; i<labelCount; i++){
            canvas.drawLine(lineOffset - 3, spacing * i, lineOffset + 3, spacing * i, linePaint);
        }
    }
}
