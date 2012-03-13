package sriracha.simulator;

import java.util.List;

/**
 * Holds all data requested by 1 .PRINT statement, also contains String labels for the various columns
 */
public interface IPrintData
{

    /**
     *
     * @return the list of all data points associated to this .PRINT statement
     */
    public List<IDataPoint> getData();

    /**
     * list of strings corresponding to the labels for each column. The first label is
     * for the x value of the IDataPoints, and the next n labels are for the n values in each
     * IDataPoint y vector.
     * @return the labels for the data
     */
    public List<String> getLabels();
}
