package sriracha.frontend.model;

import android.view.*;
import android.widget.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.util.*;

public class CircuitDesignCanvas implements View.OnTouchListener
{
    private View canvasView;
    private CircuitDesigner circuitDesigner;

    public CircuitDesignCanvas(View canvasView, CircuitDesigner circuitDesigner)
    {
        this.canvasView = canvasView;
        this.circuitDesigner = circuitDesigner;

        canvasView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        CircuitElementView elementView = circuitDesigner.instantiateElement(new Vector2(motionEvent.getX(), motionEvent.getY()));
        if (elementView != null)
        {
            ((ViewGroup) view).addView(elementView, view.getWidth(), view.getHeight());
            elementView.invalidate();
        }
        return true;
    }
}
