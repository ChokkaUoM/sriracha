package sriracha.simulator.solver.interfaces;

import sriracha.math.interfaces.IVector;

public interface IEquation {

    public IVector Solve();

    public IVector Solve(double omega);
}
