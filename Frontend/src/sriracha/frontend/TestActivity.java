package sriracha.frontend;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import sriracha.simulator.ISimulator;
import sriracha.simulator.Simulator;
import sriracha.simulator.parser.CircuitBuilder;


public class TestActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);

        ISimulator simulator = Simulator.Instance;


        String netlist =
                ".SUBCKT TwoResistors nIn nOut\n" +
                        "R1 nIn n1 10\n" +
                        "R2 n1 nOut 20\n" +
                        ".ENDS\n" +
                        "V1 n1 0 AC 5 30\n" +
                        "R1 n2 0 5\n" +
                        "I1 n1 n2 3\n" +
                        "C1 n1 0 0.4\n" +
                        "XTR1 n1 0 TwoResistors";

        CircuitBuilder builder = new CircuitBuilder(netlist);

        tv.setText(builder.getCircuit().toString());
        setContentView(tv);

    }


}
