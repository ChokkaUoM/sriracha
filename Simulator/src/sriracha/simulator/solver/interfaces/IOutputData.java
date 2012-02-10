package sriracha.simulator.solver.interfaces;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 10/02/12
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IOutputData {

    public IComplex extract(IComplexVector data);
}
