package sriracha.frontend.android.results;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import sriracha.frontend.R;


public class AxisController extends FrameLayout implements AdapterView.OnItemSelectedListener,
        TextView.OnEditorActionListener,
        Axis.OnAxisChangedListener
{
    private Spinner scaleSelector;

    private EditText minValueSelector;

    private EditText maxValueSelector;

    private TextView titleView;

    private Axis axis;


    public AxisController(Context context)
    {
        super(context);
    }

    public AxisController(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AxisController(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
    {
        if (axis == null) return;

        switch (position)
        {
            case 0:
                axis.setScaleType(Axis.LINEARSCALE);
                break;
            case 1:
                axis.setScaleType(Axis.LOGSCALE);
                axis.setLogBase(10);
                break;
            case 2:
                axis.setScaleType(Axis.LOGSCALE);
                axis.setLogBase(8);
                break;
        }

        invalidateGraph();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {
    }

    @Override
    protected void onFinishInflate()
    {
        init();
        super.onFinishInflate();
    }

    private void init()
    {
        inflate(getContext(), R.layout.results_axis_controller, this);
        scaleSelector = (Spinner) findViewById(R.id.actl_scale_select);

        scaleSelector.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,
                new String[]{"LIN", "LOG10", "LOG8"}));
        ((ArrayAdapter<String>) scaleSelector.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        titleView = (TextView) findViewById(R.id.actl_title);
        minValueSelector = (EditText) findViewById(R.id.actl_minval_select);
        maxValueSelector = (EditText) findViewById(R.id.actl_maxval_select);

        scaleSelector.setOnItemSelectedListener(this);
        minValueSelector.setOnEditorActionListener(this);
        maxValueSelector.setOnEditorActionListener(this);

    }


    public void setTitle(String title)
    {
        titleView.setText(title);
    }

    public String getTitle()
    {
        return titleView.getText().toString();
    }

    public Axis getAxis()
    {
        return axis;
    }

    public void setAxis(Axis axis)
    {
        if (axis != null)
        {
            axis.setOnAxisChangedListener(null);
        }

        this.axis = axis;

        axis.setOnAxisChangedListener(this);

        onAxisRangeChanged(axis.getMinValue(), axis.getMaxValue());
        onAxisScaleTypeChanged(axis.getScaleType());
        onAxisLogBaseChanged(axis.getLogBase());
    }

    private void invalidateGraph()
    {
        ((Graph) axis.getParent()).requestReDraw();
    }

    @Override
    public void onAxisRangeChanged(double minValue, double maxValue)
    {
        maxValueSelector.setText(Double.toString(maxValue));
        minValueSelector.setText(Double.toString(minValue));
    }

    @Override
    public void onAxisScaleTypeChanged(int scaleType)
    {
        switch (scaleType)
        {
            case Axis.LINEARSCALE:
                scaleSelector.setSelection(0);
                break;
            case Axis.LOGSCALE:
                scaleSelector.setSelection(axis.getLogBase() == 10 ? 1 : 2);
                break;
        }
    }

    @Override
    public void onAxisLogBaseChanged(int logBase)
    {
        if (axis.getScaleType() == Axis.LOGSCALE)
        {
            scaleSelector.setSelection(axis.getLogBase() == 10 ? 1 : 2);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
    {
        double value = Double.parseDouble(textView.getText().toString());

        if (textView == minValueSelector)
        {
            axis.setMinValue(value);
        } else if (textView == maxValueSelector)
        {
            axis.setMaxValue(value);
        }

        invalidateGraph();

        return true;
    }
}
