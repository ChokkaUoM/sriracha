package sriracha.simulator.model.elements.ctlsources;

import sriracha.simulator.model.CircuitElement;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 3/8/12
 * Time: 5:07 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ControlledSource extends CircuitElement
{

    /**
     * Node indices for source
     * for current source: Current flows from nPlus, through source, out nMinus
     */
    int nPlus;
    int nMinus;

    /**
     * Factor for controlled source equation
     */
    double gm;

    /**
     * Constructor for controlled sources
     *
     * @param gm - factor in source equation
     */
    protected ControlledSource(String name, double gm)
    {
        super(name);
        this.gm = gm;
    }

    @Override
    public void setNodeIndices(int... indices)
    {
        nPlus = indices[0];
        nMinus = indices[1];
    }

    @Override
    public String toString()
    {
        return super.toString() + " " + gm;
    }
}
