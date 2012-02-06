package sriracha.simulator.solver;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.model.CircuitElement;
import sriracha.simulator.solver.interfaces.IEquation;

public class EquationGenerator {

    private Circuit circuit;

    public EquationGenerator(Circuit circuit) {
        this.circuit = circuit;
    }


    public IEquation generate() {


        LinearEquation equation = new LinearEquation(circuit.getMatrixSize());
        
        circuit.assignAdditionalVarIndices();
        
        circuit.applyStamp(equation);

        return equation;
    }


}
