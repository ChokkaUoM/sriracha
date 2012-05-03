package sriracha.frontend;

import android.app.*;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import sriracha.frontend.android.AnalysisMenu;
import sriracha.frontend.android.AsyncSimulator;
import sriracha.frontend.android.designer.CircuitDesigner;
import sriracha.frontend.android.designer.CircuitDesignerMenu;
import sriracha.frontend.android.model.CircuitElementActivator;
import sriracha.frontend.android.persistence.LoadDialogFragment;
import sriracha.frontend.android.persistence.SaveDialogFragment;
import sriracha.frontend.android.persistence.Serialization;
import sriracha.frontend.android.persistence.Storage;
import sriracha.frontend.android.results.Graph;
import sriracha.frontend.resultdata.Plot;
import sriracha.frontend.resultdata.Point;
import sriracha.simulator.IPrintData;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MainActivity extends Activity
{
    private CircuitDesignerMenu circuitDesignerMenu;
    private CircuitDesigner circuitDesigner;

    private AsyncSimulator simulator;

    private String currentFileName;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);
        setWrenchMenuItems();

        circuitDesignerMenu = new CircuitDesignerMenu((MainActivity) this);
        circuitDesigner = new CircuitDesigner(findViewById(R.id.circuit_design_canvas), circuitDesignerMenu, new CircuitElementActivator(this));

        showCircuitMenu(R.id.circuit_menu);

        simulator = new AsyncSimulator(this);

