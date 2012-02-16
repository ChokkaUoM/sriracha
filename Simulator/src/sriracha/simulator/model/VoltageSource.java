package sriracha.simulator.model;

import sriracha.math.MathActivator;
import sriracha.math.interfaces.IComplex;
import sriracha.simulator.solver.interfaces.IEquation;

public class VoltageSource extends Source  {

    private Double dcVoltage;
    private IComplex voltagePhasor;

    public VoltageSource(String name, double dcVoltage) {
        super(name);
        this.dcVoltage = dcVoltage;
        voltagePhasor = null;
    }
    
    public VoltageSource(String name, IComplex voltagePhasor) {
         super(name);
         this.voltagePhasor = voltagePhasor;
         dcVoltage = null;
    }

    private int currentIndex;


    @Override
    public void applyStamp(IEquation equation) {
        equation.applyRealStamp(currentIndex, nPlus, 1);
        equation.applyRealStamp(currentIndex, nMinus, -1);
        equation.applyRealStamp(nPlus, currentIndex, 1);
        equation.applyRealStamp(nMinus, currentIndex, -1);

        //warning: if we ever have to deal with superposition this will not work
        if(dcVoltage == null){
            equation.applySourceStamp(currentIndex, voltagePhasor);
        }else{
            equation.applySourceStamp(currentIndex, MathActivator.Activator.complex(dcVoltage, 0));
        }
    }

    @Override
    public int getNodeCount() {
        return 2;
    }

    @Override
    public int getExtraVariableCount() {
        return 1;
    }

    /**
     * This is used to build a copy of the circuit element during netlist parsing
     * when adding multiple elements with the same properties.
     * Node information will of course not be copied and have to be entered afterwards
     */
    @Override
    public VoltageSource buildCopy(String name) {
        return new VoltageSource(name, voltagePhasor);
    }

    @Override
    public String toString() {
        if (voltagePhasor == null)
            return super.toString() + " " + dcVoltage;
        else 
            return super.toString() + " " + voltagePhasor;
    }

    /**
     * @return an array containing the matrix indices for the nodes in this circuit element
     */
    @Override
    public int[] getNodeIndices() {
        return new int[]{nPlus, nMinus};
    }

    @Override
    public void setFirstVarIndex(int i) {
        currentIndex = i;
    }

    public int getCurrentVarIndex() {
        return currentIndex;
    }
}
