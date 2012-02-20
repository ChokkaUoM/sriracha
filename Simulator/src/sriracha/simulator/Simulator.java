package sriracha.simulator;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.parser.CircuitBuilder;
import sriracha.simulator.solver.EquationGenerator;
import sriracha.simulator.solver.IEquation;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysis;
import sriracha.simulator.solver.analysis.IAnalysisResults;
import sriracha.simulator.solver.output.filtering.OutputFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private List<IAnalysis> requestedAnalysis;

    private List<OutputFilter> outputFilters;

    private HashMap<AnalysisType, IAnalysisResults> results;
    
  //  private List<IPlotData> filteredResults;

    private Simulator() {
        requestedAnalysis = new ArrayList<IAnalysis>();
        outputFilters = new ArrayList<OutputFilter>();
        results = new HashMap<AnalysisType, IAnalysisResults>();
        //filteredResults = new ArrayList<IPlotData>();
        
    }
    
    private void saveAll(){
        for(IAnalysis a : requestedAnalysis){
            save(a, equation);
        }
    }
    
    private void save(IAnalysis analysis, IEquation equation){
        results.put(analysis.getSubType(), analysis.analyse(equation));      
    }

    /**
     * Sets a new Circuit, and remakes the this.generator and this.equation Fields
     * @param circuit
     */
    private void setCircuit(Circuit circuit) {
        this.circuit = circuit;
        generator = new EquationGenerator(circuit);
        equation = generator.generate();
        saveAll();

    }


    /**
     * Parses the netlist and builds an internal representation
     * @param netlist
     * @return
     */
    @Override
    public void setNetlist(String netlist) {
        clearData();


        builder = new CircuitBuilder(netlist);
        setCircuit(builder.getCircuit());
        
        requestedAnalysis.addAll(builder.getAnalysisTypes());

        outputFilters.addAll(builder.getOutputFilters());

        runAll();
    }
    
    
    private void runAll(){
        results.clear();
        for(IAnalysis a : requestedAnalysis){
            results.put(a.getSubType(), a.analyse(equation));
        }
    }

    @Override
    public void addAnalysis(String analysis) {
        IAnalysis a = builder.parseAnalysis(analysis);
        requestedAnalysis.add(a);
        save(a, equation);
    }

    @Override
    public IPlotData requestPlot(String filter) {
        OutputFilter f = builder.parsePlot(filter);
        outputFilters.add(f);
        IAnalysisResults r =  results.get(f.getAnalysisType());
        return f.getPlot(r);
    }

    /**
     * list of computed and filtered results.
     * each IPlotData corresponds to a .PLOT statement
     * They are found in the list in the same order as
     * results were requested or found in the netlist.
     *
     * @return all computed results so far
     */
    @Override
    public List<IPlotData> getAllResults() {
        ArrayList<IPlotData> data = new ArrayList<IPlotData>();
        for(OutputFilter f : outputFilters){
            IAnalysisResults result = results.get(f.getAnalysisType());
            data.add(f.getPlot(result));
        }
        return data;
    }


    private void clearData(){
        outputFilters = new ArrayList<OutputFilter>();
        results = new HashMap<AnalysisType, IAnalysisResults>();
        requestedAnalysis = new ArrayList<IAnalysis>();
    }



}
