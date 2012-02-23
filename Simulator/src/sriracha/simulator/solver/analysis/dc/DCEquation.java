package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.solver.analysis.IAnalysisResults;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 22/02/12
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DCEquation {


    private IRealMatrix C;


    private IRealVector b;


    private DCEquation(int nodeCount) {
        C = MathActivator.Activator.realMatrix(nodeCount, nodeCount);
        b = MathActivator.Activator.realVector(nodeCount);
    }

    public static DCEquation generate(Circuit circuit) {
        DCEquation equation = new DCEquation(circuit.getMatrixSize());

        for (CircuitElement element : circuit.getElements()) {
            element.applyDC(equation);
        }

        return equation;

    }


    public IAnalysisResults run() {
        return null;
    }


    public void applyMatrixStamp(int i, int j, double value) {

        //no stamps to ground
        if (i == -1 || j == -1) return;

        if (value != 0)
            C.addValue(i, j, value);


    }

    public void applySourceVectorStamp(int i, double d) {
        //no stamps to ground
        if (i == -1) return;

        b.addValue(i, d);
    }


}
