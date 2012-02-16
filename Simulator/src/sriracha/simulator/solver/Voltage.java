package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.interfaces.IOutputData;

/**
 * Represents a voltage difference.
 * if nodeMinus is null ground is assumed.
 */
public class Voltage implements IOutputData{
    private Integer nodePlus, nodeMinus;


    public Voltage(Integer nodePlus) {
        this.nodePlus = nodePlus;
        nodeMinus = null;
    }

    public Voltage(Integer nodePlus, Integer nodeMinus) {
        this.nodeMinus = nodeMinus;
        this.nodePlus = nodePlus;
    }

    @Override
    public IComplex extract(IComplexVector data){
        return nodeMinus != null ? data.getValue(nodePlus).minus(data.getValue(nodeMinus)):
                                   data.getValue(nodePlus);

    }
}
