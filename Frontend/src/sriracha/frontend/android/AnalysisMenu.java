package sriracha.frontend.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import sriracha.frontend.MainActivity;
import sriracha.frontend.NetlistGenerator;
import sriracha.frontend.R;
import sriracha.frontend.android.designer.CircuitDesigner;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.designer.WireSegment;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.android.model.elements.sources.VoltageSourceView;
import sriracha.frontend.android.results.IElementSelector;
import sriracha.frontend.model.CircuitElement;
import sriracha.simulator.IPrintData;
import sriracha.simulator.ISimulator;

import java.util.ArrayList;

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
        setPlotTypeItems();
        super.onFinishInflate();
    }

    private CircuitDesigner getCircuitDesigner()
    {
        return ((MainActivity) getContext()).getCircuitDesigner();
    }

    public IPrintData addAnalysesAndPrints(ISimulator simulator)
    {
        String analysisType = ((TextView) ((Spinner) findViewById(R.id.analysis_type)).getSelectedView()).getText().toString();
        if (analysisType.equals("DC Sweep"))
        {
            addDcAnalysis(simulator);
            analysisType = "DC";
        } else if (analysisType.equals("Frequency"))
        {
            addAcAnalysis(simulator);
            analysisType = "AC";
        } else
            throw new RuntimeException("Invalid analysis type: " + analysisType);

        String printTypeLong = ((TextView) ((Spinner) findViewById(R.id.print_type)).getSelectedView()).getText().toString();
        String printType = printTypeLong.split(" ")[0];
        if (printType.startsWith("V"))
            return simulator.requestPrintData(getVoltagePrint(analysisType, printType));
        else if (printType.startsWith("I"))
            return simulator.requestPrintData(getCurrentPrint(analysisType, printType));
        else
            throw new RuntimeException("Invalid print type");
    }

    private void addDcAnalysis(ISimulator simulator)
    {
        String elementName = ((TextView) findViewById(R.id.dc_analysis_element)).getText().toString();
        CircuitElement element = getCircuitDesigner().getElementByName(elementName);
        float startV = Float.parseFloat(((TextView) findViewById(R.id.dc_analysis_startv)).getText().toString());
        float stopV = Float.parseFloat(((TextView) findViewById(R.id.dc_analysis_stopv)).getText().toString());
        float incr = Float.parseFloat(((TextView) findViewById(R.id.dc_analysis_incr)).getText().toString());

        if (element != null)
        {
            String analysis = String.format(".DC %s %f %f %f", element.getName(), startV, stopV, incr);
            simulator.addAnalysis(analysis);
        } else
        {
            Toast toast = Toast.makeText(getContext(), "You must choose an element to sweep", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void addAcAnalysis(ISimulator simulator)
    {
        float num = Float.parseFloat(((TextView) findViewById(R.id.ac_analysis_num)).getText().toString());
        float startF = Float.parseFloat(((TextView) findViewById(R.id.ac_analysis_startf)).getText().toString());
        float stopF = Float.parseFloat(((TextView) findViewById(R.id.ac_analysis_stopf)).getText().toString());

        String analysis = String.format(".AC LIN %f %f %f");
        simulator.addAnalysis(analysis);
    }

    private String getVoltagePrint(String analysisType, String printType)
    {
        String node1 = ((TextView) findViewById(R.id.print_node1)).getText().toString();
        String node2 = ((TextView) findViewById(R.id.print_node2)).getText().toString();

        if (node1 != null && node2 != null)
        {
            return String.format(".PRINT %s %s(%s,%s)", analysisType, printType, node1, node2);
        } else
        {
            Toast toast = Toast.makeText(getContext(), "You must choose a node to measure voltage", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return null;
    }

    private String getCurrentPrint(String analysisType, String printType)
    {
        String elementName = ((TextView) findViewById(R.id.dc_analysis_element)).getText().toString();
        CircuitElement element = getCircuitDesigner().getElementByName(elementName);

        if (element != null)
        {
            return String.format(".PRINT %s %s(%s)", analysisType, printType, element.getName());
        } else
        {
            Toast toast = Toast.makeText(getContext(), "You must choose an element to measure current", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        return null;
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
                if (((TextView) view).getText().toString().equals("DC Sweep"))
                    showDcMenu();
                else if (((TextView) view).getText().toString().equals("Frequency"))
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

    private void setPlotTypeItems()
    {
        Spinner spinner = (Spinner) findViewById(R.id.print_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[]{
                "V (real and complex)", "VR (real)", "VI (complex)", "VM (magnitude)", "VDB (magnitude in dB)", "VP (phase)",
                "I (real and complex)", "IR (real)", "II (complex)", "IM (magnitude)", "IDB (magnitude in dB)", "IP (phase)",
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        showVoltagePrintMenu();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (((TextView) view).getText().toString().startsWith("V"))
                    showVoltagePrintMenu();
                else if (((TextView) view).getText().toString().startsWith("I"))
                    showCurrentPrintMenu();
                else
                    throw new RuntimeException("Invalid plot type");
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

    private void showVoltagePrintMenu()
    {
        findViewById(R.id.print_type_voltage).setVisibility(VISIBLE);
        findViewById(R.id.print_type_current).setVisibility(INVISIBLE);
    }

    private void showCurrentPrintMenu()
    {
        findViewById(R.id.print_type_voltage).setVisibility(INVISIBLE);
        findViewById(R.id.print_type_current).setVisibility(VISIBLE);
    }

    private void setElementSelector()
    {
        View.OnClickListener listener = new View.OnClickListener()
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
        };

        // Choose element to sweep
        findViewById(R.id.dc_analysis_element).setOnClickListener(listener);

        // Choose element for printing current through (in future, will not be only voltage sources)
        findViewById(R.id.print_node_current).setOnClickListener(listener);
    }

    private void setNodeSelector()
    {
        View.OnClickListener listener = new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final TextView textView = (TextView) view;
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
                            textView.setText(node.toString());
                    }
                });
                getCircuitDesigner().setCursorToSelectingElement(nodeSelector);
            }
        };

        findViewById(R.id.print_node1).setOnClickListener(listener);
        findViewById(R.id.print_node2).setOnClickListener(listener);
    }
}
