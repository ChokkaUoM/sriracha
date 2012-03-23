package sriracha.simulator.solver.analysis.dc;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IRealMatrix;
import sriracha.math.interfaces.IRealVector;
import sriracha.simulator.Options;
import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CircuitElement;

public class DCEquation
{


    private IRealMatrix C;


    private IRealVector b;


    private DCEquation(int nodeCount)
    {
        C = MathActivator.Activator.realMatrix(nodeCount, nodeCount);
        b = MathActivator.Activator.realVector(nodeCount);
    }

    public static DCEquation generate(Circuit circuit)
    {
        DCEquation equation = new DCEquation(circuit.getMatrixSize());

        for (CircuitElement element : circuit.getElements())
        {
            element.applyDC(equation);
        }

        return equation;

    }

    public IRealVector solve()
    {

        if (Options.isPrintMatrix())
        {
            System.out.println(C);
            System.out.println("=\n");
            System.out.println(b);
        }

        return C.solve(b);
    }


    public void applyMatrixStamp(int i, int j, double value)
    {

        //no stamps to ground
        if (i == -1 || j == -1) return;

        if (value != 0)
            C.addValue(i, j, value);


    }

    private DCEquation(IRealMatrix c, IRealVector b)
    {
        this.C = c;
        this.b = b;
    }

    public DCEquation clone()
    {
        return new DCEquation(C.clone(), b.clone());
    }

    public void applySourceVectorStamp(int i, double d)
    {
        //no stamps to ground
        if (i == -1) return;

        b.addValue(i, d);
    }


}
