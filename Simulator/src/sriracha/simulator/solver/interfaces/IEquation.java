package sriracha.simulator.solver.interfaces;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;

public interface IEquation {

    public IComplexVector solve();

    public IComplexVector solve(double omega);


    public void applyRealStamp(int i, int j, double d);

    public void applyComplexStamp(int i, int j, double d);

    public void applySourceStamp(int i, IComplex d);

    public IEquation clone();

}
