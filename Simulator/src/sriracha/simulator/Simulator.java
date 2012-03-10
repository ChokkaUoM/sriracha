package sriracha.simulator;

import sriracha.simulator.model.Circuit;
import sriracha.simulator.parser.CircuitBuilder;
import sriracha.simulator.solver.analysis.Analysis;
import sriracha.simulator.solver.analysis.AnalysisType;
import sriracha.simulator.solver.analysis.IAnalysisResults;
import sriracha.simulator.solver.output.filtering.OutputFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main Class for interaction with the Simulator, abstracts away all the sub-components
 */
public class Simulator implements ISimulator
{

    public static Simulator Instance = new Simulator();

    private Circuit circuit;

    private CircuitBuilder builder;

    private List<Analysis> requestedAnalysis;

    private List<OutputFilter> outputFilters;

    private HashMap<AnalysisType, IAnalysisResults> results;


    private Simulator()
    {
        requestedAnalysis = new ArrayList<Analysis>();
        outputFilters = new ArrayList<OutputFilter>();
        results = new HashMap<AnalysisType, IAnalysisResults>();

    }

    private void saveAll()
    {
        for (Analysis analysis : requestedAnalysis)
        {
            save(analysis);
        }
    }

    private void save(Analysis analysis)
    {
        results.put(analysis.getType(), analysis.run());
    }

    /**
     * Sets a new Circuit, and remakes the this.generator and this.equation Fields
     *
     * @param circuit
     */
    private void setCircuit(Circuit circuit)
    {
        this.circuit = circuit;
        //    System.out.println(circuit);
        saveAll();

    }


    /**
     * Parses the netlist and builds an internal representation
     *
     * @param netlist
     * @return
     */
    @Override
    public void setNetlist(String netlist)
    {
        clearData();


        builder = new CircuitBuilder(netlist);
        setCircuit(builder.getCircuit());

        requestedAnalysis.addAll(builder.getAnalysisTypes());

        for (Analysis a : requestedAnalysis)
        {
            a.extractEquation(circuit);
        }

        outputFilters.addAll(builder.getOutputFilters());

        saveAll();
    }


    @Override
    public void addAnalysis(String analysis)
    {
        Analysis a = builder.parseAnalysis(analysis);
        requestedAnalysis.add(a);
        a.extractEquation(circuit);
        save(a);
    }

    @Override
    public IPrintData requestPrintData(String filter)
    {
        OutputFilter f = builder.parsePrint(filter);
        outputFilters.add(f);
        IAnalysisResults r = results.get(f.getAnalysisType());
        return f.filterResults(r);
    }

    /**
     * list of computed and filtered results.
     * each IPrintData corresponds to a .PRINT statement
     * They are found in the list in the same order as
     * results were requested or found in the netlist.
     *
     * @return all computed results so far
     */
    @Override
    public List<IPrintData> getAllResults()
    {
        ArrayList<IPrintData> data = new ArrayList<IPrintData>();
        for (OutputFilter f : outputFilters)
        {
            IAnalysisResults result = results.get(f.getAnalysisType());
            data.add(f.filterResults(result));
        }
        return data;
    }


    private void clearData()
    {
        outputFilters = new ArrayList<OutputFilter>();
        results = new HashMap<AnalysisType, IAnalysisResults>();
        requestedAnalysis = new ArrayList<Analysis>();
    }

    /**
     * debug routine
     */
    public void printAllInfo()
    {

        System.out.println("Circuit:");
        System.out.println(circuit);
        System.out.println("\nRequested Analysis:");
        for (Analysis a : requestedAnalysis)
        {
            System.out.println(a + "\n");
        }
        System.out.println("\nOutput Filters:");
        for (OutputFilter f : outputFilters)
        {
            System.out.println(f + "\n");
        }


    }


}
