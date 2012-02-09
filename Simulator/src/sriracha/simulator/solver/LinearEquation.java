package sriracha.simulator.solver;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.*;
import sriracha.simulator.solver.interfaces.IEquation;

/**
 * Linear equation
 * C + jwG = b
 */
class LinearEquation implements IEquation {

    private IRealMatrix C;

    private IComplexMatrix G;

    private IComplexVector b;


    /**
     * Frequency in rads
     */
    private double omega;


    LinearEquation(int circuitNodeCount) {
        C = MathActivator.Activator.realMatrix(circuitNodeCount, circuitNodeCount);
        G = MathActivator.Activator.complexMatrix(circuitNodeCount, circuitNodeCount);
        b = MathActivator.Activator.complexVector(circuitNodeCount);
    }

    protected IComplexMatrix buildMatrixA() {
        return (IComplexMatrix) C.plus(G.times(omega));
    }


    @Override
    public IComplexVector solve() {
        return solve(omega);
    }

    @Override
    public IComplexVector solve(double omega) {
        this.omega = omega;
        IComplexMatrix a = buildMatrixA();
        return a.solve(b);
    }

    @Override
    public void applyRealStamp(int i, int j, double d) {
        //no stamps to ground
        if (i == -1 || j == -1) return;

        double val = C.getValue(i, j);
        C.setValue(i, j, val + d);
    }

    @Override
    public void applyComplexStamp(int i, int j, double d) {
        //no stamps to ground
        if (i == -1 || j == -1) return;

        IComplex val = G.getValue(i, j);
        val.setImag(val.getImag() + d);
        G.setValue(i, j, val);
    }

    @Override
    public void applySourceStamp(int i, IComplex d) {
        //no stamps to ground
        if (i == -1) return;

        IComplex val = b.getValue(i);
        b.setValue(i, val.plus(d));
    }

    @Override
    public IEquation clone() {
        LinearEquation clone = new LinearEquation(b.getDimension());
        clone.G = (IComplexMatrix) G.clone();
        clone.C = (IRealMatrix) C.clone();
        clone.b = (IComplexVector) b.clone();
        return clone;
    }
}
