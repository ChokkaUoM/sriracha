package sriracha.frontend;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import sriracha.frontend.android.CircuitDesigner;
import sriracha.frontend.android.CircuitDesignerMenu;
import sriracha.frontend.android.model.*;
import sriracha.frontend.android.results.Graph;
import sriracha.frontend.resultdata.Plot;
import sriracha.frontend.resultdata.Point;

public class MainActivity extends Activity
{
    private CircuitDesignerMenu circuitDesignerMenu;
    private CircuitDesigner circuitDesigner;

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

        circuitDesignerMenu = new CircuitDesignerMenu((MainActivity) this);
        circuitDesigner = new CircuitDesigner(findViewById(R.id.circuit_design_canvas), circuitDesignerMenu, new CircuitElementActivator(this));

        showCircuitMenu(R.id.circuit_menu);

        //testGraph();
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

        g.setXRange(-10, 10);
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

    public void wrenchMenuOnClick(View view)
    {
        circuitDesigner.generateNetlist();
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
}
