package sriracha.frontend;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
    
    public void showCircuitMenu(int toShow)
    {
        ViewGroup root = (ViewGroup)findViewById(R.id.circuit_menu_container);
        for (int i = 0; i < root.getChildCount(); i++)
        {
            View child = root.getChildAt(i);
            if (child.getId() == toShow)
                child.setVisibility(View.VISIBLE);
            else
                child.setVisibility(View.GONE);
        }
    }
}
