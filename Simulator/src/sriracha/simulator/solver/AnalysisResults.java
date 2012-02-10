package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplexVector;

import java.io.PipedInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 10/02/12
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AnalysisResults {

    public abstract void addVector(double x, IComplexVector vector);

    public abstract PipedInputStream output(OutputFilter filter);
}
