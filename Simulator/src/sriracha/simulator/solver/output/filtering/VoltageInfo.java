package sriracha.simulator.solver.output.filtering;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;

/**
 * Represents a voltage difference.
 * if nodeMinus is not specified ground is assumed.
 */
public class VoltageInfo extends ResultInfo
{
    private int nodePlus, nodeMinus;

    public VoltageInfo(DataFormat format, int nodePlus)
    {
        super(format);
        this.nodePlus = nodePlus;
        nodeMinus = -1;
    }

    public VoltageInfo(DataFormat format, int nodePlus, int nodeMinus)
    {
        super(format);
        this.nodeMinus = nodeMinus;
        this.nodePlus = nodePlus;
    }

    @Override
    public double[] extractFrom(IComplexVector data)
    {
        IComplex val = nodeMinus == -1 ? data.getValue(nodePlus) :
                data.getValue(nodePlus).minus(data.getValue(nodeMinus));
        return getFromType(val);

    }

    @Override
    public String toString()
    {
        String nodes = nodeMinus == -1 ? Integer.toString(nodePlus) : nodePlus + ", " + nodeMinus;
        return "V" + getFormatName(format) + "(" + nodes + ")";
    }
}
