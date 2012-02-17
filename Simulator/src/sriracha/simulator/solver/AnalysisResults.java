package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplexVector;
import java.io.PipedInputStream;

public abstract class AnalysisResults {

    public abstract void addVector(double x, IComplexVector vector);

    public abstract PipedInputStream output(OutputFilter filter);
}
