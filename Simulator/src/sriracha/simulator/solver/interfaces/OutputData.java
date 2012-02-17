package sriracha.simulator.solver.interfaces;

import sriracha.math.interfaces.IComplex;
import sriracha.math.interfaces.IComplexVector;
import sriracha.simulator.solver.OutputType;

/**
 * Created by IntelliJ IDEA.
 * User: antoine
 * Date: 10/02/12
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class OutputData {

    protected OutputType type;


    protected OutputData(OutputType type) {
        this.type = type;
    }

    public abstract double[] extract(IComplexVector data);

    protected double[] getFromType(IComplex val){
        switch (type){
            case Complex:
                return toArray(val);
            case Real:
                return new double[] {val.getReal()};
            case Imaginary:
                return new double[] {val.getImag()};
            case Magnitude:
                return new double[] {getMagnitude(val)};
            case Phase:
                return new double[] {getPhase(val)};
            case Decibels:
                return new double[] {toDecibel(getMagnitude(val))};
            default: return null;

        }
    }


    public static double[] toArray(IComplex c){
        return new double[]{c.getReal(), c.getImag()};
    }
        
    
    public static double getMagnitude(IComplex phasor){
        return Math.sqrt(Math.pow(phasor.getReal(), 2) + Math.pow(phasor.getImag(), 2));
    }
    
    public static double getPhase(IComplex phasor){
        return Math.atan2(phasor.getImag(), phasor.getReal());
    }

    public static double toDecibel(double d){
        return 20 * Math.log10(d);
    }

}