//        testGraph();
    }

    @Override
    protected Dialog onCreateDialog(int id)
    {
        return super.onCreateDialog(id);
    }

    public CircuitDesigner getCircuitDesigner()
    {
        return circuitDesigner;
    }

    private void resetCircuitDesigner()
    {
        ViewGroup circuitDesignCanvas = (ViewGroup) findViewById(R.id.circuit_design_canvas);
        View[] toKeep = new View[]{
                circuitDesignCanvas.findViewById(R.id.elementmenu_container),
                circuitDesignCanvas.findViewById(R.id.wrenchmenu_icon),
                circuitDesignCanvas.findViewById(R.id.wrenchmenu_items),
        };
        circuitDesignCanvas.removeAllViews();
        for (View view : toKeep)
            circuitDesignCanvas.addView(view);
        circuitDesigner = new CircuitDesigner(circuitDesignCanvas, circuitDesignerMenu, new CircuitElementActivator(this));
    }

    private void testGraph()
    {
        setContentView(R.layout.results_testing);


        //create sin plot
        Plot sin = new Plot();
        for (double x = -50; x <= 50; x += 0.1)
        {
            sin.addPoint(new Point(x, Math.sin(x)));
        }

        Plot log = new Plot();
        for (double x = -10; x <= 10; x += 0.2)
        {
            log.addPoint(new Point(x, Math.pow(2, x)));
        }

        Graph g = (Graph) findViewById(R.id.graph);

        g.beginEdit();
        g.addPlot(sin, Color.rgb(200, 10, 40));
//        g.addPlot(log, Color.rgb(10, 200, 80));

        g.setXRange(0, 10);
        g.setYRange(-2, 2);

        //  g.setXLogScale(true);
//        g.setYLogScale(true);

        g.endEdit();
    }

    public void sourcesAndGroundOnClick(View view)
    {
        showCircuitMenu(R.id.sources_and_ground);
    }

    public void rlcOnClick(View view)
    {
        showCircuitMenu(R.id.rlc);
    }

    public void backButtonOnClick(View view)
    {
        showCircuitMenu(R.id.circuit_menu);
    }

    public void sourcesAndGroundBackButtonOnClick(View view)
    {
        showCircuitMenu(R.id.sources_and_ground);
    }

    public void showCircuitMenu(int toShow)
    {
        circuitDesigner.deselectAllElements();
        circuitDesignerMenu.showSubMenu(toShow);
    }

    public void wireOnClick(View view)
    {
        circuitDesigner.setCursorToWire();
    }

    public void handOnClick(View view)
    {
        circuitDesigner.setCursorToHand();
    }

    public void deleteOnClick(View view)
    {
        circuitDesigner.deleteSelectedElement();
    }

    public void deleteWireOnClick(View view)
    {
        circuitDesigner.deleteSelectedWire();
    }

    public void circuitElementOnClick(View view)
    {
        circuitDesigner.selectCircuitItem(view.getId());
    }

    public void dntOnClick(View view)
    {
        showToast("Coming Soon");
    }

    public void subCircuitOnClick(View view)
    {
        showToast("Coming Soon");
    }

    public void wrenchMenuOnClick(View view)
    {
        ListView menu = (ListView) findViewById(R.id.wrenchmenu_items);
        menu.setVisibility(menu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        if (menu.getVisibility() == View.VISIBLE)
            menu.bringToFront();
    }

    public void goButtonOnClick(View view)
    {
        String netlist;
        IPrintData result;
        final AnalysisMenu analysisMenu = (AnalysisMenu) findViewById(R.id.tab_analysis);
        try
        {
            netlist = circuitDesigner.generateNetlist();
            System.out.print(netlist);
            simulator.setNetlistAsync(netlist, new AsyncSimulator.OnSimulatorReadyListener()
            {
                @Override
                public void OnSimulatorReady()
                {
                    analysisMenu.requestAnalyses(simulator);
                }

                @Override
                public void OnSimulatorSetupCancelled()
                {
                    analysisMenu.showAnalyseButton();
                }

                @Override
                public void OnSimulatorError(Exception e)
                {
                    analysisMenu.showAnalyseButton();
                    showToast(e.getMessage());
                }
            });
        } catch (Exception e)
        {
            showToast("Something seems to have gone slightly awry.");
        }
    }

    public void cancelButtonOnClick(View view)
    {
        simulator.cancelAnalysis();
    }

    public void flipHorizontalOnClick(View view)
    {
    }

    public void flipVerticalOnClick(View view)
    {
    }

    public void rotateCCWOnClick(View view)
    {
        circuitDesigner.rotateSelectedElement(false);
    }

    public void rotateCWOnClick(View view)
    {
        circuitDesigner.rotateSelectedElement(true);
    }

    private void setWrenchMenuItems()
    {
        final ListView menu = (ListView) findViewById(R.id.wrenchmenu_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                new String[]{"New", "Load", "Save", "Save As...", "Export Netlist..."});
        menu.setAdapter(adapter);

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                switch (position)
                {
                    case 0: // New
                        resetCircuitDesigner();
                        ((AnalysisMenu) findViewById(R.id.tab_analysis)).reset();
                        currentFileName = null;
                        break;
                    case 1: // Load
                        showDialog(LoadDialogFragment.newInstance());
                        break;
                    case 2: // Save
                        if (currentFileName != null)
                        {
                            if (saveCircuit(currentFileName))
                                showToast("Saved");
                            break;
                        }
                        // fallthrough!
                    case 3: // Save As...
                        showDialog(new SaveDialogFragment(SaveDialogFragment.SAVE_CIRCUIT));
                        break;
                    case 4: // Export Netlist...
                        showDialog(new SaveDialogFragment(SaveDialogFragment.SAVE_NETLIST));
                        break;
                }
                menu.setVisibility(View.GONE);
            }
        });
    }

    public boolean loadCircuit(String fileName)
    {
        try
        {
            AnalysisMenu analysisMenu = (AnalysisMenu) findViewById(R.id.tab_analysis);
            resetCircuitDesigner();
            Serialization serialization = new Serialization(circuitDesigner, analysisMenu);
            new Storage(this).load(fileName, serialization);
            currentFileName = fileName;
            return true;
        } catch (IOException e)
        {
            showToast(e.getMessage());
        } catch (ClassNotFoundException e)
        {
            showToast(e.getMessage());
        } catch (NoSuchMethodException e)
        {
            showToast(e.getMessage());
        } catch (InstantiationException e)
        {
            showToast(e.getMessage());
        } catch (IllegalAccessException e)
        {
            showToast(e.getMessage());
        } catch (InvocationTargetException e)
        {
            showToast(e.getMessage());
        }

        return false;
    }

    public boolean save(String fileName, int dialogId)
    {
        switch (dialogId)
        {
            case SaveDialogFragment.SAVE_CIRCUIT:
                return saveCircuit(fileName);
            case SaveDialogFragment.SAVE_NETLIST:
                return saveNetlist(fileName);
        }
        throw new IllegalArgumentException("Invalid dialog type id");
    }

    private boolean saveCircuit(String fileName)
    {
        try
        {
            if (!fileName.endsWith(".occ"))
                fileName += ".occ";
            AnalysisMenu analysisMenu = (AnalysisMenu) findViewById(R.id.tab_analysis);

            Serialization serialization = new Serialization(circuitDesigner, analysisMenu);
            new Storage(this).save(fileName, serialization);
            currentFileName = fileName;
            return true;
        } catch (IOException e)
        {
            showToast(e.getMessage());
        }

        return false;
    }

    private boolean saveNetlist(String fileName)
    {
        try
        {
            if (circuitDesigner.getElements().isEmpty())
            {
                showToast("Circuit is empty.");
                return true;
            }

            AnalysisMenu analysisMenu = (AnalysisMenu) findViewById(R.id.tab_analysis);
            String netlist = circuitDesigner.generateNetlist();
            netlist += analysisMenu.getAnalysisAndPrints();

            if (!fileName.endsWith(".txt"))
                fileName += ".txt";
            new Storage(this).save(fileName, netlist);
            return true;
        } catch (IOException e)
        {
            showToast(e.getMessage());
        } catch (Exception e)
        {
            showToast("Something seems to have gone slightly awry.");
        }

        return false;
    }

    public void showDialog(DialogFragment fragment)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
        {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        fragment.show(ft, "dialog");
    }

    public void showToast(String message)
    {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
