package sriracha.simulator.solver.output.filtering;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.elements.sources.VoltageSource;

public class CurrentInfo extends ResultInfo {
    private String sourceName;

    private Circuit circuit;

    /**
     * keeps a reference to the circuit so that it can extract the correct index after equation generation.
     *
     * @param sourceName
     * @param circuit
     */
    public CurrentInfo(DataFormat format, String sourceName, Circuit circuit) {
        super(format);
        this.sourceName = sourceName;
        this.circuit = circuit;
    }

    @Override
    public double[] extractFrom(IComplexVector data) {
        VoltageSource vs = (VoltageSource) circuit.getElement(sourceName);
        if (vs == null) return null;
        int index = vs.getCurrentVarIndex();
        IComplex val = data.getValue(index);
        return getFromType(val);
    }

    @Override
    public String toString() {
        return format + " current " + sourceName;
    }
}
