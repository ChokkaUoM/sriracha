package sriracha.frontend.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import sriracha.frontend.*;
import sriracha.frontend.android.designer.CircuitDesigner;
import sriracha.frontend.android.designer.WireManager;
import sriracha.frontend.android.designer.WireSegment;
import sriracha.frontend.android.model.CircuitElementView;
import sriracha.frontend.android.model.elements.sources.CurrentSourceView;
import sriracha.frontend.android.model.elements.sources.VCCView;
import sriracha.frontend.android.model.elements.sources.VoltageSourceView;
import sriracha.frontend.android.results.Graph;
import sriracha.frontend.android.results.IElementSelector;
import sriracha.frontend.model.CircuitElement;
import sriracha.frontend.resultdata.Plot;
import sriracha.simulator.IPrintData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the layout for the analysis menu. It is responsible for displaying
 * the appropriate controls, as well as generating the appropriate analysis and
 * .PRINT statements for the netlist. This class also communicates with the
 * simulator, passing it the analysis and .PRINT statements, and delivering the
 * results to the results viewer.
 */
public class AnalysisMenu extends LinearLayout
{
    private ColoredStringAdapter adapter;
    private boolean node1IsValid = false;
    private boolean voltageSourceIsValid = false;

    private Spinner analysisType;
    private TextView dcElement;
    private TextView dcStart;
    private TextView dcStop;
    private TextView dcStep;
    private TextView acNum;
    private TextView acStart;
    private TextView acStop;
    private Spinner acScale;
    private Spinner printType;
    private TextView printN1;
    private TextView printN2;
    private TextView printNodeCurrent;
    private ListView printStatements;
    private Button addPrint;

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
        setInnerControls();
        setAnalysisTypeItems();
        setElementSelector();
        setNodeSelector();
        setPlotTypeItems();
        setPrintAddButton();
        setFrequencyScale();
        super.onFinishInflate();
    }

    private void setInnerControls()
    {
        analysisType = (Spinner) findViewById(R.id.analysis_type);
        dcElement = (TextView) findViewById(R.id.dc_analysis_element);
        dcStart = (TextView) findViewById(R.id.dc_analysis_startv);
        dcStop = (TextView) findViewById(R.id.dc_analysis_stopv);
        dcStep = (TextView) findViewById(R.id.dc_analysis_incr);
        acNum = (TextView) findViewById(R.id.ac_analysis_num);
        acStart = (TextView) findViewById(R.id.ac_analysis_startf);
        acStop = (TextView) findViewById(R.id.ac_analysis_stopf);
        acScale = (Spinner) findViewById(R.id.ac_frequency_scale);
        printType = (Spinner) findViewById(R.id.print_type);
        printN1 = (TextView) findViewById(R.id.print_node1);
        printN2 = (TextView) findViewById(R.id.print_node2);
        printNodeCurrent = (TextView) findViewById(R.id.print_node_current);
        printStatements = (ListView) findViewById(R.id.print_statements);
        addPrint = (Button) findViewById(R.id.print_add);
    }

    public void reset()
    {
        analysisType.setSelection(0);
        dcElement.setText("[Select...]");
        dcStart.setText("");
        dcStop.setText("");
        dcStep.setText("");
        acNum.setText("");
        acStart.setText("");
        acStop.setText("");
        acScale.setSelection(0);
        printType.setSelection(0);
        resetPrints();
    }

    private void resetPrints()
    {
        printN1.setText("[...]");
        printN2.setText("0");
        printNodeCurrent.setText("[...]");
        ((ArrayAdapter<String>) printStatements.getAdapter()).clear();
        addPrint.setEnabled(false);
    }

    private CircuitDesigner getCircuitDesigner()
    {
        return ((MainActivity) getContext()).getCircuitDesigner();
    }

    public String getAnalysisAndPrints()
    {
        String analysis = null;
        try
        {
            String analysisType = getAnalysisType();
            if (analysisType.equals("DC"))
                analysis = getDcAnalysis();
            else if (analysisType.equals("AC"))
                analysis = getAcAnalysis();
        } catch (RuntimeException e)
        {
            analysis = "";
        }

        ArrayList<String> prints = getPrints();

        String out = analysis;
        for (String print : prints)
            out += "\n" + print;

        return out;
    }

    public void requestAnalyses(final AsyncSimulator simulator)
    {
        String analysis = null;

        try
        {
            String analysisType = getAnalysisType();
            if (analysisType.equals("DC"))
                analysis = getDcAnalysis();
            else if (analysisType.equals("AC"))
                analysis = getAcAnalysis();
        } catch (RuntimeException e)
        {
            showToast(e.getMessage());
            return;
        }

        final ArrayList<String> prints = getPrints();
        if (prints.isEmpty())
        {
            showToast("You haven't requested any plots");
            return;
        }

        showCancelButton();

        System.out.println(analysis);
        simulator.requestAnalysisAsync(analysis, new AsyncSimulator.OnSimulatorAnalysisDoneListener()
        {
            @Override
            public void OnSimulatorAnalysisDone()
            {
                MainActivity mainActivity = (MainActivity) getContext();
                MainLayout mainLayout = (MainLayout) mainActivity.findViewById(R.id.main);
                Graph graph = (Graph) mainActivity.findViewById(R.id.graph);
                graph.clearPlots();
                switch (getFreqScaleType())
                {
                    case 0:
                        graph.setXLogScale(false);
                        break;
                    case 1:
                        graph.setXLogScale(true);
                        graph.setXLogBase(10);
                        break;
                    case 2:
                        graph.setXLogScale(true);
                        graph.setXLogBase(8);
                }

                for (int i = 0; i < prints.size(); i++)
                {
                    System.out.println(prints.get(i));
                    IPrintData result = simulator.requestResults(prints.get(i));
                    ResultsParser parser = new ResultsParser();
                    List<Plot> plots = parser.getPlots(result);

                    graph.addPlot(plots.get(0), Colors.get(i));
                    if (plots.size() > 1)
                        graph.addPlot(plots.get(1), Colors.getSecondary(i));
                }

                showAnalyseButton();
                int lmode = mainLayout.getLayoutMode();
                if (lmode == 1) mainLayout.shiftRight();

                graph.autoScale();
            }

            @Override
            public void OnSimulatorAnalysisCancelled()
            {
                showAnalyseButton();
            }

            @Override
            public void OnSimulatorError(Exception e)
            {
                showAnalyseButton();
                showToast(e.getMessage());
            }
        });
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
        String analysisType = ((TextView) (this.analysisType).getSelectedView()).getText().toString();
        if (analysisType.equals("DC Sweep"))
            return "DC";
        else if (analysisType.equals("Frequency"))
            return "AC";
        else
            throw new RuntimeException("Invalid analysis type: " + analysisType);
    }

    public void serialize(ObjectOutputStream out) throws IOException
    {
        out.writeBoolean(node1IsValid);
        out.writeBoolean(voltageSourceIsValid);
        out.writeInt(analysisType.getSelectedItemPosition());
        out.writeInt(acScale.getSelectedItemPosition());
        out.writeObject(acNum.getText().toString());
        out.writeObject(acStart.getText().toString());
        out.writeObject(acStop.getText().toString());
        out.writeObject(dcStep.getText().toString());
        out.writeObject(dcStart.getText().toString());
        out.writeObject(dcStop.getText().toString());
        out.writeObject(dcElement.getText().toString());
        out.writeInt(printType.getSelectedItemPosition());
//        out.writeObject(printN1.getText().toString());
//        out.writeObject(printN2.getText().toString());
//        int count = printStatements.getAdapter().getCount();
//        out.writeInt(count);
//        for (int i = 0; i < count; i++)
//        {
//            out.writeObject(printStatements.getAdapter().getItem(i));
//        }

    }

    public void deserialize(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        node1IsValid = in.readBoolean();
        voltageSourceIsValid = in.readBoolean();
        analysisType.setSelection(in.readInt());
        acScale.setSelection(in.readInt());
        acNum.setText((String) in.readObject());
        acStart.setText((String) in.readObject());
        acStop.setText((String) in.readObject());
        dcStep.setText((String) in.readObject());
        dcStart.setText((String) in.readObject());
        dcStop.setText((String) in.readObject());
        dcElement.setText((String) in.readObject());
        printType.setSelection(in.readInt());

        resetPrints();
//        printN1.setText((String) in.readObject());
//        printN2.setText((String) in.readObject());
//        int count = in.readInt();
//        ArrayAdapter<String> adapter = (ArrayAdapter<String>) printStatements.getAdapter();
//        for (int i = 0; i < count; i++)
//        {
//            adapter.add((String) in.readObject());
//        }

    }


    private String getDcAnalysis()
    {
        String elementName = dcElement.getText().toString();
        CircuitElement element = getCircuitDesigner().getElementByName(elementName);

        float startV, stopV, incr;

        try
        {
            startV = Float.parseFloat(dcStart.getText().toString());
            stopV = Float.parseFloat(dcStop.getText().toString());
            incr = Float.parseFloat(dcStep.getText().toString());
        } catch (NumberFormatException e)
        {
            throw new NumberFormatException("You must specify a valid number for start, stop and increment voltages.");
        }

        if (element != null)
        {
            String analysis = String.format(".DC %s %f %f %f", element.getName(), startV, stopV, incr);
            return analysis;
        } else
        {
            throw new RuntimeException("You must choose an element to sweep");
        }
    }

    private String getAcAnalysis()
    {
        int num;
        float startF, stopF;
        try
        {
            num = Integer.parseInt(acNum.getText().toString());
            startF = Float.parseFloat(acStart.getText().toString());
            stopF = Float.parseFloat(acStop.getText().toString());
        } catch (NumberFormatException e)
        {
            throw new NumberFormatException("You must specify a valid number for number of steps, and start and stop frequencies.");
        }

        String freqScale = new String[]{"LIN", "DEC", "OCT"}[getFreqScaleType()];

        String analysis = String.format(".AC %s %d %f %f", freqScale, num, startF, stopF);
        return analysis;
    }

    private int getFreqScaleType()
    {
        return acScale.getSelectedItemPosition();
    }

    private String getPrint()
    {
        String printTypeLong = ((TextView) printType.getSelectedView()).getText().toString();
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
        String node1 = printN1.getText().toString();
        String node2 = printN2.getText().toString();

        if (node1 != null && node2 != null)
        {
            return String.format("%s(%s,%s)", printType, node1, node2);
        } else
        {
            showToast("You must choose a node to measure voltage");
        }
        return null;
    }

    private String getCurrentPrint(String printType)
    {
        String elementName = printNodeCurrent.getText().toString();
        CircuitElement element = getCircuitDesigner().getElementByName(elementName);

        if (element != null)
        {
            return String.format("%s(%s)", printType, element.getName());
        } else
        {
            showToast("You must choose an element to measure current");
        }

        return null;
    }

    private void setAnalysisTypeItems()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[]{
                "DC Sweep",
                "Frequency"
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        analysisType.setAdapter(adapter);

        showDcMenu();

        analysisType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[]{
                "V (real and complex)", "VR (real)", "VI (complex)", "VM (magnitude)", "VDB (magnitude in dB)", "VP (phase)",
                "I (real and complex)", "IR (real)", "II (complex)", "IM (magnitude)", "IDB (magnitude in dB)", "IP (phase)",
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        printType.setAdapter(adapter);

        showVoltagePrintMenu();

        printType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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
                setAddPrintButtonEnabled();
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
                    if (elementView instanceof VoltageSourceView || elementView instanceof CurrentSourceView || elementView instanceof VCCView)
                        elementViews.add(elementView);
                }

                ElementSelector elementSelector = new ElementSelector((TextView) view, elementViews);
                elementSelector.setOnSelectListener(new IElementSelector.OnSelectListener()
                {
                    @Override
                    public void onSelect(Object selectedElement)
                    {
                        voltageSourceIsValid = true;
                        setAddPrintButtonEnabled();
                    }
                });
                getCircuitDesigner().setCursorToSelectingElement(elementSelector);
            }
        };

        // Choose element to sweep
        dcElement.setOnClickListener(listener);

        // Choose element for printing current through (in future, will not be only voltage sources)
        printNodeCurrent.setOnClickListener(listener);
    }

    private void setNodeSelector()
    {
        View.OnClickListener listener = new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final WireManager wireManager = getCircuitDesigner().getWireManager();
                final NodeCrawler crawler = new NodeCrawler(wireManager);
                crawler.computeMappings();

                final TextView textView = (TextView) view;
                NodeSelector nodeSelector = new NodeSelector((TextView) view, wireManager.getSegments());
                nodeSelector.setOnSelectListener(new IElementSelector.OnSelectListener<WireSegment>()
                {
                    @Override
                    public void onSelect(WireSegment selectedSegment)
                    {
                        NetlistNode node = crawler.nodeFromSegment(selectedSegment);
                        if (node != null)
                        {
                            textView.setText(node.toString());
                            if (textView == printN1)
                                node1IsValid = true;
                            setAddPrintButtonEnabled();
                        }
                    }
                });
                getCircuitDesigner().setCursorToSelectingElement(nodeSelector);
            }
        };

        printN1.setOnClickListener(listener);
        printN2.setOnClickListener(listener);
    }

    private void setPrintAddButton()
    {
        adapter = new ColoredStringAdapter(getContext(), android.R.layout.simple_list_item_1);

        printStatements.setAdapter(adapter);
        printStatements.setOnItemClickListener(new AdapterView.OnItemClickListener()
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

        addPrint.setEnabled(false);
        addPrint.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                adapter.add(getPrint());
            }
        });
    }

    private void setFrequencyScale()
    {

        acScale.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,
                new String[]{"LIN", "LOG10", "LOG8"}));
        ((ArrayAdapter<String>) acScale.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }


    private void setAddPrintButtonEnabled()
    {
        if (printType.getSelectedItem().toString().startsWith("V"))
            addPrint.setEnabled(node1IsValid);
        else if (printType.getSelectedItem().toString().startsWith("I"))
            addPrint.setEnabled(voltageSourceIsValid);
        else
            throw new RuntimeException("Invalid Print Type");
    }

    private void showToast(String message)
    {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
