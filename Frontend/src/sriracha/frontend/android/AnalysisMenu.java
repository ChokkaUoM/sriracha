package sriracha.frontend.android;

import android.app.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.R;
import sriracha.frontend.android.designer.*;
import sriracha.frontend.android.model.*;
import sriracha.frontend.android.model.elements.sources.*;
import sriracha.frontend.android.results.*;
import sriracha.frontend.model.*;
import sriracha.frontend.resultdata.*;
import sriracha.simulator.*;

import java.util.*;

public class AnalysisMenu extends LinearLayout
{
    private ColoredStringAdapter adapter;

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
        setPrintAddButton();
        super.onFinishInflate();
    }

    private CircuitDesigner getCircuitDesigner()
    {
        return ((MainActivity) getContext()).getCircuitDesigner();
    }

    public void requestAnalyses(final AsyncSimulator simulator)
    {
        String analysis = null;

        String analysisType = getAnalysisType();
        if (analysisType.equals("DC"))
            analysis = getDcAnalysis();
        else if (analysisType.equals("AC"))
            analysis = getAcAnalysis();

        if (analysis != null)
        {
            showCancelButton();

            simulator.requestAnalysisAsync(analysis, new AsyncSimulator.OnSimulatorAnalysisDoneListener()
            {
                @Override
                public void OnSimulatorAnalysisDone()
                {
                    MainActivity mainActivity = (MainActivity) getContext();
                    MainLayout mainLayout = (MainLayout) mainActivity.findViewById(R.id.main);
                    Graph graph = (Graph) mainActivity.findViewById(R.id.graph);
                    graph.clearPlots();

                    ArrayList<String> prints = getPrints();
                    for (int i = 0; i < prints.size(); i++)
                    {
                        IPrintData result = simulator.requestResults(prints.get(i));
                        ResultsParser parser = new ResultsParser();
                        List<Plot> plots = parser.getPlots(result);

                        graph.addPlot(plots.get(0), Colors.get(i));
                    }

                    showAnalyseButton();
                    mainLayout.shiftRight();
                }
                @Override
                public void OnSimulatorAnalysisCancelled()
                {
                    showAnalyseButton();
                }
            });
        }
    }

    private ArrayList<String> getPrints()
    {
        String analysisType = getAnalysisType();
        ArrayList<String> printStatements = new ArrayList<String>();
        for (int i = 0; i < adapter.getCount(); i++)
            printStatements.add(String.format(".PRINT %s %s", analysisType, adapter.getItem(i)));

        return printStatements;
    }

    private String getAnalysisType()
    {
        String analysisType = ((TextView) ((Spinner) findViewById(R.id.analysis_type)).getSelectedView()).getText().toString();
        if (analysisType.equals("DC Sweep"))
            return "DC";
        else if (analysisType.equals("Frequency"))
            return "AC";
        else
            throw new RuntimeException("Invalid analysis type: " + analysisType);
    }

    private String getDcAnalysis()
    {
        String elementName = ((TextView) findViewById(R.id.dc_analysis_element)).getText().toString();
        CircuitElement element = getCircuitDesigner().getElementByName(elementName);
        float startV = Float.parseFloat(((TextView) findViewById(R.id.dc_analysis_startv)).getText().toString());
        float stopV = Float.parseFloat(((TextView) findViewById(R.id.dc_analysis_stopv)).getText().toString());
        float incr = Float.parseFloat(((TextView) findViewById(R.id.dc_analysis_incr)).getText().toString());

        if (element != null)
        {
            String analysis = String.format(".DC %s %f %f %f", element.getName(), startV, stopV, incr);
            return analysis;
        }
        else
        {
            Toast toast = Toast.makeText(getContext(), "You must choose an element to sweep", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return null;
    }

    private String getAcAnalysis()
    {
        float num = Float.parseFloat(((TextView) findViewById(R.id.ac_analysis_num)).getText().toString());
        float startF = Float.parseFloat(((TextView) findViewById(R.id.ac_analysis_startf)).getText().toString());
        float stopF = Float.parseFloat(((TextView) findViewById(R.id.ac_analysis_stopf)).getText().toString());

        String analysis = String.format(".AC LIN %f %f %f");
        return analysis;
    }

    private String getPrint()
    {
        String printTypeLong = ((TextView) ((Spinner) findViewById(R.id.print_type)).getSelectedView()).getText().toString();
        String printType = printTypeLong.split(" ")[0];
        if (printType.startsWith("V"))
            return getVoltagePrint(printType);
        else if (printType.startsWith("I"))
            return getCurrentPrint(printType);
        else
            throw new RuntimeException("Invalid print type");
    }

    private String getVoltagePrint(String printType)
    {
        String node1 = ((TextView) findViewById(R.id.print_node1)).getText().toString();
        String node2 = ((TextView) findViewById(R.id.print_node2)).getText().toString();

        if (node1 != null && node2 != null)
        {
            return String.format("%s(%s,%s)", printType, node1, node2);
        }
        else
        {
            Toast toast = Toast.makeText(getContext(), "You must choose a node to measure voltage", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return null;
    }

    private String getCurrentPrint(String printType)
    {
        String elementName = ((TextView) findViewById(R.id.print_node_current)).getText().toString();
        CircuitElement element = getCircuitDesigner().getElementByName(elementName);

        if (element != null)
        {
            return String.format("%s(%s)", printType, element.getName());
        }
        else
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
        findViewById(R.id.ac_analysis_options).setVisibility(GONE);
    }

    private void showAcMenu()
    {
        findViewById(R.id.dc_analysis_options).setVisibility(GONE);
        findViewById(R.id.ac_analysis_options).setVisibility(VISIBLE);
    }

    private void showVoltagePrintMenu()
    {
        findViewById(R.id.print_type_voltage).setVisibility(VISIBLE);
        findViewById(R.id.print_type_current).setVisibility(GONE);
    }

    private void showCurrentPrintMenu()
    {
        findViewById(R.id.print_type_voltage).setVisibility(GONE);
        findViewById(R.id.print_type_current).setVisibility(VISIBLE);
    }

    public void showAnalyseButton()
    {
        findViewById(R.id.analyse_button).setVisibility(VISIBLE);
        findViewById(R.id.cancel_button).setVisibility(GONE);
        findViewById(R.id.analysis_progress).setVisibility(GONE);
    }

    public void showCancelButton()
    {
        findViewById(R.id.analyse_button).setVisibility(GONE);
        findViewById(R.id.cancel_button).setVisibility(VISIBLE);
        findViewById(R.id.analysis_progress).setVisibility(VISIBLE);
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

    private void setPrintAddButton()
    {
        adapter = new ColoredStringAdapter(getContext(), android.R.layout.simple_list_item_1);

        ListView listView = (ListView) findViewById(R.id.print_statements);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, long id)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Delete this .PRINT statement?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                String item = adapter.getItem(position);
                                adapter.remove(item);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        findViewById(R.id.print_add).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                adapter.add(getPrint());
            }
        });
    }
}
