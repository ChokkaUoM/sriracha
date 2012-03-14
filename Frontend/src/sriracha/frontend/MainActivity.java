package sriracha.frontend;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import sriracha.frontend.model.*;

public class MainActivity extends Activity
{
    private CircuitDesigner circuitDesigner;
    private CircuitDesignCanvas circuitDesignCanvas;

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

        circuitDesigner = new CircuitDesigner(new CircuitElementActivator(this));
        circuitDesignCanvas = new CircuitDesignCanvas(findViewById(R.id.circuit_design_canvas), circuitDesigner);

        showCircuitMenu(R.id.circuit_menu);
    }

    public void sourcesAndGroundOnClick(View view)
    {
        showCircuitMenu(R.id.sources_and_ground);
    }

    public void backButtonOnClick(View view)
    {
        showCircuitMenu(R.id.circuit_menu);
    }

    private void showCircuitMenu(int toShow)
    {
        ViewGroup root = (ViewGroup) findViewById(R.id.circuit_menu_container);
        for (int i = 0; i < root.getChildCount(); i++)
        {
            View child = root.getChildAt(i);
            if (child.getId() == toShow)
                child.setVisibility(View.VISIBLE);
            else
                child.setVisibility(View.GONE);
        }
    }

    public void circuitElementOnClick(View view)
    {
        circuitDesigner.selectCircuitElement(view.getId());
    }
}
