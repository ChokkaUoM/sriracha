package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.VoltageSource;
import sriracha.simulator.solver.interfaces.OutputData;

public class Current extends OutputData {
    private String sourceName;

    private Circuit circuit;

    /**
     * keeps a reference to the circuit so that it can extract the correct index after equation generation.
     * @param sourceName
     * @param circuit
     */
    public Current(OutputType type, String sourceName, Circuit circuit) {
        super(type);
        this.sourceName = sourceName;
        this.circuit = circuit;
    }

    @Override
    public double[] extract(IComplexVector data) {
        VoltageSource vs = (VoltageSource)circuit.getElement(sourceName);
        if(vs == null) return null;
        int index = vs.getCurrentVarIndex();
        IComplex val = data.getValue(index);
        return getFromType(val);
    }

    @Override
    public String toString() {
        return type + " current " + sourceName;
    }
}
