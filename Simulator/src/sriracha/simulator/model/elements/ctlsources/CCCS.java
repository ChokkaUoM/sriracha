package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.model.elements.sources.VoltageSource;
import sriracha.simulator.solver.analysis.ac.ACEquation;
import sriracha.simulator.solver.analysis.dc.DCEquation;

public class CCCS extends CCSource
{

    private int currentIndex;

    /**
     * Current controlled Current Source
     * Is = gm * i0
     *
     * @param name - name from netlist
     * @param gm   - factor in source equation
     */
    public CCCS(String name, double gm, VoltageSource source)
    {
        super(name, gm, source);
    }

    @Override
    public void applyAC(ACEquation equation)
    {
        int cNodes[] = dummySource.getNodeIndices();
        equation.applyRealMatrixStamp(currentIndex, cNodes[0], 1 / gm - 1);
        equation.applyRealMatrixStamp(currentIndex, cNodes[1], 1 - 1 / gm);
        equation.applyRealMatrixStamp(currentIndex, nPlus, 1);
        equation.applyRealMatrixStamp(currentIndex, nMinus, -1);
    }

    @Override
    public void applyDC(DCEquation equation)
    {
        int cNodes[] = dummySource.getNodeIndices();
        equation.applyMatrixStamp(currentIndex, cNodes[0], 1 / gm - 1);
        equation.applyMatrixStamp(currentIndex, cNodes[1], 1 - 1 / gm);
        equation.applyMatrixStamp(currentIndex, nPlus, 1);
        equation.applyMatrixStamp(currentIndex, nMinus, -1);

    }

    @Override
    public int getNodeCount()
    {
        return 2;
    }

    @Override
    public int getExtraVariableCount()
    {
        return 1;
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public CCCS buildCopy(String name, CircuitElement referencedElement)
    {
        return new CCCS(name, gm, (VoltageSource) referencedElement);
    }


    @Override
    public void setFirstVarIndex(int i)
    {
        currentIndex = i;
    }
}
