package sriracha.simulator;

import java.util.List;

/**
 * Interface for the only class that the FrontEnd should ever use from the backend.
 */
public interface ISimulator {

    /**
     * Sets the current netlist.
     * If the initial netlist contains analysis and output filtering statements
     * the results will all be streamed right away.
     * @param netlist
     */
    public void setNetlist(String netlist);

    /**
     * adds an analysis request to the simulator. The string should be one line that 
     * matches what you would find in a netlist.
     * ex.: ".AC LIN 100 1000 2000"
     * @param analysis string representation of analysis spec
     */
    public void addAnalysis(String analysis);

    /**
     * Corresponds to one of spices output specification lines.
     * ex.: ".PLOT V(1) I(Vin) V(5, 4)"
     * @param filter string representation of output spec
     */
    public IPlotData requestPlot(String filter);

    /**
     * list of computed and filtered results.
     * each IPlotData corresponds to a .PLOT statement
     * They are found in the list in the same order as
     * results were requested or found in the netlist.
     *
     * @return all computed results so far
     */
    public List<IPlotData> getAllResults();




    
}
