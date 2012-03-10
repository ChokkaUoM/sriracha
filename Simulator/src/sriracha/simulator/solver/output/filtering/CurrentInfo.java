package sriracha.simulator.solver.output.filtering;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.elements.sources.VoltageSource;

public class CurrentInfo extends NodeInfo
{
    private String sourceName;

    private Circuit circuit;

    /**
     * keeps a reference to the circuit so that it can extract the correct index after equation generation.
     *
     * @param sourceName
     * @param circuit
     */
    public CurrentInfo(NodeDataFormat format, String sourceName, Circuit circuit)
    {
        super(format);
        this.sourceName = sourceName;
        this.circuit = circuit;
    }

    @Override
    public double[] extractFrom(IComplexVector data)
    {
        VoltageSource vs = (VoltageSource) circuit.getElement(sourceName);
        if (vs == null) return null;
        int index = vs.getCurrentVarIndex();
        IComplex val = data.getValue(index);
        return getFromType(val);
    }

    @Override
    public double[] extractFrom(IRealVector data)
    {
        VoltageSource vs = (VoltageSource) circuit.getElement(sourceName);
        if (vs == null) return null;
        int index = vs.getCurrentVarIndex();
        double val = data.getValue(index);
        return new double[]{val};
    }

    @Override
    public String toString()
    {
        return "I" + getFormatName(format) + "(" + sourceName + ")";
    }
}
