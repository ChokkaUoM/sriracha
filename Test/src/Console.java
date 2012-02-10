import sriracha.simulator.model.Capacitor;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.Resistor;
import sriracha.simulator.model.VoltageSource;
import sriracha.simulator.parser.CircuitBuilder;
import sriracha.simulator.solver.EquationGenerator;
import sriracha.simulator.solver.SSType;
import sriracha.simulator.solver.SmallSignal;
import sriracha.simulator.solver.Solver;
import sriracha.simulator.solver.interfaces.IAnalysis;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

/**
 * Eventually this should be an interactive test console for playing around with various parts of the simulator.
 */
public class Console {

    public static void main(String[] args) throws IOException {
        testBasicParsing();
    }


    public static void testSequence1() throws IOException {
        Circuit test1 = new Circuit();

        Resistor r1 = new Resistor("r1", 10);
        Capacitor c1 = new Capacitor("c1", 4);

        EquationGenerator generator = new EquationGenerator(test1);

        Solver solver = new Solver(generator.generate());

        SmallSignal analysis = new SmallSignal(SSType.Linear, 1000, 3000, 500);


        /*DataInputStream dataStream = new DataInputStream(solver.solve(analysis));
        try {
            while (true) {

                System.out.print(dataStream.readDouble() + " ");

            }
        } catch (EOFException e) {
            dataStream.close();
        }*/
    }

    public static void testBasicParsing() throws IOException 
    {
        String netlist =
                "V1 n1 0 AC 5 30\n" +
                "R1 n2 0 5\n" +
                "I1 n1 n2 3\n" +
                "C1 n1 0 0.4\n" +
                "L1 n1 0 0.2\n" +
                ".AC DEC 10 1 10000";

        CircuitBuilder builder = new CircuitBuilder(netlist);
        System.out.println(builder.getCircuit());
        
        for (IAnalysis analysis : builder.getAnalysisTypes())
            System.out.println(analysis);
    }

    public static void testSubcircuitParsing() throws IOException
    {
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
        System.out.println(builder.getCircuit());
    }
}
