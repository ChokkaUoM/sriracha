package sriracha.frontend.android;

import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.android.model.elements.sources.*;
import sriracha.frontend.android.results.*;

import java.util.*;

public class AnalysisMenu extends RelativeLayout
{
    public AnalysisMenu(Context context)
    {
        super(context);
    }
    public AnalysisMenu(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public AnalysisMenu(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate()
    {
        setAnalysisTypeItems();
        setElementSelector();
        setNodeSelector();
        super.onFinishInflate();
    }

    private CircuitDesigner getCircuitDesigner()
    {
        return ((MainActivity) getContext()).getCircuitDesigner();
    }

    private void setAnalysisTypeItems()
    {
        Spinner spinner = (Spinner) findViewById(R.id.analysis_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[]{
                "DC Sweep",
                "Frequency"
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        showDcMenu();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (((TextView) view).getText().equals("DC Sweep"))
                    showDcMenu();
                else if (((TextView) view).getText().equals("Frequency"))
                    showAcMenu();
                else
                    throw new RuntimeException("Invalid analysis type");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });
    }

    private void showDcMenu()
    {
        findViewById(R.id.dc_analysis_options).setVisibility(VISIBLE);
        findViewById(R.id.ac_analysis_options).setVisibility(INVISIBLE);
    }

    private void showAcMenu()
    {
        findViewById(R.id.dc_analysis_options).setVisibility(INVISIBLE);
        findViewById(R.id.ac_analysis_options).setVisibility(VISIBLE);
    }

    private void setElementSelector()
    {
        ((TextView) findViewById(R.id.dc_analysis_element)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ArrayList<CircuitElementView> elementViews = new ArrayList<CircuitElementView>();
                for (CircuitElementView elementView : getCircuitDesigner().getElements())
                {
                    if (elementView instanceof VoltageSourceView)
                        elementViews.add(elementView);
                }

                ElementSelector elementSelector = new ElementSelector((TextView) view, elementViews);
                getCircuitDesigner().setCursorToSelectingElement(elementSelector);
            }
        });
    }

    private void setNodeSelector()
    {
        ((TextView) findViewById(R.id.print_node1)).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final WireManager wireManager = getCircuitDesigner().getWireManager();
                NodeSelector nodeSelector = new NodeSelector((TextView) view, wireManager.getSegments());
                nodeSelector.setOnSelectListener(new IElementSelector.OnSelectListener<WireSegment>()
                {
                    @Override
                    public void onSelect(WireSegment selectedSegment)
                    {
                        NetlistGenerator generator = new NetlistGenerator();
                        NetlistGenerator.NetlistNode node = generator.mapSegmentToNode(selectedSegment, wireManager);
                        if (node != null)
                            ((TextView) findViewById(R.id.print_node1)).setText(node.toString());
                    }
                });
                getCircuitDesigner().setCursorToSelectingElement(nodeSelector);
            }
        });
    }
}
