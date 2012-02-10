package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.interfaces.IOutputData;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 10/02/12
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Current implements IOutputData{
    private String sourceName;

    public Current(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public IComplex extract(IComplexVector data) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
