package sriracha.simulator;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.solver.AnalysisType;
import sriracha.simulator.solver.EquationGenerator;
import sriracha.simulator.solver.OutputFilter;
import sriracha.simulator.solver.Solver;
import sriracha.simulator.solver.interfaces.IAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main Class for interaction with the Simulator, abstracts away all the sub-components 
 * giving one cohesive object to deal with from the frontend
 */
public class Simulator {

    private Circuit circuit;

    private ArrayList<IAnalysis> requestedAnalysis;

    private HashMap<AnalysisType, ArrayList<OutputFilter>> outputFilters;

    public void runAll(){
        EquationGenerator gen = new EquationGenerator(circuit);
        Solver solver = new Solver(gen.generate());
        for(IAnalysis a : requestedAnalysis){
            if(outputFilters.containsKey(a.getType())){
                for(OutputFilter f : outputFilters.get(a.getType())){
                    solver.solve(a, f);
                }
            }else{
                solver.solve(a, new OutputFilter());
            }
        }
    }
    
    


}
