package sriracha.simulator;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.solver.*;
import sriracha.simulator.solver.interfaces.IAnalysis;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main Class for interaction with the Simulator, abstracts away all the sub-components 
 * giving one cohesive object to deal with from the frontend
 */
class Simulator implements ISimulator {

    public static Simulator Instance = new Simulator();
    
    private Circuit circuit;

    private ArrayList<IAnalysis> requestedAnalysis;

    private HashMap<AnalysisType, ArrayList<OutputFilter>> outputFilters;

    private HashMap<AnalysisType, AnalysisResults> results;

    
    public void addAnalysis(IAnalysis analysis){
        requestedAnalysis.add(analysis);
    }
    

    private Simulator() {
        requestedAnalysis = new ArrayList<IAnalysis>();
        outputFilters = new HashMap<AnalysisType, ArrayList<OutputFilter>>();
        results = new HashMap<AnalysisType, AnalysisResults>();
        
    }

    //todo: Finish this!
    public void runAll(){
        EquationGenerator gen = new EquationGenerator(circuit);
        Solver solver = new Solver(gen.generate());
        
        for(IAnalysis a : requestedAnalysis){
            if(outputFilters.containsKey(a.getType())){
                AnalysisResults res = solver.getResults(a); 
                results.put(a.getType(), res);
                for(OutputFilter f : outputFilters.get(a.getType())){
                    res.output(f);
                }
            }else{
                solver.solve(a, new OutputFilter());
            }
        }
    }


    public Circuit getCircuit() {
        return circuit;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }


    @Override
    public void setNetlist(String netlist) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addAnalysis(String analysis) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addFilter(String filter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ISimulator getInstance() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
