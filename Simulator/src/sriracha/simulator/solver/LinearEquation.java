package sriracha.simulator.solver;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.*;
import sriracha.simulator.solver.interfaces.IEquation;

class LinearEquation implements IEquation{

    private IRealMatrix C;

    private IComplexMatrix G;

    private IRealVector b;
    
    /**
     * Frequency in rads
     * */
    private double omega;
    
    
    LinearEquation(int circuitNodeCount){
        C = MathActivator.Activator.realMatrix(circuitNodeCount, circuitNodeCount);
        G = MathActivator.Activator.complexMatrix(circuitNodeCount, circuitNodeCount);
        b = MathActivator.Activator.realVector(circuitNodeCount);
    }
    
    protected IComplexMatrix buildMatrixA(){
       return (IComplexMatrix)C.plus(G.times(omega));
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
        double val = C.getValue(i, j);
        C.setValue(i, j, val + d);
    }

    @Override
    public void applyComplexStamp(int i, int j, double d) {
        IComplex val = G.getValue(i, j);
        val.setImag(val.getImag() + d);
        G.setValue(i, j, val);
    }

    @Override
    public void applySourceStamp(int i, double d) {
        double val = b.getValue(i);
        b.setValue(i, val + d);
    }

    @Override
    public IEquation clone() {
        LinearEquation clone = new LinearEquation(b.getDimension());
        clone.G = (IComplexMatrix)G.clone();
        clone.C = (IRealMatrix)C.clone();
        clone.b = (IRealVector)b.clone();
        return clone;
    }
}
