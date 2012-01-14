package sriracha.simulator.solver;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.solver.interfaces.IEquation;

public class EquationGenerator {

	private Circuit circuit;

	public EquationGenerator(Circuit circuit)
	{
		this.circuit = circuit;
	}


    public IEquation generate(){
        return null;
    }
	
	
}
