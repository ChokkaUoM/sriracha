package sriracha.simulator.solver.analysis.ac;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexMatrix;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.simulator.Options;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CircuitElement;

/**
 * Linear equation
 * C + jwG = b
 */
public class ACEquation
{

    private MathActivator activator = MathActivator.Activator;

    private int circuitNodeCount;


    private IRealMatrix C;

    private IComplexMatrix G;

    private IComplexVector b;


    private ACEquation(int circuitNodeCount)
    {
        this.circuitNodeCount = circuitNodeCount;
        C = activator.realMatrix(circuitNodeCount, circuitNodeCount);
        G = activator.complexMatrix(circuitNodeCount, circuitNodeCount);
        b = activator.complexVector(circuitNodeCount);
    }

    /**
     * builds matrix for solving circuit at specified frequency point
     *
     * @param frequency in Hz
     * @return C + G*2*PI*frequency
     */
    private IComplexMatrix buildMatrixA(double frequency)
    {
        return (IComplexMatrix) C.plus(G.times(Math.PI * 2 * frequency));
    }

    /**
     * solves the equation for the specified frequency point
     *
     * @param frequency in Hz
     * @return solution vector
     */
    IComplexVector solve(double frequency)
    {

        IComplexMatrix a = buildMatrixA(frequency);

        if (Options.isPrintMatrix())
        {
            System.out.println(a);
            System.out.println("=\n");
            System.out.println(b);
        }

        return a.solve(b);
    }


    public void applyComplexMatrixStamp(int i, int j, double value)
    {
        //no stamps to ground
        if (i == -1 || j == -1) return;

        if (value != 0)
        {
            G.addValue(i, j, activator.complex(0, value));
        }
    }

    public void applyRealMatrixStamp(int i, int j, double value)
    {
        //no stamps to ground
        if (i == -1 || j == -1) return;

        if (value != 0)
        {
            C.addValue(i, j, value);
        }
    }

    public void applySourceVectorStamp(int i, IComplex d)
    {
        //no stamps to ground
        if (i == -1) return;

        b.addValue(i, d);
    }


    @Override
    public ACEquation clone()
    {
        ACEquation clone = new ACEquation(b.getDimension());
        clone.G = (IComplexMatrix) G.clone();
        clone.C = (IRealMatrix) C.clone();
        clone.b = (IComplexVector) b.clone();
        return clone;
    }


    public static ACEquation generate(Circuit circuit)
    {
        ACEquation equation = new ACEquation(circuit.getMatrixSize());

        for (CircuitElement element : circuit.getElements())
        {
            element.applyAC(equation);
        }

        return equation;
    }


}
