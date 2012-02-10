package sriracha.simulator;

/**
 * Interface for the only class that the FrontEnd should ever use from the backend.
 */
public interface ISimulator {

    /**
     * Sets the current netlist. 
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
    public void addFilter(String filter);

    /**
     * Gets the only instance of the singleton Simulator class
     * @return the simulator
     */
    public ISimulator getInstance();


    
}
