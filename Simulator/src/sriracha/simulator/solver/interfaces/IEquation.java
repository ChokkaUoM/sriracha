package sriracha.simulator.solver.interfaces;

import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IVector;

public interface IEquation {

    public IComplexVector solve();

    public IVector solve(double omega);


    public void applyRealStamp(int i, int j, double d);

    public void applyComplexStamp(int i, int j, double d);

    public void applySourceStamp(int i, double d);

    public IEquation clone();

}
