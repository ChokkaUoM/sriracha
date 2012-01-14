package sriracha.simulator.solver;

import sriracha.math.interfaces.*;
import sriracha.simulator.solver.interfaces.IEquation;

class LinearEquation implements IEquation{

    private IRealMatrix C;

    private IRealMatrix G;

    private IRealVector b;
    
    /**
     * Frequency in rads
     * */
    private double omega;
    
    
    protected IComplexMatrix buildMatrixA(){
       return (IComplexMatrix)C.plus(G.times_j().times(omega));
    }


    @Override
    public IVector Solve() {
        return Solve(omega);
    }

    @Override
    public IVector Solve(double omega) {
        this.omega = omega;
        IComplexMatrix a = buildMatrixA();
        return a.solve(b);
    }
}
