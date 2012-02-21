import sriracha.simulator.IPrintData;
import sriracha.simulator.ISimulator;
import sriracha.simulator.Simulator;
import sriracha.simulator.parser.CircuitBuilder;
import sriracha.simulator.solver.analysis.IAnalysis;
import sriracha.simulator.solver.output.filtering.OutputFilter;

import java.io.IOException;
import java.util.List;

/**
 * Eventually this should be an interactive test console for playing around with various parts of the simulator.
 */
public class Console {


    public static void main(String[] args) throws IOException {

        FileReader reader = new FileReader(args[0]);

        gnuplotOutput(reader.getContents(), args[1]);


        

    }

    public static void gnuplotOutput(String netlist, String cmdFile) {
        ISimulator sim = Simulator.Instance;
        sim.setNetlist(netlist);
        List<IPrintData> results = sim.getAllResults();
        
        GnuplotFileMaker gnu = new GnuplotFileMaker(results, cmdFile);
        gnu.writeFiles();
        
    }


    public static void testBasicParsing() throws IOException {
        String netlist =
                "V1 n1 0 AC 5 30\n" +
                        "R1 n2 0 5\n" +
                        "I1 n1 n2 3\n" +
                        "C1 n1 0 0.4\n" +
                        "L1 n1 0 0.2\n" +
                        ".AC DEC 10 1 10000\n" +
                        ".PRINT AC I(V1) VM(n2, n1)";

        CircuitBuilder builder = new CircuitBuilder(netlist);
        System.out.println(builder.getCircuit());

        for (IAnalysis analysis : builder.getAnalysisTypes())
            System.out.println(analysis);

        for (OutputFilter filter : builder.getOutputFilters())
            System.out.println(filter);
    }

    public static void testSubcircuitParsing() throws IOException {
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
