package sriracha.simulator;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.parser.CircuitBuilder;
import sriracha.simulator.solver.*;
import sriracha.simulator.solver.interfaces.IAnalysis;
import sriracha.simulator.solver.interfaces.IEquation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main Class for interaction with the Simulator, abstracts away all the sub-components 
 *
 */
public class Simulator implements ISimulator {

    public static Simulator Instance = new Simulator();
    
    private Circuit circuit;

    private CircuitBuilder builder;
    
    private EquationGenerator generator;

    private IEquation equation;

    private ArrayList<IAnalysis> requestedAnalysis;

    private HashMap<AnalysisType, ArrayList<OutputFilter>> outputFilters;

    private HashMap<AnalysisType, AnalysisResults> results;

    private Simulator() {
        requestedAnalysis = new ArrayList<IAnalysis>();
        outputFilters = new HashMap<AnalysisType, ArrayList<OutputFilter>>();
        results = new HashMap<AnalysisType, AnalysisResults>();
        
    }
    
    private void saveAll(){
        for(IAnalysis a : requestedAnalysis){
            save(a, equation);
        }
    }
    
    private void save(IAnalysis analysis, IEquation equation){
        Solver solver = new Solver(equation);
        results.put(analysis.getType(), solver.getResults(analysis));
    }

    private void setCircuit(Circuit circuit) {
        this.circuit = circuit;
        generator = new EquationGenerator(circuit);
        equation = generator.generate();

    }


    @Override
    public void setNetlist(String netlist) {
        builder = new CircuitBuilder(netlist);
        setCircuit(builder.getCircuit());
        requestedAnalysis.addAll(builder.getAnalysisTypes());

    }

    @Override
    public void addAnalysis(String analysis) {
        IAnalysis a = builder.parseAnalysis(analysis);
        requestedAnalysis.add(a);
        save(a, equation);
    }

    @Override
    public void addFilter(String filter) {
        OutputFilter f = builder.parsePlot(filter);

        if(!outputFilters.containsKey(f.getAnalysisType())){
            outputFilters.put(f.getAnalysisType(), new ArrayList<OutputFilter>());
        }

        outputFilters.get(f.getAnalysisType()).add(f);

    }


}
