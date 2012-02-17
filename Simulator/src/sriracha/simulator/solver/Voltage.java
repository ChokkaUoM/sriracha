package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.interfaces.OutputData;

/**
 * Represents a voltage difference.
 * if nodeMinus is null ground is assumed.
 */
public class Voltage extends OutputData {
    private Integer nodePlus, nodeMinus;
    
    public Voltage(OutputType type, int nodePlus) {
        super(type);
        this.nodePlus = nodePlus;
        nodeMinus = null;
    }

    public Voltage(OutputType type, Integer nodePlus, Integer nodeMinus) {
        super(type);
        this.nodeMinus = nodeMinus;
        this.nodePlus = nodePlus;
    }

    @Override
    public double[] extract(IComplexVector data){
        IComplex val = nodeMinus == null ? data.getValue(nodePlus) :
                data.getValue(nodePlus).minus(data.getValue(nodeMinus));
        return getFromType(val);

    }
}
