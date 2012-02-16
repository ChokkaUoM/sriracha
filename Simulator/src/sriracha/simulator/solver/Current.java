package sriracha.simulator.solver;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.VoltageSource;
import sriracha.simulator.solver.interfaces.IOutputData;

public class Current implements IOutputData{
    private String sourceName;

    private Circuit circuit;

    /**
     * keeps a reference to the circuit so that it can extract the correct index after equation generation.
     * @param sourceName
     * @param circuit
     */
    public Current(String sourceName, Circuit circuit) {
        this.sourceName = sourceName;
        this.circuit = circuit;
    }

    @Override
    public IComplex extract(IComplexVector data) {
        VoltageSource vs = (VoltageSource)circuit.getElement(sourceName);
        if(vs != null){
            int index = vs.getCurrentVarIndex();
            return data.getValue(index);
        }
        return null;
    }
}
