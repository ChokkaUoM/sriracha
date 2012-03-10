package sriracha.simulator.solver.output.filtering;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.math.interfaces.IRealVector;
import sriracha.math.interfaces.IVector;

public abstract class NodeInfo
{

    protected NodeDataFormat format;


    protected NodeInfo(NodeDataFormat format)
    {
        this.format = format;
    }

    public double[] extractFrom(IVector data)
    {
        if (data instanceof IComplexVector)
        {
            return extractFrom((IComplexVector) data);
        } else if (data instanceof IRealVector)
        {
            return extractFrom((IRealVector) data);
        }
        return null;
    }

    public abstract double[] extractFrom(IComplexVector data);

    public abstract double[] extractFrom(IRealVector data);

    protected double[] getFromType(IComplex val)
    {


        switch (format)
        {
            case Complex:
                return toArray(val);
            case Real:
                return new double[]{val.getReal()};
            case Imaginary:
                return new double[]{val.getImag()};
            case Magnitude:
                return new double[]{getMagnitude(val)};
            case Phase:
                return new double[]{getPhase(val)};
            case Decibels:
                return new double[]{toDecibel(getMagnitude(val))};
            default:
                return null;

        }
    }

    protected String getFormatName(NodeDataFormat format)
    {
        switch (format)
        {
            case Complex:
                return "";
            case Real:
                return "R";
            case Imaginary:
                return "I";
            case Magnitude:
                return "M";
            case Phase:
                return "P";
            case Decibels:
                return "DB";
            default:
                return null;

        }
    }


    public static double[] toArray(IComplex c)
    {
        return new double[]{c.getReal(), c.getImag()};
    }


    public static double getMagnitude(IComplex phasor)
    {
        return Math.sqrt(Math.pow(phasor.getReal(), 2) + Math.pow(phasor.getImag(), 2));
    }

    public static double getPhase(IComplex phasor)
    {
        return Math.atan2(phasor.getImag(), phasor.getReal());
    }

    public static double toDecibel(double d)
    {
        return 20 * Math.log10(d);
    }

}
